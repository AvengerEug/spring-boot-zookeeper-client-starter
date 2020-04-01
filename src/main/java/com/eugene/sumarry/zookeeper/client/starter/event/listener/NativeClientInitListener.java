package com.eugene.sumarry.zookeeper.client.starter.event.listener;

import com.eugene.sumarry.zookeeper.client.starter.common.Constants;
import com.eugene.sumarry.zookeeper.client.starter.common.NativeClient;
import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import com.eugene.sumarry.zookeeper.client.starter.utils.ValidataUtil;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
            ZookeeperClientFactoryBean factoryBean = (ZookeeperClientFactoryBean) resource;
            try {
                if (!ObjectUtils.isEmpty(factoryBean.getObject())) {
                    logger.warn("Zookeeper client has been initialized, stop initialization ZKClient ");
                    return;
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Try to load class {}", Constants.ZOOKEEPER_CLIENT_CLASS);
                }

                initZookeeperNativeClient(factoryBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initZookeeperNativeClient(ZookeeperClientFactoryBean factoryBean) {
        try {
            Class nativeClientClazz = Class.forName(Constants.ZOOKEEPER_CLIENT_CLASS);
            Constructor constructor = nativeClientClazz.getConstructor(
                    String.class,
                    int.class,
                    Watcher.class
            );

            String zookeeperHost = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_HOST,
                    String.class);

            int sessionTime = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_NATIVE_CLIENT_SESSTION_TIME_OUT,
                    int.class);

            String watcherClass = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_NATIVE_WATCHER_CLASS,
                    String.class);

            Watcher watcher = (Watcher) Class.forName(watcherClass).getConstructor().newInstance();

            ZooKeeper zooKeeper = (ZooKeeper) constructor.newInstance(zookeeperHost, sessionTime, watcher);

            NativeClient nativeClient = new NativeClient();
            nativeClient.setClient(zooKeeper);

            factoryBean.setZookeeperClient(nativeClient);
        } catch (ClassNotFoundException e) {
            logger.warn("Not found {} class, " +
                            "if you want to initialize native zookeeper client then " +
                            "you should import zookeeper jar dependency",
                    Constants.CURATOR_FRAMEWORK_FACTORY_CLASS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
