package com.company.c_025;

import java.util.PriorityQueue;

public class T07_01_PriorityQueue {


    public static void main(String[] args) {

        PriorityQueue<String> q = new PriorityQueue<>();
        q.add("d");
        q.add("e");
        q.add("a");
        q.add("b");
        q.add("c");

        for (int i = 0; i < 5; i++) {
            System.out.println(q.poll());
        }

    }
}
