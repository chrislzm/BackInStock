package com.chrisleung.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
class Log {

    private static final Logger log = LoggerFactory.getLogger(Log.class);
    private boolean verbose;
    private String tag;
    
    @Autowired
    Log(LogConfig config) {
        verbose = config.getVerbose();
        tag = config.getTag();
    }
    
    private String withTag(String msg) {
        return tag + ' ' + msg;
    }
    void error(String msg) {
        log.error(withTag(msg));
    }
    void message(String msg) {
        log.info(withTag(msg));
    }
    
    void verbose(String msg){
        if(verbose) {
            message(msg);
        }
    }
}
