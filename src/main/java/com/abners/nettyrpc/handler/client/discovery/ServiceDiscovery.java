package com.abners.nettyrpc.handler.client.discovery;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.abners.nettyrpc.handler.client.connection.ConnectManage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 类ServiceDiscovery.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年03月02日 10:21:15
 */
@Component
@Slf4j
@Order(1)
public class ServiceDiscovery {

    private static final String        ZK_REGISTRY_PATH = "/rpc";
    @Value("${rpc.registry.server}")
    private              String        registryAddress;
    @Autowired
    private              ConnectManage connectManage;
    private volatile     List<String>  addressList      = new ArrayList<>();

    private ZkClient zkClient;

    @PostConstruct
    public void init() {
        zkClient = connectServer();
        if (zkClient != null) {
            watchNode(zkClient);
        }
    }

    private void watchNode(final ZkClient client) {
        List<String> nodeList = client.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
            log.info("listened child node change,info:{}", JSON.toJSONString(nodes));
            addressList.clear();

            getNodeData(nodes);
            updateConnectServer();
        });

        log.info("discovery service list...{}", JSONObject.toJSONString(addressList));
        getNodeData(nodeList);
        updateConnectServer();
    }

    private void updateConnectServer() {
        connectManage.updateConnectServer(addressList);
    }

    /**
     * 获取节点数据
     *
     * @param nodes
     */
    private void getNodeData(List<String> nodes) {
        for (String node : nodes) {
            String address = zkClient.readData(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
        }
    }

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress, 4000, 4000);

        return client;
    }
}
