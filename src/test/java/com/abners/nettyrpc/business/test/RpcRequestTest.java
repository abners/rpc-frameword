package com.abners.nettyrpc.business.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.abners.nettyrpc.business.consumer.RequestService;
import com.abners.nettyrpc.main.Boot;

/**
 * 类RpcRequestTest.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年03月02日 15:22:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Boot.class)
public class RpcRequestTest {

    @Autowired
    RequestService requestService;

    @Test
    public void testConsume() {
        requestService.sendAReuest();
    }
}
