package com.chrisleung.notifications.objects;

public interface ProductVariant {
    public String getHandle();
    public int getProductId();
    public int getVariantId();
    public String getImageUrl();
    public int getInventoryQuantity();
    public String getVariantTitle();
    public String getProductTitle();
    public String getFullTitle();
}
