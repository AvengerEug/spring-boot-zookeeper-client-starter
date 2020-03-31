package com.eugene.sumarry.zookeeper.client.starter.event.listener;

import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Order(3)
public class NativeClientInitListener implements ApplicationListener<ZookeeperClientInitEvent> {

    private Logger logger = LoggerFactory.getLogger(NativeClientInitListener.class);

    private Environment environment;

    public NativeClientInitListener(Environment environment) {
        this.environment = environment;
    }

    public void onApplicationEvent(ZookeeperClientInitEvent event) {
        Object resource = event.getSource();
        if (resource instanceof ZookeeperClientFactoryBean) {
            logger.error("NativeClientInitListener");
        }
    }
}
