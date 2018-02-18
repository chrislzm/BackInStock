package com.chrisleung.notifications.service;

import com.chrisleung.notifications.objects.Product;
import com.chrisleung.notifications.objects.Variant;

public interface StoreApi {
    public Variant getVariant(int variantId);
    public Product getProduct(int productId);
}
