package com.chrisleung.notifications.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.notifications.shopifyapi")
public class ShopifyApiConfig {
    public static class Product {
        public static class Variant {
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            @Override
            public String toString() {
                return "Variant [url=" + url + "]";
            }
        }
        private Variant variant;
        private String url;
        
        public Variant getVariant() {
            return variant;
        }
        public void setVariant(Variant variant) {
            this.variant = variant;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        @Override
        public String toString() {
            return "Product [variant=" + variant + ", url=" + url + "]";
        }
    }
    private Product product;
    private String apiKey;
    private String password;
    private String urlPostFix;
    
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUrlPostFix() {
        return urlPostFix;
    }
    public void setUrlPostFix(String urlPostFix) {
        this.urlPostFix = urlPostFix;
    }
    @Override
    public String toString() {
        return "ShopifyApi [product=" + product + ", apiKey=" + apiKey + ", password=" + password + ", urlPostFix="
                + urlPostFix + "]";
    }
}
