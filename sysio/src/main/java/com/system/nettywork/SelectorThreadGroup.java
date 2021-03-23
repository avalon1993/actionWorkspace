package com.system.nettywork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
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


            //注册到哪个selector呢？
            nextSelector(server);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void nextSelector(Channel c) {
        SelectorThread st = next();


        //1. 通过队列传递数据 消息
        st.lbq.add(c);
        //2. 通过打断阻塞，让对应的线程在打断后完成注册selector
        st.selector.wakeup();


        //重点 ：c可能是server ，也可能是client
//        ServerSocketChannel s = (ServerSocketChannel) c;
//
//        try {
//
//            st.selector.wakeup();// 功能是让selector的select()方法，立刻返回，不阻塞
//            s.register(st.selector, SelectionKey.OP_ACCEPT);
//            System.out.println("aaaaa");
//        } catch (ClosedChannelException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * 无论serverSocker socker都复用这个方法
     */
    private SelectorThread next() {

        int index = xid.incrementAndGet() % (sts.length + 1);
        return sts[index - 1];

    }

}
