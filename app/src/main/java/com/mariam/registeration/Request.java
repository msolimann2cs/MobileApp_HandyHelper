package com.mariam.registeration;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class Request {
    String title;
    String description;
    LocalDate reqDate;
    String date;

    int dateNum;
    double locationLong;
    double locationLat;
    int price;
    int id;
    String cat;
    String userId;
    String time;
    ArrayList<Application> apps;

    public static double currLat;
    public static double currLon;

    public Request(){

    }

    public Request(int id,String cat, String title, String description, String date,String time, double locationLat, double locationLong, int price,  String userId) {
        //Current User Location
        this.reqDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        this.id = id;
        this.cat = cat;
        this.userId = userId;
        this.time = time;

        try {
            LocalDate currDate = LocalDate.now();
            Duration diff = Duration.between(currDate.atStartOfDay(), reqDate.atStartOfDay());
            this.dateNum =(int)diff.toDays();
            if(this.dateNum ==0){
                this.date = "Today";
            }
            else if(this.dateNum == 1){
                this.date = "Tommorrow";
            }else{
                this.date = "In "+this.dateNum+" Days";
            }
        }catch (Exception e){

        }

        this.title = title;
        this.description = description;
        this.apps = new ArrayList<Application>();

        this.locationLat = locationLat;
        this.locationLong = locationLong;

        this.price = price;
    }

    public double distance() {

        double lat1 = this.locationLat;
        double lon1 = this.locationLong;

        double lat2 = this.currLat;
        double lon2 = this.currLon;

        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;


            return (dist);
        }
    }

    public void setCurrentLocations(double lat1, double lon1){
        this.currLat = lat1;
        this.currLon = lon1;
    }

    public void addApplication(User user,int price){
        Application app = new Application(user, price);
        apps.add(app);
    }

    public void acceptApp(int i){
        apps.get(i).setAccepted(true);
    }

    public void setDateString() {

    }


}
