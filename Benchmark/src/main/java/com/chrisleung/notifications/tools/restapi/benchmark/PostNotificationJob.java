package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;

public class PostNotificationJob implements Runnable {

    RestTemplate restTemplate;
    HttpEntity<Notification> entity;
    String endPoint;
    ArrayList<Object[]> completedTimes;
    
    PostNotificationJob(RestTemplate r, String url, HttpHeaders headers, String email, int index, ArrayList<Object[]> c) {
        restTemplate = r;
        endPoint = url;
        Notification obj = new Notification(email,index);
        entity = new HttpEntity<>(obj,headers);
        completedTimes = c;
    }
    
    @Override
    public void run() {
        ResponseEntity<Response> response = restTemplate.exchange(endPoint, HttpMethod.POST, entity, Response.class);
        Object[] completedInfo = new Object[2];
        completedInfo[0] = new Date();
        completedInfo[1] = response.getBody().getId();
        synchronized(completedTimes) {
            completedTimes.add(completedInfo);
        }
    }
}
