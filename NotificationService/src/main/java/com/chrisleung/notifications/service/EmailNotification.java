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
    private Product product;
    private Variant variant;
    private Notification notification;
    
    EmailNotification(Product p, Variant v, Notification n) {
        this.product = p;
        this.variant = v;
        this.notification = n;
    }
    
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Variant getVariant() {
        return variant;
    }
    public void setVariant(Variant variant) {
        this.variant = variant;
    }
    public Notification getNotification() {
        return notification;
    }
    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "EmailNotification [product=" + product + ", variant=" + variant + ", notification=" + notification
                + "]";
    }
}
