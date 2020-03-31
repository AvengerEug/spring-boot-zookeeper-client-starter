package com.eugene.sumarry.zookeeper.client.starter.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.zookeeper.CreateMode;
import org.springframework.util.Assert;

public class CuratorClient extends ZookeeperClientAdapter<CuratorFramework> {

    @Override
    public void afterPropertiesSet() {
        this.getClient().start();
    }

    private CreateBuilder createBuilder() {
        return this.getClient().create();
    }

    private String createWithMode(CreateMode mode, String path) throws Exception {
        return createBuilder().withMode(mode).forPath(path);
    }

    private String createWithMode(CreateMode mode, String path, String content) throws Exception {
        Assert.notNull(content, path + " node content can not null");
        return createBuilder().withMode(mode).forPath(path, content.getBytes());
    }

    public String createNode(String path) throws Exception {
        return createWithMode(CreateMode.EPHEMERAL, path);
    }

    public String createPerNode(String path) throws Exception {
        return createWithMode(CreateMode.PERSISTENT, path);
    }

    public String createPerSeqNode(String path) throws Exception {
        return createWithMode(CreateMode.PERSISTENT_SEQUENTIAL, path);
    }

    public String createEphNode(String path) throws Exception {
        return createWithMode(CreateMode.EPHEMERAL, path);
    }

    public String createPerEphNode(String path) throws Exception {
        return createWithMode(CreateMode.EPHEMERAL_SEQUENTIAL, path);
    }

    public void deleteNode(String path) throws Exception {
        this.getClient().delete().forPath(path);
    }

    public String createPerNode(String path, String content) throws Exception {
        return createWithMode(CreateMode.PERSISTENT, path, content);
    }

    public String createPerSeqNode(String path, String content) throws Exception {
        return createWithMode(CreateMode.PERSISTENT_SEQUENTIAL, path, content);
    }

    public String createEphNode(String path, String content) throws Exception {
        return createWithMode(CreateMode.EPHEMERAL, path, content);
    }

    public String createPerEphNode(String path, String content) throws Exception {
        return createWithMode(CreateMode.PERSISTENT_SEQUENTIAL, path, content);
    }

}
