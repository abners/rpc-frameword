package com.abners.nettyrpc.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 类Response.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月24日 23:26:38
 */
@Setter
@Getter
public class Response {

    String requestId;
    private Integer resultCode;
    private String  errorMsg;
    private Object  data;
}
