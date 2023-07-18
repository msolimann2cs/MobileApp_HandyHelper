package com.mariam.registeration;

import java.io.Serializable;

public class User implements Serializable {
    private String username = "";
    private String email = "";
    private String password = "";
    private String gender = "";
    private String date_of_birth="";
//    private int b_year = 0, b_month = 0, b_day = 0;
    private String nat_ID = "";

    private String phone = "";
    private String interest="";
    private int notify = 0;
    private String description="";
    private byte[] imageBytes = null;

    public User(String username, String email, String pass, String gender,
                int b_year, int b_month, int b_day, String nat_ID) {
        this.username = username;
        this.email = email;
        this.password = pass;
        this.gender = gender;
        this.date_of_birth = b_year+"-"+b_month+"-"+b_day;
//        this.b_year = b_year;
//        this.b_month = b_month;
//        this.b_day = b_day;
        this.nat_ID = nat_ID;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
    //    public int getB_year() {
//        return b_year;
//    }
//
//    public int getB_month() {
//        return b_month;
//    }
//
//    public int getB_day() {
//        return b_day;
//    }

    public String getNat_ID() {
        return nat_ID;
    }

    public String getPhone() {
        return phone;
    }

    public String getInterest() {
        return interest;
    }

    public int getNotify() {
        return notify;
    }

    public String getDescription() {
        return description;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.password = pass;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(int b_day,int b_month,int b_year )
    {
        this.date_of_birth=b_year+"-"+b_month+"-"+b_day;
    }
    public void setNat_ID(String nat_ID) {
        this.nat_ID = nat_ID;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
}
