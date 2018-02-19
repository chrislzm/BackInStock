/**
 * Refer to https://help.shopify.com/api/reference/product_variant
 * 
 * 
 * @author Chris Leung
 */

package com.shopify.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {
    
    private Integer id;
    private Integer product_id;
    private String title;
    private String price;
    private String sku;
    private Integer position;
    private String inventory_policy;
    private String compare_at_price;
    private String fulfillment_service;
    private String inventory_management;
    private String option1;
    private String option2;
    private String option3;
    private String created_at;
    private String updated_at;
    private Boolean taxable ;
    private String barcode;
    private Integer grams;
    private Integer image_id;
    private Integer inventory_quantity;
    private Float weight;
    private String weight_unit;
    private Integer old_inventory_quantity;
    private Boolean requires_shipping;
    
    @Override
    public String toString() {
        return "Variant {" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", sku='" + sku + '\'' +
                ", inventory_quantity='" + inventory_quantity + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getInventory_policy() {
        return inventory_policy;
    }

    public void setInventory_policy(String inventory_policy) {
        this.inventory_policy = inventory_policy;
    }

    public String getCompare_at_price() {
        return compare_at_price;
    }

    public void setCompare_at_price(String compare_at_price) {
        this.compare_at_price = compare_at_price;
    }

    public String getFulfillment_service() {
        return fulfillment_service;
    }

    public void setFulfillment_service(String fulfillment_service) {
        this.fulfillment_service = fulfillment_service;
    }

    public String getInventory_management() {
        return inventory_management;
    }

    public void setInventory_management(String inventory_management) {
        this.inventory_management = inventory_management;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getTaxable() {
        return taxable;
    }

    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getGrams() {
        return grams;
    }

    public void setGrams(Integer grams) {
        this.grams = grams;
    }

    public Integer getImage_id() {
        return image_id;
    }

    public void setImage_id(Integer image_id) {
        this.image_id = image_id;
    }

    public Integer getInventory_quantity() {
        return inventory_quantity;
    }

    public void setInventory_quantity(Integer inventory_quantity) {
        this.inventory_quantity = inventory_quantity;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public Integer getOld_inventory_quantity() {
        return old_inventory_quantity;
    }

    public void setOld_inventory_quantity(Integer old_inventory_quantity) {
        this.old_inventory_quantity = old_inventory_quantity;
    }

    public Boolean getRequires_shipping() {
        return requires_shipping;
    }

    public void setRequires_shipping(Boolean requires_shipping) {
        this.requires_shipping = requires_shipping;
    }
}
