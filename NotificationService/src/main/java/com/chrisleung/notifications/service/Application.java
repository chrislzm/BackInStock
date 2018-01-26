package com.chrisleung.notifications.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;
import com.shopify.api.Variant;
import com.shopify.api.VariantWrapper;

@SpringBootApplication
public class Application {

	@Value("${my.notifications.restapi.username}")
	private String notificationApiUsername;

	@Value("${my.notifications.restapi.password}")
	private String notificationApiPassword;
	
	@Value("${my.notifications.restapi.url}")
	private String notificationApiUrl;

    @Value("${my.notifications.restapi.param.sent}")
    private String notificationApiParamSent;

    @Value("${my.notifications.restapi.param.createdDate}")
    private String notificationApiParamCreatedDate;

    @Value("${my.notifications.refresh}")
    private int interval;

    @Value("${my.notifications.shopifyapi.apikey}")
    private String shopifyApiKey;

    @Value("${my.notifications.shopifyapi.password}")
    private String shopifyPassword;
   
    @Value("${my.notifications.shopifyapi.product.variant.url}")
    private String shopifyVariantUrl;

    @Value("${my.notifications.shopifyapi.product.variant.url.postfix}")
    private String shopifyVariantPostfix;
    
    @Value("${my.notifications.log.tag}")
    private String logTag;

    // Username+Password Authentication
    private BasicAuthorizationInterceptor notificationApiAuth;
    private BasicAuthorizationInterceptor shopifyApAuth;
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
		    
		    /* 1. Security Setup */
        	    notificationApiAuth = new BasicAuthorizationInterceptor(notificationApiUsername, notificationApiPassword); 
       	    shopifyApAuth = new BasicAuthorizationInterceptor(shopifyApiKey, shopifyPassword);

        	    /* 2. Retrieve unsent notifications from Notifications REST API */
       	    NotificationWrapper notificationResponse = getAllUnsentNotifications(restTemplate); 
			Iterable<Notification> newNotifications = notificationResponse.getNotifications();
			Date lastUpdate = notificationResponse.getCurrentDate();
			Set<String> allNotifications = new HashSet<>();
			for(Notification n : newNotifications) {
			    allNotifications.add(n.getId());
			}
			
			/* Program Loop */
            long sleepMs = interval * 1000;
            Map<Integer,List<Notification>> variantIdToNotificationMap = new HashMap<Integer,List<Notification>>();
            int totalSent = 0;
            int totalFails = 0;
            
            log.info(String.format("%s Starting...", logTag));
			while(true) {
			    
        			/* 2. Detect notifications that have back-in-stock products */
			    
			    /* 2a. Prep for batch API reads - One request per variant id  */
			    int numNew = 0;
			    for(Notification n : newNotifications) {
    			        List<Notification> l = variantIdToNotificationMap.get(n.getVariantId());
    			        if(l == null) {
    			            l = new LinkedList<>();
    			            variantIdToNotificationMap.put(n.getVariantId(), l);
    			        }
    			        l.add(n);
                    numNew++;
			    }
			    if(numNew > 0)
			        log.info(String.format("%s Fetched %s new notification(s)", logTag, numNew));
			    
			    /* 2b. Check Shopify inventory levels for back-in-stock variants */
			    List<Variant> inStock = new LinkedList<>();
			    int totalOutOfStock = 0;
			    for(Integer variantId : variantIdToNotificationMap.keySet()) {
			        Variant v = getVariant(variantId, restTemplate);
                    if(v.getInventory_quantity() > 0) {
                        inStock.add(v);
                    } else {
                        totalOutOfStock += variantIdToNotificationMap.get(variantId).size();
                    }
			    }

        			/* 3. Send notifications */ 
	            Iterator<Variant> variantsToNotify = inStock.iterator();
        			while(variantsToNotify.hasNext()) {
        			    Variant v = variantsToNotify.next();
        			    List<Notification> variantNotifications = variantIdToNotificationMap.get(v.getId());
        			    Iterator<Notification> toNotify = variantNotifications.iterator();
                    int numFailed = 0;
                    int numSent = 0;
        			    while(toNotify.hasNext()) {
        			        Notification n = toNotify.next();
        			        // Tell API server to email the notification
        			        boolean sentSuccess = false;
        			        
        			        // If success, remove the notification from the list
        			        if(sentSuccess) {
        			            toNotify.remove();
        			            numSent++;
        			            totalSent++;
        			        } else {
        			            numFailed++;
        			        }
        			    }
        			    String result = String.format("%s Notification(s) Sent, %s Failed for SKU %s (Variant ID %s)", numSent, numFailed, v.getSku(), v.getId()); 
        			    if(variantIdToNotificationMap.get(v.getId()).size() == 0) {
                        // If all notifications sent, remove the variant
        			        log.info(String.format("%s Success: %s",logTag,result));
        			        variantIdToNotificationMap.remove(v.getId());
        			    } else {
        			        log.info(String.format("%s Failure: %s",logTag,result));
        			        totalFails++;
        			    }
        			}
	            
        			log.info(String.format("%s Status: %s Total Notification(s), %s Sent, %s Unsent (%s Failed/%s Out of Stock), %s Total Failed Attempts",
        			        logTag,
        			        allNotifications.size(),
        			        totalSent,
        			        allNotifications.size()-totalSent,
        			        allNotifications.size()-totalSent-totalOutOfStock,
        			        totalOutOfStock,
        			        totalFails));
        			
			    /* 4. Sleep */
			    Thread.sleep(sleepMs);
			    
        			/* 5. Get new notifications */
			    notificationResponse = getNewNotificationsSince(lastUpdate,restTemplate);
                newNotifications = notificationResponse.getNotifications();
                lastUpdate = notificationResponse.getCurrentDate();
                Iterator<Notification> newNotificationsIterator = newNotifications.iterator();
                while(newNotificationsIterator.hasNext()) {
                    Notification n = newNotificationsIterator.next();
                    if(allNotifications.contains(n.toString()))
                        newNotificationsIterator.remove();
                    else 
                        allNotifications.add(n.toString());
                }
			}			
		};
	}
	
	private NotificationWrapper getAllUnsentNotifications(RestTemplate restTemplate) {
        String url = String.format("%s?%s=%s",notificationApiUrl,notificationApiParamSent,false);
        return getNotifications(url, restTemplate);
	}
	
	private NotificationWrapper getNewNotificationsSince(Date lastUpdate, RestTemplate restTemplate) {
        String url = String.format("%s?%s=%s",notificationApiUrl,notificationApiParamCreatedDate,lastUpdate.getTime());
        return getNotifications(url, restTemplate);
	}
	
	private NotificationWrapper getNotifications(String url, RestTemplate restTemplate) {
	    restTemplate.getInterceptors().add(notificationApiAuth);
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return response.getBody();
	}
	
	private Variant getVariant(int variantId, RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(shopifyApAuth);
        String url = String.format("%s%s%s",shopifyVariantUrl,variantId,shopifyVariantPostfix);
        ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return shopifyResponse.getBody().getVariant();
	}
}