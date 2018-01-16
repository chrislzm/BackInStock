package com.chrisleung.notifications.service;

import java.util.List;
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
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

	@Value("${my.notifications.api.server.auth.username}")
	private String username;

	@Value("${my.notifications.api.server.auth.password}")
	private String password;
	
	@Value("${my.notifications.api.server.url}")
	private String url;
	
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
			log.info(String.format("Username: %s Password: %s URL: %s", username, password, url));
			restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
			ResponseEntity<List<Notification>> notificationsResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Notification>>() {});
			List<Notification> notifications = notificationsResponse.getBody();
			for(Notification n : notifications) {
				log.info(n.toString());
			}
		};
	}
}