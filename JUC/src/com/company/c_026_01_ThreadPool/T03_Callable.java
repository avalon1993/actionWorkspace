package com.company.c_026_01_ThreadPool;

import java.util.concurrent.*;

public class T03_Callable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<String> c = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "hello callable";
            }
        };


        ExecutorService service = Executors.newCachedThreadPool();
        Future<String> futureTask = service.submit(c);

        System.out.println(futureTask.get());
        service.shutdown();
    }
}
