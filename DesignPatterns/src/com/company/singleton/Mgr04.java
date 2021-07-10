package com.company.singleton;

/**
 * DCL  双重检查
 */
public class Mgr04 {

    public static volatile Mgr04 INSTANCE;


    public Mgr04() {

    }

    public static Mgr04 getInstance() {
        Object o = new Object();
        if (INSTANCE == null) {
            synchronized (o) {
                if (INSTANCE == null) {
                    INSTANCE = new Mgr04();
                }
            }
        }
        return INSTANCE;
    }

}
