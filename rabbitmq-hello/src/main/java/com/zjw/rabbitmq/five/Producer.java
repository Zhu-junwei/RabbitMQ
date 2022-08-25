package com.zjw.rabbitmq.five;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * exchange发布订阅模式fanout
 * @author 朱俊伟
 * @date 2022/03/20 20:53
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /*声明交换机
         *  交换机类型：
         *      direct:直接(路由类型)
         *      topic:主题
         *      headers:标题
         *      fanout:扇出(发布订阅)
         */
        channel.exchangeDeclare(ExchangeUtils.LOGS, BuiltinExchangeType.FANOUT);
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("输入发送的消息:");
            String message = scanner.nextLine();
            channel.basicPublish(ExchangeUtils.LOGS,"",null,message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
