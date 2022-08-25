package com.zjw.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

/**
 * @author 朱俊伟
 * @date 2022/03/15 8:10
 *  生产者：发消息
 */
public class Producer {

    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "hello" ;

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ的队列
        factory.setHost("192.168.234.128");
        //用户名
        factory.setUsername("admin");
        //密码
        factory.setPassword("admin");
        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        //创建队列
        //1.queue 队列名
        //2.durable 队列持久的 默认false
        //3.exclusive 独有的,true:可以多个消费者消费 false:一个消费者消费
        //4.autoDelete 自动删除
        //5.arguments 其他参数Map
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        //发消息
        String message = "hello world" + LocalTime.now();
        //发送一个消息
        //交换机
        //路由的Key值是哪个(本次是队列的名称)
        //其他参数 null,MessageProperties.PERSISTENT_TEXT_PLAIN 消息持久化 首先队列持久化
        //发送消息的消息体
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println("消息发送完毕");

    }
}
