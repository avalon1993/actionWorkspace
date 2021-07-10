package com.company.singleton;

/**
 * 饿汉式
 * 类加载到内存后，实例化一个单例，jvm保证线程安全
 */
public class Mgr01 {

    private static final Mgr01 INSTANCE = new Mgr01();

    public Mgr01() {
    }

    public static Mgr01 getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        Mgr01 m1 = Mgr01.getInstance();
        Mgr01 m2 = Mgr01.getInstance();
        System.out.println(m1 == m2);

    }

}
