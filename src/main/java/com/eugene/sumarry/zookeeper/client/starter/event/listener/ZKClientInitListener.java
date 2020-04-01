package com.eugene.sumarry.zookeeper.client.starter.event.listener;

import com.eugene.sumarry.zookeeper.client.starter.common.Constants;
import com.eugene.sumarry.zookeeper.client.starter.common.ZKClient;
import com.eugene.sumarry.zookeeper.client.starter.common.ZookeeperClientFactoryBean;
import com.eugene.sumarry.zookeeper.client.starter.event.ZookeeperClientInitEvent;
import com.eugene.sumarry.zookeeper.client.starter.utils.ValidataUtil;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
            try {
                ZookeeperClientFactoryBean factoryBean = (ZookeeperClientFactoryBean) resource;
                if (!ObjectUtils.isEmpty(factoryBean.getObject())) {
                    logger.warn("Zookeeper client has been initialized, stop initialization ZKClient ");
                    return;
                }

                if (logger.isInfoEnabled()) {
                    logger.info("Try to load class {}", Constants.ZK_CLIENT_CLASS);
                }

                initZKClient(factoryBean);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initZKClient(ZookeeperClientFactoryBean factoryBean) {
        ZkClient zkClient = null;
        try {
            Class zkClientClazz = Class.forName(Constants.ZK_CLIENT_CLASS);
            Constructor zkClientConstructor = zkClientClazz.getConstructor(
                    String.class,
                    int.class,
                    int.class,
                    ZkSerializer.class);
            String zookeeperHost = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_HOST,
                    String.class);

            int sessionTime = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_ZK_CLIENT_SESSION_TIME,
                    int.class);

            int connectionTimeOut = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_ZK_CLIENT_CONNECTION_TIME_OUT,
                    int.class);

            String zkSerializerClassValue = ValidataUtil.validAndGet(
                    environment,
                    Constants.ZOOKEEPER_ZK_SERIALIZER_CLASS,
                    String.class);
            // Check config class exist。
            Class zkSerializerClass = Class.forName(zkSerializerClassValue);
            ZkSerializer zkSerializer = (ZkSerializer) zkSerializerClass.getConstructor().newInstance();

            zkClient = (ZkClient) zkClientConstructor.newInstance(zookeeperHost, sessionTime, connectionTimeOut, zkSerializer);

            ZKClient zookeeperClient = new ZKClient();
            zookeeperClient.setClient(zkClient);
            factoryBean.setZookeeperClient(zookeeperClient);
        } catch (ClassNotFoundException e) {
            logger.warn("Not found {} class, " +
                    "if you want to initialize zkclient then you should import zkclient dependency",
                    Constants.ZK_CLIENT_CLASS);
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
