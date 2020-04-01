package com.eugene.sumarry.zookeeper.client.starter.common;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public interface ZookeeperClient {

    // Create node part
    String createPerNode(String path) throws Exception;

    String createPerNode(String path, String content) throws Exception;

    String createPerSeqNode(String path) throws Exception;

    String createPerSeqNode(String path, String content) throws Exception;

    String createEphNode(String path) throws Exception;

    String createEphNode(String path, String content) throws Exception;

    String createEphSeqNode(String path) throws Exception;

    String createEphSeqNode(String path, String content) throws Exception;

    // Delete node part
    void deleteNode(String path) throws Exception;

    // Change node part
    Stat changeNode(String path, String content)throws Exception;

    // Get node part
    List<String> findChildren(String path) throws Exception;

    // Watch part
    default void watchDesignationNode(String path, CuratorWatcher curatorWatcher) throws Exception {

    }

    default void watchDesignationNode(String path, Watcher watcher) throws Exception {

    }


    default void watchChildrenNode(String parentPath, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {

    }

    default void watchDesignationNode(String path, IZkDataListener iZkDataListener) throws Exception {

    }

    default void watchChildrenNode(String parentPath, IZkChildListener iZkChildListener) throws Exception {

    }

    default void watchChildrenNode(String parentPath, Watcher watcher) throws Exception {

    }

    default void watchTreeCacheNode(String parentPath, TreeCacheListener treeCacheListener) throws Exception {

    }


    // Other part
    default <T> T checkExist(String path) throws Exception {
        return null;
    }

    void close() throws InterruptedException;

}
