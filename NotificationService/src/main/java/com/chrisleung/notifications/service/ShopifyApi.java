package com.chrisleung.notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.shopify.api.Product;
import com.shopify.api.ProductWrapper;
import com.shopify.api.Variant;
import com.shopify.api.VariantWrapper;

@Component
@Scope("singleton")
public class ShopifyApi {

    private String variantUrl;
    private String productUrl;
    private String urlPostfix;
    private BasicAuthorizationInterceptor auth; // For Username+Password auth
    private RestTemplate restTemplate;

    @Autowired
    ShopifyApi(ShopifyApiConfig config, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.auth = new BasicAuthorizationInterceptor(config.getApiKey(), config.getPassword()); 
        this.variantUrl = config.getProduct().getVariant().getUrl();
        this.productUrl = config.getProduct().getUrl();
        this.urlPostfix = config.getUrlPostFix();
    }
    
    public Variant getVariant(int variantId) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s%s%s",variantUrl,variantId,urlPostfix);
        ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return shopifyResponse.getBody().getVariant();
    }
    
    public Product getProduct(Variant v) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s%s%s",productUrl,v.getProduct_id(),urlPostfix);
        ResponseEntity<ProductWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, ProductWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return shopifyResponse.getBody().getProduct();
    }
}
