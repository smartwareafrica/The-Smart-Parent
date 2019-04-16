package com.MwandoJrTechnologies.the_smart_parent.Stories;

public class StoriesModalAdapterClass {
    // define four String variables
    private String title;
    private String contents;
    private String imageUrl;


    private String authorName;

    // generate their respective constructors
    public StoriesModalAdapterClass(String title, String contents, String imageUrl, String authorName) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl;
        this.authorName = authorName;
    }

    // create an empty constructor
    public StoriesModalAdapterClass() {
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setcontents(String contents) {
        this.contents = contents;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getcontents() {
        return contents;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}

