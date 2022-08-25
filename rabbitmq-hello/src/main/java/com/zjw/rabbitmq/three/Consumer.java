package com.zjw.rabbitmq.three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.zjw.rabbitmq.utils.QueueUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;
import com.zjw.rabbitmq.utils.SleepUtils;

/**
 * @author 朱俊伟
 * @date 2022/08/20 18:02
 */
public class Consumer {
    public static void main(String[] args) {
        ConsumerThread consumerA = new ConsumerThread("ConsumerA",2,2);
        ConsumerThread consumerB = new ConsumerThread("ConsumerB",5,30);
        consumerA.start();
        consumerB.start();
    }
}

class ConsumerThread extends Thread{

    private final String name ;
    private final int fetchCount;
    private final int sleepSecond;
    public ConsumerThread(String name, int fetchCount, int sleepSecond){
        this.name = name;
        this.fetchCount = fetchCount;
        this.sleepSecond = sleepSecond;
    }

    @Override
    public void run() {
        try {
            Channel channel = RabbitMqUtils.getChannel();
            //设置不公平分发
            channel.basicQos(fetchCount);
            DeliverCallback deliverCallback = (consumerTag, message) -> {
                SleepUtils.sleep(sleepSecond);
                System.out.println(name + "接收消息：" + new String(message.getBody()));
                //处理完消息后手动应答
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            };

            CancelCallback cancelCallback = (consumerTag) -> System.out.println("消费消息被中断");

            System.out.println(name + "等待接收消息.....");
            //消费消息
            channel.basicConsume(QueueUtils.HELLO_QUEUE, false, deliverCallback, cancelCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}