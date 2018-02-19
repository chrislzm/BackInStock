package com.chrisleung.notifications.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.*;

/**
 * This class implements the Notifications Service application. It periodically
 * polls the database for new notifications, retrieves corresponding inventory
 * and product data from the Shopify API, and queues email notifications
 * whenever back in stock product variants are detected. The email service
 * handles sending the notifications on a separate thread.
 * 
 * @author Chris Leung
 */
@SpringBootApplication
public class Application {

    @Autowired
    private EmailService emailService;
    @Autowired
    private Log logger;
    @Autowired
    private DatabaseRestApi notificationsApi;
    @Autowired
    ShopifyApi shopifyApi;
    
    @Value("${my.notifications.refresh.interval.seconds}")
    private Integer sleepTime;
    
    private StoreApi storeApi;
    
    private BlockingQueue<EmailNotification> emailQueue;    
    
	public static void main(String args[]) {
		SpringApplication.run(Application.class);
	}
		
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
		    /* 0. Set the online store API to use */
		    storeApi = shopifyApi;

		    /* 1. Email Service Setup */
            emailQueue = emailService.getQueue();
       	    emailService.start();

        	    /* 2. Retrieve all unsent notifications from the Stock Notifications REST API */
       	    NotificationWrapper response = notificationsApi.getAllUnsentNotifications(); 
			Iterable<Notification> newNotifications = response.getNotifications();
			Date lastUpdate = response.getCurrentDate();
			Set<String> allNotifications = new HashSet<>(); // Used to detect duplicates when updating
			for(Notification n : newNotifications) {
			    allNotifications.add(n.getId());
			}
			
			/* 3. Program Loop Setup */
            // The main data structure: variant-ID to notifications map
            Map<Integer,List<Notification>> variantNotificationMap = new HashMap<Integer,List<Notification>>();
            int totalQueued = 0; // For log output
            
            logger.message("Starting Notification Service...");
            
            while(true) {

                /* 4. Add new notifications to the variant-notification map */
                int numNew = 0; // For log output
			    for(Notification n : newNotifications) {
    			        List<Notification> variantNotifications = variantNotificationMap.get(n.getVariantId());
    			        if(variantNotifications == null) {
    			            variantNotifications = new ArrayList<>();
    			            variantNotificationMap.put(n.getVariantId(), variantNotifications);
    			        }
    			        variantNotifications.add(n);
                    numNew++;
			    }

			    /* 5. Detect variants that are back in stock */
                int numOutOfStock = 0; // For log output
                List<ProductVariant> inStock = new ArrayList<>();
			    for(Integer variantId : variantNotificationMap.keySet()) {
			        ProductVariant pv = storeApi.getProductVariant(variantId);
                    if(pv.getInventoryQuantity() > 0) {
                        inStock.add(pv);
                    } else {
                        numOutOfStock += variantNotificationMap.get(variantId).size();
                    }
			    }
			    
			    /* 5. Enqueue email notifications for all back in stock variants */
			    if(!inStock.isEmpty()) {
			        for(ProductVariant pv : inStock) {
            			    List<Notification> variantNotifications = variantNotificationMap.remove(pv.getVariantId());
            			    for (Notification n: variantNotifications) {
            			        emailQueue.put(new EmailNotification(pv,n));
            			        totalQueued++;
            			    }
			        }
            			synchronized(emailQueue) {
            			    emailQueue.notify(); // Notify once more in case of race condition
            			}
        			}
	            
        			/* 6. Standard Log Output: Summary for this iteration */ 
        			logger.message(String.format("Status: %s New Notification(s), %s Total, %s Sent, %s Unsent (%s Queued/%s Out of Stock)",
        			        numNew,
        			        allNotifications.size(),
        			        totalQueued-emailQueue.size(),
        			        emailQueue.size()+numOutOfStock,
        			        emailQueue.size(),
        			        numOutOfStock));
        			
			    /* 7. Sleep */
        			Thread.sleep(TimeUnit.SECONDS.toMillis(sleepTime));
			    
        			/* 8. Fetch new notifications since last update */
			    response = notificationsApi.getNewNotificationsSince(lastUpdate);
                newNotifications = response.getNotifications();
                Iterator<Notification> newNotificationsIterator = newNotifications.iterator();
                // Filter any duplicates
                while(newNotificationsIterator.hasNext()) {
                    Notification n = newNotificationsIterator.next();
                    if(allNotifications.contains(n.getId())) {
                        newNotificationsIterator.remove();
                    } else { 
                        allNotifications.add(n.getId());
                    }
                }
                lastUpdate = response.getCurrentDate();
			}			
		};
	}
}