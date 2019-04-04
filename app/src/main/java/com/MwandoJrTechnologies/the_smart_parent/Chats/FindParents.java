package com.MwandoJrTechnologies.the_smart_parent.Chats;

//modal class to retrieve data from fireBase database

public class FindParents {

    public String profileImage;
    public String fullName;
    public String status;
    public String uid;

    //default constructor
    public FindParents() {

    }

    //generate constructor
    public FindParents(String profileImage, String fullName, String status, String uid) {
        this.profileImage = profileImage;
        this.fullName = fullName;
        this.status = status;
        this.uid = uid;


    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}
