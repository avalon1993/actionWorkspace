package com.company.c_022_RefTypeAndThreadLocal;

import java.io.IOException;

public class T01_NormalReference {

    public static void main(String[] args) throws IOException {
        M m = new M();
        m = null;
        System.gc();
        System.in.read();
    }
}
