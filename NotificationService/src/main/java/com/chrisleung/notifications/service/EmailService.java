package com.chrisleung.notifications.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chrisleung.notifications.objects.Notification;
import com.shopify.api.Product;
import com.shopify.api.Variant;

public class EmailService extends Thread {
    
    private Mailer api;
    private String bodyTemplate;
    private String subjectTemplate;
    private String shopName;
    private String shopDomain;
    private String senderName;
    private String senderAddress;
    private boolean logVerbose;
    private String logTag;
    private BlockingQueue<EmailNotification> queue;
    private NotificationsApi notificationsApi;
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    EmailService(ApplicationProperties.Email props, BlockingQueue<EmailNotification> q, NotificationsApi nApi, ApplicationProperties.Log logProps) throws IOException {
        api = MailerBuilder
                .withSMTPServer(
                        props.getSmtp().getAddress(),
                        props.getSmtp().getPort(),
                        props.getSmtp().getUsername(),
                        props.getSmtp().getPassword())
                .withTransportStrategy(TransportStrategy.SMTPS)
                .buildMailer();
        bodyTemplate = new String(Files.readAllBytes(Paths.get(props.getTemplate().getPath())));
        subjectTemplate = props.getSubject().getTemplate();
        shopName = props.getShop().getName();
        shopDomain = props.getShop().getDomain();
        senderName = props.getSender().getName();
        senderAddress = props.getSender().getAddress();
        queue = q;
        notificationsApi = nApi;
        logVerbose = logProps.getVerbose();
        logTag = logProps.getTag();
    }
    
    @Override
    public void run() {
        log.info(String.format("%s Starting Email Service...", logTag));
        while(true) {
            int sent = 0;
            while(queue.isEmpty()) {
                synchronized(queue) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            while(!queue.isEmpty()) { 
                EmailNotification en = queue.peek();
                Notification n = en.getNotification();
                boolean sentSuccess = sendEmailNotification(n,en.getProduct(),en.getVariant());
                if(sentSuccess) {
                    sent++;
                    queue.remove();
                    // Update the Stock Notifications REST API that we have sent the notification
                    n.setIsSent(true);
                    n.setSentDate(new Date());
                    notificationsApi.updateNotification(n);
                } else {
                    if(logVerbose) {
                        log.info(String.format("%s EmailService: Failed to send notification for Variant %s to %s. Requeuing.", logTag, n.getVariantId(), n.getEmail()));
                    }
                    // Put again the end of the queue
                    synchronized(queue) {
                        queue.add(queue.remove());
                    }
                }
            }
            log.info(String.format("%s EmailService: Sent %s email notification(s).", logTag, sent));
        }
    }
    
    private boolean sendEmailNotification(Notification n, Product p, Variant v) {
        
        String imageUrl = p.getImages()[0].getSrc();
        String emailImageUrl = imageUrl.substring(0, imageUrl.indexOf(".jpg")) + "_560x.jpg"; // TODO: Size should be property
        String emailSubject = subjectTemplate
                                .replace("{{shop.name}}", shopName)
                                .replace("{{product.title}}", p.getTitle());
        
        String emailBody = bodyTemplate
                                .replace("{{shop.domain}}", shopDomain)
                                .replace("{{product.handle}}", p.getHandle())
                                .replace("{{product.title}}", p.getTitle())
                                .replace("{{variant.title}}", v.getTitle())
                                .replace("{{shop.name}}", shopName)
                                .replace("{{product.image}}", emailImageUrl);
                                
        Email email = EmailBuilder.startingBlank()
                        .to(n.getEmail())
                        .from(senderName, senderAddress)
                        .withSubject(emailSubject)
                        .withHTMLText(emailBody)
                        .buildEmail();
        
        boolean success = false;
        if(!api.validate(email)) return false;
        try {
            api.sendMail(email);
            success = true;
        } catch(Exception e) {
            if(logVerbose) e.printStackTrace();
        }
        return success;
    }
}
