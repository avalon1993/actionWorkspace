package com.company.abstractFacotry;

public class MagicFactory extends AbstractFactory {
    @Override
    Food createFood() {
        return new MushRoom();
    }

    @Override
    Vehicle createVehicle() {
        return new Broom();
    }

    @Override
    Weanpon createWeanpon() {
        return new MagicStick();
    }
}
