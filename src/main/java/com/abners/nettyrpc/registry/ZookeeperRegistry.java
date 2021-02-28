package com.abners.nettyrpc.registry;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 类ZookeeperRegistry.java的实现描述：zk服务注册
 *
 * @author baoxing.peng 2021年02月26日 11:42:34
 */
@Component
@Slf4j
public class ZookeeperRegistry implements Registry {

    @Value("rpc.registry.server")
    private String   registryServer;
    @Autowired
    private ZkClient zkClient;

    private final String REGISTRY_ROOT_PATH = "/rpc";

    public void registry(String dataPath) {
        if (!zkClient.exists(REGISTRY_ROOT_PATH)) {
            zkClient.createPersistent(REGISTRY_ROOT_PATH);
            log.info("create registry root path:{}", REGISTRY_ROOT_PATH);
        }
        String path = zkClient.create(REGISTRY_ROOT_PATH + "/provider", dataPath, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                      CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("export service success,path:{}=>{}", path, dataPath);
    }
}
