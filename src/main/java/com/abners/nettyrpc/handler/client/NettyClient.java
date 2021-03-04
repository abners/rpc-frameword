package com.abners.nettyrpc.handler.client;

import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.coder.JSONDecoder;
import com.abners.nettyrpc.common.coder.JSONEncoder;
import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.common.model.Response;
import com.abners.nettyrpc.handler.client.connection.ConnectManage;
import com.alibaba.fastjson.JSON;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 类NettyClient.java的实现描述：
 *
 * @author baoxing.peng 2021年02月28日 15:25:39
 */
@Component
@Slf4j
@Order(2)
public class NettyClient {

    private NioEventLoopGroup group     = new NioEventLoopGroup(1);
    private Bootstrap         bootstrap = new Bootstrap();
    @Autowired
    private NettClientHandler nettClientHandler;

    @Autowired
    private ConnectManage connectManage;

    public NettyClient() {
        bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).option(
                ChannelOption.SO_KEEPALIVE, true).handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //创建NIOSocketChannel成功后，在进行初始化时，
                // 将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件

                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 0, 30)).addLast(new JSONEncoder()).addLast(
                        new JSONDecoder()).addLast("handler", nettClientHandler);
            }
        });
    }

    @PreDestroy
    public void destroy() {
        log.info("client exit,release resource");
        group.shutdownGracefully();
    }

    public Object send(Request request) throws InterruptedException {
        Channel channel = connectManage.chooseChannel();
        if (channel != null && channel.isActive()) {
            SynchronousQueue queue = nettClientHandler.sendRequest(request, channel);
            Object result = queue.take();
            return JSON.toJSONString(result);
        } else {
            Response re = new Response();
            re.setResultCode(500);
            re.setErrorMsg("connect to server error,please check your config");
            return JSON.toJSONString(re);
        }

    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        Channel channel = future.sync().channel();
        return channel;
    }
}
