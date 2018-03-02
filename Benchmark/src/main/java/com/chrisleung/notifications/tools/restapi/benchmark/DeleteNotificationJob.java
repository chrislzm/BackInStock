package com.chrisleung.notifications.tools.restapi.benchmark;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DeleteNotificationJob extends SingleNotificationJob {

    DeleteNotificationJob(RestTemplate r, String endpoint, String id, ArrayList<Object[]> c) {
        super(r, endpoint, id, c);
    }

    @Override
    public void run() {
        ResponseEntity<Response> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Response.class);
        Object[] completedInfo = new Object[] {new Date(), response.getBody().getId()};
        synchronized(completedData) {
            completedData.add(completedInfo);
        }
    }
}
