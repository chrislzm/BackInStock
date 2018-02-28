package com.chrisleung.notifications.tools.restapi.benchmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response for POST request to the Notifications REST API  
 * 
 * @author Chris Leung
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    Boolean saved;

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }
    
}
