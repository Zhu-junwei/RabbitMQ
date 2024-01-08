package com.zjw.rabbitmq.seven;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * exchange主题模式topic
 *  通过routingKey的匹配规则（*、#），将消息存入不同的队列
 * @author 朱俊伟
 * @since 2022/08/21 22:39
 */
public class Producer {

    public static void main(String[] args) throws Exception {

        //设置Exchange名字
        String exchange = ExchangeUtils.TOPIC_LOGS;
        Channel channel = RabbitMqUtils.getChannel("生产者");
        /*声明交换机
         *  交换机类型：
         *      direct:直接(路由类型)
         *      topic:主题
         *      headers:标题
         *      fanout:扇出(发布订阅)
         */
        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("输选择routingKey:");
            String routingKey = scanner.nextLine();
            System.out.println("输入发送的消息:");
            String message = scanner.nextLine();
            channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }

}
