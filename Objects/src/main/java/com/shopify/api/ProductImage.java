/*
 * Refer to https://help.shopify.com/api/reference/product
 * 
 * Note: We are ignoring most of the fields
 */

package com.shopify.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductImage {
    
    private String src;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
    
    @Override
    public String toString() {
        return src;
    }
}
