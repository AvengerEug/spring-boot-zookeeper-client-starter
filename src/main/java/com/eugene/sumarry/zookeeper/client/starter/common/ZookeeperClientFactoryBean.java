package com.eugene.sumarry.zookeeper.client.starter.common;

import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import com.eugene.sumarry.zookeeper.client.starter.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

public class ZookeeperClientFactoryBean implements InitializingBean, FactoryBean<ZookeeperClient> {

    private final Logger logger = LoggerFactory.getLogger(ZookeeperClientFactoryBean.class);

    private ZookeeperClient zookeeperClient;

    private Environment environment;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public ZookeeperClient getObject() throws Exception {
        return zookeeperClient;
    }

    /**
     * 在ZookeeperClientRegistrar中指定了此属性不交给spring注入
     * 否则的话会报错:
     *
         The dependencies of some of the beans in the application context form a cycle:

         ┌─────┐
         |  zookeeperClientProcessor
         └─────┘
     * TODO 待确认此错误的含义
     */
    public void setZookeeperClient(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    public Class<?> getObjectType() {
        return ZookeeperClient.class;
    }

    public void afterPropertiesSet() throws Exception {

        SpringContextHolder.getApplicationContext().publishEvent(new ZookeeperClientInitEvent(this));

        if (ObjectUtils.isEmpty(zookeeperClient)) {
            logger.error("No zookeeper client found, initialize failed.");
            throw new RuntimeException("No zookeeper client found, initialize failed.");
        }
    }

}