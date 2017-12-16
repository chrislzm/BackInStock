package com.spring.restapi.repositories;

import com.spring.restapi.models.Notification;
import org.springframework.data.repository.CrudRepository;

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
}
