package com.company.c_012_volatile;

public class DoubleCheckLock {

    private static volatile DoubleCheckLock doubleCheckLock;

    public static DoubleCheckLock getDoubleCheckLock() {
        if (doubleCheckLock == null) {
            synchronized (DoubleCheckLock.class) {
                if (doubleCheckLock == null) {
                    doubleCheckLock = new DoubleCheckLock();
                }
            }
        }
        return doubleCheckLock;
    }


    public static void main(String[] args) {
        for (int i = 0;i<100;i++){
            new Thread(()->{
                getDoubleCheckLock();
            }).start();
        }
    }

}
