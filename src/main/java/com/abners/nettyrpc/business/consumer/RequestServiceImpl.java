package com.abners.nettyrpc.business.consumer;

import org.springframework.stereotype.Service;

import com.abners.nettyrpc.business.provider.RequestHandlerService;
import com.abners.nettyrpc.common.RpcReference;

import lombok.extern.slf4j.Slf4j;

/**
 * 类RequestServiceImpl.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年03月02日 15:19:36
 */
@Service("requestService")
@Slf4j
public class RequestServiceImpl implements RequestService {

    @RpcReference
    private RequestHandlerService requestHandlerService;

    @Override
    public void sendAReuest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String a = requestHandlerService.sendReq("jsdifsdf");
        log.info("receive resp:{}", a);
    }
}
