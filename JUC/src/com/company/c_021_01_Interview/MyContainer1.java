package com.company.c_021_01_Interview;

import com.company.c_001.T;

import java.util.LinkedList;

public class MyContainer1<T> {
    final private int MAX = 10; //最多10个元素
    private int count = 0;
    LinkedList<T> lists = new LinkedList<>();


    public synchronized void put(T t) {
        while (lists.size() == MAX) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        lists.add(t);

        ++count;
        this.notifyAll();
    }


    public synchronized T get() {
        T t = null;

        while (lists.size() == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        t = lists.removeFirst();
        count--;
        this.notifyAll();
        return t;
    }


    public static void main(String[] args) {
        MyContainer1<String> c = new MyContainer1<>();


        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    System.out.println(c.get());
                }
            }, "c" + i).start();
        }


        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 25; j++) {
                    c.put(Thread.currentThread().getName() + "   " + j);
                }
            }, "p" + i).start();
        }

    }

}
