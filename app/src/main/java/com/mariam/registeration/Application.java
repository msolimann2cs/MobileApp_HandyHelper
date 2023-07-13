package com.mariam.registeration;

public class Application {
    private User user;
    private boolean isAccepted;
    private int appliedPrice;

    public Application(User user, int appliedPrice) {
        this.user = user;
        this.appliedPrice = appliedPrice;
        this.isAccepted = false;
    }

    public User getUser() {
        return user;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
