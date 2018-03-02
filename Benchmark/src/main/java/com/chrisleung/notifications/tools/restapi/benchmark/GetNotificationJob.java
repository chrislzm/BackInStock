package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.chrisleung.notifications.objects.NotificationWrapper;

public class GetNotificationJob extends SingleNotificationJob {

    GetNotificationJob(RestTemplate r, String endpoint, String id, ArrayList<Object[]> c) {
        super(r, endpoint, id, c);
    }

    @Override
    public void run() {
        ResponseEntity<NotificationWrapper> response = restTemplate.exchange(url, HttpMethod.GET, null, NotificationWrapper.class);
        Object[] completedInfo = new Object[] {new Date(), response.getBody().getNotifications().iterator().next().getId()};
        synchronized(completedData) {
            completedData.add(completedInfo);
        }
    }
}
