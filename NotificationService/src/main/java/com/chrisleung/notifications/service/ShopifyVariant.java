package com.chrisleung.notifications.service;

import com.chrisleung.notifications.objects.Variant;

public class ShopifyVariant implements Variant {

    com.shopify.api.Variant shopifyVariant;
    
    ShopifyVariant(com.shopify.api.Variant v) {
        shopifyVariant = v;
    }
    
    @Override
    public int getId() {
        return shopifyVariant.getId();
    }

    @Override
    public int getInventoryQuantity() {
        return shopifyVariant.getInventory_quantity();
    }

    @Override
    public int getProductId() {
        return shopifyVariant.getProduct_id();
    }

    @Override
    public String getTitle() {
        return shopifyVariant.getTitle();
    }

}
