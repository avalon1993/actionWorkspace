package com.system.io.myNettyDemo.rpcFramework;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class ClientResponses extends ChannelInboundHandlerAdapter {

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

            ResponseHandler.runCallBack(header.requestID);

        }

    }
}
