package com.chrisleung.notifications.service;

import java.util.Date;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;

public class NotificationsApi {
    
    private BasicAuthorizationInterceptor auth; // For username+password auth
    private RestTemplate restTemplate;
    private String baseUrl;
    private String paramSent;
    private String paramCreatedDate;
    
    NotificationsApi(RestTemplate rt, String username, String password, String url, String ps, String pcd) {
        restTemplate = rt;
        auth = new BasicAuthorizationInterceptor(username, password); 
        baseUrl = url;
        paramSent = ps;
        paramCreatedDate = pcd;
    }
    
    public NotificationWrapper getAllUnsentNotifications() {
        String url = String.format("%s?%s=%s",baseUrl,paramSent,false);
        return getNotifications(url);
    }
    
    public NotificationWrapper getNewNotificationsSince(Date lastUpdate) {
        String url = String.format("%s?%s=%s",baseUrl,paramCreatedDate,lastUpdate.getTime());
        return getNotifications(url);
    }
    
    public NotificationWrapper getNotifications(String url) {
        restTemplate.getInterceptors().add(auth);
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return response.getBody();
    }
    
    public void updateNotification(Notification n) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s/%s",baseUrl,n.getId());
        restTemplate.put(url, n);
        restTemplate.getInterceptors().remove(0);
    }
}
