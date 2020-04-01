package com.eugene.sumarry.zookeeper.client.starter.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperAutoConfiguration {

    private String host = "127.0.0.1:2181";

    private Curator curator = new Curator();

    private ZKClient zkClient = new ZKClient();

    private NativeClient nativeClient = new NativeClient();

    public static class Curator {
        private int sleepMsBetweenRetries = 10000;
        private int retryTime = 2;

        public int getSleepMsBetweenRetries() {
            return sleepMsBetweenRetries;
        }

        public void setSleepMsBetweenRetries(int sleepMsBetweenRetries) {
            this.sleepMsBetweenRetries = sleepMsBetweenRetries;
        }

        public int getRetryTime() {
            return retryTime;
        }

        public void setRetryTime(int retryTime) {
            this.retryTime = retryTime;
        }
    }

    public static class ZKClient {
        private int sessionTime = 10000;

        private int connectionTimeout = 10000;

        private String zkSerializerClass;

        public int getSessionTime() {
            return sessionTime;
        }

        public void setSessionTime(int sessionTime) {
            this.sessionTime = sessionTime;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public String getZkSerializerClass() {
            return zkSerializerClass;
        }

        public void setZkSerializerClass(String zkSerializerClass) {
            this.zkSerializerClass = zkSerializerClass;
        }
    }

    public static class NativeClient {
        private int sessionTime = 10000;
        private String watcherClass;

        public int getSessionTime() {
            return sessionTime;
        }

        public void setSessionTime(int sessionTime) {
            this.sessionTime = sessionTime;
        }

        public String getWatcherClass() {
            return watcherClass;
        }

        public void setWatcherClass(String watcherClass) {
            this.watcherClass = watcherClass;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Curator getCurator() {
        return this.curator;
    }

    public ZKClient getZkClient() {
        return this.zkClient;
    }

    public NativeClient getNativeClient() {
        return nativeClient;
    }
}
