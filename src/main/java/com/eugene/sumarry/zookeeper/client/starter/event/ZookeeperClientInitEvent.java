package com.eugene.sumarry.zookeeper.client.starter.event;

import org.springframework.context.ApplicationEvent;

public class ZookeeperClientInitEvent extends ApplicationEvent {

    public ZookeeperClientInitEvent(Object source) {
        super(source);
    }
}
