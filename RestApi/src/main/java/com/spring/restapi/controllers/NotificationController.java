package com.spring.restapi.controllers;

import com.chrisleung.notifications.objects.Notification;
import com.chrisleung.notifications.objects.NotificationWrapper;
import com.spring.restapi.repositories.NotificationRepository;

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
 * | Method | EndPoint                |  Parameters             | Auth   | Description                  |
 * |--------|-------------------------|-------------------------|--------|------------------------------|
 * | POST   | /notifications          | none                    | No     |  Submit new notification     |
 * | GET    | /notifications          | none                    | Yes    |  All notifications           |
 * | GET    | /notifications/id       | none                    | Yes    |  Single notifications        |
 * | GET    | /notifications          | sent (boolean)          | Yes    |  Sent/unsent notifications   |
 * | GET    | /notifications          | createdDate (unix date) | Yes    |  Notifications created after |
 * | PUT    | /notifications/id       | none                    | Yes    |  Update notification         |
 * | DELETE | /notifications/id       | none                    | Yes    |  Delete notification         |
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
    public Map<String,Boolean> save(@RequestBody Notification notification) {
        Map<String,Boolean> response = new HashMap<>();
        List<Notification> results = notificationRepository.findByEmailAndVariantId(notification.getEmail(), notification.getVariantId());
        boolean saved = false;
        if(results.size() == 0) {
            notificationRepository.save(notification);
            saved = true;
        }
        response.put("saved", saved);
        return response;
    }

    @RequestMapping(method=RequestMethod.GET, value="/notifications/{id}")
    public Notification show(@PathVariable String id) {
        return notificationRepository.findOne(id);
    }

    @RequestMapping(method=RequestMethod.PUT, value="/notifications/{id}")
    public Notification update(@PathVariable String id, @RequestBody Notification notification) {
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
        return n;
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/notifications/{id}")
    public String delete(@PathVariable String id) {
        Notification notification = notificationRepository.findOne(id);
        notificationRepository.delete(notification);

        return "notification deleted";
    }
}