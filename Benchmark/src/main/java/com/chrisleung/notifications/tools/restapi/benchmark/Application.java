package com.chrisleung.notifications.tools.restapi.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.*;

/**
 * This class implements a simple benchmarking tool for the REST API. It
 * measures performance of single endpoint exposed to the world: Submitting
 * a new notification.
 * 
 * @author Chris Leung
 */
@SpringBootApplication
public class Application {
        
    static final String ENDPOINT = "https://service.artofsilk.com:8090/notifications";
    
    @Autowired
    private RestTemplate restTemplate;
    
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
		    Notification obj = new Notification("christopher.leung@gmail.com",3);

		    //set your headers
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);

		    //set your entity to send
		    HttpEntity<Notification> entity = new HttpEntity<>(obj,headers);

		    ResponseEntity<Response> response = restTemplate.exchange(ENDPOINT, HttpMethod.POST, entity, Response.class);
		    System.out.println("RESPONSE: " + response.getBody().getSaved());
		};
	}
}