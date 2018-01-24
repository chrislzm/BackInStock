package com.chrisleung.notifications.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
		    configure(restTemplate);
        	    BasicAuthorizationInterceptor notificationApiAuth = new BasicAuthorizationInterceptor(notificationApiUsername, notificationApiPassword); 
       	    BasicAuthorizationInterceptor shopifyApAuth = new BasicAuthorizationInterceptor(shopifyApiKey, shopifyPassword); 

        	    /* 2. Retrieve unsent notifications from Notifications REST API */
        	    restTemplate.getInterceptors().add(notificationApiAuth);
            String url = String.format("%s?%s=%s",notificationApiUrl,notificationApiParamSent,false);
			ResponseEntity<NotificationWrapper> notificationsResponse = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
			Iterable<Notification> newNotifications = notificationsResponse.getBody().getNotifications();
			Date lastUpdate = notificationsResponse.getBody().getCurrentDate();
			Set<String> allNotifications = new HashSet<>();
			for(Notification n : newNotifications) {
			    allNotifications.add(n.getId());
			}
            restTemplate.getInterceptors().remove(0);
			
			/* Program Loop (Step 2, 3, 4) */
            long sleepMs = interval * 1000;
            Map<Integer,List<Notification>> variantIdToNotificationMap = new HashMap<Integer,List<Notification>>();
            int totalSent = 0;
            int totalFails = 0;
            
            log.info(String.format("%s Beginning program loop", logTag));
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
			    
			    /* 2b. Check inventory levels for back-in-stock variants */
			    List<Variant> inStock = new LinkedList<>();
			    restTemplate.getInterceptors().add(shopifyApAuth);
			    int numOutOfStock = 0;
			    for(Integer variantId : variantIdToNotificationMap.keySet()) {
                    url = String.format("%s%s%s",shopifyVariantUrl,variantId,shopifyVariantPostfix);
                    ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class); 
                    Variant v = shopifyResponse.getBody().getVariant();
                    if(v.getInventory_quantity() > 0) {
                        inStock.add(v);
                    } else {
                        numOutOfStock += variantIdToNotificationMap.get(variantId).size();
                    }
			    }
	            restTemplate.getInterceptors().remove(0);
			    
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
        			            totalFails++;
        			        }
        			    }
        			    String result = String.format("%s Notification(s) Sent, %s Failed for SKU %s (Variant ID %s)", numSent, numFailed, v.getSku(), v.getId()); 
        			    if(variantIdToNotificationMap.get(v.getId()).size() == 0) {
                        // If all notifications sent, remove the variant
        			        log.info(String.format("%s Success: %s",logTag,result));
        			        variantIdToNotificationMap.remove(v.getId());
        			    } else {
        			        log.info(String.format("%s Failure: %s",logTag,result));
        			    }
        			}
	            
        			log.info(String.format("%s Status: %s Total Notification(s), %s Out of Stock, %s Sent, %s Failed Send Attempts",logTag, allNotifications.size(),numOutOfStock,totalSent,totalFails));
        			
			    /* 4. Sleep */
			    Thread.sleep(sleepMs);
			    
        			/* 5. Get new notifications */
                restTemplate.getInterceptors().add(notificationApiAuth);
                url = String.format("%s?%s=%s",notificationApiUrl,notificationApiParamCreatedDate,lastUpdate.getTime());
                notificationsResponse = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
                restTemplate.getInterceptors().remove(0);
                newNotifications = notificationsResponse.getBody().getNotifications();
                lastUpdate = notificationsResponse.getBody().getCurrentDate();
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
	
    /* Configure to accept unverified SSL certificates */
    /* TODO: This should be removed in production code */
	private void configure(RestTemplate restTemplate) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);
	}
}