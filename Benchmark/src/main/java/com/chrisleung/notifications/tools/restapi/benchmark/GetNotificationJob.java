package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;

public class GetNotificationJob implements Runnable {

    RestTemplate restTemplate;
    HttpEntity<Notification> entity;
    String endPoint;
    ArrayList<Date> completedTimes;
    
    GetNotificationJob(RestTemplate r, String url, HttpHeaders headers, int index, ArrayList<Date> c) {
        restTemplate = r;
        endPoint = url;
        Notification obj = new Notification(email,index);
        entity = new HttpEntity<>(obj,headers);
        completedTimes = c;
    }
    
    @Override
    public void run() {
        restTemplate.exchange(endPoint, HttpMethod.POST, entity, Response.class);
        synchronized(completedTimes) {
            completedTimes.add(new Date());
        }
    }
}
