package com.eugene.sumarry.zookeeper.client.starter.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.Assert;

import java.util.List;

public class CuratorClient extends ZookeeperClientAdapter<CuratorFramework> {

    @Override
    public void afterPropertiesSet(Object object) {
        this.getClient().start();
    }

    private CreateBuilder createBuilder() {
        return this.getClient().create();
    }

    private String createWithMode(CreateMode mode, String path) throws Exception {
        return createBuilder().creatingParentsIfNeeded().withMode(mode).forPath(path);
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

    public String createEphSeqNode(String path) throws Exception {
        return createWithMode(CreateMode.EPHEMERAL_SEQUENTIAL, path);
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

    public String createEphSeqNode(String path, String content) throws Exception {
        return createWithMode(CreateMode.PERSISTENT_SEQUENTIAL, path, content);
    }

    // ------------------Change node part

    public SetDataBuilder getSetDataBuild() {
        return this.getClient().setData();
    }

    public Stat changeNode(String path, String content) throws Exception {
        Assert.notNull(content, path + " node content can not null");
        return getSetDataBuild().forPath(path, content.getBytes());
    }


    // -------------------Delete node part
    public void deleteNode(String path) throws Exception {
        this.getClient().delete().forPath(path);
    }

    // -------------------Get node part
    public GetDataBuilder getGetDataBuilder() {
        return this.getClient().getData();
    }

    public List<String> findChildren(String path) throws Exception {
        return this.getClient().getChildren().forPath(path);
    }

    // -------------------Watch part
    public void watchDesignationNode(String path, CuratorWatcher curatorWatcher) throws Exception {
        getGetDataBuilder().usingWatcher(curatorWatcher).forPath(path);
    }

    public void watchDesignationNode(String path, Watcher watcher) throws Exception {
        getGetDataBuilder().usingWatcher(watcher).forPath(path);
    }

    public void watchChildrenNode(String parentPath, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(this.getClient(), parentPath, true);
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public void watchTreeCacheNode(String parentPath, TreeCacheListener treeCacheListener) throws Exception {
        TreeCache treeCache = new TreeCache(getClient(), parentPath);
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }

    // -------------------Other part
    public Stat checkExist(String path) throws Exception {
        return this.getClient().checkExists().forPath(path);
    }

    @Override
    public void close() {
        getClient().close();
    }
}
