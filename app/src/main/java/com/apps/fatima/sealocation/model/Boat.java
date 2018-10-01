package com.apps.fatima.sealocation.model;

public class Boat {
    private String boatName, boatType, timing, locationTrip, directionTrip, durationTrip,
            conditionTrip, seatNumber, tripPrice, code, mobile, date, is_rating, trip_type, user_name;

    private String width, widthMeasureEn, widthMeasureAr, height, heightMeasureEn, heightMeasureAr,
            value, passNumber, id, trip_id;

    private String boatNumber, location, hourly_price, driver_licence_end_date, boat_licence_end_date,
            licence_image, fishingString, divingString, tourString, created_at, approved;

    public Boat(String id, String boatName, String width, String height, String value,
                String passNumber, String location, String widthMeasureEn, String widthMeasureAr,
                String heightMeasureEn, String heightMeasureAr) {
        this.id = id;
        this.boatName = boatName;
        this.width = width;
        this.height = height;
        this.value = value;
        this.passNumber = passNumber;
        this.location = location;
        this.widthMeasureEn = widthMeasureEn;
        this.widthMeasureAr = widthMeasureAr;
        this.heightMeasureEn = heightMeasureEn;
        this.heightMeasureAr = heightMeasureAr;
    }


    public Boat(String id, String trip_id, String boatName, String boatType, String timing,
                String date, String approved, String is_rating, String user_name, String guid) {
        this.trip_id = trip_id;
        this.id = id;
        this.boatName = boatName;
        this.boatType = boatType;
        this.timing = timing;
        this.date = date;
        this.approved = approved;
        this.is_rating = is_rating;
        this.user_name = user_name;
        this.code = guid;
    }

    public Boat(String trip_id, String id, String boatName, String boatType, String timing, String locationTrip,
                String directionTrip, String durationTrip, String conditionTrip, String seatNumber,
                String tripPrice, String date) {
        this.trip_id = trip_id;
        this.id = id;
        this.boatName = boatName;
        this.boatType = boatType;
        this.timing = timing;
        this.locationTrip = locationTrip;
        this.directionTrip = directionTrip;
        this.durationTrip = durationTrip;
        this.conditionTrip = conditionTrip;
        this.seatNumber = seatNumber;
        this.tripPrice = tripPrice;
        this.date = date;
    }

    public String getWidthMeasureEn() {
        return widthMeasureEn;
    }

    public void setWidthMeasureEn(String widthMeasureEn) {
        this.widthMeasureEn = widthMeasureEn;
    }

    public String getWidthMeasureAr() {
        return widthMeasureAr;
    }

    public void setWidthMeasureAr(String widthMeasureAr) {
        this.widthMeasureAr = widthMeasureAr;
    }

    public String getHeightMeasureEn() {
        return heightMeasureEn;
    }

    public void setHeightMeasureEn(String heightMeasureEn) {
        this.heightMeasureEn = heightMeasureEn;
    }

    public String getHeightMeasureAr() {
        return heightMeasureAr;
    }

    public void setHeightMeasureAr(String heightMeasureAr) {
        this.heightMeasureAr = heightMeasureAr;
    }

    public String getIs_rating() {
        return is_rating;
    }

    public void setIs_rating(String is_rating) {
        this.is_rating = is_rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getBoatNumber() {
        return boatNumber;
    }

    public void setBoatNumber(String boatNumber) {
        this.boatNumber = boatNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHourly_price() {
        return hourly_price;
    }

    public void setHourly_price(String hourly_price) {
        this.hourly_price = hourly_price;
    }

    public String getDriver_licence_end_date() {
        return driver_licence_end_date;
    }

    public void setDriver_licence_end_date(String driver_licence_end_date) {
        this.driver_licence_end_date = driver_licence_end_date;
    }

    public String getBoat_licence_end_date() {
        return boat_licence_end_date;
    }

    public void setBoat_licence_end_date(String boat_licence_end_date) {
        this.boat_licence_end_date = boat_licence_end_date;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getLicence_image() {
        return licence_image;
    }

    public void setLicence_image(String licence_image) {
        this.licence_image = licence_image;
    }

    public String getFishingString() {
        return fishingString;
    }

    public void setFishingString(String fishingString) {
        this.fishingString = fishingString;
    }

    public String getDivingString() {
        return divingString;
    }

    public void setDivingString(String divingString) {
        this.divingString = divingString;
    }

    public String getTourString() {
        return tourString;
    }

    public void setTourString(String tourString) {
        this.tourString = tourString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boat(String id, String boatName, String code, String mobile, String created_at,
                String approved, String is_rating, String passengers, String start_time,
                String start_date, String duration, String trip_type, String route, String userName, String data) {
        this.id = id;
        this.boatName = boatName;
        this.code = code;
        this.mobile = mobile;
        this.created_at = created_at;
        this.approved = approved;
        this.is_rating = is_rating;
        this.passNumber = passengers;
        this.timing = start_time;
        this.date = start_date;
        this.durationTrip = duration;
        this.trip_type = trip_type;
        this.directionTrip = route;
        this.user_name = userName;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBoatType() {
        return boatType;
    }

    public void setBoatType(String boatType) {
        this.boatType = boatType;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getLocationTrip() {
        return locationTrip;
    }

    public void setLocationTrip(String locationTrip) {
        this.locationTrip = locationTrip;
    }

    public String getDirectionTrip() {
        return directionTrip;
    }

    public void setDirectionTrip(String directionTrip) {
        this.directionTrip = directionTrip;
    }

    public String getDurationTrip() {
        return durationTrip;
    }

    public void setDurationTrip(String durationTrip) {
        this.durationTrip = durationTrip;
    }

    public String getConditionTrip() {
        return conditionTrip;
    }

    public void setConditionTrip(String conditionTrip) {
        this.conditionTrip = conditionTrip;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getTripPrice() {
        return tripPrice;
    }

    public void setTripPrice(String tripPrice) {
        this.tripPrice = tripPrice;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }
}
