package com.chrisleung.notifications.service;

import java.util.List;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
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
		    long sleepMs = interval * 1000;
		    
		    /* 1. Security Setup */
		    
		    /* 1a. Configure to accept unverified SSL certificates */
		    /* TODO: This should be removed in production code    */
		    CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        	    requestFactory.setHttpClient(httpClient);
        	    restTemplate.setRequestFactory(requestFactory);
        	    
        	    /* 1b. Username + Password Auths */
        	    BasicAuthorizationInterceptor notificationAuth = new BasicAuthorizationInterceptor(notificationApiUsername, notificationApiPassword); 
       	    BasicAuthorizationInterceptor shopifyAuth = new BasicAuthorizationInterceptor(shopifyApiKey, shopifyPassword); 

        	    /* 2. Retrieve unsent notifications from Notifications REST API */
        	    restTemplate.getInterceptors().add(notificationAuth);
            String url = String.format("%s?%s=%s",notificationApiUrl,notificationApiParamSent,false);
			ResponseEntity<List<Notification>> notificationsResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Notification>>() {});
			List<Notification> unsentNotifications = notificationsResponse.getBody();
            restTemplate.getInterceptors().remove(0);
			
			/* Program Loop (Step 2, 3, 4) */
			while(true) {
			    
        			/* 2. Add email notification to queue for products that are Back in Stock */
			    restTemplate.getInterceptors().add(shopifyAuth);
	            for(Notification n : unsentNotifications) {
	                url = String.format("%s%s%s",shopifyVariantUrl,n.getVariantId(),shopifyVariantPostfix);
	                log.info("Contacting - " + url);
	                ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class); 
	                Variant v = shopifyResponse.getBody().getVariant();
	                log.info(String.format("%s has inventory %s", v.getSku(), v.getInventory_quantity()));
	            }
	            restTemplate.getInterceptors().remove(0);
			    
        			/* 3. Send notifications (if any) */
        			
			    /* 4. Sleep */
			    Thread.sleep(sleepMs);
			    
        			/* 5. Poll for new notifications */
			}			
		};
	}
}