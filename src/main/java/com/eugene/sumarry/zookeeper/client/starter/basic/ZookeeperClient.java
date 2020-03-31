package com.eugene.sumarry.zookeeper.client.starter.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ZookeeperClient implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    private Object client;

    private final String CURATOR_FRAMEWORK_FACTORY_CLASS = "org.apache.curator.framework.CuratorFrameworkFactory";

    private final String ZOOKEEPER_CLIENT_CLASS = "org.apache.zookeeper.ZooKeeper";

    private final String ZK_CLIENT_CLASS = "org.I0Itec.zkclient.ZkClient";

    private Environment environment;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void afterPropertiesSet() throws Exception {
        Class clazz = null;
        Constructor constructor = null;
        // zookeeper客户端加载顺序
        try {
            logger.info("Try to load class {}", CURATOR_FRAMEWORK_FACTORY_CLASS);
            clazz = Class.forName(CURATOR_FRAMEWORK_FACTORY_CLASS);
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            Class retryNTimesClass = Class.forName("org.apache.curator.retry.RetryNTimes");
            Constructor retryNTimesConstructor = retryNTimesClass.getDeclaredConstructor(int.class, int.class);
            int retryTime = environment.getProperty("zookeeper.curator.retryTime", int.class);
            int sleepMsBetweenRetries = environment.getProperty("zookeeper.curator.sleepMsBetweenRetries", int.class);

            Object retryNTimesObject = retryNTimesConstructor.newInstance(retryTime, sleepMsBetweenRetries);

            Method newClientMethod = clazz.getMethod("newClient", String.class, Class.forName("org.apache.curator.RetryPolicy"));
            client = newClientMethod.invoke(instance, environment.getProperty("zookeeper.host"), retryNTimesObject);

        } catch (ClassNotFoundException e) {
            logger.warn("Not found class {}. Try to load {}", CURATOR_FRAMEWORK_FACTORY_CLASS, ZOOKEEPER_CLIENT_CLASS);
            try {
                logger.info("Try to load class {}", ZOOKEEPER_CLIENT_CLASS);
                clazz = Class.forName(ZOOKEEPER_CLIENT_CLASS);
            } catch (ClassNotFoundException e1) {
                logger.warn("Not found class {}. Try to load {}", ZOOKEEPER_CLIENT_CLASS, ZK_CLIENT_CLASS);
                try {
                    logger.info("Try to load class {}", ZK_CLIENT_CLASS);
                    clazz = Class.forName(ZK_CLIENT_CLASS);
                } catch (ClassNotFoundException e2) {
                    logger.warn("Not found class {}. ", ZK_CLIENT_CLASS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (clazz == null) {
                logger.warn("Zookeeper client initializer failed");
                throw new RuntimeException("Zookeeper client initializer failed");
            }
            logger.info("Current zookeeper client is {} ", client);
        }
    }
}