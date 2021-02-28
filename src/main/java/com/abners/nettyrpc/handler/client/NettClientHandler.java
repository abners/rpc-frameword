package com.abners.nettyrpc.handler.client;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;

import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.common.model.Response;
import com.abners.nettyrpc.handler.client.connection.ConnectManage;
import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 类NettClientHandler.java的实现描述：客户端处理类
 *
 * @author baoxing.peng 2021年02月26日 17:15:29
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class NettClientHandler extends ChannelOutboundHandlerAdapter {

    ConnectManage connectManage;
    private ConcurrentMap<String, SynchronousQueue> queueMap = new ConcurrentHashMap<>();

    public void channelActive(ChannelHandlerContext context) {
        log.info("connect to server:{} successful", context.channel().remoteAddress());
    }

    public void channelInActive(ChannelHandlerContext ctx) {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("与RPC服务器断开连接." + address);
        ctx.channel().close();
        connectManage.removeChannel(ctx.channel());
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSON.parseObject(msg.toString(), Response.class);
        String requestId = response.getRequestId();
        SynchronousQueue queue = queueMap.get(requestId);
        queue.put(response);
        //
        queueMap.remove(requestId);

    }

    public SynchronousQueue<Object> sendRequest(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

}
