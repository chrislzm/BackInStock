package com.chrisleung.notifications.service;

import com.chrisleung.notifications.objects.Product;

public class ShopifyProduct implements Product {
    
    com.shopify.api.Product shopifyProduct;
    
    ShopifyProduct(com.shopify.api.Product p) {
        shopifyProduct = p;
    }
    
    @Override
    public String getTitle() {
        return shopifyProduct.getTitle();
    }

    @Override
    public String getHandle() {
        return shopifyProduct.getHandle();
    }

    @Override
    public String getImageUrl() {
        return shopifyProduct.getImages()[0].getSrc();
    }

}
