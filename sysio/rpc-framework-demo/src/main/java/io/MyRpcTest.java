package io;


import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 1. 写一个rpc
 * 2. 来回通信.连接数量,拆包
 * 3. 动态代理\序列化 协议封装
 * 4. 连接池
 */
public class MyRpcTest {


    //模拟consumer端
    @Test
    public void get() {
        Car car = proxyGet(Car.class); //基于动态代理
        car.ooxx("hello");
    }

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
                byte[] msgHeader = out.toByteArray();


                //3. 连接池:: 取得连接


                //4. 发送 --> 走io out
                channel.writeAndFlush(ByteBUf);


                //5. 如果从io回来了,怎么将代码执行到这里

                return null;
            }
        });
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

class ClientFactory {
    //一个consumer可以链接很多provider,每一个provider都有自己的poll
}

class ClientPool {
    NioSocketChannel[] client;
    Object[] lock;

    ClientPool(int size) {
        client = new NioSocketChannel[size];
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
    public void ooxx(String msg);
}

interface Fly {
    public void xxoo(String msg);
}