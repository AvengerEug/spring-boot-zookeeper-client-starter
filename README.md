# 基于spring-boot搭建扩展性高的zookeeper框架

## 一、目标

1. 使用工厂设计模式，根据指定加载顺序和yml配置生成可用的zookeeper客户端
2. 抽象出zookeeper的一些基本操作: 增加节点、删除节点、给指定节点添加监听器，并根据抽象出来的客户端处理对应的操作

## 二、缺点
* 实例化客户端单一，每个客户端只提供一种


## 约定
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

