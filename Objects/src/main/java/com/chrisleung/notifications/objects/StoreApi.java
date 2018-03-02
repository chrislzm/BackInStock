package com.chrisleung.notifications.objects;

/**
 * The online store interface that needs to be implemented for the Notification Service.
 * 
 * @author Chris Leung
 */
public interface StoreApi {
    public ProductVariant getProductVariant(long variantId);
    
    /**
     * This method is used to clear a cache of Product/Variant data, if any.
     * This is useful if you are caching the data in order to reduce API calls.
     * If product and variant data is updated (e.g. inventory quantity) and we
     * are using cached data, we need to clear the cached data between runs
     * before we'll see those updates. 
     */
    public void clearCache();
}
