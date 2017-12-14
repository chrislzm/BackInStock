package com.spring.restapi.repositories;

import com.spring.restapi.models.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, String> {
    @Override
    Notification findOne(String id);

    @Override
    void delete(Notification deleted);
}
