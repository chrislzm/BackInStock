package com.chrisleung.notifications.tools.restapi.benchmark;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
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
        
    @Value("${restapi.endpoint}")
    private String endpoint;
    @Value("${restapi.benchmark.request.total}")
    private int numRequests;
    @Value("${restapi.benchmark.request.concurrent}")
    private int numConcurrent;
    @Value("${restapi.benchmark.request.email}")
    private String email;
    @Value("${restapi.benchmark.timelimit}")
    private int timelimit;
    @Value("${restapi.benchmark.request.type}")
    private String requestType;
    @Value("${restapi.username}")
    private String username;
    @Value("${restapi.password}")
    private String password;
    @Value("${restapi.benchmark.notification.id.output.file}")
    private String outputFilename;

    @Autowired
    private RestTemplate restTemplate;    
    
	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}
		
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
	        ExecutorService threadpool = Executors.newFixedThreadPool(numConcurrent);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
	        ArrayList<Object[]> completedData = new ArrayList<>();
	        completedData.ensureCapacity(numRequests);
	        
		    switch(requestType) {
		    case "post":
		        postBenchmark(threadpool,headers,completedData);
		        break;
		    case "get":
		        getBenchmark(threadpool,headers,completedData);
		        break;
	        default:
		    }

		};
	}
	
	private void postBenchmark(ExecutorService threadpool,HttpHeaders headers,ArrayList<Object[]> completedData) throws Exception {
         
        Runnable[] jobs = new Runnable[numRequests];
        
        /* Create jobs */
        for(int i=0; i<numRequests; i++) {
            jobs[i] = new PostNotificationJob(restTemplate,endpoint,headers,email,i,completedData);
        }
        
        /* Run jobs */
        runJobs(jobs,threadpool,completedData);

        /* Write IDs to file */
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename,true));
        for(Object[] data : completedData) {
            writer.write((String)data[1]+'\n');
        }
        writer.close();
	}
	
	private void getBenchmark(ExecutorService threadpool,HttpHeaders headers,ArrayList<Object[]> completedData) throws Exception {
	    BasicAuthorizationInterceptor auth  = new BasicAuthorizationInterceptor(username, password);
        restTemplate.getInterceptors().add(auth);

        Scanner scanner = new Scanner(new FileReader(outputFilename));
        int count = 0;
        while(scanner.hasNext()) {
            count++;
            scanner.nextLine();
        }
        scanner.close();
        scanner = new Scanner(new FileReader(outputFilename));
        
        /* Create jobs */
        
        Runnable[] jobs = new Runnable[count];
        for(int i=0; i<count; i++) {
            jobs[i] = new GetNotificationJob(restTemplate, endpoint, scanner.next(), headers, completedData);
        }
        scanner.close();
        
        /* Run jobs */
        runJobs(jobs,threadpool,completedData);
	}
	
	private void runJobs(Runnable[] jobs, ExecutorService threadpool, ArrayList<Object[]> completedData) throws Exception {
        /* Submit jobs */
        for(Runnable job : jobs) {
            threadpool.execute(job);
        }
        threadpool.shutdown();
        Date start = new Date();
        threadpool.awaitTermination(timelimit, TimeUnit.SECONDS);
        
        /* Find the first job after the shutdown was submitted */
        for(int i=0; i<completedData.size(); i++) {
            if(((Date)completedData.get(i)[0]).getTime() >= start.getTime()) {
                Date first = (Date)completedData.get(i)[0];
                Date last = (Date)completedData.get(completedData.size()-1)[0];
                long elapsed = last.getTime() - first.getTime();
                int numCompleted = completedData.size()-i;
                /* Log Status */
                System.out.println(String.format("Completed %s/%s %s requests with %s concurrent connections in %s seconds. Average = %s requests/second", numCompleted,numRequests,requestType,numConcurrent,elapsed/1000.0f,numCompleted/(elapsed/1000.0f)));
                break;
            }
        }
	}
}