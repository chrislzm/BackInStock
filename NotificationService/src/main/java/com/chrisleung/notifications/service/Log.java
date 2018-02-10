package com.chrisleung.notifications.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Log {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private boolean verbose;
    private String tag;
    
    Log(ApplicationProperties.Log props) {
        verbose = props.getVerbose();
        tag = props.getTag();
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
