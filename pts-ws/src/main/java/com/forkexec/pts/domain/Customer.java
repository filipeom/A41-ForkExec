package com.forkexec.pts.domain;

public class Customer {

    private int balance;
    private String email;

    public Customer(String email, int balance) {
        this.email = email;
        this.balance = balance;
    }

    public int getBalance() {
        return this.balance;
    } 

    public String getEmail() {
        return this.email;
    }

    //look at this better
    public void changeBalance(int value) {
        this.balance += value;
    }

}