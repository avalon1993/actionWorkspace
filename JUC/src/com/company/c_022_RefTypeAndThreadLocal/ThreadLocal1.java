package com.company.c_022_RefTypeAndThreadLocal;

import com.company.c_001.T;

import java.util.concurrent.TimeUnit;

public class ThreadLocal1 {

    static ThreadLocal<Person> tl = new ThreadLocal<>();


    static class Person {
        String name = "zhangsan";
    }


    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tl.set(new Person());
            System.out.println(tl.get());
            System.out.println("22222222222222");
            System.out.println(tl.toString());
            System.out.println("22222222222222");
        }).start();


        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tl.set(new Person());
            System.out.println(tl.get());
            Person person = tl.get();
            System.out.println("1111111111111");
            System.out.println(person.name);
            System.out.println("1111111111111");
        }).start();

    }
}
