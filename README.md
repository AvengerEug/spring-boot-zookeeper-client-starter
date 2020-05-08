# 基于spring-boot搭建根据依赖 + 配置初始化zookeeper client框架

## 一、目标

1. 根据指定加载顺序和yml配置生成可用的zookeeper客户端(**支持curator、zkClient、zookeeper native client**)
2. 抽象出zookeeper的一些通用基本操作: 增加节点、删除节点、修改节点、给指定节点、孩子节点添加监听器

## 二、使用到的技术点

* **Spring 事件驱动模型、ImportBeanDefinitionRegistrar、FactoryBean、InitializingBean、DisposableBean**

* **通过BeanDefinition设置bean自动装配的类型以及手动不需要自动装配的属性**

* **观察者设计模式、适配器设计模式、代理设计模式**

* **jdk1.8接口实现默认方法特性**

* **SpringBoot自动装配、SPI特性**

  ****

  ****

## 三、缺点
* 实例化客户端单一，每个客户端目前只提供一种初始化策略。后续若版本升级，可添加**策略者模式**，根据用户配置的参数来决定使用哪种初始化api。目前初始化策略api详细如下:

  |                  客户端类型                   |                        使用初始化api                         | 备注 |
  | :-------------------------------------------: | :----------------------------------------------------------: | :--: |
  | org.apache.curator.framework.CuratorFramework | org.apache.curator.framework.CuratorFrameworkFactory#newClient(String connectString, RetryPolicy retryPolicy) |  无  |
  |         org.I0Itec.zkclient.ZkClient          | org.I0Itec.zkclient.ZkClient#ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer) |  无  |
  |        org.apache.zookeeper.ZooKeeper         | org.apache.zookeeper.ZooKeeper#ZooKeeper(String connectString, int sessionTimeout, Watcher watcher) |  无  |

  

* **没有完全覆盖三个客户端对zookeeper的操作的api**

## 四、开发中遇到的问题
1. ZookeeperClientFactoryBean循环依赖(**这里我设置了此bean的名字为:zookeeperClientProcessor **)

   ```java
   The dependencies of some of the beans in the application context form a cycle:
   
   ┌─────┐
   |  zookeeperClientProcessor
   └─────┘
   ```

   出现此原因大概是因为: `ZookeeperClientFactoryBean`内部维护的bean`zookeeperClient`提供了set方法，在**autowireMode为AUTOWIRE_BY_TYPE**时spring会自动为这个属性进行**byType**装配。具体规则可参考: [Spring自动装配注意事项](https://github.com/AvengerEug/spring/tree/develop/resourcecode-study#十-spring自动装配注意事项-以及spring默认不装配的几种类型)

2. 在初始化ZKClientlistener时，因为listener是@Import注解导入的，所以它会被spring当成一个bean处理，会去校验所以被@Bean注解标识的方法，进而会去获取所有的`参数值、返回值`，若返回值的类型在classpath中找不到会抛出`'java.lang.NoClassDefFoundError' exception.`。这其实就是jdk反射获取方法时抛出的异常。eg如下: 在`ZKClientInitListener`中获取`initZKClient()`方法时，当检测到方法的返回值是`ZkClient`, 若当前的classpath中无`ZkClient`的依赖，则会报上述错误信息。解决方法就是把方法返回值改成void，传入引用进去， 如下述的第二段java代码的`initZKClient`方法核心就是不要让jvm编译时去校验方法参数和返回值)

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

3. `ZookeeperClient`接口中的抽象出来的重载方法中若参数名包含其他jar包的类型时，若使用其中一个重载方法运行程序，编译会报错。 eg:

   ```java
   default void watchChildrenNode(String parentPath, IZkChildListener iZkChildListener) throws Exception {
   
   }
   
   default void watchChildrenNode(String parentPath, Watcher watcher) throws Exception {
   
   }
   ```

   当在使用**zookeeperClient.watchChildrenNode**方法时，若当前的项目只添加了原生客户端的依赖，依赖如下:

   ```xml
   <dependency>
       <groupId>org.apache.zookeeper</groupId>
       <artifactId>zookeeper</artifactId>
       <version>3.4.6</version>
   </dependency>
   ```

   那么项目启动不了，会报`org.I0Itec.zkclient.IZkChildListener`找不到的错误。


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
   <!-- 添加至repositories标签中 -->
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
   
   <!-- 添加至project标签中, 若使用mvn clean compile命令一直连接不上服务器时，可将协议改成http。然后重试，
    若两者皆不行，那就多试几次吧。 国外网站嘛，大家都懂~~~-->
   <repositories>
       <repository>
           <id>github</id>
           <name>avengerEug</name>
           <url>https://raw.github.com/AvengerEug/maven-repository/master</url>
       </repository>
   </repositories>
   ```

2. yml或properties文件中添加如下配置:

   ```yml
   zookeeper:
     host: 192.168.111.146:2181
     curator:
       sleep-ms-between-retries: 5000
       retry-time: 2
   ```
   
3. spring boot入口中添加如下注解

   ```java
   @EnableZookeeperClient
   ```

### 6.2  初始化ZkClient

1. 添加`ZkClient`和`spring-boot-zookeeper-client-starter`的依赖

   ```xml
   <!-- 添加至repositories标签中 -->
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
   
   <!-- 添加至project标签中, 若使用mvn clean compile命令一直连接不上服务器时，可将协议改成http。然后重试，
    若两者皆不行，那就多试几次吧。 国外网站嘛，大家都懂~~~-->
   <repositories>
       <repository>
           <id>github</id>
           <name>avengerEug</name>
           <url>https://raw.github.com/AvengerEug/maven-repository/master</url>
       </repository>
   </repositories>
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
   <!-- 添加至repositories标签中 -->
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
   
   <!-- 添加至project标签中, 若使用mvn clean compile命令一直连接不上服务器时，可将协议改成http。然后重试，
    若两者皆不行，那就多试几次吧。 国外网站嘛，大家都懂~~~-->
   <repositories>
       <repository>
           <id>github</id>
           <name>avengerEug</name>
           <url>https://raw.github.com/AvengerEug/maven-repository/master</url>
       </repository>
   </repositories>
   ```

2. 新建`ZookeeperInitWatcher.java`类，并实现`org.apache.zookeeper.Watcher`接口作为默认监听器。如下:

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

### 6.4 如何操作zookeeper

* 将使用spring常用@Autowired注解注入

  ```java
  @Service
  public class UserService {
      @Autowired
      private ZookeeperClient zookeeperClient
      
      public void createEphNode() {
          try {
              // 创建临时节点
              zookeeperClient.createEphNode("/test", "value");
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }
  ```

* 使用spring上下文获取

  ```java
  AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.java);
  ZookeeperClient zookeeperClient = context.getBean(ZookeeperClient.class);
  try {
      zookeeperClient.createEphNode("/test", "value");
  } catch (Exception e) {
      e.printStackTrace();
  }
  ```

* 具体的api参考**ZookeeperClient**类

  ```java
  public interface ZookeeperClient {
      String createPerNode(String var1) throws Exception;
  
      String createPerNode(String var1, String var2) throws Exception;
  
      String createPerSeqNode(String var1) throws Exception;
  
      String createPerSeqNode(String var1, String var2) throws Exception;
  
      String createEphNode(String var1) throws Exception;
  
      String createEphNode(String var1, String var2) throws Exception;
  
      String createEphSeqNode(String var1) throws Exception;
  
      String createEphSeqNode(String var1, String var2) throws Exception;
  
      void deleteNode(String var1) throws Exception;
  
      Stat changeNode(String var1, String var2) throws Exception;
  
      List<String> findChildren(String var1) throws Exception;
  
      default void watchDesignationNodeUsingCurator(String path, CuratorWatcher curatorWatcher) throws Exception {
      }
  
      default void watchTreeCacheNodeUsingCurator(String parentPath, TreeCacheListener treeCacheListener) throws Exception {
      }
  
      default void watchChildrenNodeUsingCurator(String parentPath, PathChildrenCacheListener pathChildrenCacheListener) throws Exception {
      }
  
      default void watchDesignationNodeUsingZKClient(String path, IZkDataListener iZkDataListener) throws Exception {
      }
  
      default void watchChildrenNodeUsingZKClient(String parentPath, IZkChildListener iZkChildListener) throws Exception {
      }
  
      default void watchChildrenNodeUsingNative(String parentPath, Watcher watcher) throws Exception {
      }
  
      default void watchDesignationNodeUsingNative(String path, Watcher watcher) throws Exception {
      }
  
      default <T> T checkExist(String path) throws Exception {
          return null;
      }
  
      void close() throws InterruptedException;
  }
  ```

### 6.5 注意

* **同上述约定，三种客户端的加载顺序为`curator`, `zkclient`, `native client`**。若上述的依赖包都存在，则会按照如下顺序初始化客户端

  ```txt
  curator、zkClient、zookeeper原生客户端
  ```

* 因为springboot中内嵌了`sl4j`的绑定器和具体的日志实现类(`log4j`)，而zookeeper原生客户端的jar包也包含了`sl4j`和`log4j`。所以建议修改zookeeper的jar包依赖如下(去除zookeeper jar包中依赖的日志体系):

  ```xml
  <!-- 添加至repositories标签中 -->
  <dependency>
      <groupId>org.apache.zookeeper</groupId>
      <artifactId>zookeeper</artifactId>
      <version>3.4.6</version>
      <exclusions>
          <exclusion>
              <artifactId>log4j</artifactId>
              <groupId>log4j</groupId>
          </exclusion>
          <exclusion>
              <artifactId>slf4j-api</artifactId>
              <groupId>org.slf4j</groupId>
          </exclusion>
          <exclusion>
              <artifactId>slf4j-log4j12</artifactId>
              <groupId>org.slf4j</groupId>
          </exclusion>
      </exclusions>
  </dependency>
  
    
  ```