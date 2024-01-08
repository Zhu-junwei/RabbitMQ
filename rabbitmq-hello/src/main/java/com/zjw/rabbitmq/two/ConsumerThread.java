package com.zjw.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.zjw.rabbitmq.utils.QueueUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

/**
 * @author 朱俊伟
 * @since 2022/08/13 10:50
 */
public class ConsumerThread extends Thread{

    private final String name ;
    public ConsumerThread(String name){
        this.name = name;
    }

    @Override
    public void run() {

        try {
            Channel channel = RabbitMqUtils.getChannel();

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                System.out.println(name + "接收消息：" + new String(message.getBody()));
            };

            CancelCallback cancelCallback = (consumerTag) -> {
                System.out.println("消费消息被中断");
            };

            System.out.println(name + "等待接收消息.....");
            //消费消息
            channel.basicConsume(QueueUtils.HELLO_QUEUE, true, deliverCallback, cancelCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
