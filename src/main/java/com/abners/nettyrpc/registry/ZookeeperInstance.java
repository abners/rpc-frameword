package com.abners.nettyrpc.registry;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 类ZookeeperInstance.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月26日 14:27:41
 */
@Component
public class ZookeeperInstance {

    @Value("${rpc.registry.server}")
    private String zkServer;


    @Bean
    public ZkClient createClient() {
        ZkClient zkClient = new ZkClient(zkServer, 1000, 5000);
        return zkClient;
    }
}
