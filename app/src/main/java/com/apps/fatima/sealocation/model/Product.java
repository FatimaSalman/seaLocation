package com.apps.fatima.sealocation.model;

public class Product {
    private String id, product_name, type_ar, type_en, product_note, price;

    public Product(String id, String product_name, String type_ar, String type_en, String product_note, String price) {
        this.id = id;
        this.type_ar = type_ar;
        this.type_en = type_en;
        this.product_note = product_note;
        this.price = price;
        this.product_name = product_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_ar() {
        return type_ar;
    }

    public void setType_ar(String type_ar) {
        this.type_ar = type_ar;
    }

    public String getType_en() {
        return type_en;
    }

    public void setType_en(String type_en) {
        this.type_en = type_en;
    }

    public String getProduct_note() {
        return product_note;
    }

    public void setProduct_note(String product_note) {
        this.product_note = product_note;
    }
}
