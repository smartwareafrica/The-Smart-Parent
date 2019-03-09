package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

//The modules class
public class Posts {

    public String uid;
    public String time;
    public String profileImage;
    public String postImage;
    public String fullName;
    public String date;
    public String description;

//create a public constructor
    public Posts(){
        //default constructor
    }

//generate constructor
    public Posts(String uid, String time, String profileImage, String postImage, String fullName, String date, String description) {
        this.uid = uid;
        this.time = time;
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.fullName = fullName;
        this.date = date;
        this.description = description;
    }

    //generate getter and setter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
