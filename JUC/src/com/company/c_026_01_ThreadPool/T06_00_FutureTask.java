package com.company.c_026_01_ThreadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class T06_00_FutureTask {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            return 100;
        });
        new Thread(futureTask).start();


        System.out.println(futureTask.get());
    }
}
