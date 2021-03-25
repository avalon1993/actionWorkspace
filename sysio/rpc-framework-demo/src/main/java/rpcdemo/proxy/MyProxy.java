package rpcdemo.proxy;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpc.ClientFactory;
import rpc.MyContent;
import rpc.MyHeader;
import rpc.ResponseMappingHandler;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class MyProxy {

    public static <T> T proxyGet(Class<T> interfaceInfo) {

        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1. 调用服务 方法 参数 =>封装成message
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


                //2. requesetId + message,本地要缓存id
                // 协议 :[header] [body]
                MyHeader header = createHeader(msgBody);
                out.reset();
                oout = new ObjectOutputStream(out);
                oout.writeObject(header);

                //todo: 解决数据decode问题
                byte[] msgHeader = out.toByteArray();
                System.out.println(msgHeader.length);
                //3. 连接池:: 取得连接
                ClientFactory factory = ClientFactory.getFactory();
                NioSocketChannel clientChannel = factory
                        .getClient(new InetSocketAddress("localhost", 9090));


                //4. 发送 --> 走io out--> 走netty


                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(msgHeader.length + msgBody.length);

                CountDownLatch countDownLatch = new CountDownLatch(1);
                long id = header.getRequestID();

                CompletableFuture<String> res = new CompletableFuture<>();


                ResponseMappingHandler.addCallBack(id, res);


                byteBuf.writeBytes(msgHeader);
                byteBuf.writeBytes(msgBody);
                ChannelFuture channelFuture = clientChannel.writeAndFlush(byteBuf);
                channelFuture.sync(); //io是双向的，看似有个sync，它仅代表out


//                countDownLatch.await();


                //5. 如果从io回来了,怎么将代码执行到这里

                return res.get();
            }
        });
    }
}
