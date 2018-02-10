package com.chrisleung.notifications.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.shopify.api.Product;
import com.shopify.api.ProductWrapper;
import com.shopify.api.Variant;
import com.shopify.api.VariantWrapper;

public class ShopifyApi {

    private String variantUrl;
    private String productUrl;
    private String urlPostfix;
    private BasicAuthorizationInterceptor auth; // For Username+Password auth
    private RestTemplate restTemplate;

    ShopifyApi(RestTemplate rt, ApplicationProperties.ShopifyApi shopifyProps) {
        restTemplate = rt;
        auth = new BasicAuthorizationInterceptor(shopifyProps.getApiKey(), shopifyProps.getPassword()); 
        variantUrl = shopifyProps.getProduct().getVariant().getUrl();
        productUrl = shopifyProps.getProduct().getUrl();
        urlPostfix = shopifyProps.getUrlPostFix();
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
