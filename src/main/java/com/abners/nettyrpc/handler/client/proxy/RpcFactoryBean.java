package com.abners.nettyrpc.handler.client.proxy;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 类RpcFactoryBean.java的实现描述：代理工厂bean
 *
 * @author baoxing.peng 2021年03月01日 15:50:11
 */
@Component
@Scope(scopeName = "prototype")
public class RpcFactoryBean<T> implements FactoryBean {

    private Class<T> interfaceClass;

    @Autowired
    private RpcBeanFactory factory;

    public RpcFactoryBean() {

    }

    public RpcFactoryBean(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[] { interfaceClass }, factory);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceClass;
    }
}
