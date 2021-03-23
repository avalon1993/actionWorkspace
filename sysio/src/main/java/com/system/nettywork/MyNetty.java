package com.system.nettywork;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class MyNetty {

    @Test
    public void myByteBuf() {
        ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(8, 20);

//        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(8, 20);
        print(buf);

        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);
        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);
        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);
        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);
        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);
        System.out.println("-----------------------");
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

    }


    @Test
    public void clientMode() throws IOException, InterruptedException {
        NioEventLoopGroup thread = new NioEventLoopGroup(1);

        NioSocketChannel client = new NioSocketChannel();
        thread.register(client);
//        thread.


        Bootstrap bootstrap = new Bootstrap();
//        bootstrap.group(thread)
//                .channel(SocketChannel.class)

        ChannelFuture connect = client.connect(new InetSocketAddress("localhost", 9090));


        ByteBuf buf = Unpooled.copiedBuffer("hello server".getBytes());
        ChannelFuture send = client.writeAndFlush(buf);
        send.sync();

        send.channel().closeFuture().sync();

    }


    public static void print(ByteBuf buf) {
        System.out.println("buf.isReadable()    " + buf.isReadable());
        System.out.println("buf.readerIndex()    " + buf.readerIndex());
        System.out.println("buf.readableBytes(    " + buf.readableBytes());
        System.out.println("buf.isWritable()    " + buf.isWritable());
        System.out.println("buf.writerIndex()     " + buf.writerIndex());
        System.out.println("buf.writableBytes()     " + buf.writableBytes());
        System.out.println("buf.capacity()        " + buf.capacity());
        System.out.println("buf.maxCapacity()       " + buf.maxCapacity());
        System.out.println("buf.isDirect()       " + buf.isDirect());
    }


    @Test
    public void serverModal() {

        NioEventLoopGroup thread = new NioEventLoopGroup(1);
        NioServerSocketChannel server = new NioServerSocketChannel();

        thread.register(server);

        ChannelPipeline p = server.pipeline();
        p.addLast(new MyAcceptHandler());

        server.bind(new InetSocketAddress("localhost", 9090));


    }


}

class MyAcceptHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SocketChannel client = (SocketChannel) msg;


        SocketChannel socketChannel = (SocketChannel) ctx;

        ByteBuf buf = (ByteBuf) msg;
        super.channelRead(ctx, msg);
    }
}

class readTest extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        CharSequence str = buf.getCharSequence(0, buf.readableBytes(), CharsetUtil.UTF_8);
        System.out.println(str);
        ctx.writeAndFlush(buf);

    }
}

