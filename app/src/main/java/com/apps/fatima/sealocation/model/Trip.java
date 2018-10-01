package com.apps.fatima.sealocation.model;

public class Trip {
    private String id, boat_id, diver_id, start_location, boat_name, tripType, timing, tripDuration,
            conditionTrip, seatNumber, tripPrice, tripName, trip_route, for_diver, gears_available,
            gears_price, time, code, approved, is_rating, user_id, partner_id, trip_id, mobile, user_name, created_at;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Trip(String tripType, String timing, String tripDuration, String conditionTrip,
                String seatNumber, String tripPrice, String tripName) {
        this.tripType = tripType;
        this.timing = timing;
        this.tripDuration = tripDuration;
        this.conditionTrip = conditionTrip;
        this.seatNumber = seatNumber;
        this.tripPrice = tripPrice;
        this.tripName = tripName;
    }

    public Trip(String id, String user_id, String partner_id, String trip_id,
                String approved, String is_rating, String guid, String trip_name, String mobile,
                String user_name, String created_at, String tripType) {
        this.id = id;
        this.user_id = user_id;
        this.partner_id = partner_id;
        this.trip_id = trip_id;
        this.approved = approved;
        this.is_rating = is_rating;
        this.code = guid;
        this.tripName = trip_name;
        this.mobile = mobile;
        this.user_name = user_name;
        this.created_at = created_at;
        this.tripType = tripType;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Trip(String id, String boat_id, String diver_id, String boat_name, String trip_type,
                String start_date, String start_location, String trip_route, String trip_duration,
                String trip_terms, String available_seats, String trip_price, String for_diver,
                String gears_available, String gears_price, String title, String time) {
        this.id = id;
        this.boat_id = boat_id;
        this.diver_id = diver_id;
        this.boat_name = boat_name;
        this.tripType = trip_type;
        this.timing = start_date;
        this.start_location = start_location;
        this.trip_route = trip_route;
        this.tripDuration = trip_duration;
        this.conditionTrip = trip_terms;
        this.seatNumber = available_seats;
        this.tripPrice = trip_price;
        this.for_diver = for_diver;
        this.gears_available = gears_available;
        this.gears_price = gears_price;
        this.tripName = title;
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStart_location() {
        return start_location;
    }

    public void setStart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getBoat_name() {
        return boat_name;
    }

    public void setBoat_name(String boat_name) {
        this.boat_name = boat_name;
    }

    public String getTrip_route() {
        return trip_route;
    }

    public void setTrip_route(String trip_route) {
        this.trip_route = trip_route;
    }

    public String getFor_diver() {
        return for_diver;
    }

    public void setFor_diver(String for_diver) {
        this.for_diver = for_diver;
    }

    public String getGears_available() {
        return gears_available;
    }

    public void setGears_available(String gears_available) {
        this.gears_available = gears_available;
    }

    public String getGears_price() {
        return gears_price;
    }

    public void setGears_price(String gears_price) {
        this.gears_price = gears_price;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(String tripDuration) {
        this.tripDuration = tripDuration;
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

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }
}
