package com.system.io.testreactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class SelectorThreadGroup {

    SelectorThread[] sts;

    ServerSocketChannel server = null;

    AtomicInteger xid = new AtomicInteger(0);

    SelectorThreadGroup(int num) {
        sts = new SelectorThread[num];

        for (int i = 0; i < num; i++) {
            sts[i] = new SelectorThread(this);
            new Thread(sts[i]).start();
        }

    }


    public void bind(int port) {

        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);

            server.bind(new InetSocketAddress(port));
            nextSelector(server);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSelector(Channel c) {
        SelectorThread st = next();

        st.lbq.add(c);

        st.selector.wakeup();




//        ServerSocketChannel s = (ServerSocketChannel) c;
//        try {
//
//            s.register(st.selector, SelectionKey.OP_ACCEPT);
//            st.selector.wakeup();
//            System.out.println("aaaaaaaaaaaaaaaaaaa");
//
//        } catch (ClosedChannelException e) {
//            e.printStackTrace();
//        }


    }

    private SelectorThread next() {
        int index = xid.incrementAndGet() % sts.length;
        return sts[index];
    }
}
