# 基于spring-boot搭建扩展性高的zookeeper client框架

## 一、目标

1. 使用工厂、观察者设计模式，根据指定加载顺序和yml配置生成可用的zookeeper客户端
2. 抽象出zookeeper的一些通用基本操作: 增加节点、删除节点、给指定节点、孩子节点添加监听器

## 二、特点

## 三、缺点
* 实例化客户端单一，每个客户端目前只提供一种初始化策略。后续若版本升级，可添加策略者模式，根据用户配置的参数来决定使用哪种初始化api，具体初始化策略api详细如下:

  |                  客户端类型                   |                        使用初始化api                         | 备注 |
  | :-------------------------------------------: | :----------------------------------------------------------: | :--: |
  | org.apache.curator.framework.CuratorFramework | org.apache.curator.framework.CuratorFrameworkFactory#newClient(String connectString, RetryPolicy retryPolicy) |      |
  |         org.I0Itec.zkclient.ZkClient          | org.I0Itec.zkclient.ZkClient#ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer) |      |
  |        org.apache.zookeeper.ZooKeeper         | org.apache.zookeeper.ZooKeeper#ZooKeeper(String connectString, int sessionTimeout, Watcher watcher) |      |

  

* 没有完全覆盖三个客户端对zookeeper的操作的api

## 四、开发中遇到的问题
1. ZookeeperClientFactoryBean循环依赖
2. 在初始化ZKClientlistener时，因为listener是@Import注解导入的，所以它会被spring当成一个bean出来。最终会去校验所以被@Bean注解标识的方法，会去获取所有的`参数值、返回值`，若返回值的class path在classpath中找不到的话，会抛出`'java.lang.NoClassDefFoundError' exception.`。这其实就是jdk反射获取
   方法时抛出的异常，但是被spring捕获了。如下: 在获取initZKClient()方法的返回值时，若当前的class
   path中无ZkClient的依赖，则会报错，解决方法就是把方法返回值改成void，传入引用进去， 如下述的第二段java代码(核心就是不要让jdk去校验方法参数和返回值)
   
   ```java
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
    
                    ZKClient zookeeperClient = new ZKClient();
                    zookeeperClient.setClient(initZKClient());
    
                    factoryBean.setZookeeperClient(zookeeperClient);
    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    
        private ZkClient initZKClient() {
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
    
            } catch (ClassNotFoundException e) {
                logger.warn("Not found {} class, please import zkclient dependency", Constants.ZK_CLIENT_CLASS);
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
    
            return zkClient;
        }
    
    }
   ```
   
    改动后:
    ```java
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
                   e.printStackTrace();
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
    ```


## 五、约定
* zookeeper的默认加载顺序为: curator、zkClient、zookeeper原生客户端
* curator客户端的初始化目前只提供重试的实例化策略，即采用如下api:
  ```java
  public static CuratorFramework newClient(String connectString, RetryPolicy retryPolicy)
  ```
* zkClient客户端初始化目前只支持如下api实例化:
  ```java
  public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout)
  ```
* zookeeper原生客户端目前只支持如下api实例化:
  ```java
  public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
  ```

## 六、初始化客户端

### 6.1 初始化`curator client`

1. 添加`curator client`和`spring-boot-zookeeper-client-starter`的依赖

   ```xml
   <dependency>
       <groupId>com.eugene.sumarry</groupId>
       <artifactId>spring-boot-zookeeper-client-starter</artifactId>
       <version>0.0.1</version>
   </dependency>
   
   <dependency>
       <groupId>org.apache.curator</groupId>
       <artifactId>curator-framework</artifactId>
       <version>2.10.0</version>
   </dependency>
   
   <dependency>
       <groupId>org.apache.curator</groupId>
       <artifactId>curator-recipes</artifactId>
       <version>2.10.0</version>
   </dependency>
   
   <dependency>
       <groupId>org.apache.curator</groupId>
       <artifactId>curator-client</artifactId>
       <version>2.10.0</version>
   </dependency>
   ```

2. yml或properties文件中添加如下配置:

   ```yml
   zookeeper:
     host: 192.168.111.146:2181
     curator:
       sleepMsBetweenRetries: 5000
       retryTime: 2
     zk-client:
       sessionTime: 5000
   ```

3. spring boot入口中添加如下注解

   ```java
   @EnableZookeeperClient
   ```

### 6.2  初始化ZkClient

1. 添加`ZkClient`和`spring-boot-zookeeper-client-starter`的依赖

   ```xml
   <dependency>
       <groupId>com.eugene.sumarry</groupId>
       <artifactId>spring-boot-zookeeper-client-starter</artifactId>
       <version>0.0.1</version>
   </dependency>
   
   <dependency>
       <groupId>com.101tec</groupId>
       <artifactId>zkclient</artifactId>
       <version>0.10</version>
   </dependency>	
   ```

2. yml或properties文件中添加如下配置:

   ```yml
   zookeeper:
     host: 192.168.111.146:2181
     zk-client:
       session-time: 10000
       connection-timeout: 10000
       zk-serializer-class: org.I0Itec.zkclient.serialize.SerializableSerializer
   ```

3. spring boot入口中添加如下注解

   ```java
   @EnableZookeeperClient
   ```

###  6.3 初始化zookeeper原生客户端

1. 添加`zookeeper`和`spring-boot-zookeeper-client-starter`的依赖

   ```xml
   <dependency>
       <groupId>com.eugene.sumarry</groupId>
       <artifactId>spring-boot-zookeeper-client-starter</artifactId>
       <version>0.0.1</version>
   </dependency>
   
   <dependency>
       <groupId>org.apache.zookeeper</groupId>
       <artifactId>zookeeper</artifactId>
       <version>3.4.6</version>
   </dependency>
   ```

2. 新建`ZookeeperInitWatcher.java`类，并实现`org.apache.zookeeper.Watcher`接口。如下:

   ```java
   package com.eugene.sumarry.springbootstudy.watch;
   
   import org.apache.zookeeper.WatchedEvent;
   import org.apache.zookeeper.Watcher;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   
   public class ZookeeperInitWatcher implements Watcher {
   
       private final static Logger logger = LoggerFactory.getLogger(ZookeeperInitWatcher.class);
   
       @Override
       public void process(WatchedEvent watchedEvent) {
           logger.info("Zookeeper native client init successful");
       }
   }
   ```

3. yml或properties文件中添加如下配置:

   ```yml
   zookeeper:
     host: 192.168.111.146:2181
     native-client:
       session-time: 10000
       watcher-class: com.eugene.sumarry.springbootstudy.watch.ZookeeperInitWatcher
   ```

4. spring boot入口中添加如下注解

   ```java
   @EnableZookeeperClient
   ```

### 6.4 注意

* **同上述约定，三种客户端的加载顺序为`curator`, `zkclient`, `native client`**。若上述的依赖包都存在，则会按照如下顺序初始化客户端