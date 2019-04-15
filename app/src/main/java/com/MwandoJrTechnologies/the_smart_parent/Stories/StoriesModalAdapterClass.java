package com.MwandoJrTechnologies.the_smart_parent.Stories;

public class StoriesModalAdapterClass {
    // define four String variables
    private String title, desc, imageUrl, username;

    // generate their respective constructors
    public StoriesModalAdapterClass(String title, String desc, String imageUrl, String username) {
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    // create an empty constructor
    public StoriesModalAdapterClass() {
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsername() {
        return username;
    }
}

