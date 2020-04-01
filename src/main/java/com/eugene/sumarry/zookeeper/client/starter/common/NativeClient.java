package com.eugene.sumarry.zookeeper.client.starter.common;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.Assert;

import java.util.List;


public class NativeClient extends ZookeeperClientAdapter<ZooKeeper> {


    private String createBaseNode(String path, String content, CreateMode createMode) throws Exception {
        Assert.notNull(content, "Node content not allow null");
        return getClient().create(path, content.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    public String createNode(String path) throws Exception {
        return createBaseNode(path, "", CreateMode.PERSISTENT);
    }

    public String createPerNode(String path) throws Exception {
        return createNode(path);
    }

    public String createPerSeqNode(String path) throws Exception {
        return createBaseNode(path, "", CreateMode.PERSISTENT_SEQUENTIAL);
    }

    public String createEphNode(String path) throws Exception {
        return createBaseNode(path, "", CreateMode.EPHEMERAL);
    }

    public String createEphSeqNode(String path) throws Exception {
        return createBaseNode(path, "", CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public String createPerNode(String path, String content) throws Exception {
        return createBaseNode(path, content, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public String createPerSeqNode(String path, String content) throws Exception {
        return createBaseNode(path, content, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    public String createEphNode(String path, String content) throws Exception {
        return createBaseNode(path, content, CreateMode.EPHEMERAL);
    }

    public String createEphSeqNode(String path, String content) throws Exception {
        return createBaseNode(path, content, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public Stat changeNode(String path, String content) throws Exception {
        return getClient().setData(path, content.getBytes(), -1);
    }

    public List<String> findChildren(String path) throws Exception {
        return getClient().getChildren(path, false);
    }

    public void watchDesignationNodeUsingNative(String path, Watcher watcher) throws Exception {
        getClient().getData(path, watcher, null);
    }

    public void watchChildrenNodeUsingNative(String parentPath, Watcher watcher) throws Exception {
        getClient().getChildren(parentPath, watcher);
    }

    public Stat checkExist(String path) throws Exception {
        return getClient().exists(path, false);
    }

    public void deleteNode(String path) throws Exception {
        getClient().delete(path, -1);
    }

    @Override
    public void close() throws InterruptedException {
        getClient().close();
    }
}
