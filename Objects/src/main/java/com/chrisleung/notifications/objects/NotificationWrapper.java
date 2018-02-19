package com.chrisleung.notifications.objects;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for notification objects retrieved from the Database REST API
 * 
 * @author Chris Leung
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationWrapper {
 
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date currentDate;
    private Iterable<Notification> notifications;
    
    public NotificationWrapper() {
    }
    
    public NotificationWrapper(Iterable<Notification> list, Date d) {
        notifications = list;
        this.currentDate = d;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Iterable<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(Iterable<Notification> notifications) {
        this.notifications = notifications;
    }
}
