package com.chrisleung.notifications.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;

/**
 * Implements an interface to the Notifications database via its REST API. 
 * 
 * @author Chris Leung
 */
@Component
@Scope("singleton")
public class DatabaseRestApi {
    
    private BasicAuthorizationInterceptor auth; // For username+password auth
    private RestTemplate restTemplate;
    private String baseUrl;
    private String paramSent;
    private String paramCreatedDate;
    private long sleepTime;

    @Autowired
    DatabaseRestApi(DatabaseRestApiConfig config, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        auth = new BasicAuthorizationInterceptor(config.getUsername(), config.getPassword()); 
        baseUrl = config.getUrl();
        paramSent = config.getParam().getSent();
        paramCreatedDate = config.getParam().getCreatedDate();
        sleepTime = TimeUnit.SECONDS.toMillis(config.getRefresh());
    }
    
    public NotificationWrapper getAllUnsentNotifications() {
        String url = String.format("%s?%s=%s",baseUrl,paramSent,false);
        return getNotifications(url);
    }
    
    /** 
     * @param lastUpdate the database system time (retrieved from a previous 
     *                   NotificationWrapper) from which to query for new
     *                   notifications.
     */
    public NotificationWrapper getNewNotificationsSince(Date lastUpdate) {
        String url = String.format("%s?%s=%s",baseUrl,paramCreatedDate,lastUpdate.getTime());
        return getNotifications(url);
    }
    
    /**
     * Generic method to retrieve a notification given a pre-configured URL
     * and username/password auth.
     */
    public NotificationWrapper getNotifications(String url) {
        restTemplate.getInterceptors().add(auth);
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return response.getBody();
    }
    
    /**
     * Updates the database with any changes made to the notification.
     */
    public void updateNotification(Notification n) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s/%s",baseUrl,n.getId());
        restTemplate.put(url, n);
        restTemplate.getInterceptors().remove(0);
    }

    /**
     * Causes the calling thread to sleep for the amount of time specified in
     * the configuration. Use this between polls on the database.
     */
    public void sleep() throws InterruptedException {
        Thread.sleep(sleepTime);
    }
}
