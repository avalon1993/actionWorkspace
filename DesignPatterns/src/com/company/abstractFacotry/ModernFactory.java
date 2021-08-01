package com.company.abstractFacotry;

public class ModernFactory extends AbstractFactory {


    @Override
    Food createFood() {
        return new Bread();
    }

    @Override
    Vehicle createVehicle() {
        return new Car();
    }

    @Override
    Weanpon createWeanpon() {
        return new AK47();
    }
}
