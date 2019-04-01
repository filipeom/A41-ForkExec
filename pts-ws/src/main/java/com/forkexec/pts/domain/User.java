package com.forkexec.pts.domain;

public class User {

    private int points;
    private String email;

    public User(String email, int points) {
        this.email = email;
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    } 

    public String getEmail() {
        return this.email;
    }

    //TODO
    public void changeBalance(int points) {
        this.points += points;
    }

    public void setBalance(int points) {
        this.points = points;
    }

}