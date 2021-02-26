package com.abners.nettyrpc.handler;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.common.model.Response;
import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 类NettyServerHandler.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月24日 18:31:11
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger              logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端连接：{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = JSON.parseObject(msg.toString(), Request.class);
        if (request.getMethodName().equals("heartBeart")) {
            logger.info("client heart beat,ip:{}", ctx.channel().remoteAddress());
        } else {
            logger.info("recive client request,requestId:{},className:{},methodName:{}", request.getId(),
                        request.getClassName(), request.getMethodName());
            Response response = new Response();
            response.setRequestId(request.getId());
            try {
                Object result = this.handler(request);
                response.setData(result);
            } catch (Exception e) {
                response.setErrorMsg(e.getMessage());
                response.setResultCode(500);
                logger.error("Rpc request failed,requestId:{},invoke className:{},methodName:{},exception:{}",
                             request.getId(), request.getClassName(), request.getMethodName(), e);
            }
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 通过反射调用业务接口
     *
     * @param request
     * @return
     * @throws Exception
     */
    private Object handler(Request request) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Object bean = serviceMap.get(className);
        if (bean != null) {

            Class cl = bean.getClass();
            Object[] parameters = request.getParameters();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Method method = cl.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(bean, getParametes(parameters, parameterTypes));
        }
        throw new Exception("service interface can't be find，please check your config，className:" + className);

    }

    /**
     * 组装参数
     *
     * @param parameters
     * @param parameterTypes
     * @return
     */
    private Object[] getParametes(Object[] parameters, Class<?>[] parameterTypes) {
        if (parameters == null || parameters.length == 0) {
            return parameters;
        }
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            objects[i] = JSON.parseObject(parameters[i].toString(), parameterTypes[i]);
        }
        return objects;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                logger.info("client is idle more than 60 seconds,close the connection,{} ",
                            ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    /**
     * Calls {@link ChannelHandlerContext#fireChannelInactive()} to forward
     * Sub-classes may override this method to change behavior.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端断开连接:{}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }
}
