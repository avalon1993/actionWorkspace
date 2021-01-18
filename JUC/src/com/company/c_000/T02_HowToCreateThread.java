package com.company.c_000;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class T02_HowToCreateThread {


    static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println("Hello MyThread!");
        }
    }

    static class MyRun implements Runnable{

        @Override
        public void run() {
            System.out.println("Hello MyRun!");
        }
    }


    static class MyCallableThread implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            int i = 0;
         for (;i<100;i++){
                System.out.println(Thread.currentThread().getName()+" "+i);
            }
            return i;
        }
    }

    public static void main(String[] args) {

//        new MyThread().start();
//
//        new Thread(new MyRun()).start();
//
//        new Thread(()->{
//            System.out.println("Hello Lambda!");
//        }).start();


        MyCallableThread  myCallableThread = new MyCallableThread();

        FutureTask<Integer> futureTask = new FutureTask<>(myCallableThread);

        new Thread(futureTask).start();
        try {
            Integer result = futureTask.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}

/**
启动线程的方式
    1.Thread
    2.Runnable
    3.lambda表达式
    4.Executors.newCachedThread
    5.使用Callable和Future创建线程
 **/