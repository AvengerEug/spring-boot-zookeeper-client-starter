package com.eugene.sumarry.zookeeper.client.starter.register;

import com.eugene.sumarry.zookeeper.client.starter.anno.EnableZookeeperClient;
import com.eugene.sumarry.zookeeper.client.starter.basic.ZookeeperClient;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ZookeeperClientRegistrar implements ImportBeanDefinitionRegistrar {

    private final static String ZOOKEEPER_CLIENT_BEAN = "zookeeperClient";

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (importingClassMetadata.hasAnnotation(EnableZookeeperClient.class.getName())) {
            GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(ZookeeperClient.class);
            genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

            registry.registerBeanDefinition(ZOOKEEPER_CLIENT_BEAN, genericBeanDefinition);
        }
    }
}
