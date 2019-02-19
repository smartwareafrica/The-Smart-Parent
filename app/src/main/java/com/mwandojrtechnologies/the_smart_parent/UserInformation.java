package com.mwandojrtechnologies.the_smart_parent;

public class UserInformation {

    public String name;
    public String contact;
    private String query;

    public UserInformation() {

    }

    // constructor for user information
    public UserInformation(String name, String contact) {
        this.name = name;
        this.contact = contact;
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

//for newsfeed

