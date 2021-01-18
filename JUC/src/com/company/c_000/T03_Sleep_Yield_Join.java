package com.company.c_000;

public class T03_Sleep_Yield_Join {


    static void testJoin() {
        Thread t1 = new Thread(() -> {
            System.out.println("t1 start");
            for (int i = 0; i < 100; i++) {
                System.out.println("A" + i);
                try {
                    Thread.sleep(500);
                    //TimeUnit.Milliseconds.sleep(500)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println("t2 start");
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 100; i++) {
                System.out.println("B" + i);
                try {
                    Thread.sleep(500);
                    //TimeUnit.Milliseconds.sleep(500)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        t1.start();
        t2.start();


    }

    public static void main(String[] args) {
        testJoin();
    }
}
