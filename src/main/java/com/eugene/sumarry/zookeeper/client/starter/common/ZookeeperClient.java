package com.eugene.sumarry.zookeeper.client.starter.common;

public interface ZookeeperClient {

    void createNode(String path);

    void deleteNode(String path);

}
