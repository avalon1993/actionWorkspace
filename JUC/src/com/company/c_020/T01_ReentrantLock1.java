package com.company.c_020;

import java.util.concurrent.TimeUnit;

public class T01_ReentrantLock1 {


    synchronized void m1() {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void m2() {
        System.out.println("m2 .... ");
    }


    public static void main(String[] args) {

        T01_ReentrantLock1 r1 = new T01_ReentrantLock1();
        new Thread(r1::m1).start();


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(r1::m2).start();

    }
}
