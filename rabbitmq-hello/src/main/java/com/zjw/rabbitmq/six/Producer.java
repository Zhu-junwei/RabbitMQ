package com.zjw.rabbitmq.six;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.zjw.rabbitmq.utils.ExchangeUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 直接交换机
 *  与发布订阅的区别是指定了routingKey,也就是指定消息了发送到哪些队列中
 * @author 朱俊伟
 * @date 2022/08/21 22:39
 */
public class Producer {

    public static void main(String[] args) throws Exception {

        //创建routingKey集合
        Map<String, String> routingKeyMap = new HashMap<>();
        routingKeyMap.put("1", "info");
        routingKeyMap.put("2", "waring");
        routingKeyMap.put("3", "error");

        //设置Exchange名字
        String exchange = ExchangeUtils.DIRECT_LOGS;
        int channelCount = 2047 ;
        List<Channel> channelList = new ArrayList<>();
        for (int i = 0; i < channelCount; i++) {
            Channel channel = RabbitMqUtils.getChannel("生产者");
            channelList.add(channel);
            System.out.println("channel:" + channel.getChannelNumber());
        }
        Channel channel = channelList.get(0);
        /*声明交换机
         *  交换机类型：
         *      direct:直接(路由类型)
         *      topic:主题
         *      headers:标题
         *      fanout:扇出(发布订阅)
         */
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("输选择routingKey"+ routingKeyMap +":");
            String routingKey = routingKeyMap.getOrDefault(scanner.nextLine(),"");
            System.out.println("输入发送的消息:");
            String message = scanner.nextLine();
            channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }

}
