package com.eugene.sumarry.zookeeper.client.starter.common;

public abstract class ZookeeperClientAdapter<T> implements ZookeeperClient {

    private T t;

    public T getClient() {
        return t;
    }

    public void setClient(T t) {
        this.t = t;
    }

    public abstract String createNode(String path) throws Exception;

    public abstract void deleteNode(String path) throws Exception;

    public void afterPropertiesSet() {

    }
}
