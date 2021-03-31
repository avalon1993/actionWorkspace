package com.viewscenes.netsupervisor.server;

import com.viewscenes.netsupervisor.annotation.RpcService;
import com.viewscenes.netsupervisor.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    private Map<String, Object> serviceMap = new HashMap<>();

    @Value("${rpc.server.address}")
    private String serverAddress;

    @Autowired
    ServiceRegistry registry;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);

        for (Object serviceBean : beans.values()) {
            Class<?> clazz = serviceBean.getClass();

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> inter : interfaces) {
                String interfaceName = inter.getName();
                logger.info("加载服务类：{}", interfaceName);
                serviceMap.put(interfaceName, serviceBean);
            }
        }
        logger.info("已加载全部服务接口:{}", serviceMap);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public void start() {

//        final  NettyServerHandler handler = new NettyServerHandler(serviceMap);
        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {

                            }
                        });
                String[] array = serverAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);

                ChannelFuture cf = bootstrap.bind(host, port).sync();
                logger.info("RPC 服务器启动.监听端口:" + port);

                registry.register(serverAddress);

                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }


        }).start();
    }
}
