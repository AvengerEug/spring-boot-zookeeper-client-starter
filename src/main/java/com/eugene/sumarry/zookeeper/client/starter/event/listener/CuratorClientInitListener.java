package com.eugene.sumarry.zookeeper.client.starter.event.listener;

import com.eugene.sumarry.zookeeper.client.starter.common.Constants;
import com.eugene.sumarry.zookeeper.client.starter.common.CuratorClient;
import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


@Order(1)
public class CuratorClientInitListener implements ApplicationListener<ZookeeperClientInitEvent> {

    private Logger logger = LoggerFactory.getLogger(CuratorClientInitListener.class);

    private final String INITIALIZE_METHOD = "newClient";

    private Environment environment;

    public CuratorClientInitListener(Environment environment) {
        this.environment = environment;
    }

    public void onApplicationEvent(ZookeeperClientInitEvent event) {
        Object resource = event.getSource();
        if (resource instanceof ZookeeperClientFactoryBean) {
            ZookeeperClientFactoryBean processor = (ZookeeperClientFactoryBean) resource;

            Class clazz = null;
            Constructor constructor = null;
            // zookeeper客户端加载顺序
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Try to load class {}", Constants.CURATOR_FRAMEWORK_FACTORY_CLASS);
                }

                clazz = Class.forName(Constants.CURATOR_FRAMEWORK_FACTORY_CLASS);

                constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object instance = constructor.newInstance();

                Class retryNTimesClass = Class.forName(Constants.RETRY_NUMBER_TIMES);
                Constructor retryNTimesConstructor = retryNTimesClass.getDeclaredConstructor(int.class, int.class);

                int retryTime = validAndGet(Constants.ZOOKEEPER_CURATOR_RETRY_TIME, int.class);

                int sleepMsBetweenRetries = validAndGet(Constants.ZOOKEEPER_CURATOR_SLEEP_MS_BETWEEN_RETRIES, int.class);

                Object retryNTimesObject = retryNTimesConstructor.newInstance(retryTime, sleepMsBetweenRetries);

                Method newClientMethod = clazz.getMethod(INITIALIZE_METHOD, String.class, Class.forName(Constants.RETRY_POLICY));

                CuratorFramework curatorFramework = (CuratorFramework) newClientMethod.invoke(instance, validAndGet(Constants.ZOOKEEPER_HOST, String.class), retryNTimesObject);
                CuratorClient curatorClient = new CuratorClient();
                curatorClient.setCuratorFramework(curatorFramework);

                processor.setZookeeperClient(curatorClient);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private <T> T validAndGet(String key, Class<T> clazz) {
        T retryTime = environment.getProperty(key, clazz);
        Assert.notNull(retryTime, "Please config " + key + " attribute from application.yml or application.properties");
        return retryTime;
    }

}
