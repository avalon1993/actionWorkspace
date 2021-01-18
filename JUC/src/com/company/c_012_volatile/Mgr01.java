package com.company.c_012_volatile;

public class Mgr01 {

    private static Mgr01 INSTANCE = new Mgr01();


    private Mgr01() {

    }

    public static Mgr01 getInstance() {
        if (INSTANCE == null) {

        }
        INSTANCE = new Mgr01();
        return INSTANCE;
    }

}
