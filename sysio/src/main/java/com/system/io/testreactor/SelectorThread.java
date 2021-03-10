package com.system.io.testreactor;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class SelectorThread extends ThreadLocal<LinkedBlockingDeque<Channel>> implements Runnable {

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
        while (true) {
            try {

                System.out.println(Thread.currentThread().getName() + "   :  before select...." + selector.keys().size());
                int nums = selector.select();
//                Thread.sleep(1000);

                System.out.println(Thread.currentThread().getName() + "   :  after select...." + selector.keys().size());

                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isAcceptable()) {
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHander(key);
                        } else if (key.isWritable()) {

                        }
                    }
                }

                //处理task: listen client
                if (!lbq.isEmpty()) {
                    Channel c = lbq.take();
                    if (c instanceof ServerSocketChannel) {
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(selector, SelectionKey.OP_ACCEPT);

                        System.out.println(Thread.currentThread().getName() + "     register listen");

                    } else if (c instanceof SocketChannel) {
                        SocketChannel client = (SocketChannel) c;
                        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);

                        System.out.println(Thread.currentThread().getName() + "     register client: " + client.getRemoteAddress());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void acceptHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + "   acceptHandler......");

        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);
//            stg.nextSelector(client);
//            stg.nextSelectorV2(client);
            stg.nextSelectorV3(client);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void readHander(SelectionKey key) {
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        SocketChannel client = (SocketChannel) key.channel();

        buffer.clear();
        while (true) {
            try {
                int nums = client.read(buffer);
                if (nums > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    buffer.clear();
                } else if (nums == 0) {
                    break;
                } else if (nums < 0) {
                    System.out.println("client: " + client.getRemoteAddress() + " closed......");

                    key.cancel();
                    break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void setWorker(SelectorThreadGroup stgWorker) {
        this.stg = stgWorker;
    }
}
