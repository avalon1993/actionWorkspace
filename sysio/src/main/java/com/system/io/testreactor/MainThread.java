package com.system.io.testreactor;

public class MainThread {
    public static void main(String[] args) {
//        SelectorThreadGroup stg = new SelectorThreadGroup(3);
//        SelectorThreadGroup stg = new SelectorThreadGroup(3);
        SelectorThreadGroup boss = new SelectorThreadGroup(3);

        SelectorThreadGroup worker = new SelectorThreadGroup(3);
//        stg.bind(9999);


        boss.setWorker(worker);

        boss.bind(9999);
    }
}
