package com.system.io.myNettyDemo.rpcFramework;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

public class ServerRequestHandler extends ChannelInboundHandlerAdapter {

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
            System.out.println("----------------------ServerRequestHandler header start-----------------");
            System.out.println(header.dataLen);
            System.out.println(header.requestID);
            System.out.println("----------------------ServerRequestHandler header-----------------");

            if (buf.readableBytes() >= header.getDataLen()) {
                byte[] data = new byte[(int) header.getDataLen()];

                buf.readBytes(data);
                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);





            }
        }


    }
}
