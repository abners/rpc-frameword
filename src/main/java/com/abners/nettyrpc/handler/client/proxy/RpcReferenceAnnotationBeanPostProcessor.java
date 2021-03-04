package com.abners.nettyrpc.handler.client.proxy;

import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.common.RpcReference;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 类ReferenceAnnotationBeanPostProcessor.java的实现描述：注解实例变量处理
 *
 * @author baoxing.peng 2021年03月03日 17:34:46
 */
@Slf4j
@Component
public class RpcReferenceAnnotationBeanPostProcessor
        implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, DisposableBean {

    private ApplicationContext applicationContext;
    //缓存bean工厂

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void destroy() throws Exception {

    }

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.info("bean:{},beanName:{}", bean, beanName);
        Class<?> beanClass = bean.getClass();
        do {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                //对于标注RpcReference注解的属性进行代理实现
                if (field.getAnnotation(RpcReference.class) != null) {
                    field.setAccessible(true);
                    field.set(bean, applicationContext.getBean(RpcFactoryBean.class, field.getType()).getObject());
                }
            }
        } while ((beanClass = beanClass.getSuperclass()) != null);

        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
