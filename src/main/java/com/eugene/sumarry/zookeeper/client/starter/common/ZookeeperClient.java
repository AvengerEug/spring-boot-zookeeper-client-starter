package com.eugene.sumarry.zookeeper.client.starter.common;

public interface ZookeeperClient {

    String createPerNode(String path) throws Exception;

    String createPerNode(String path, String content) throws Exception;

    String createPerSeqNode(String path) throws Exception;

    String createPerSeqNode(String path, String content) throws Exception;

    String createEphNode(String path) throws Exception;

    String createEphNode(String path, String content) throws Exception;

    String createPerEphNode(String path) throws Exception;

    String createPerEphNode(String path, String content) throws Exception;

    void deleteNode(String path) throws Exception;

}
