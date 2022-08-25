package com.zjw.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

/**
 * 创建临时队列
 * @author 朱俊伟
 * @date 2022/08/21 21:20
 */
public class TempQueue {
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /*
            临时队列：
                autoDelete:true
                exclusive:true
         */
        String queue = channel.queueDeclare().getQueue();
        System.out.println("创建临时队列：" + queue);
    }
}
