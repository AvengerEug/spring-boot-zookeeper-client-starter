package com.eugene.sumarry.zookeeper.client.starter.common;

import org.apache.curator.framework.CuratorFramework;

public class CuratorClient implements ZookeeperClient {

    private CuratorFramework curatorFramework;

    public CuratorFramework getCuratorFramework() {
        return curatorFramework;
    }

    public void setCuratorFramework(CuratorFramework curatorFramework) {
        this.curatorFramework = curatorFramework;
    }

    public void createNode(String path) {

    }

    public void deleteNode(String path) {

    }
}
