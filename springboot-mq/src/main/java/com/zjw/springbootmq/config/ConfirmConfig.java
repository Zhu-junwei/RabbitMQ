package com.zjw.springbootmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布确认SpringBoot
 * @author 朱俊伟
 * @date 2022/09/08
 */
@Configuration
public class ConfirmConfig {

    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    public static final String WARNING_QUEUE_NAME = "warning.queue";

    /**
     * 创建发布确认交换机
     * @return 交换机
     */
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        ExchangeBuilder builder = ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true)
                //设置交换机的备份交换机
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME);
        return builder.build();
    }

    /**
     * 创建备份交换机
     * @return
    交换机
     */
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    /**
     * 创建发布确认队列
     * @return 队列
     */
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    /**
     * 创建备份队列
     * @return 队列
     */
    @Bean("backupQueue")
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    /**
     * 创建告警队列
     * @return 队列
     */
    @Bean("warningQueue")
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    /**
     * 绑定发布确认队列和交换机
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定关系
     */
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                @Qualifier("confirmExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("key1");
    }

    /**
     * 绑定备份队列和备份交换机
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定关系
     */
    @Bean
    public Binding backupBinding(@Qualifier("backupQueue") Queue queue,
                                @Qualifier("backupExchange") FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }

    /**
     * 绑定告警队列和备份交换机
     * @param queue 队列
     * @param exchange 交换机
     * @return 绑定关系
     */
    @Bean
    public Binding warningBinding(@Qualifier("warningQueue") Queue queue,
                                 @Qualifier("backupExchange") FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }
}
