package com.system.io.myNettyDemo.rpcFramework;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class ServerStart {

    public static void main(String[] args) {
        System.out.println("server accept cliet port");

        NioEventLoopGroup boss = new NioEventLoopGroup(1);

        NioEventLoopGroup worker = boss;
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture bind = bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        System.out.println("server accept cliet port:" +
                                nioSocketChannel.remoteAddress().getPort());

                        ChannelPipeline channelPipeline = nioSocketChannel.pipeline();
                        channelPipeline.addLast(new ServerRequestHandler());
                    }
                }).bind(new InetSocketAddress("localhost", 9090));


        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
