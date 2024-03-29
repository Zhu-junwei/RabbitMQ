package com.zjw.rabbitmq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接工厂，创建信道的工具类
 * @author 朱俊伟
 * @since 2022/03/20 11:54
 */
public class RabbitMqUtils {

    public static ConnectionFactory factory;
    public static final String DEFAULT_CONNECTION_NAME = "default";
    public static Map<String,Connection> connectionMap = new HashMap<>();

    private RabbitMqUtils() {
        // 防止实例化的私有构造方法
    }

    public static synchronized ConnectionFactory getFactory() {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost("rabbitmq");
            factory.setUsername("admin");
            factory.setPassword("admin");
        }
        return factory;
    }

    /**
     * 得到一个连接的channel
     */
    public static synchronized Channel getChannel() throws Exception{
        return getChannel(DEFAULT_CONNECTION_NAME);
    }

    /**
     * 得到一个连接的channel,ChannelMax最大为2047,获取次数超过这个最大值抛出异常
     * @param connectionName 连接名
     * @return 连接
     */
    public static synchronized Channel getChannel(String connectionName) throws Exception{
        Connection connection = connectionMap.get(connectionName);
        if (connection == null){
            connection = getFactory().newConnection(connectionName);
            connectionMap.put(connectionName,connection);

            System.out.println("ChannelMax:" + connection.getChannelMax());
        }
        return connection.createChannel();
    }

}
