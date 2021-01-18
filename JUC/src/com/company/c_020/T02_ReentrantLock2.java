package com.company.c_020;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class T02_ReentrantLock2 {

    Lock lock = new ReentrantLock();

    void m1() {
        try {
            lock.lock();

            for (int i = 0; i < 10; i++) {

                TimeUnit.SECONDS.sleep(1);

                System.out.println(i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void m2() {
        try {
            lock.lock();
            System.out.println("m2 ...");
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        T02_ReentrantLock2 r2 = new T02_ReentrantLock2();


        new Thread(r2::m1).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new Thread(r2::m2).start();
    }
}
