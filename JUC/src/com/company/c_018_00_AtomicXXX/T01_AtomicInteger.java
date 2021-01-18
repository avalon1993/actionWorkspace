package com.company.c_018_00_AtomicXXX;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class T01_AtomicInteger {
    AtomicInteger count = new AtomicInteger(0);

    void m() {
        for (int i = 0; i < 1000; i++) {
            count.incrementAndGet();
            System.out.println(Thread.currentThread().getName());
        }
    }


    public static void main(String[] args) {
        T01_AtomicInteger t = new T01_AtomicInteger();

        List<Thread> threadList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            threadList.add(new Thread(()->{
                t.m();
//                System.out.println(Thread.currentThread().getName());
            }, "thread" + i));

        }

        threadList.forEach((o) -> o.start());

        threadList.forEach((o) -> {
            try {
                o.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(t.count);


    }
}
