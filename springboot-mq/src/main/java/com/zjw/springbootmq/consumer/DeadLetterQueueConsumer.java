package com.zjw.springbootmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 朱俊伟
 * @since 2022/09/06 23:42
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    @RabbitListener(queues = {"QD"})
    public void receiveD(Message message, Channel channel){
        String msg = new String(message.getBody());
        log.info("当前时间:{},收到死信队列的消息:{}",new Date().toString(), msg);
    }
}
