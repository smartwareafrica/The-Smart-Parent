package com.MwandoJrTechnologies.the_smart_parent.Chats;


//modal class to retrieve messages
public class Messages {

    public String date, time, type, message, from;

    public Messages(){
        //empty default constructor
    }

    //generate constructor with parameters

    public Messages(String date, String time, String type, String message, String from) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.message = message;
        this.from = from;
    }

    //generate getter and setter


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
