package com.chrisleung.notifications.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chrisleung.notifications.objects.*;

/**
 * Implements a service that emails notifications and updates the Notifications
 * database (by setting the respective notification's "sent" field to "true")
 * whenever a notification has been successfully emailed. Here, success is
 * defined as having been successfully transmitted to the SMTP server. It does
 * not indicate that the user has/will receive it (e.g. that the email address
 * is valid, that the recipient's inbox is not full, etc.).
 * 
 * @author Chris Leung
 */
@Component
public class EmailService extends Thread {
    
    @Autowired
    private Log logger;
    @Autowired
    private DatabaseRestApi notificationsApi;

    private Mailer api;
    private String bodyTemplate;

    private BlockingQueue<EmailNotification> queue;

    private boolean enableRateLimit;
    private Integer emailsPerHour;
    
    private String subjectTemplate;
    private String senderName;
    private String senderAddress;
    
    private String imgFileExtension;
    private String imgSizePostfix;
    
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
        senderName = config.getSender().getName();
        senderAddress = config.getSender().getAddress();
        imgFileExtension = config.getProductImage().getFileExtension();
        imgSizePostfix = config.getProductImage().getSizePostfix();
        
        // Replace any shopName and shopDomain email template tags with values (only needs to be done once)
        String shopName = config.getShop().getName();
        String shopDomain = config.getShop().getDomain();
        subjectTemplate = replaceTemplateShopTagsWithValues(subjectTemplate, shopName, shopDomain);
        bodyTemplate = replaceTemplateShopTagsWithValues(bodyTemplate, shopName, shopDomain); 
    }
    
    @Override
    public void run() {
        Deque<Date> sentLastHour = new LinkedList<>(); // Used when rate limit is enabled
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
                logger.verbose(String.format("Email Service: Queue contains emails. Attempting to send them all.", sent));
                EmailNotification en = queue.peek(); // Don't remove, in case we need to add it back into the queue
                Notification n = en.getNotification();
                if(enableRateLimit && sentLastHour.size() == emailsPerHour) { // If we have hit the rate limit
                    Date oldest = sentLastHour.remove();
                    long timeLeft = TimeUnit.HOURS.toMillis(1) - (new Date().getTime() - oldest.getTime());
                    if(timeLeft > 0) {
                        logger.verbose(String.format("Email Service: Reached send limit (%s/hour), waiting for %s minute(s).",
                                emailsPerHour,
                                round(timeLeft/(double)TimeUnit.MINUTES.toMillis(1),1)));
                        try {
                            sleep(timeLeft);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                boolean sentSuccess = sendEmailNotification(n,en.getProductVariant());
                if(enableRateLimit) sentLastHour.add(new Date());
                if(sentSuccess) {
                    sent++;
                    queue.remove();
                    // Update the database that the notification is sent
                    n.setIsSent(true);
                    n.setSentDate(new Date());
                    notificationsApi.updateNotification(n);
                } else {
                    logger.verbose(String.format("EmailService: Failed to send notification for Variant %s to %s. Requeuing.",
                            n.getVariantId(),
                            n.getEmail()));
                    // Put again the end of the queue
                    synchronized(queue) {
                        queue.add(queue.remove());
                    }
                }
            }
            logger.verbose(String.format("EmailService: Queue now empty. Sent %s email notification(s).", sent));
        }
    }

    private boolean sendEmailNotification(Notification n, ProductVariant pv) {
        
        String imageUrl = pv.getImageUrl();
        String emailImageUrl = imageUrl.substring(0, imageUrl.indexOf(imgFileExtension)) + imgSizePostfix + imgFileExtension;
        String emailSubject = replaceTemplateProductVariantTagsWithValues(subjectTemplate,pv,emailImageUrl);
        String emailBody = replaceTemplateProductVariantTagsWithValues(bodyTemplate,pv,emailImageUrl);
                                
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
    
    private String replaceTemplateShopTagsWithValues(String template, String shopName, String shopDomain) {
        return template.replace("{{shop.domain}}", shopDomain)
                       .replace("{{shop.name}}", shopName);
    }
    
    private String replaceTemplateProductVariantTagsWithValues(String s, ProductVariant pv, String emailImageUrl) {
        return s.replace("{{product.handle}}", pv.getHandle())
                .replace("{{product.title}}", pv.getProductTitle())
                .replace("{{variant.title}}", pv.getVariantTitle())
                .replace("{{product.image}}", emailImageUrl);
    }
    
    /**
     * Rounds a floating point number to a specified number of decimal points
     * @param value the value to round
     * @param precision the number of decimal points of precision
     * @return the rounded number
     */
    private double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * Used by the main application to queue email notifications for this class
     * to send.
     * @return the email notification queue
     */
    public BlockingQueue<EmailNotification> getQueue() {
        return queue;
    }
}
