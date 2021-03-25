package rpc;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.Test;
import rpcdemo.proxy.MyProxy;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1. 写一个rpc
 * 2. 来回通信.连接数量,拆包
 * 3. 动态代理\序列化 协议封装
 * 4. 连接池
 */
public class MyRpcTest {


    @Test
    public void startServer() {

        MyCar car = new MyCar();
        MyFly fly = new MyFly();

        Dispatcher dis = new Dispatcher();

        dis.register(Car.class.getName(), car);
        dis.register(Fly.class.getName(), fly);


        NioEventLoopGroup boss = new NioEventLoopGroup(50);
        NioEventLoopGroup worker = boss;


        ServerBootstrap sbs = new ServerBootstrap();

        ChannelFuture bind = sbs.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        System.out.println("server accept client port: " + ch.remoteAddress().getPort());
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ServerDecode());
                        p.addLast(new ServerRequestHandler(dis));


                    }
                }).bind(new InetSocketAddress("localhost", 9090));
        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //模拟consumer端
    @Test
    public void get() {
        new Thread(() -> {
            startServer();
        }).start();

        System.out.println("server started ......");


        AtomicInteger num = new AtomicInteger(0);
        int size = 20;
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(() -> {
                Car car = MyProxy.proxyGet(Car.class); //动态代理调用

                String arg = "hello" + num.incrementAndGet();
                String res = car.ooxx(arg);

                System.out.println("client over msg: " + res + "  src arg:" + arg);


            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Car car = proxyGet(Car.class); //基于动态代理
//        car.ooxx("hello");
    }



    public static MyHeader createHeader(byte[] msg) {
        MyHeader header = new MyHeader();
        int size = msg.length;
        int f = 0x14141414;
        //0x14 0001 0100
        long requestID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        header.setFlag(f);
        header.setDataLen(size);
        header.setRequestID(requestID);
        return header;
    }

}

class Dispatcher {
    public static ConcurrentHashMap<String, Object> invokeMap = new ConcurrentHashMap<>();

    public void register(String k, Object obj) {
        invokeMap.put(k, obj);
    }

    public Object get(String k) {
        return invokeMap.get(k);
    }
}


class MyCar implements Car {

    @Override
    public String ooxx(String msg) {
        System.out.println("server get client arg: " + msg);
        return "server res " + msg;
    }
}

class MyFly implements Fly {

    @Override
    public void xxoo(String msg) {
        System.out.println("server get client arg: " + msg);
    }
}

class ServerDecode extends ByteToMessageDecoder {


    //父类一定有channelRead -> byteBuf
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {

//        System.out.println("channel start : " + buf.readableBytes());

        while (buf.readableBytes() >= 81) {
            byte[] bytes = new byte[81];
            buf.getBytes(buf.readerIndex(), bytes); //从哪里读,读多少,但是readindex不变
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
//            System.out.println(header.dataLen);
//            System.out.println("Server response @id:  " + header.getRequestID());


            //decode在2个方向都使用
            //通信协议
            if (buf.readableBytes() >= header.getDataLen()) {
                buf.readBytes(81); //移动指针到body开始的位置
                byte[] data = new byte[(int) header.getDataLen()];
                buf.readBytes(data);
                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);

                if (header.getFlag() == 0x14141414) {
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new Packmsg(header, content));
                } else if (header.getFlag() == 0x14141424) {
                    MyContent content = (MyContent) doin.readObject();
                    out.add(new Packmsg(header, content));
                }


            } else {
                break;
            }
        }
    }
}


class ServerRequestHandler extends ChannelInboundHandlerAdapter {
    Dispatcher dis;

    public ServerRequestHandler(Dispatcher dis) {
        this.dis = dis;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Packmsg requestPkg = (Packmsg) msg;
//        System.out.println("Server handler : " + requestPkg.content.getArgs()[0]);

        //如果假设处理完成,要给客户端返回

        //byteBuf
        //因为是rpc,需要requestID
        //在client那一侧也要解决解码问题

        // 关注rpc通信协议, 来的时候flag 0x14141414

        String ioThreadName = Thread.currentThread().getName();

        //1. 直接在当前方法处理 io 业务 返回
        //2. 使用netty的eventloop来处理业务及返回
        //3. 自己创建线程池

//        ctx.executor().parent().next().execute(new Runnable() {
        ctx.executor().execute(new Runnable() {

            @Override
            public void run() {


                String serviceName = requestPkg.content.getName();
                String method = requestPkg.content.getMethodName();
                Object c = dis.get(serviceName);
                Class<?> clazz = c.getClass();
                Object res = null;
                try {
                    Method m = clazz.getMethod(method, requestPkg.content.parameterTypes);
                    res = m.invoke(c, requestPkg.content.getArgs());


                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


                String execThreadName = Thread.currentThread().getName();
                MyContent content = new MyContent();

//                String s = ("io thread: " + ioThreadName + " exec thread : " + execThreadName +
//                        " from args : " + requestPkg.content.getArgs()[0]);
//                System.out.println(s);

                content.setRes((String) res);

                byte[] contentByte = SerDerUtil.ser(content);

                MyHeader resHeader = new MyHeader();
                resHeader.setRequestID(requestPkg.header.getRequestID());
                resHeader.setFlag(0x14141424);
                resHeader.setDataLen(contentByte.length);

                byte[] headerByte = SerDerUtil.ser(resHeader);

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT
                        .directBuffer(headerByte.length + contentByte.length);

                byteBuf.writeBytes(headerByte);
                byteBuf.writeBytes(contentByte);

                ctx.writeAndFlush(byteBuf);

            }
        });
    }

}

class ClientFactory {

    int poolSize = 10;
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


    //一个consumer可以链接很多provider,每一个provider都有自己的poll
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
        //基于netty 的客户端 创建方式
        clientWorker = new NioEventLoopGroup(1);
        Bootstrap bs = new Bootstrap();

        ChannelFuture connect = bs.group(clientWorker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ServerDecode());
                        p.addLast(new ClientResponses());
                    }
                }).connect(address);
        try {
            NioSocketChannel client = (NioSocketChannel) connect.sync().channel();
            return client;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }


}

class ResponseMappingHandler {
    static ConcurrentHashMap<Long, CompletableFuture> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(long requestID, CompletableFuture cb) {
        mapping.putIfAbsent(requestID, cb);

    }

    public static void runCallBack(Packmsg msg) {
        CompletableFuture cf = mapping.get(msg.header.getRequestID());
//        runnable.run();
        cf.complete(msg.getContent().getRes());
        removeCB(msg.header.getRequestID());
    }

    private static void removeCB(long requestID) {
        mapping.remove(requestID);
    }
}


class ClientResponses extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Packmsg responsepkg = (Packmsg) msg;

        //曾经没考虑返回的事情
        ResponseMappingHandler.runCallBack(responsepkg);

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
    /**
     * 1.ooxx值
     * 2.uuid:requestID
     * 3. DATA_len
     */

    int flag; //32bit
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
    String res;


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

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}

interface Car {
    public String ooxx(String msg);
}

interface Fly {
    public void xxoo(String msg);
}