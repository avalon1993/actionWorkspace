package com.company.c_025;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LinkedBlockingQueue {

    static BlockingQueue<String> strs = new LinkedBlockingDeque<>();


    static Random r = new Random();

    static {
        for (int i = 0; i < 1000; i++) {
            strs.add("test:" + i);
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                String s = strs.poll();
                if (s == null) break;
                else System.out.println("销售了--" + s);
            }

        }).start();
    }
}
