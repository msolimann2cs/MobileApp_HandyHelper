package com.mariam.registeration;

import java.io.Serializable;

public class User implements Serializable {
    private String username = "";
    private String email = "";
    private String pass = "";
    private String gender = "";
    private int b_year = 0, b_month = 0, b_day = 0;
    private String Nat_ID = "";

    public User(String username, String email, String pass, String gender,
                int b_year, int b_month, int b_day, String Nat_ID) {
        this.username = username;
        this.email = email;
        this.pass = pass;
        this.gender = gender;
        this.b_year = b_year;
        this.b_month = b_month;
        this.b_day = b_day;
        this.Nat_ID = Nat_ID;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPass() {
        return pass;
    }

    public String getGender() {
        return gender;
    }

    public int getB_year() {
        return b_year;
    }

    public int getB_month() {
        return b_month;
    }

    public int getB_day() {
        return b_day;
    }

    public String getNat_ID() {
        return Nat_ID;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setB_year(int b_year) {
        this.b_year = b_year;
    }

    public void setB_month(int b_month) {
        this.b_month = b_month;
    }

    public void setB_day(int b_day) {
        this.b_day = b_day;
    }

    public void setNat_ID(String Nat_ID) {
        this.Nat_ID = Nat_ID;
    }
}
