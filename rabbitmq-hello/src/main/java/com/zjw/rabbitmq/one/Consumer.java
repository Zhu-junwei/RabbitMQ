package com.zjw.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author 朱俊伟
 * @since 2022/03/15 9:11
 * 消费者 接收消息
 */
public class Consumer {

    public static final String QUEUE_NAME = "hello";
    public static final String CONNECTION_NAME = "conn1";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        factory.setUsername("admin");
        factory.setPassword("admin");

        // 未指定连接名 192.168.234.1:1654 ?
//        Connection connection = factory.newConnection();

        // 可以指定来连接名，192.168.234.1:174 conn1
        Connection connection = factory.newConnection(CONNECTION_NAME);
        // 也是通过createChannel(int channelNumber) 设置channelNumber，默认是1
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag , message) -> {
            System.out.println(new String(message.getBody()));
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消费消息被中断");
        };

        //消费消息
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);

    }

}
