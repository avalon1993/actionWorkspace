package com.company.c_020;

import java.util.concurrent.CountDownLatch;

public class T06_TestCountDownLatch {


    private static void usingCountDownLatch() {
        Thread[] threads = new Thread[100];

        CountDownLatch latch = new CountDownLatch(8);

        for (int i = 0; i < 100; i++) {
            threads[i] = new Thread(() -> {
                int result = 0;
                latch.countDown();
                System.out.println(Thread.currentThread().getName());
                for (int j = 0; j < 10000; j++) {
                    result += j;

                }
            });
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end latch");


    }

    public static void main(String[] args) {
        usingCountDownLatch();
    }
}
