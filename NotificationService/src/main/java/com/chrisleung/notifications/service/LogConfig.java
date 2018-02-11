package com.chrisleung.notifications.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class for the Log class. Loads configuration
 * data from application.properties.
 * 
 * @author Chris Leung
 */
@Configuration
@ConfigurationProperties(prefix = "my.notifications.log")
public class LogConfig {
    private String tag;
    private Boolean verbose;
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public Boolean getVerbose() {
        return verbose;
    }
    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }
    @Override
    public String toString() {
        return "Log [tag=" + tag + ", verbose=" + verbose + "]";
    }
}
