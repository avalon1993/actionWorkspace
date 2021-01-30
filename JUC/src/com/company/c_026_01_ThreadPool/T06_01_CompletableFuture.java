package com.company.c_026_01_ThreadPool;

import com.sun.xml.internal.ws.util.CompletedFuture;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class T06_01_CompletableFuture {

    public static void main(String[] args) {
        long start, end;


        start = System.currentTimeMillis();


        CompletableFuture<Double> futureTM = CompletableFuture.supplyAsync(() -> priceOfTB());
        CompletableFuture<Double> futureTB = CompletableFuture.supplyAsync(() -> priceOfTB());
        CompletableFuture<Double> futureJD = CompletableFuture.supplyAsync(() -> priceOfJD());

        CompletableFuture.allOf(futureTM, futureTB, futureJD).join();

//        CompletableFuture.supplyAsync(() -> priceOfTB())
//                .thenApply(String::valueOf)
//                .thenApply(str -> "price " + str)
//                .thenAccept(System.out::println);

        end = System.currentTimeMillis();
        System.out.println("use completable future! " + (end - start));

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double priceOfTM() {
        delay();
        return 1.00;
    }

    private static double priceOfTB() {
        delay();
        return 2.00;
    }

    private static double priceOfJD() {
        delay();
        return 3.00;
    }

    private static void delay() {
        int time = new Random().nextInt(500);
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("After %s sleep!\n", time);
    }
}
