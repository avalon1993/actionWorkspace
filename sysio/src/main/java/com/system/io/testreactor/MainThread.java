package com.system.io.testreactor;

public class MainThread {
    public static void main(String[] args) {
        SelectorThreadGroup stg = new SelectorThreadGroup(1);
//        SelectorThreadGroup stg = new SelectorThreadGroup(3);

        stg.bind(9999);
    }
}
