package com.eugene.sumarry.zookeeper.client.starter;

import com.eugene.sumarry.zookeeper.client.starter.anno.EnableZookeeperClient;
import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClient;
import com.eugene.sumarry.zookeeper.client.starter.utils.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZookeeperClient
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class);

        System.out.println(SpringContextHolder.getBean(ZookeeperClient.class));
    }
}
