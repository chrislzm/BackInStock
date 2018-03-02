package com.chrisleung.notifications.tools.restapi.benchmark;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    static final int EMAIL_ADDRESS_LENGTH = 9; // total length will be 2x this plus 4 (@ and .com chars)
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Value("${restapi.endpoint}")
    private String endpoint;
    @Value("${restapi.benchmark.request.total}")
    private int numRequests;
    @Value("${restapi.benchmark.request.concurrent}")
    private int numConcurrent;
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
    @Value("${restapi.benchmark.runs}")
    private int runs;

    private Map<RequestType,List<Float>> allRates;
    private RandomString randomString;
    
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
            ExecutorService threadpool;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ArrayList<Object[]> completedData = new ArrayList<>();
            completedData.ensureCapacity(numRequests);
            
            allRates = new HashMap<>();
            for(RequestType t : RequestType.values()) {
                allRates.put(t, new ArrayList<>());
            }
            randomString = new RandomString(EMAIL_ADDRESS_LENGTH);

            for(int i=0; i<runs; i++) {
                log.info(String.format("\nBenchmark Run %s/%s", i+1, runs));
                if(requestType.equals(RequestType.ALL.toString()) || requestType.equals(RequestType.POST.toString())) {
                    threadpool = Executors.newFixedThreadPool(numConcurrent);
                    postBenchmark(threadpool,headers,completedData);
                    completedData.clear();
                }
                if(requestType.equals(RequestType.ALL.toString()) || requestType.equals(RequestType.GET.toString())) {
                    threadpool = Executors.newFixedThreadPool(numConcurrent);
                    getBenchmark(threadpool,completedData);
                    completedData.clear();
                }
                if(requestType.equals(RequestType.ALL.toString()) || requestType.equals(RequestType.PUT.toString())) {
                    threadpool = Executors.newFixedThreadPool(numConcurrent);
                    updateBenchmark(threadpool,headers,completedData);
                    completedData.clear();
                }
                if(requestType.equals(RequestType.ALL.toString()) || requestType.equals(RequestType.DELETE.toString())) {
                    threadpool = Executors.newFixedThreadPool(numConcurrent);
                    deleteBenchmark(threadpool,completedData);
                    completedData.clear();
                }
            }
            float totalDataPoints = 0;
            float overallAverage = 0;
            float overallBest = 0;
            float overallWorst = Float.MAX_VALUE;
            for(RequestType t : RequestType.values()) {
                for(float datapoint : allRates.get(t)) {
                    overallBest = Math.max(datapoint, overallBest);
                    overallWorst = Math.min(datapoint, overallWorst);
                    overallAverage = ((overallAverage*totalDataPoints) + datapoint) / (totalDataPoints+1);
                    totalDataPoints++;
                }
            }
            log.info(String.format("\nTotal Runs: %s.\nOverall Rates - Best: %s req/s. Worst: %s req/s. Average: %s.", runs,overallBest,overallWorst,overallAverage));
        };
    }

    private void postBenchmark(ExecutorService threadpool,HttpHeaders headers,ArrayList<Object[]> completedData) throws Exception {

        Runnable[] jobs = new Runnable[numRequests];

        /* Create jobs */
        for(int i=0; i<numRequests; i++) {
            jobs[i] = new PostNotificationJob(restTemplate,endpoint,headers,getRandomEmail(),getRandomVariantId(),completedData);
        }

        /* Run jobs */
        runBenchmark(jobs,threadpool,completedData,RequestType.POST);

        /* Write IDs to file */
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename,true));
        for(Object[] data : completedData) {
            writer.write((String)data[1]+'\n');
        }
        writer.close();
    }

    private void getBenchmark(ExecutorService threadpool, ArrayList<Object[]> completedData) throws Exception {
        setupAuth();
        int numIds = Math.min(getNumIdRecords(), numRequests);
        Scanner scanner = new Scanner(new FileReader(outputFilename));        
        /* Create jobs */
        Runnable[] jobs = new Runnable[numIds];
        for(int i=0; i<numIds; i++) {
            jobs[i] = new GetNotificationJob(restTemplate, endpoint, scanner.next(), completedData);
        }
        scanner.close();

        /* Run jobs */
        runBenchmark(jobs,threadpool,completedData,RequestType.GET);
    }

    private void updateBenchmark(ExecutorService threadpool,HttpHeaders headers,ArrayList<Object[]> completedData) throws Exception {
        setupAuth();
        int numIds = Math.min(getNumIdRecords(), numRequests);
        Scanner scanner = new Scanner(new FileReader(outputFilename));        
        /* Create jobs */
        Runnable[] jobs = new Runnable[numIds];
        for(int i=0; i<numIds; i++) {
            jobs[i] = new UpdateNotificationJob(restTemplate, endpoint, scanner.next(), headers, getRandomEmail(), getRandomVariantId(), completedData);
        }
        scanner.close();

        /* Run jobs */
        runBenchmark(jobs,threadpool,completedData,RequestType.PUT);	    
    }

    private void deleteBenchmark(ExecutorService threadpool, ArrayList<Object[]> completedData) throws Exception {
        setupAuth();
        int numIds = Math.min(getNumIdRecords(), numRequests);
        Scanner scanner = new Scanner(new FileReader(outputFilename));

        /* Create jobs */
        Runnable[] jobs = new Runnable[numIds];
        for(int i=0; i<numIds; i++) {
            jobs[i] = new DeleteNotificationJob(restTemplate, endpoint, scanner.next(), completedData);
        }
        scanner.close();

        /* Run jobs */
        runBenchmark(jobs,threadpool,completedData,RequestType.DELETE);

        /* Delete deleted ids from file */
        scanner = new Scanner(new FileReader(outputFilename));
        HashSet<String> remainingIds = new HashSet<>();
        while(scanner.hasNextLine()) {
            remainingIds.add(scanner.nextLine());
        }
        scanner.close();
        for(Object[] data : completedData) {
            String id = (String)data[1];
            if(remainingIds.contains(id)) {
                remainingIds.remove(id);
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
        for(String id : remainingIds) {
            writer.write(id+'\n');
        }
        writer.close();
    }

    private void setupAuth() {
        BasicAuthorizationInterceptor auth  = new BasicAuthorizationInterceptor(username, password);
        restTemplate.getInterceptors().add(auth);
    }

    private int getNumIdRecords() throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(outputFilename));
        int count = 0;
        while(scanner.hasNextLine()) {
            count++;
            scanner.nextLine();
        }
        scanner.close();
        return count;
    }
    
    private String getRandomEmail() {
        return randomString.nextString() + '@' + randomString.nextString() + ".com";
    }
    
    private int getRandomVariantId() {
        return (int)(Math.random() * Integer.MAX_VALUE);
    }

    private void runBenchmark(Runnable[] jobs, ExecutorService threadpool, ArrayList<Object[]> completedData, RequestType requestType) throws Exception {
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
                float seconds = elapsed/1000.0f;
                float currentRate = numCompleted/seconds;
                float bestRate = 0;
                float worstRate = Float.MAX_VALUE;
                List<Float> rates = allRates.get(requestType);
                rates.add(currentRate);
                float averageRate = 0;
                for(float rate : rates) {
                    averageRate += rate;
                    worstRate = Math.min(worstRate, rate);
                    bestRate = Math.max(bestRate, rate);
                }
                averageRate /= rates.size();
                log.info(String.format("\nCompleted %s %s rqs w/%s connections in %ss (%s rq/s). Worst: %s rq/s, Best: %s rq/s, Avg: %s rq/s", numCompleted,requestType.toString(),numConcurrent,seconds,currentRate,worstRate,bestRate,averageRate));
                break;
            }
        }
    }
}