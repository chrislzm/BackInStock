/**
 * Refer to https://help.shopify.com/api/reference/product
 * 
 * Note: We are ignoring most of the fields here
 * 
 * @author Chris Leung
 */


package com.shopify.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductImage {
    
    private String src;
    private Long id;
    
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return src;
    }
}
