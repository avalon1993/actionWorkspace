package com.company;

import java.util.concurrent.TimeUnit;

public class Main {

    private static  class T1 extends Thread{
        public void run(){
            for (int i = 0;i<10;i++){
                try {
                    TimeUnit.MICROSECONDS.sleep(1);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("T1");
            }
        }
    }

    public static void main(String[] args) {
	// write your code here
        new T1().start();
        for (int i = 0;i<10;i++){
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("main");
        }
    }
}
