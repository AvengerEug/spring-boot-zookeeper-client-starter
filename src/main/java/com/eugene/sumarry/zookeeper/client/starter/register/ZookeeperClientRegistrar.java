package com.eugene.sumarry.zookeeper.client.starter.register;

import com.eugene.sumarry.zookeeper.client.starter.anno.EnableZookeeperClient;
import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class ZookeeperClientRegistrar implements ImportBeanDefinitionRegistrar {

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (importingClassMetadata.hasAnnotation(EnableZookeeperClient.class.getName())) {
            GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(ZookeeperClientFactoryBean.class);
            genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            // 保证zookeeperClient不使用spring来自动装配
            genericBeanDefinition.getPropertyValues().add("zookeeperClient", null);

            registry.registerBeanDefinition("zookeeperClientProcessor", genericBeanDefinition);
        }
    }
}
