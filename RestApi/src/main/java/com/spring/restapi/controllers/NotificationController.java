package com.spring.restapi.controllers;

import com.chrisleung.notifications.objects.Notification;
import com.spring.restapi.repositories.NotificationRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
 * <table>
 * <tr>td>Method</td><td>Endpoint</td><td>Notes</td></tr>
 * <tr><td>GET</td><td>/notifications</td><td>Get all notifications data</td></tr>
 * <tr><td>GET</td><td>/notifications/59be3c34b1a24167ad2779b5</td><td>Get single notification</td></tr>
 * <tr><td>POST</td><td>/notifications</td><td>Post data</td></tr>
 * <tr><td>PUT</td><td>/notifications/59be3c34b1a24167ad2779b5</td><td>Update data</td></tr>
 * <tr><td>DELETE</td><td>/products/59be3c34b1a24167ad2779b5</td><td>Delete data/<td></tr>
 * </table>
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
    public Iterable<Notification> notification(
    		@RequestParam(value="sent", required=false) Boolean sent,
    		@RequestParam(value="createdDate", required=false) Long createdDateMilliseconds) {
    	
    	Date createdDate = createdDateMilliseconds == null ? null : new Date(createdDateMilliseconds);
    	
    	if(sent == null && createdDate == null) {
            return notificationRepository.findAll();
    	} else if(sent == null && createdDate != null) {
    		return notificationRepository.findByCreatedDateAfter(createdDate);
    	} else if(sent != null && createdDate == null) {
    		return sent ? notificationRepository.findBySentTrue() : notificationRepository.findBySentFalse();
    	} else {
    		return sent ? notificationRepository.findByCreatedDateAfterAndSentTrue(createdDate) : notificationRepository.findByCreatedDateAfterAndSentFalse(createdDate);
    	}
    }

    @RequestMapping(method=RequestMethod.POST, value="/notifications")
    public Map<String,Boolean> save(@RequestBody Notification notification) {
    	Map<String,Boolean> response = new HashMap<>();
    	List<Notification> results = notificationRepository.findByEmailAndSku(notification.getEmail(), notification.getSku());
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
        if(notification.getSku() != null)
            n.setSku(notification.getSku());
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