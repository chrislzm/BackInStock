package com.chrisleung.notifications.tools.restapi.benchmark;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;

public class PostNotificationJob implements Runnable {

    RestTemplate restTemplate;
    HttpEntity<Notification> entity;
    String endPoint;
    
    PostNotificationJob(RestTemplate r, String url, HttpHeaders headers, String email, int index) {
        restTemplate = r;
        endPoint = url;
        Notification obj = new Notification(email,index);
        entity = new HttpEntity<>(obj,headers);
    }
    
    @Override
    public void run() {
        restTemplate.exchange(endPoint, HttpMethod.POST, entity, Response.class);
    }
}
