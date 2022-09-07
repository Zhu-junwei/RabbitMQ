package com.zjw.springbootmq.consumer;

import com.rabbitmq.client.Channel;
import com.zjw.springbootmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 消费者，基于插件的延迟消息
 * @author 朱俊伟
 * @date 2022/09/07 23:08
 */
@Slf4j
@Component
public class DelayQueueConsumer {

    @RabbitListener(queues = {DelayedQueueConfig.DELAYED_QUEUE_NAME})
    public void receiveD(Message message, Channel channel){
        String msg = new String(message.getBody());
        log.info("当前时间:{},收到死信队列的消息:{}",new Date().toString(), msg);
    }
}
