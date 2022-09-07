package com.zjw.rabbitmq.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;
import com.zjw.rabbitmq.utils.SleepUtils;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建一个消费者，里面包含一个正常队列，一个死信队列
 * @author 朱俊伟
 * @date 2022/03/20 21:10
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        /*
         *创建一个消费者，里面包含一个正常队列，一个死信队列
         */
        ConsumerThread consumerA = new ConsumerThread();
        consumerA.setConsumerName("ConsumerA");
        consumerA.setQueueName("normalQueue");
        consumerA.setExchange(ExchangeUtils.DIRECT_LOGS);
        consumerA.setExchangeType(BuiltinExchangeType.DIRECT);
        consumerA.setRoutingKeys(new String[]{"log"});

        //正常队列绑定死信队列信息
        Map<String,Object> arguments = new HashMap<>(2);
        arguments.put("x-dead-letter-exchange",ExchangeUtils.DIRECT_DEAD_LOGS);
        arguments.put("x-dead-letter-routing-key", "log_dead");
//        arguments.put("x-max-length",6);
        consumerA.setQueueArguments(arguments);

        //死信队列和死信交换机信息
        consumerA.setDeadQueueName("deadQueue");
        consumerA.setDeadExchange(ExchangeUtils.DIRECT_DEAD_LOGS);
        consumerA.setDeadExchangeType(BuiltinExchangeType.DIRECT);
        consumerA.setDeadRoutingKeys(new String[]{"log_dead"});

        consumerA.start();
        SleepUtils.sleep(5);
        consumerA.interrupt();
    }
}

/**
 * 创建消费者的线程
 */
@Setter
class ConsumerThread extends Thread{

    /**
     * 消费者名称
     */
    private String consumerName;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 死信队列名称
     */
    private String deadQueueName;

    /**
     * 交换机名称
     */
    private String exchange;

    /**
     * 死信交换机名称
     */
    private String deadExchange;

    /**
     * 交换机类型
     */
    private BuiltinExchangeType exchangeType;

    /**
     * 死信交换机类型
     */
    private BuiltinExchangeType deadExchangeType;

    /**
     * routingKey
     */
    private String[] routingKeys;

    /**
     * 死信routingKey
     */
    private String[] deadRoutingKeys;

    /**
     * 正常队列和死信队列关联信息
     */
    private Map<String, Object> queueArguments;


    @Override
    public void run() {
        try {
            Channel channel = RabbitMqUtils.getChannel("消费者");
            channel.exchangeDeclare(exchange, exchangeType);
            channel.exchangeDeclare(deadExchange, deadExchangeType);

            //声明队列
            channel.queueDeclare(queueName,false,false,false,queueArguments);
            channel.queueDeclare(deadQueueName,false,false,false,null);
            //把该队列绑定我们的exchange 其中routingKey 也为字符串
            for (String routingKey : routingKeys) {
                channel.queueBind(queueName, exchange, routingKey);
            }

            for (String deadRoutingKey : deadRoutingKeys) {
                channel.queueBind(deadQueueName, deadExchange, deadRoutingKey);
            }

            DeliverCallback deliverCallback = (consumerTag, message) -> {
                String msg = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.println(consumerName + Arrays.toString(routingKeys) + "接收消息：" + msg);
                if (msg.contains("5")){
                    channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
                } else {
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                }
            };

            CancelCallback cancelCallback = (consumerTag) -> System.out.println("消费消息被中断");

            System.out.println(consumerName + "等待接收消息.....");
            //消费消息
            channel.basicConsume(queueName, false, deliverCallback, cancelCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
