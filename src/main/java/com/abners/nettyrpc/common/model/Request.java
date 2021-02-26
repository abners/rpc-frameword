package com.abners.nettyrpc.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 类Request.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月24日 23:19:00
 */
@Getter
@Setter
public class Request {

    private Object[]   parameters;
    private Class<?>[] parameterTypes;
    private String     id;
    private String     className;
    private String     methodName;
}
