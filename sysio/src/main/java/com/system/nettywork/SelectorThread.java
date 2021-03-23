package com.system.nettywork;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class SelectorThread implements Runnable {
    /**
     * 每线程对应一个selector
     * 多线程情况下，该主机，该程序的并发客户端北分配到多个selector伤
     * 每个客户端，只绑定到其中一个selector
     * 不会有交互问题
     **/

    Selector selector = null;

    LinkedBlockingQueue<Channel> lbq = new LinkedBlockingQueue<>();

    SelectorThreadGroup stg;

    SelectorThread(SelectorThreadGroup stg) {
        try {
            this.stg = stg;
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

//                System.out.println(Thread.currentThread().getName()
//                        + "  :before select......" +
//                        selector.keys().size());
                int nums = selector.select();
//                System.out.println(Thread.currentThread().getName()
//                        + "  :after select......" +
//                        selector.keys().size());
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
                            acceptHandler(key);

                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {

                        }
                    }
                }
                //3.处理一些task :listen client

                if (!lbq.isEmpty()) {
                    Channel c = lbq.take();

                    if (c instanceof ServerSocketChannel) {
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(selector, SelectionKey.OP_ACCEPT);

                        System.out.println(Thread.currentThread().getName()
                                + "  register listen");

                    } else if (c instanceof SocketChannel) {
                        SocketChannel client = (SocketChannel) c;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println(Thread.currentThread().getName()
                                + "   register client:   " + client.getRemoteAddress());


                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void readHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + "   read.....");

        ByteBuffer buffer = (ByteBuffer) key.attachment();

        SocketChannel client = (SocketChannel) key.channel();
        buffer.clear();
        while (true) {
            try {
                int num = client.read(buffer);
                if (num > 0) {
                    buffer.flip();  //蒋读出的内容翻转，然后读出
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }

                    buffer.clear();
                } else if (num == 0) {
                    break;
                } else if (num < 0) {
                    //客户端断开了
                    System.out.println("client:  " + client.getRemoteAddress() + " closed.....");
                    key.cancel();
                    break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + "    acceptHandler .... ");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            //choose a selector and register!!
//            client.register();

            stg.nextSelector(client);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
