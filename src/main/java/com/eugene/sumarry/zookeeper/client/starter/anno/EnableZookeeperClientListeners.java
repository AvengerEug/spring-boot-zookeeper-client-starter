package com.eugene.sumarry.zookeeper.client.starter.anno;

import com.eugene.sumarry.zookeeper.client.starter.event.listener.CuratorClientInitListener;
import com.eugene.sumarry.zookeeper.client.starter.event.listener.NativeClientInitListener;
import com.eugene.sumarry.zookeeper.client.starter.event.listener.ZKClientInitListener;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这里采用注解的方式将三个监听器添加至spring容器
 * 因为要通过构造方法依赖注入，所以不建议使用springboot的spring.factories特性
 * 这种特性不会走构造方法依赖注入
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        NativeClientInitListener.class,
        ZKClientInitListener.class,
        CuratorClientInitListener.class
})
public @interface EnableZookeeperClientListeners {
}
