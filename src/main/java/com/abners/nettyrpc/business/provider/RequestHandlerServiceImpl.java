package com.abners.nettyrpc.business.provider;

import com.abners.nettyrpc.common.RpcService;

import lombok.extern.slf4j.Slf4j;

/**
 * 类RequestHandlerServiceImpl.java的实现描述：
 *
 * @author baoxing.peng 2021年02月24日 17:32:32
 */
@RpcService("requestHandlerService")
@Slf4j
public class RequestHandlerServiceImpl implements RequestHandlerService {

    @Override
    public String sendReq(String param) {
        log.info("receive req:{}", param);
        return "abs";
    }
}
