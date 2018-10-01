package com.apps.fatima.sealocation.model;

public class Orders {

    private String id, boat_id, user_id, code_request, mobile, trip_type, partner_id, diver_id,
            tank_id, approved;

    public Orders(String id, String boat_id, String user_id, String trip_type, String code_request,
                  String mobile, String partner_id, String diver_id, String tank_id, String approved) {
        this.id = id;
        this.boat_id = boat_id;
        this.user_id = user_id;
        this.code_request = code_request;
        this.mobile = mobile;
        this.trip_type = trip_type;
        this.partner_id = partner_id;
        this.diver_id = diver_id;
        this.tank_id = tank_id;
        this.approved = approved;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getTank_id() {
        return tank_id;
    }

    public void setTank_id(String tank_id) {
        this.tank_id = tank_id;
    }

    public String getDiver_id() {
        return diver_id;
    }

    public void setDiver_id(String diver_id) {
        this.diver_id = diver_id;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCode_request() {
        return code_request;
    }

    public void setCode_request(String code_request) {
        this.code_request = code_request;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }
}
