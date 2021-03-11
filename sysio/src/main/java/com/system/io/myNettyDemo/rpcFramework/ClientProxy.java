package com.system.io.myNettyDemo.rpcFramework;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class ClientProxy {

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

                MyHeader header = createHeader(msgBody);

                out.reset();
                oout = new ObjectOutputStream(out);
                oout.writeObject(header);
                byte[] msgHeader = out.toByteArray();

                ClientFactory factory = ClientFactory.getFactory();
                NioSocketChannel clientChannel =
                        factory.getClient(new InetSocketAddress("localhost", 8888));

                ByteBuf byteBuf = PooledByteBufAllocator
                        .DEFAULT.directBuffer(msgHeader.length + msgBody.length);


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
                channelFuture.sync();

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
