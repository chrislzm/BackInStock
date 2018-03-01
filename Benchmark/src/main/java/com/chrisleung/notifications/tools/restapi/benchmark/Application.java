package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * This class implements a simple benchmarking tool for the Database REST API.
 * It measures performance of the single endpoint exposed to the world:
 * Submittinga new notification.
 * 
 * @author Chris Leung
 */
@SpringBootApplication
public class Application {
        
    @Value("${restapi.benchmark.endpoint}")
    private String endpoint;
    @Value("${restapi.benchmark.requests.total}")
    private int numRequests;
    @Value("${restapi.benchmark.requests.concurrent}")
    private int numConcurrent;
    @Value("${restapi.benchmark.request.email}")
    private String email;
    @Value("${restapi.benchmark.timelimit}")
    private int timelimit;

    private ArrayList<Date> completedTimes;

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
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        PostNotificationJob[] jobs = new PostNotificationJob[numRequests];
	        
	        /* Create the jobs first */
	        completedTimes = new ArrayList<>();
	        completedTimes.ensureCapacity(numRequests);
		    for(int i=0; i<numRequests; i++) {
		        jobs[i] = new PostNotificationJob(restTemplate,endpoint,headers,email,i,completedTimes);
		    }
		    
		    /* Submit jobs */
            ExecutorService threadPool = Executors.newFixedThreadPool(numConcurrent);
            for(PostNotificationJob job : jobs) {
                threadPool.execute(job);
            }
            threadPool.shutdown();
            Date start = new Date();
            threadPool.awaitTermination(timelimit, TimeUnit.SECONDS);
            
            /* Find the first job after the shutdown was submitted */
            for(int i=0; i<completedTimes.size(); i++) {
                if(completedTimes.get(i).getTime() >= start.getTime()) {
                    Date first = completedTimes.get(i);
                    Date last = completedTimes.get(completedTimes.size()-1);
                    long elapsed = last.getTime() - first.getTime();
                    int numCompleted = completedTimes.size()-i;
                    /* Log Status */
                    System.out.println(String.format("Completed %s/%s requests with %s concurrent connections in %s seconds. Average = %s requests/second", numCompleted,numRequests,numConcurrent,elapsed/1000.0f,numCompleted/(elapsed/1000.0f)));
                    break;
                }
            }

		};
	}
}