package com.abners.nettyrpc.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 类RpcService.java的实现描述：使用该注解后相应接口将会发布为RPC Service
 *
 * @author baoxing.peng 2021年02月24日 17:43:03
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

    String value() default "";
}
