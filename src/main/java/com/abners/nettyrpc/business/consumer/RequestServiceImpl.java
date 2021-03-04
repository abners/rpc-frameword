package com.abners.nettyrpc.business.consumer;

import org.springframework.stereotype.Service;

import com.abners.nettyrpc.business.provider.RequestHandlerService;
import com.abners.nettyrpc.common.RpcReference;

import lombok.SneakyThrows;
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

    @SneakyThrows
    @Override
    public void sendRequest(String akx) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            String x = akx + ",,,,," + thread.getName();
            String a = requestHandlerService.sendReq(x);
            log.info("receive resp:{},{}", a, a.equals(x));
//            Thread.sleep(10000);
        }
    }
}
