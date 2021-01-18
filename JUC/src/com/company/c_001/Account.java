package com.company.c_001;

public class Account {
    String name;
    double balance;


    public synchronized void set(String name, double balance) {
        this.name = name;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.balance = balance;
    }


    public /*synchronized */    double getBalance(String name) {
        return this.balance;
    }




    public static void main(String[] args) {

    }

}
