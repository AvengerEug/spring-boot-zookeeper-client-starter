package com.eugene.sumarry.zookeeper.client.starter.anno;

import com.eugene.sumarry.zookeeper.client.starter.utils.SpringContextHolder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ SpringContextHolder.class})
@interface EnableSpringContext {
}
