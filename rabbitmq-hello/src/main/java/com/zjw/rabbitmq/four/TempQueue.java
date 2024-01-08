package com.zjw.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

/**
 * 创建临时队列，临时队列在程序停止时自动删除
 * @author 朱俊伟
 * @since 2022/08/21 21:20
 */
public class TempQueue {
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /*
            临时队列：
                autoDelete:true
                exclusive:true
         */
        // amq.gen-5IXz0BKQhbcDR3xl8fANuA
        String queue = channel.queueDeclare().getQueue();
        String queue1 = channel.queueDeclare("queue1", false, true, true, null).getQueue();
        System.out.println("创建临时队列：" + queue);
        System.out.println("创建临时队列：" + queue1);
    }
}
