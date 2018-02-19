package com.chrisleung.notifications.service;

import com.chrisleung.notifications.objects.ProductVariant;
import com.shopify.api.*;

/**
 * Implements the ProductVariant interface for Shopify products. 
 * 
 * @author Chris Leung
 */
public class ShopifyProductVariant implements ProductVariant {

    Product product;
    Variant variant;
    
    ShopifyProductVariant(Product shopifyProduct, Variant shopifyVariant) {
        product = shopifyProduct;
        variant = shopifyVariant;
    }
    
    @Override
    public int getProductId() {
        return product.getId();
    }
    
    @Override
    public int getVariantId() {
        return variant.getId();
    }

    @Override
    public int getInventoryQuantity() {
        return variant.getInventory_quantity();
    }

    @Override
    public String getVariantTitle() {
        return variant.getTitle();
    }

    @Override
    public String getFullTitle() {
        return getProductTitle() + " - " + getVariantTitle();
    }

    @Override
    public String getHandle() {
        return product.getHandle();
    }

    @Override
    public String getImageUrl() {
        for(ProductImage image : product.getImages()) {
            if(variant.getImage_id().equals(image.getId())) {
                return image.getSrc();
            }
        }
        return null;
    }

    @Override
    public String getProductTitle() {
        return product.getTitle();
    }
}
