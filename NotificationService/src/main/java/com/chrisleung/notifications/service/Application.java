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

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
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
import com.shopify.api.*;

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
    @Value("${my.notifications.shopifyapi.product.url}")
    private String shopifyProductUrl;
    @Value("${my.notifications.shopifyapi.postfix}")
    private String shopifyPostfix;
    @Value("${my.notifications.log.tag}")
    private String logTag;
    @Value("${my.notifications.log.verbose}")
    private boolean logVerbose;

    @Value("${my.notifications.email.smtp.address}")
    private String emailServer;
    @Value("${my.notifications.email.smtp.port}")
    private int emailPort;
    @Value("${my.notifications.email.smtp.username}")
    private String emailUsername;
    @Value("${my.notifications.email.smtp.password}")
    private String emailPassword;
    @Value("${my.notifications.email.template.path}")
    private String emailTemplatePath;
    @Value("${my.notifications.email.sender.name}")
    private String emailSenderName;
    @Value("${my.notifications.email.sender.address}")
    private String emailSenderAddress;
    @Value("${my.notifications.email.subject.template}")
    private String emailSubjectTemplate;
    @Value("${my.notifications.email.shop.name}")
    private String emailShopName;
    @Value("${my.notifications.email.shop.domain}")
    private String emailShopDomain;
    
    // REST API Username+Password Authentication
    private BasicAuthorizationInterceptor notificationApiAuth;
    private BasicAuthorizationInterceptor shopifyApAuth;
    
    // For sending email
    Mailer emailer;
    String emailTemplate;
    
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
		    
		    /* 0. Email Setup */
		    emailer = MailerBuilder
		                .withSMTPServer(emailServer, emailPort, emailUsername, emailPassword)
		                .withTransportStrategy(TransportStrategy.SMTPS)
		                .buildMailer();
		    emailTemplate = new String(Files.readAllBytes(Paths.get(emailTemplatePath)));
		    
		    /* 1. REST APIs Security Setup */
        	    notificationApiAuth = new BasicAuthorizationInterceptor(notificationApiUsername, notificationApiPassword); 
       	    shopifyApAuth = new BasicAuthorizationInterceptor(shopifyApiKey, shopifyPassword);

        	    /* 2. Retrieve unsent notifications from the Stock Notifications REST API */
       	    NotificationWrapper notificationResponse = getAllUnsentNotifications(restTemplate); 
			Iterable<Notification> newNotifications = notificationResponse.getNotifications();
			Date lastUpdate = notificationResponse.getCurrentDate();
			Set<String> allNotifications = new HashSet<>(); // Used to detect duplicates when updating
			for(Notification n : newNotifications) {
			    allNotifications.add(n.getId());
			}
			
			/* Program Loop Setup */
			long sleepMs = interval * 1000;
            // The main data structure: variant-ID to notifications map
            Map<Integer,List<Notification>> variantNotificationMap = new HashMap<Integer,List<Notification>>();
            int totalSent = 0, totalFails = 0; // For log output
            
            /* Program Loop */
            log.info(String.format("%s Starting...", logTag));

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
			    for(Integer variantId : variantNotificationMap.keySet()) {
			        Variant v = getVariant(variantId, restTemplate);
                    if(v.getInventory_quantity() > 0) {
                        inStock.add(v);
                    } else {
                        numOutOfStock += variantNotificationMap.get(variantId).size();
                    }
			    }

        			/* 5. Download product data for back in stock variants */ 
			    Map<Variant,Product> variantProductMap = new HashMap<>();
			    for(Variant v : inStock) {
			        variantProductMap.put(v, getProduct(v,restTemplate));
			    }
			    
			    /* 6. Send notifications for all back in stock variants */
	            Iterator<Variant> variantsToNotify = inStock.iterator();
        			while(variantsToNotify.hasNext()) {
        			    Variant v = variantsToNotify.next();
        			    Product p = variantProductMap.get(v);
        			    List<Notification> variantNotifications = variantNotificationMap.get(v.getId());
        			    Iterator<Notification> toNotify = variantNotifications.iterator();
                    int numFailed = 0, numSent = 0; // For verbose log output
        			    while(toNotify.hasNext()) {
        			        Notification n = toNotify.next();
        			        /* 6a. Attempt to email the notification */
        			        boolean sentSuccess = sendNotification(n,p,v);
        			        if(sentSuccess) {
        			            toNotify.remove();
        			            numSent++;
        			            totalSent++;
        			            /* 6b. Update the Stock Notifications REST API that we have sent the notification */
        			            // TODO: Handle failure
        			        } else {
        			            numFailed++;
        			        }
        			    }
        			    
        			    // 6c: Remove variants from variant-notification map if all of its notifications have been sent
        			    String result; // For log output
        			    if(variantNotificationMap.get(v.getId()).size() == 0) {
        			        variantNotificationMap.remove(v.getId());
        			        result = "Success"; // For log output
        			    } else {
        			        totalFails++; result = "Failure"; // For Log output
        			    }
        			    if(logVerbose) {
        			        log.info(String.format("%s %s: %s Notification(s) Sent, %s Failed for SKU %s (Variant ID %s)",logTag,result,numSent, numFailed, v.getSku(), v.getId()));
        			    }
        			}
	            
        			/* 7. Standard Log Output: Summary for this iteration */ 
        			log.info(String.format("%s Status: %s New Notification(s), %s Total, %s Sent, %s Unsent (%s Failed/%s Out of Stock), %s Total Failed Attempts",
        			        logTag,
        			        numNew,
        			        allNotifications.size(),
        			        totalSent,
        			        allNotifications.size()-totalSent,
        			        allNotifications.size()-totalSent-numOutOfStock,
        			        numOutOfStock,
        			        totalFails));
        			
			    /* 8. Sleep */
			    Thread.sleep(sleepMs);
			    
        			/* 9. Fetch new notifications */
			    notificationResponse = getNewNotificationsSince(lastUpdate,restTemplate);
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
        String url = String.format("%s%s%s",shopifyVariantUrl,variantId,shopifyPostfix);
        ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return shopifyResponse.getBody().getVariant();
	}
	
	private Product getProduct(Variant v, RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(shopifyApAuth);
        String url = String.format("%s%s%s",shopifyProductUrl,v.getProduct_id(),shopifyPostfix);
        ResponseEntity<ProductWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, ProductWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return shopifyResponse.getBody().getProduct();
	}
	
	private boolean sendNotification(Notification n, Product p, Variant v) {
	    
	    String imageUrl = p.getImages()[0].getSrc();
	    String emailImageUrl = imageUrl.substring(0, imageUrl.indexOf(".jpg")) + "_560x.jpg";
	    String emailSubject = emailSubjectTemplate
	                            .replace("{{shop.name}}", emailShopName)
	                            .replace("{{product.title}}", p.getTitle());
	    
	    String emailBody = emailTemplate
	                            .replace("{{shop.domain}}", emailShopDomain)
	                            .replace("{{product.handle}}", p.getHandle())
	                            .replace("{{product.title}}", p.getTitle())
	                            .replace("{{variant.title}}", v.getTitle())
	                            .replace("{{shop.name}}", emailShopName)
	                            .replace("{{product.image}}", emailImageUrl);
	                            
	    Email email = EmailBuilder.startingBlank()
	                    .to(n.getEmail())
	                    .from(emailSenderName, emailSenderAddress)
	                    .withSubject(emailSubject)
	                    .withHTMLText(emailBody)
	                    .buildEmail();
	    
        boolean success = false;
	    if(!emailer.validate(email)) return false;
	    try {
	        emailer.sendMail(email);
	        success = true;
	    } catch(Exception e) {
	        if(logVerbose) e.printStackTrace();
	    }
	    return success;
	}
}