package com.system.io.nettyDemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MyRPCTest {
    @Test
    public void startServer() {
        System.out.println("server accept cliet port");
        NioEventLoopGroup boss = new NioEventLoopGroup(1);

        NioEventLoopGroup worker = boss;
        ServerBootstrap sbs = new ServerBootstrap();

        ChannelFuture bind = sbs.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        System.out.println("server accept cliet port: " + ch.remoteAddress().getPort());
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ServerRequestHandler());
//                        p.addLast(new)
                    }
                }).bind(new InetSocketAddress("localhost", 9090));

        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void get() {

        new Thread(() -> {
            startServer();
        }).start();
        System.out.println("server started......");

//        Car car = proxyGet(Car.class);// 动态代理
//        car.ooxx("hello");


//        Fly fly = proxyGet(Fly.class);
//        fly.xxoo("hello");

    }


    public static <T> T proxyGet(Class<T> interfaceInfo) {
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};


        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


                String name = interfaceInfo.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                MyContent content = new MyContent();

                content.setArgs(args);
                content.setName(name);
                content.setMethodName(methodName);
                content.setParameterTypes(parameterTypes);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(content);
                byte[] msgBody = out.toByteArray();

                //2.requestId + message 本地要换成
                // 协议
                MyHeader header = createHeader(msgBody);

                out.reset();
                oout = new ObjectOutputStream(out);
                oout.writeObject(header);
                byte[] msgHeader = out.toByteArray();

                //3.连接池，取得连接
                ClientFactory factory = ClientFactory.getFactory();
                NioSocketChannel clientChannel =
                        factory.getClient(new InetSocketAddress("localhost", 9090));
                //获取连接过程中，开始->创建,过程-直接取

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(msgHeader.length + msgBody.length);


                CountDownLatch countDownLatch = new CountDownLatch(1);
                long id = header.getRequestID();

                ResponseHandler.addCallBack(id, new Runnable() {
                    @Override
                    public void run() {
                        countDownLatch.countDown();
                    }
                });

                byteBuf.writeBytes(msgHeader);
                byteBuf.writeBytes(msgBody);

                ChannelFuture channelFuture = clientChannel.writeAndFlush(byteBuf);
                channelFuture.sync();   //io是双向的，看似有个sync，实际仅代表out


                countDownLatch.await();


                return null;
            }
        });
    }


    public static MyHeader createHeader(byte[] msg) {
        MyHeader header = new MyHeader();

        int size = msg.length;
        int f = 0x14141414;
        long requestID = Math.abs(UUID.randomUUID().getLeastSignificantBits());

        header.setFlag(f);
        header.setDataLen(size);
        header.setRequestID(requestID);

        return header;
    }


}

class ServerRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        ByteBuf sendBuf = buf.copy();

        if (buf.readableBytes() >= 160) {
            byte[] bytes = new byte[160];
            buf.readBytes(bytes);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();


            System.out.println(header.dataLen);
            System.out.println(header.requestID);


            if (buf.readableBytes() >= header.getDataLen()) {
                byte[] data = new byte[(int) header.getDataLen()];
                buf.readBytes(data);
                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);

                MyContent content = (MyContent) doin.readObject();
                System.out.println(content.getName());
            }

        }

        ChannelFuture channelFuture = ctx.writeAndFlush(sendBuf);
        channelFuture.sync();

    }
}

class ClientFactory {

    int poolSize = 1;
    NioEventLoopGroup clientWorker;
    Random rand = new Random();


    private ClientFactory() {
    }

    private static final ClientFactory factory;

    static {
        factory = new ClientFactory();
    }

    public static ClientFactory getFactory() {
        return factory;
    }


    ConcurrentHashMap<InetSocketAddress, ClientPool> outboxs = new ConcurrentHashMap<>();

    public synchronized NioSocketChannel getClient(InetSocketAddress address) {
        ClientPool clientPool = outboxs.get(address);

        if (clientPool == null) {
            outboxs.putIfAbsent(address, new ClientPool(poolSize));
            clientPool = outboxs.get(address);
        }

        int i = rand.nextInt(poolSize);

        if (clientPool.clients[i] != null && clientPool.clients[i].isActive()) {
            return clientPool.clients[i];
        }
        synchronized (clientPool.lock[i]) {
            return clientPool.clients[i] = create(address);
        }

    }

    private NioSocketChannel create(InetSocketAddress address) {

        //基于netty的客户端创建方式
        clientWorker = new NioEventLoopGroup(1);

        Bootstrap bs = new Bootstrap();
        bs.group(clientWorker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline p = nioSocketChannel.pipeline();
                        p.addLast(new ClientResponses());
                    }
                }).connect(address);

        return null;
    }
}

class ResponseHandler {
    static ConcurrentHashMap<Long, Runnable> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(long requestID, Runnable cb) {
        mapping.putIfAbsent(requestID, cb);
    }

    public static void runCallBack(long requestID) {
        Runnable runnable = mapping.get(requestID);
        runnable.run();
        removeCB(requestID);
    }

    private static void removeCB(long requestID) {
        mapping.remove(requestID);
    }
}


class ClientResponses extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;


        if (buf.readableBytes() >= 160) {
            byte[] bytes = new byte[160];
            buf.readBytes(bytes);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();


            System.out.println(header.dataLen);
            System.out.println(header.requestID);

            //todo 服务端任务
            ResponseHandler.runCallBack(header.requestID);
//            if (buf.readableBytes() >= header.getDataLen()) {
//                byte[] data = new byte[(int) header.getDataLen()];
//                buf.readBytes(data);
//                ByteArrayInputStream din = new ByteArrayInputStream(data);
//                ObjectInputStream doin = new ObjectInputStream(din);
//            }

        }

//        super.channelRead(ctx, msg);
    }
}


class ClientPool {
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

class MyHeader implements Serializable {
    //通信上的协议
    int flag;
    long requestID;
    long dataLen;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestID() {
        return requestID;
    }

    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }

    public long getDataLen() {
        return dataLen;
    }

    public void setDataLen(long dataLen) {
        this.dataLen = dataLen;
    }


}


class MyContent implements Serializable {

    String name;
    String methodName;
    Class<?>[] parameterTypes;
    Object[] args;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }


}


interface Car {
    void ooxx(String msg);
}

interface Fly {
    void xxoo(String msg);
}















