package com.chrisleung.notifications.objects;

/**
 * Supplies the required product + variant data to the Notification Service application.
 * 
 * @author Chris Leung
 */
public interface ProductVariant {
    /**
     * getHandle: Returns the unique handle used in the full URL path to this 
     * product variant. Example: http://mystore.com/products/{{product.handle}}
     */
    public String getHandle();
    public long getProductId();
    public long getVariantId();
    public String getImageUrl();
    public int getInventoryQuantity();
    public String getVariantTitle();
    public String getProductTitle();
    /**
     * getFullTitle(): Just the product and variant title combined. Example:
     * Ball - 12" Blue
     */
    public String getFullTitle();
}
