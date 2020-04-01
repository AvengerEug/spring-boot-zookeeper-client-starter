package com.eugene.sumarry.zookeeper.client.starter.common;

public interface Constants {

    String CURATOR_FRAMEWORK_FACTORY_CLASS = "org.apache.curator.framework.CuratorFrameworkFactory";

    String ZOOKEEPER_CLIENT_CLASS = "org.apache.zookeeper.ZooKeeper";

    String ZK_CLIENT_CLASS = "org.I0Itec.zkclient.ZkClient";

    String RETRY_NUMBER_TIMES = "org.apache.curator.retry.RetryNTimes";

    String RETRY_POLICY = "org.apache.curator.RetryPolicy";

    String ZOOKEEPER_HOST = "zookeeper.host";

    // Curator client parameters
    String ZOOKEEPER_CURATOR_RETRY_TIME = "zookeeper.curator.retry-time";

    String ZOOKEEPER_CURATOR_SLEEP_MS_BETWEEN_RETRIES = "zookeeper.curator.sleep-ms-between-retries";

    // ZKClient parameters
    String ZOOKEEPER_ZK_CLIENT_CONNECTION_TIME_OUT = "zookeeper.zk-client.connection-timeout";
    String ZOOKEEPER_ZK_CLIENT_SESSION_TIME = "zookeeper.zk-client.session-time";
    String ZOOKEEPER_ZK_SERIALIZER_CLASS = "zookeeper.zk-client.zk-serializer-class";

    // Native client parameters
    String ZOOKEEPER_NATIVE_CLIENT_SESSTION_TIME_OUT = "zookeeper.native-client.session-time";
    String ZOOKEEPER_NATIVE_WATCHER_CLASS = "zookeeper.native-client.watcher-class";

}
