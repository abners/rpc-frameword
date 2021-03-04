package com.abners.nettyrpc.handler.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.model.Request;
import com.abners.nettyrpc.common.model.Response;
import com.abners.nettyrpc.handler.client.NettyClient;
import com.abners.nettyrpc.util.UUIDUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 类RpcFactory.java的实现描述：rpc代理请求工程
 *
 * @author baoxing.peng 2021年02月28日 23:48:27
 */
@Component
public class RpcBeanFactory implements InvocationHandler {

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
        Object result = nettyClient.send(request);
        if (result == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        Response response = JSON.parseObject(result.toString(), Response.class);
        if (response.getResultCode() != 1) {
            throw new Exception(response.getErrorMsg());
        }
        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)) {
            return response.getData();

        } else if (Collection.class.isAssignableFrom(returnType)) {
            return JSONArray.parseArray(response.getData().toString(), Object.class);
        } else if (Map.class.isAssignableFrom(returnType)) {
            return JSON.parseObject(response.getData().toString(), Map.class);
        } else {
            Object obj = response.getData();
            return JSONObject.parseObject(obj.toString(), returnType);
        }

    }
}
