package com.eugene.sumarry.zookeeper.client.starter.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperAutoConfiguration {

    private String host = "127.0.0.1:2181";

    private Curator curator = new Curator();

    private ZKClient zkClient = new ZKClient();

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

        public int getSessionTime() {
            return sessionTime;
        }

        public void setSessionTime(int sessionTime) {
            this.sessionTime = sessionTime;
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
}
