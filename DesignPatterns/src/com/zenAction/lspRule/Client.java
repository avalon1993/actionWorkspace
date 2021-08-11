package com.zenAction.lspRule;

public class Client {
    public static void main(String[] args) {
        Soldier sanMao = new Soldier();
        sanMao.setGun(new Rifle());
        sanMao.killEnemy();

        Snipper sanMao2 = new Snipper();

        AUG aug = new AUG();
        sanMao2.setGun(aug);
        sanMao2.killEnemy(aug);

    }
}
