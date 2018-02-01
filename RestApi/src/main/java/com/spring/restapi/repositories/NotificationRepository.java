package com.spring.restapi.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.chrisleung.notifications.objects.Notification;

/**
 * Interface that connects Notification model and controller. (We only add these two methods as CrudRepository handles the rest.)
 * @author Chris Leung
 *
 */
public interface NotificationRepository extends CrudRepository<Notification, String> {
    @Override
    Notification findOne(String id);

    @Override
    void delete(Notification deleted);
    
    List<Notification> findByEmailAndVariantIdAndSentFalse(String email, Integer variantId);
    
    List<Notification> findBySentTrue();
    List<Notification> findBySentFalse();
    
    List<Notification> findByCreatedDateAfter(@DateTimeFormat(iso = ISO.DATE_TIME) Date createdDate);
    
    List<Notification> findByCreatedDateAfterAndSentTrue(@DateTimeFormat(iso = ISO.DATE_TIME) Date createdDate);
    List<Notification> findByCreatedDateAfterAndSentFalse(@DateTimeFormat(iso = ISO.DATE_TIME) Date createdDate);
}
