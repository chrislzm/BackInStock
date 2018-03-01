package com.spring.restapi.controllers;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;
import com.spring.restapi.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RESTful controller for accessing Notification data
 * 
 * | Method | EndPoint            | Parameters              | Auth | Description                         |
 * |--------|---------------------|-------------------------|------|-------------------------------------|
 * | POST   | /notifications      | none                    | No   | Submit new notification             |
 * | GET    | /notifications      | none                    | Yes  | Get all notifications               |
 * |        |                     | sent=[boolean]          |      | Query on sent status == [boolean]   |
 * |        |                     | createdDate=[unix date] |      | Query on createdDate >= [unix date] |
 * | GET    | /notifications/{id} | none                    | Yes  | Get a single notification           |
 * | PUT    | /notifications/{id} | none                    | Yes  | Update a notification               |
 * | DELETE | /notifications/{id} | none                    | Yes  | Delete a notification               |
 * 
 * "Auth = Yes" means that HTTP basic authentication is required to access these endpoints.
 * For the POST and PUT methods: The HTTP body content most contain a Notification object in JSON.
 * 
 * @author Chris Leung
 *
 */
@CrossOrigin
@RestController
public class NotificationController {

    @Autowired
    NotificationRepository notificationRepository;

    @RequestMapping(method=RequestMethod.GET, value="/notifications")
    public NotificationWrapper notification(
            @RequestParam(value="sent", required=false) Boolean sent,
            @RequestParam(value="createdDate", required=false) Long createdDateMilliseconds) {

        Date createdDate = createdDateMilliseconds == null ? null : new Date(createdDateMilliseconds);
        Date currentDate = new Date();

        if(sent == null && createdDate == null) {
            return new NotificationWrapper(notificationRepository.findAll(),currentDate);
        } else if(sent == null && createdDate != null) {
            return new NotificationWrapper(notificationRepository.findByCreatedDateAfter(createdDate),currentDate);
        } else if(sent != null && createdDate == null) {
            return sent ?
                    new NotificationWrapper(notificationRepository.findBySentTrue(),currentDate) :
                        new NotificationWrapper(notificationRepository.findBySentFalse(),currentDate);
        } else {
            return sent ?
                    new NotificationWrapper(notificationRepository.findByCreatedDateAfterAndSentTrue(createdDate),currentDate) :
                        new NotificationWrapper(notificationRepository.findByCreatedDateAfterAndSentFalse(createdDate),currentDate);
        }
    }

    @RequestMapping(method=RequestMethod.POST, value="/notifications")
    public Map<String,Object> save(@RequestBody Notification notification) {
        Map<String,Object> response = new HashMap<>();
        List<Notification> results = notificationRepository.findByEmailAndVariantIdAndSentFalse(notification.getEmail(), notification.getVariantId());
        boolean saved = false;
        if(results.size() == 0) {
            notification = notificationRepository.save(notification);
            saved = true;
            response.put("id", notification.getId());
        }
        response.put("saved", saved);
        return response;
    }

    @RequestMapping(method=RequestMethod.GET, value="/notifications/{id}")
    public NotificationWrapper show(@PathVariable String id) {
        Date currentDate = new Date();
        return wrapSingleNotification(notificationRepository.findOne(id),currentDate);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/notifications/{id}")
    public NotificationWrapper update(@PathVariable String id, @RequestBody Notification notification) {
        Date currentDate = new Date();
        Notification n = notificationRepository.findOne(id);
        if(notification.getCreatedDate() != null)
            n.setCreatedDate(notification.getCreatedDate());
        if(notification.getEmail() != null)
            n.setEmail(notification.getEmail());
        if(notification.getIsSent() != null)
            n.setIsSent(notification.getIsSent());
        if(notification.getSentDate() != null) {
            n.setSentDate(notification.getSentDate());
        }
        if(notification.getVariantId() != null)
            n.setVariantId(notification.getVariantId());
        notificationRepository.save(n);
        return wrapSingleNotification(n,currentDate);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/notifications/{id}")
    public String delete(@PathVariable String id) {
        Notification notification = notificationRepository.findOne(id);
        notificationRepository.delete(notification);

        return "notification deleted";
    }
    
    private NotificationWrapper wrapSingleNotification(Notification n, Date d) {
        List<Notification> l = new ArrayList<>();
        l.add(n);
        return new NotificationWrapper(l,d);
    }
}