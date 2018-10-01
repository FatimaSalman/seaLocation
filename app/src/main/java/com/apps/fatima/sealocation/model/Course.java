package com.apps.fatima.sealocation.model;

public class Course {
    private String id, courseName, courseRequirement, courseDuration, courseValue, divingStatus, gears_price;
    private String user_id, partner_id, course_id, approved, is_rating, guid, mobile, user_name;

    public Course(String id, String courseName, String courseRequirement, String courseDuration,
                  String courseValue, String divingStatus, String gears_price) {
        this.id = id;
        this.courseName = courseName;
        this.courseRequirement = courseRequirement;
        this.courseDuration = courseDuration;
        this.courseValue = courseValue;
        this.divingStatus = divingStatus;
        this.gears_price = gears_price;
    }

    public Course(String id, String user_id, String partner_id, String course_id, String approved,
                  String is_rating, String guid, String course_title, String mobile, String user_name) {
        this.id = id;
        this.user_id = user_id;
        this.partner_id = partner_id;
        this.course_id = course_id;
        this.approved = approved;
        this.is_rating = is_rating;
        this.guid = guid;
        this.courseName = course_title;
        this.mobile = mobile;
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGears_price() {
        return gears_price;
    }

    public void setGears_price(String gears_price) {
        this.gears_price = gears_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseRequirement() {
        return courseRequirement;
    }

    public void setCourseRequirement(String courseRequirement) {
        this.courseRequirement = courseRequirement;
    }

    public String getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(String courseDuration) {
        this.courseDuration = courseDuration;
    }

    public String getCourseValue() {
        return courseValue;
    }

    public void setCourseValue(String courseValue) {
        this.courseValue = courseValue;
    }

    public String getDivingStatus() {
        return divingStatus;
    }

    public void setDivingStatus(String divingStatus) {
        this.divingStatus = divingStatus;
    }
}
