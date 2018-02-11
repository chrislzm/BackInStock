package com.chrisleung.notifications.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chrisleung.notifications.objects.Notification;
import com.shopify.api.Product;
import com.shopify.api.Variant;

@Component
public class EmailService extends Thread {
    
    @Autowired
    private Log logger;
    @Autowired
    private NotificationsApi notificationsApi;

    private Mailer api;
    private String bodyTemplate;

    private BlockingQueue<EmailNotification> queue;

    private boolean enableRateLimit;
    private Integer emailsPerHour;
    
    private String subjectTemplate;
    private String shopName;
    private String shopDomain;
    private String senderName;
    private String senderAddress;
    
    @Autowired
    EmailService(EmailServiceConfig config) {
        if(config.getQueue().getEnableLimit()) {
            queue = new LinkedBlockingQueue<>(config.getQueue().getSize());
        } else {
            queue = new LinkedBlockingQueue<>();
        }
        
        api = MailerBuilder
                .withSMTPServer(
                        config.getSmtp().getAddress(),
                        config.getSmtp().getPort(),
                        config.getSmtp().getUsername(),
                        config.getSmtp().getPassword())
                .withTransportStrategy(TransportStrategy.SMTPS)
                .buildMailer();
        try {
            bodyTemplate = new String(Files.readAllBytes(Paths.get(config.getTemplate().getPath())));
        } catch (IOException e1) {
            logger.error("Email Service: Could not read email template file: " + config.getTemplate().getPath());
            e1.printStackTrace();
        }
        enableRateLimit = config.getRate().getEnableLimit();
        emailsPerHour = config.getRate().getPerHour();
        subjectTemplate = config.getSubject().getTemplate();
        shopName = config.getShop().getName();
        shopDomain = config.getShop().getDomain();
        senderName = config.getSender().getName();
        senderAddress = config.getSender().getAddress();
    }
    
    @Override
    public void run() {
        Deque<Date> sentLastHour = new LinkedList<>();
        logger.message("Starting Email Service...");
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
                logger.verbose(String.format("Email Service: Detected emails. Attempting to send all emails in queue.", sent));
                EmailNotification en = queue.peek();
                Notification n = en.getNotification();
                if(enableRateLimit && sentLastHour.size() == emailsPerHour) {
                    Date oldest = sentLastHour.remove();
                    long difference = 3600000 - (new Date().getTime() - oldest.getTime());
                    if(difference > 0) {
                        logger.verbose(String.format("Email Service: Reached send limit (%s/hour), waiting for %s minute(s).", emailsPerHour, round(difference/60000.0,1)));
                        try {
                            sleep(difference);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean sentSuccess = sendEmailNotification(n,en.getProduct(),en.getVariant());
                sentLastHour.add(new Date());
                if(sentSuccess) {
                    sent++;
                    queue.remove();
                    // Update the Stock Notifications REST API that we have sent the notification
                    n.setIsSent(true);
                    n.setSentDate(new Date());
                    notificationsApi.updateNotification(n);
                } else {
                    logger.verbose(String.format("EmailService: Failed to send notification for Variant %s to %s. Requeuing.", n.getVariantId(), n.getEmail()));
                    // Put again the end of the queue
                    synchronized(queue) {
                        queue.add(queue.remove());
                    }
                }
            }
            logger.verbose(String.format("EmailService: Queue now empty. Sent %s email notification(s).", sent));
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
            e.printStackTrace();
        }
        return success;
    }
    
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public BlockingQueue<EmailNotification> getQueue() {
        return queue;
    }
}
