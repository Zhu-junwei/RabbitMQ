package com.zjw.rabbitmq.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.zjw.rabbitmq.utils.QueueUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 使用控制台发送消息
 * @author 朱俊伟
 * @since 2022/03/20 12:20
 */
public class Producer {

    public static final String QUEUE_NAME = QueueUtils.HELLO_QUEUE;
    public static void main(String[] args) throws Exception {
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        //创建队列
        //队列名 持久的 独有的 自动删除 其他参数
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        //发消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String next = scanner.nextLine();
            //发送一个消息
            //交换机 路由的Key值是哪个(本次是队列的名称) 其他参数 发送消息的消息体
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, next.getBytes(StandardCharsets.UTF_8));
            System.out.println("消息发送完毕");
        }

    }
}
