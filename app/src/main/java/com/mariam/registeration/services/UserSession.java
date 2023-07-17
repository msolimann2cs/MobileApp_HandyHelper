package com.mariam.registeration.services;

import com.mariam.registeration.User;

public class UserSession {
    private static UserSession instance;
    private User logged_user;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getLoggedUser() {
        return logged_user;
    }

    public void setLoggedUser(User logged_user){
        this.logged_user = logged_user;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }
}
