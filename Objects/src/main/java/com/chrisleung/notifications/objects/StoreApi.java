package com.chrisleung.notifications.objects;

/**
 * The online store interface that needs to be implemented for the Notification Service.
 * 
 * @author Chris Leung
 */
public interface StoreApi {
    public ProductVariant getProductVariant(int variantId);
}
