package com.abners.nettyrpc.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 类RpcService.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月24日 17:43:03
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {

}
