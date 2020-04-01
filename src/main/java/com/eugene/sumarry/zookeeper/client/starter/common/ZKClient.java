package com.eugene.sumarry.zookeeper.client.starter.common;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class ZKClient extends ZookeeperClientAdapter<org.I0Itec.zkclient.ZkClient> {

    public String createNode(String path) throws Exception {
        getClient().writeData(path, "".getBytes());
        return null;
    }

    public void deleteNode(String path) throws Exception {
        getClient().delete(path);
    }

    public String createPerNode(String path) throws Exception {
        getClient().createPersistent(path, "".getBytes());
        return null;
    }

    public String createPerNode(String path, String content) throws Exception {
        getClient().createPersistent(path, content);
        return null;
    }

    public String createPerSeqNode(String path) throws Exception {
        getClient().createPersistentSequential(path, "".getBytes());
        return null;
    }

    public String createPerSeqNode(String path, String content) throws Exception {
        getClient().createPersistentSequential(path, content);
        return null;
    }

    public String createEphNode(String path) throws Exception {
        getClient().createEphemeral(path, "".getBytes());
        return null;
    }

    public String createEphNode(String path, String content) throws Exception {
        getClient().createEphemeral(path, "".getBytes());
        return null;
    }

    public String createEphSeqNode(String path) throws Exception {
        getClient().createEphemeralSequential(path, "".getBytes());
        return null;
    }

    public String createEphSeqNode(String path, String content) throws Exception {
        getClient().createEphemeralSequential(path, content);
        return null;
    }

    public Stat changeNode(String path, String content) throws Exception {
        getClient().writeData(path, "".getBytes(), -1);
        return null;
    }

    public List<String> findChildren(String path) throws Exception {
        return getClient().getChildren(path);
    }

    public void watchDesignationNode(String path, IZkDataListener iZkDataListener) throws Exception {
        getClient().subscribeDataChanges(path, iZkDataListener);
    }

    public void watchChildrenNode(String parentPath, IZkChildListener iZkChildListener) throws Exception {
        getClient().subscribeChildChanges(parentPath, iZkChildListener);
    }

    public Boolean checkExist(String path) throws Exception {
        return getClient().exists(path);
    }

    @Override
    public void close() throws InterruptedException {
        getClient().close();
    }
}
