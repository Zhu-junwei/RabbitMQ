package com.zjw.rabbitmq.six;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;

/**
 * 创建两个消费者
 * @author 朱俊伟
 * @since 2022/03/20 21:10
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        ConsumerThread consumerA = new ConsumerThread("ConsumerA", ExchangeUtils.DIRECT_LOGS, BuiltinExchangeType.DIRECT, new String[]{"error"});
        ConsumerThread consumerB = new ConsumerThread("ConsumerB", ExchangeUtils.DIRECT_LOGS, BuiltinExchangeType.DIRECT, new String[]{"info", "waring"});
        consumerA.start();
        consumerB.start();
    }
}

/**
 * 创建消费者的线程
 */
class ConsumerThread extends Thread{

    /**
     * 消费者名称
     */
    private final String consumerName;

    /**
     * 交换机名称
     */
    private final String exchange;

    /**
     * 交换机类型
     */
    private final BuiltinExchangeType exchangeType;

    /**
     * routingKey
     */
    private final String[] routingKeys;


    public ConsumerThread(String consumerName, String exchange, BuiltinExchangeType exchangeType, String[] routingKeys){
        this.consumerName = consumerName;
        this.exchange = exchange;
        this.exchangeType = exchangeType;
        this.routingKeys = routingKeys;
    }

    @Override
    public void run() {
        try {
            Channel channel = RabbitMqUtils.getChannel("消费者");
            channel.exchangeDeclare(exchange, exchangeType);
            //生成一个临时队列 临时队列在消费者断开连接的时候删除
            String queue = channel.queueDeclare().getQueue();
            //把该队列绑定我们的exchange 其中routingKey 也为字符串
            for (String routingKey : routingKeys) {
                channel.queueBind(queue, exchange, routingKey);
            }

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                String msg = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.println(consumerName + "接收消息：" + msg);
            };

            CancelCallback cancelCallback = (consumerTag) -> System.out.println("消费消息被中断");

            System.out.println(consumerName + "等待接收消息.....");
            //消费消息
            channel.basicConsume(queue, true, deliverCallback, cancelCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
