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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.*;
import com.shopify.api.*;

/**
 * This class implements the Notifications Service application. It periodically
 * polls the database (via REST API) for new notifications, retrieves inventory
 * and product data from the Shopify API, and queues email notifications when
 * it detects back in stock product variants. (The email service handles
 * sending the notifications and runs on a separate thread.)
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
    private ShopifyApi shopifyApi;
    
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
                List<Variant> inStock = new ArrayList<>();
                Map<Variant,Product> variantProductMap = new HashMap<>(); // Variant-product data map
			    for(Integer variantId : variantNotificationMap.keySet()) {
			        Variant v = shopifyApi.getVariant(variantId);
                    if(v.getInventory_quantity() > 0) {
                        inStock.add(v);
                        variantProductMap.put(v, shopifyApi.getProduct(v));
                    } else {
                        numOutOfStock += variantNotificationMap.get(variantId).size();
                    }
			    }
			    
			    /* 5. Enqueue email notifications for all back in stock variants */
			    if(!inStock.isEmpty()) {
			        for(Variant v : inStock) {
            			    Product p = variantProductMap.get(v); // Product that contains this variant
            			    List<Notification> variantNotifications = variantNotificationMap.remove(v.getId());
            			    for (Notification n: variantNotifications) {
            			        emailQueue.put(new EmailNotification(p,v,n));
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
        			notificationsApi.sleep();
			    
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