package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;

public class GetNotificationJob implements Runnable {

    RestTemplate restTemplate;
    HttpEntity<Notification> entity;
    String url;
    ArrayList<Object[]> completedData;
    
    GetNotificationJob(RestTemplate r, String endpoint, String id, HttpHeaders headers, ArrayList<Object[]> c) {
        restTemplate = r;
        url = endpoint + '/' + id;
        completedData = c;
    }
    
    @Override
    public void run() {
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
        synchronized(completedData) {
            completedData.add(new Object[] {new Date()});
        }
    }
}
