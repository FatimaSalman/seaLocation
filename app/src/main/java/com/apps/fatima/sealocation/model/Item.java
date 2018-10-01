package com.apps.fatima.sealocation.model;


public class Item {
    private String userName, cityName, id, image, name_ar, name_en, user_id, diver_bio,
            title_en, title_ar, email, mobile, boat, jekski, diver, supplier, seller, service,
            boat_id, diver_id, supplier_id, service_id, seller_id, jekski_id, pageName;

    public Item(String userName, String cityName) {
        this.userName = userName;
        this.cityName = cityName;
    }

    public Item(String userName, String cityName, String id, String image) {
        this.userName = userName;
        this.cityName = cityName;
        this.id = id;
        this.image = image;
    }

    public Item(String userName, String cityName, String id, String image, String title_en, String title_ar) {
        this.userName = userName;
        this.cityName = cityName;
        this.id = id;
        this.image = image;
        this.title_en = title_en;
        this.title_ar = title_ar;
    }

    public Item(String userName, String cityName, String id, String image, String name_ar,
                String name_en, String user_id, String mobile, String pageName) {
        this.userName = userName;
        this.cityName = cityName;
        this.id = id;
        this.image = image;
        this.name_ar = name_ar;
        this.name_en = name_en;
        this.user_id = user_id;
        this.mobile = mobile;
        this.pageName = pageName;
    }

    public Item(String userName, String cityName, String id, String image, String name_ar,
                String name_en, String user_id, String email, String mobile, String pageName) {
        this.userName = userName;
        this.cityName = cityName;
        this.id = id;
        this.image = image;
        this.name_ar = name_ar;
        this.name_en = name_en;
        this.user_id = user_id;
        this.email = email;
        this.mobile = mobile;
        this.pageName = pageName;
    }

    public Item(String boat_id, String service_id, String supplier_id, String seller_id,
                String jekski_id, String diver_id, String userName, String cityName, String user_id,
                String image, String name_ar, String name_en, String email, String mobile,
                String boat, String service, String seller, String diver, String jekski,
                String supplier, String diver_bio, String title_ar, String title_en, String pageName) {
        this.userName = userName;
        this.cityName = cityName;
        this.user_id = user_id;
        this.boat_id = boat_id;
        this.service_id = service_id;
        this.supplier_id = supplier_id;
        this.seller_id = seller_id;
        this.jekski_id = jekski_id;
        this.diver_id = diver_id;
        this.image = image;
        this.name_ar = name_ar;
        this.name_en = name_en;
        this.email = email;
        this.mobile = mobile;
        this.boat = boat;
        this.service = service;
        this.seller = seller;
        this.diver = diver;
        this.jekski = jekski;
        this.supplier = supplier;
        this.diver_bio = diver_bio;
        this.title_ar = title_ar;
        this.title_en = title_en;
        this.pageName = pageName;
    }

    public Item(String userName, String cityName, String id, String image, String name_ar,
                String name_en, String user_id, String diver_bio, String title_en, String title_ar, String mobile, String pageName) {
        this.userName = userName;
        this.cityName = cityName;
        this.id = id;
        this.image = image;
        this.name_ar = name_ar;
        this.name_en = name_en;
        this.user_id = user_id;
        this.diver_bio = diver_bio;
        this.title_ar = title_ar;
        this.title_en = title_en;
        this.mobile = mobile;
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getBoat_id() {
        return boat_id;
    }

    public void setBoat_id(String boat_id) {
        this.boat_id = boat_id;
    }

    public String getDiver_id() {
        return diver_id;
    }

    public void setDiver_id(String diver_id) {
        this.diver_id = diver_id;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getJekski_id() {
        return jekski_id;
    }

    public void setJekski_id(String jekski_id) {
        this.jekski_id = jekski_id;
    }

    public String getBoat() {
        return boat;
    }

    public void setBoat(String boat) {
        this.boat = boat;
    }

    public String getJekski() {
        return jekski;
    }

    public void setJekski(String jekski) {
        this.jekski = jekski;
    }

    public String getDiver() {
        return diver;
    }

    public void setDiver(String diver) {
        this.diver = diver;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getTitle_ar() {
        return title_ar;
    }

    public void setTitle_ar(String title_ar) {
        this.title_ar = title_ar;
    }

    public String getDiver_bio() {
        return diver_bio;
    }

    public void setDiver_bio(String diver_bio) {
        this.diver_bio = diver_bio;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
