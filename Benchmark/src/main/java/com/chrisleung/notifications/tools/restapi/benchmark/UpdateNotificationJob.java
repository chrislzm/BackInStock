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

public class UpdateNotificationJob extends SingleNotificationJob {

    HttpEntity<Notification> entity;

    UpdateNotificationJob(RestTemplate r, String endpoint, String id, HttpHeaders headers, String updatedEmail, int updatedVariantId, ArrayList<Object[]> c) {
        super(r, endpoint, id, c);
        Notification obj = new Notification(updatedEmail,updatedVariantId);
        obj.setId(id);
        entity = new HttpEntity<>(obj,headers);
    }

    @Override
    public void run() {
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.PUT, entity, NotificationWrapper.class);
        Object[] completedInfo = new Object[] {new Date(),response.getBody().getNotifications().iterator().next().getId()};
        synchronized(completedData) {
            completedData.add(completedInfo);
        }
    }

}
