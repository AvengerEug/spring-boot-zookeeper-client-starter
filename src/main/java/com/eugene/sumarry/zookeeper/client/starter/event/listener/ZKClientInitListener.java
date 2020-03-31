package com.eugene.sumarry.zookeeper.client.starter.event.listener;

import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;


@Order(2)
public class ZKClientInitListener implements ApplicationListener<ZookeeperClientInitEvent> {

    private Logger logger = LoggerFactory.getLogger(ZKClientInitListener.class);

    private Environment environment;

    public ZKClientInitListener(Environment environment) {
        this.environment = environment;
    }

    public void onApplicationEvent(ZookeeperClientInitEvent event) {
        Object resource = event.getSource();
        if (resource instanceof ZookeeperClientFactoryBean) {
            logger.error("ZKClientInitListener");
        }
    }
}
