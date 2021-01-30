package com.company.c_026_00_Interview;

import java.util.concurrent.locks.LockSupport;

public class T06_00_sync_wait_notify {


    static Thread t1 = null, t2 = null;

    public static void main(String[] args) {
        final Object o = new Object();

        char[] aI = "1234567".toCharArray();
        char[] aC = "ABCDEFG".toCharArray();

        t1 = new Thread(() -> {
            synchronized (o) {
                for (char c : aI) {
                    System.out.println(c);

                    try {
                        o.notify();
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                o.notify();
            }

        }, "t1");

        t2 = new Thread(() -> {
            synchronized (o) {
                for (char c : aC) {
                    System.out.println(c);
                    try {
                        o.notify();
                        o.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                o.notify();
            }

        }, "t2");

        t1.start();
        t2.start();


    }
}
