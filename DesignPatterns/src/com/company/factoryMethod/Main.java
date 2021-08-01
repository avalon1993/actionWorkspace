package com.company.factoryMethod;

public class Main {

    public static void main(String[] args) {
//        Moveable m = new Plane();
//        m.go();


        Moveable m = new CarFactory().createCar();


    }
}
