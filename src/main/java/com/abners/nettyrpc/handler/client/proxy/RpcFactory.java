package com.abners.nettyrpc.handler.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;

import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.handler.client.NettyClient;
import com.abners.nettyrpc.util.UUIDUtil;

/**
 * 类RpcFactory.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月28日 23:48:27
 */

public class RpcFactory implements InvocationHandler {

    @Autowired
    NettyClient nettyClient;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setId(UUIDUtil.uuid());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        nettyClient.send(request);
        return null;
    }
}
