package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

public class Reviews {
    public String date, time, review, uid;

    //default constructor
    public Reviews() {

    }

    public Reviews(String date, String time, String review, String uid) {
        this.date = date;
        this.time = time;
        this.review = review;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
