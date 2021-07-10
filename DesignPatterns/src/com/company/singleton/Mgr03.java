package com.company.singleton;

/**
 * 懒加载
 * <p>
 * 多线程访问有问题
 */

public class Mgr03 {

    private static Mgr03 INSTANCE;


    private Mgr03() {
    }


    public static Mgr03 getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new Mgr03();
        }
        return INSTANCE;


    }

    public static void main(String[] args) {


    }

}
