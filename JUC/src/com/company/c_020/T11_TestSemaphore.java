package com.company.c_020;

import java.util.concurrent.Semaphore;

public class T11_TestSemaphore {


    public static void main(String[] args) {

        Semaphore s = new Semaphore(2, false);

        new Thread(() -> {
            try {
                s.acquire();
                System.out.println("T1 running...");
                Thread.sleep(200);
                System.out.println("T1 running...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                s.release();
            }

        }).start();
        new Thread(() -> {
            try {
                s.acquire();

                System.out.println("T2 running...");
                Thread.sleep(200);
                System.out.println("T2 running...");


            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                s.release();
            }
        }).start();
        new Thread(() -> {
            try {
                s.acquire();

                System.out.println("T3 running...");
                Thread.sleep(200);
                System.out.println("T3 running...");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                s.release();
            }
        }).start();
    }
}
