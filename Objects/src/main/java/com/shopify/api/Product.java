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
public class Product {
    
    private Long id;
    private String title;
    private String handle;
    private ProductImage[] images;
    
    @Override
    public String toString() {
        return "Product {" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", handle='" + handle + '\'' +
                ", images='" + images.length + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public ProductImage[] getImages() {
        return images;
    }

    public void setImages(ProductImage[] images) {
        this.images = images;
    }
}
