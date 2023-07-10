package com.mariam.registeration;

public class Request {
    String title;
    String description;
    String date;
    String location;
    int image;
    float time;
    float price;

    public Request(String title, String description, String date, String location, float time, float price, int image) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.time = time;
        this.price = price;
        this.image = image;
    }
}