package com.chrisleung.notifications.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.shopify.api.ProductWrapper;
import com.shopify.api.VariantWrapper;


/**
 * Implements an interface to a Shopify store via its REST API. 
 * 
 * @author Chris Leung
 */
@Component
@Scope("singleton")
public class ShopifyApi implements StoreApi {

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
    
    @Override
    public com.chrisleung.notifications.objects.Variant getVariant(int variantId) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s%s%s",variantUrl,variantId,urlPostfix);
        ResponseEntity<VariantWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, VariantWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return new ShopifyVariant(shopifyResponse.getBody().getVariant());
    }

    @Override
    public com.chrisleung.notifications.objects.Product getProduct(int productId) {
        restTemplate.getInterceptors().add(auth);
        String url = String.format("%s%s%s",productUrl,productId,urlPostfix);
        ResponseEntity<ProductWrapper> shopifyResponse = restTemplate.exchange(url, HttpMethod.GET, null, ProductWrapper.class);
        restTemplate.getInterceptors().remove(0);
        return new ShopifyProduct(shopifyResponse.getBody().getProduct());
    }
}
