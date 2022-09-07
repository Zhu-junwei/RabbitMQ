package com.zjw.rabbitmq.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;
import com.zjw.rabbitmq.utils.SleepUtils;

import java.nio.charset.StandardCharsets;

/**
 * 消费者将消息发送至死信队列
 * 三种场景：
 *  消息TTL过期
 *  队列达到最大长度
 *  消息被拒
 * @author 朱俊伟
 */
public class Producer {

    public static void main(String[] args) throws Exception {

        //设置Exchange名字
        String exchange = ExchangeUtils.DIRECT_LOGS;
        Channel channel = RabbitMqUtils.getChannel("生产者");
        /*声明交换机
         *  交换机类型：
         *      direct:直接(路由类型)
         *      topic:主题
         *      headers:标题
         *      fanout:扇出(发布订阅)
         */
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        //1.进入死信队列 设置为1s过期，为了进入死信队列
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 0; i < 10; i++) {
            SleepUtils.sleep(1);
            String message = "message " + i;
            System.out.println("发送:" + message);
//            channel.basicPublish(exchange, "log", properties, message.getBytes(StandardCharsets.UTF_8));
            channel.basicPublish(exchange, "log", null, message.getBytes(StandardCharsets.UTF_8));
        }
    }

}
