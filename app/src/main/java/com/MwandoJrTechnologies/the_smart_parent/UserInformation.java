package com.MwandoJrTechnologies.the_smart_parent;

public class UserInformation {

    public String name;
    public String contact;
    public String username;
    private String profileImageUrl;

    private String query;


    public UserInformation(){
//empty constructor
    }

    // constructor for user information
    public UserInformation(String name, String contact, String username, String profileImageUrl) {
        this.name = name;
        this.contact = contact;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    //constructor for query and post
    public UserInformation(String query) {

        this.query = query;
    }

    //getter method
    public String getQuery() {
        return query;
    }

    // sets the post
    public void setQuery(String query) {

        this.query = query;
    }
}
