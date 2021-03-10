package com.system.io.myNettyDemo.rpcFramework;

import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientPool {

    NioSocketChannel[] clients;
    Object[] lock;


    ClientPool(int size) {
        clients = new NioSocketChannel[size];
        lock = new Object[size];

        for (int i = 0; i < size; i++) {
            lock[i] = new Object();
        }

    }

}
