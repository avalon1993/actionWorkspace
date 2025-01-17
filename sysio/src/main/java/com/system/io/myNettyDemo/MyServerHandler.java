package com.system.io.myNettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class MyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //获取客户端发送过来的信息
//        ByteBuf byteBuf = (ByteBuf) msg;
//
//        System.out.println("收到客户端" +
//                ctx.channel().remoteAddress() +
//                "发送的消息：" +
//                byteBuf.toString(CharsetUtil.UTF_8));
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                    System.out.println("长时间的业务处理");

                    //获取客户端发送过来的信息
                    ByteBuf byteBuf = (ByteBuf) msg;

                    System.out.println("收到客户端" +
                            ctx.channel().remoteAddress() +
                            "发送的消息：" +
                            byteBuf.toString(CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //发送消息给客户端
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务端已收到消息，并给你发送一个问号?",
                CharsetUtil.UTF_8));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
