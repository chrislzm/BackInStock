package com.chrisleung.notifications.service;

import com.chrisleung.notifications.objects.*;

/**
 * An "email notification" object which is simply a container for a Notification
 * object from the database and its corresponding product and variant data from
 * Shopify.
 * 
 * @author Chris Leung
 */
class EmailNotification {
    private ProductVariant productVariant;
    private Notification notification;
    
    EmailNotification(ProductVariant pv, Notification n) {
        this.setProductVariant(pv);
        this.notification = n;
    }

    public ProductVariant getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(ProductVariant productVariant) {
        this.productVariant = productVariant;
    }

    public Notification getNotification() {
        return notification;
    }
    
    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
