package com.eugene.sumarry.zookeeper.client.starter.common;

public interface Constants {

    String CURATOR_FRAMEWORK_FACTORY_CLASS = "org.apache.curator.framework.CuratorFrameworkFactory";

    String ZOOKEEPER_CLIENT_CLASS = "org.apache.zookeeper.ZooKeeper";

    String ZK_CLIENT_CLASS = "org.I0Itec.zkclient.ZkClient";

    String RETRY_NUMBER_TIMES = "org.apache.curator.retry.RetryNTimes";

    String RETRY_POLICY = "org.apache.curator.RetryPolicy";

    String ZOOKEEPER_HOST = "zookeeper.host";

    String ZOOKEEPER_CURATOR_RETRY_TIME = "zookeeper.curator.retryTime";

    String ZOOKEEPER_CURATOR_SLEEP_MS_BETWEEN_RETRIES = "zookeeper.curator.sleepMsBetweenRetries";
}
