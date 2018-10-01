package com.apps.fatima.sealocation.model;

public class Tanks {
    private String id, tankType, codeRequest, mobile, approved, is_rating, name;
    private String tankNumber, rentValue, title_ar, title_en, quantity, guid;

    public Tanks(String id, String title_ar, String title_en, String tankNumber, String rentValue) {
        this.id = id;
        this.tankNumber = tankNumber;
        this.rentValue = rentValue;
        this.title_ar = title_ar;
        this.title_en = title_en;
    }

    public Tanks(String id, String quantity, String title_ar, String title_en, String codeRequest,
                 String mobile, String is_rating, String approved, String name) {
        this.id = id;
        this.title_ar = title_ar;
        this.title_en = title_en;
        this.codeRequest = codeRequest;
        this.mobile = mobile;
        this.quantity = quantity;
        this.is_rating = is_rating;
        this.approved = approved;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getIs_rating() {
        return is_rating;
    }

    public void setIs_rating(String is_rating) {
        this.is_rating = is_rating;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle_ar() {
        return title_ar;
    }

    public void setTitle_ar(String title_ar) {
        this.title_ar = title_ar;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTankNumber() {
        return tankNumber;
    }

    public String getRentValue() {
        return rentValue;
    }

    public void setTankNumber(String tankNumber) {
        this.tankNumber = tankNumber;
    }

    public void setRentValue(String rentValue) {
        this.rentValue = rentValue;
    }

    public String getCodeRequest() {
        return codeRequest;
    }

    public void setCodeRequest(String codeRequest) {
        this.codeRequest = codeRequest;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTankType() {
        return tankType;
    }

    public void setTankType(String tankType) {
        this.tankType = tankType;
    }

}
