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


    SelectorThreadGroup stg = this;


    public void setWorker(SelectorThreadGroup stg) {
        this.stg = stg;

    }


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
//            nextSelector(server);
//            nextSelectorV2(server);
            nextSelectorV3(server);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextSelector(Channel c) {
        SelectorThread st = next();

        st.lbq.add(c);

        st.selector.wakeup();

    }

    public void nextSelectorV2(Channel c) {
        try {
            if (c instanceof ServerSocketChannel) {
                sts[0].lbq.put(c);
                sts[0].selector.wakeup();
            } else {
                SelectorThread st = nextv2();
                st.lbq.add(c);
                st.selector.wakeup();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void nextSelectorV3(Channel c) {

        try {
            if (c instanceof ServerSocketChannel) {
                SelectorThread st = next();
                st.lbq.put(c);

                st.setWorker(stg);

                st.selector.wakeup();
            } else {
                SelectorThread st = nextv3();
                st.lbq.add(c);
                st.selector.wakeup();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private SelectorThread next() {
        int index = xid.incrementAndGet() % sts.length;
        return sts[index];
    }

    private SelectorThread nextv2() {
        int index = xid.incrementAndGet() % (sts.length - 1);
        return sts[index + 1];
    }

    private SelectorThread nextv3() {
        int index = xid.incrementAndGet() % stg.sts.length;
        return stg.sts[index];
    }


}
