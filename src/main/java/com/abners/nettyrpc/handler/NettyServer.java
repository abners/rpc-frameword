package com.abners.nettyrpc.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.RpcService;
import com.abners.nettyrpc.common.coder.JSONDecoder;
import com.abners.nettyrpc.common.coder.JSONEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 类NettyServer.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月24日 17:40:37
 */
@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final Logger              logger      = LoggerFactory.getLogger(NettyServer.class);
    private static final EventLoopGroup      bossGroup   = new NioEventLoopGroup(1);
    private static final EventLoopGroup      workerGroup = new NioEventLoopGroup(4);
    private              Map<String, Object> serviceMap  = new HashMap<>();

    @Value("${rpc.server.address}")
    private String serverAddress;

    @Override
    public void afterPropertiesSet() throws Exception {
        final NettyServerHandler serverHandler = new NettyServerHandler(serviceMap);
        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();

                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(
                        ChannelOption.SO_BACKLOG, 1024).
                                 childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY,
                                                                                           true).childHandler(
                        new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                ChannelPipeline pipeline = channel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(serverHandler);
                            }
                        });

                String[] array = serverAddress.split(":");
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                ChannelFuture cf = bootstrap.bind(host, port).sync();
                logger.info("rpc service start complete");
                cf.channel().closeFuture().sync();
            } catch (Exception e) {
                logger.error("socket failed,", e);
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(RpcService.class);

        for (Object service : serviceBeans.values()) {
            Class<?> cl = service.getClass();
            Class<?>[] interfaces = cl.getInterfaces();
            for (Class<?> inter : interfaces) {
                String interName = inter.getName();
                logger.info("加载服务类：{}", interName);
                serviceMap.put(interName, service);
            }
        }
        logger.info("已加载全部服务类....");
    }
}
