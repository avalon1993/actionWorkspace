package com.company.strategy;

import java.util.Arrays;

public class Main {


    public static void main(String[] args) {

        Cat[] c = {new Cat(2, 2), new Cat(5, 5), new Cat(3, 3)};
        Dog[] a = {new Dog(3), new Dog(5), new Dog(4)};
        Sorter<Dog> sorter = new Sorter<>();
        sorter.sort(a, new DogComparator());
        System.out.println(Arrays.toString(a));

    }
}
