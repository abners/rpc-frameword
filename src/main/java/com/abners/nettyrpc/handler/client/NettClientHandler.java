package com.abners.nettyrpc.handler.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.common.model.Response;
import com.abners.nettyrpc.handler.client.connection.ConnectManage;
import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 类NettClientHandler.java的实现描述：客户端处理类
 *
 * @author baoxing.peng 2021年02月26日 17:15:29
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class NettClientHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ConnectManage connectManage;
    private ConcurrentMap<String, SynchronousQueue> queueMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext context) {
        log.info("connect to server:{} successful", context.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("与RPC服务器=>{}断开连接", address);
        ctx.channel().close();
        connectManage.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSON.parseObject(msg.toString(), Response.class);
        String requestId = response.getRequestId();
        SynchronousQueue queue = queueMap.get(requestId);
        queue.put(response);
        //
        queueMap.remove(requestId);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("所有连接空闲，发送心跳");
                Request request = new Request();
                request.setMethodName("heartBeat");
                ctx.channel().writeAndFlush(request);
            }

        }
    }

    public SynchronousQueue<Object> sendRequest(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

}
