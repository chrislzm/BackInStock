package com.chrisleung.notifications.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;
import com.shopify.api.*;

@SpringBootApplication
public class Application {

    private String logTag;
    private boolean logVerbose;
    private BlockingQueue<EmailNotification> emailQueue;

    private ApplicationProperties appProperties;
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
	public static void main(String args[]) {
		SpringApplication.run(Application.class);
	}

	@Autowired
	public void setApp(ApplicationProperties ap) {
	    this.appProperties = ap;
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
		    /* 0. General Setup  */
		    logTag = appProperties.getLog().getTag();
		    logVerbose = appProperties.getLog().getVerbose();
		    emailQueue = new LinkedBlockingQueue<>(appProperties.getEmail().getQueueSize());
		    
		    /* 1. API Setup */
            NotificationsApi notificationsApi = new NotificationsApi(restTemplate, appProperties.getRestapi()); 
       	    ShopifyApi shopifyApi= new ShopifyApi(restTemplate, appProperties.getShopifyapi());
       	    EmailService emailService = new EmailService(appProperties.getEmail(),emailQueue,notificationsApi,appProperties.getLog());
       	    emailService.start();

        	    /* 2. Retrieve unsent notifications from the Stock Notifications REST API */
       	    NotificationWrapper notificationResponse = notificationsApi.getAllUnsentNotifications(); 
			Iterable<Notification> newNotifications = notificationResponse.getNotifications();
			Date lastUpdate = notificationResponse.getCurrentDate();
			Set<String> allNotifications = new HashSet<>(); // Used to detect duplicates when updating
			for(Notification n : newNotifications) {
			    allNotifications.add(n.getId());
			}
			
			/* Program Loop Setup */
			long sleepMs = appProperties.getRestapi().getRefresh() * 1000;
            // The main data structure: variant-ID to notifications map
            Map<Integer,List<Notification>> variantNotificationMap = new HashMap<Integer,List<Notification>>();
            int totalQueued = 0; // For log output
            
            /* Program Loop */
            log.info(String.format("%s Starting Notification Service...", logTag));

            while(true) {

                /* 3. Add new notifications to the variant ID-notification map */
                int numNew = 0; // For current iteration's log output
			    for(Notification n : newNotifications) {
    			        List<Notification> l = variantNotificationMap.get(n.getVariantId());
    			        if(l == null) {
    			            l = new LinkedList<>();
    			            variantNotificationMap.put(n.getVariantId(), l);
    			        }
    			        l.add(n);
                    numNew++;
			    }

			    /* 4. Detect variants that are back in stock */
                int numOutOfStock = 0; // For current iteration's log output
                List<Variant> inStock = new LinkedList<>();
                Map<Variant,Product> variantProductMap = new HashMap<>(); // Variant product data
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
	            Iterator<Variant> variantsToNotify = inStock.iterator();
        			while(variantsToNotify.hasNext()) {
        			    Variant v = variantsToNotify.next();
        			    Product p = variantProductMap.get(v);
        			    // Get all unsent notifications for this variant
        			    List<Notification> variantNotifications = variantNotificationMap.remove(v.getId());
        			    for (Notification n: variantNotifications) {
        			        emailQueue.put(new EmailNotification(p,v,n));
        			        totalQueued++;
        			    }
        			}
        			synchronized(emailQueue) {
        			    emailQueue.notify();
        			}
	            
        			/* 6. Standard Log Output: Summary for this iteration */ 
        			log.info(String.format("%s Status: %s New Notification(s), %s Total, %s Sent, %s Unsent (%s Queued/%s Out of Stock)",
        			        logTag,
        			        numNew,
        			        allNotifications.size(),
        			        totalQueued-emailQueue.size(),
        			        emailQueue.size()+numOutOfStock,
        			        emailQueue.size(),
        			        numOutOfStock));
        			
			    /* 7. Sleep */
			    Thread.sleep(sleepMs);
			    
        			/* 8. Fetch new notifications */
			    notificationResponse = notificationsApi.getNewNotificationsSince(lastUpdate);
                newNotifications = notificationResponse.getNotifications();
                Iterator<Notification> newNotificationsIterator = newNotifications.iterator();
                while(newNotificationsIterator.hasNext()) {
                    Notification n = newNotificationsIterator.next();
                    // Handle duplicates
                    if(allNotifications.contains(n.toString()))
                        newNotificationsIterator.remove();
                    else 
                        allNotifications.add(n.toString());
                }
                lastUpdate = notificationResponse.getCurrentDate();
			}			
		};
	}
}