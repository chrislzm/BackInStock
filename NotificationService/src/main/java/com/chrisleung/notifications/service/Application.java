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

@SpringBootApplication
public class Application {

	@Value("${my.notifications.restapi.username}")
	private String restApiUsername;

	@Value("${my.notifications.restapi.password}")
	private String restApiPassword;
	
	@Value("${my.notifications.restapi.url}")
	private String restApiUrl;

    @Value("${my.notifications.restapi.param.sent}")
    private String restApiParamSent;

    @Value("${my.notifications.restapi.param.createdDate}")
    private String restApiParamCreatedDate;

    @Value("${my.notifications.refresh}")
    private int interval;

    @Value("${my.notifications.shopifyapi.apikey}")
    private String shopifyApiKey;

    @Value("${my.notifications.shopifyapi.password}")
    private String shopifyPassword;
   
    @Value("${my.notifications.shopifyapi.product.variant.url}")
    private String shopifyVariantUrl;

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
		    /* Security Setup (Configure to accept unverified SSL certificates + username, password auth) */
		    CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        	    requestFactory.setHttpClient(httpClient);
        	    restTemplate.setRequestFactory(requestFactory);
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(restApiUsername, restApiPassword));

        	    /* 1. Download all unsent notifications */
            String urlQuery = String.format("%s?%s=%s",restApiUrl,restApiParamSent,false);
			ResponseEntity<List<Notification>> notificationsResponse = restTemplate.exchange(urlQuery, HttpMethod.GET, null, new ParameterizedTypeReference<List<Notification>>() {});
			List<Notification> unsentNotifications = notificationsResponse.getBody();
			for(Notification n : unsentNotifications) {
				log.info(n.toString());
			}
			
			/* Program Loop (Step 2, 3, 4) */
			
			/* 2. Detect products that are Back in Stock */
			
			/* 3. Send notifications (if any) */
			
			/* 4. Poll for new notifications */
			
		};
	}
}