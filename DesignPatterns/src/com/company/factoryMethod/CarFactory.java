package com.company.factoryMethod;

public class CarFactory {

    public Car createCar() {
        System.out.println(" a car created");
        return new Car();
    }
}
