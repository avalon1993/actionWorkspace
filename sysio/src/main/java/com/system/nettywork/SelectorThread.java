package com.system.nettywork;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorThread implements Runnable {
    /**
     * 每线程对应一个selector
     * 多线程情况下，该主机，该程序的并发客户端北分配到多个selector伤
     * 每个客户端，只绑定到其中一个selector
     * 不会有交互问题
     **/

    Selector selector = null;

    SelectorThread() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {


        //loop
        while (true) {

            try {
                //1.select
                int nums = selector.select();

                //2.处理selectKeys
                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isAcceptable()) {
                            /**复杂，接受客户端过程
                             * 接受过后，需要注册，
                             * 多线程下
                             */


                        } else if (key.isReadable()) {

                        } else if (key.isWritable()) {

                        }
                    }
                }
                //3.处理一些task

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void acceptHandler(SelectionKey key) {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
    }
}
