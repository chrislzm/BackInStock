package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;

import org.springframework.web.client.RestTemplate;

public abstract class SingleNotificationJob implements Runnable {
    RestTemplate restTemplate;
    String url;
    ArrayList<Object[]> completedData;
    
    SingleNotificationJob(RestTemplate r, String endpoint, String id, ArrayList<Object[]> c) {
        restTemplate = r;
        url = endpoint + '/' + id;
        completedData = c;
    }
    
    @Override
    public abstract void run();
}
