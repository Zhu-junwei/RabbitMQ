package com.zjw.rabbitmq.two;

/**
 * @author 朱俊伟
 * @date 2022/08/13 10:56
 * 模拟多个消费者
 */
public class Consumer {
    public static void main(String[] args) {
        ConsumerThread consumerA = new ConsumerThread("consumerA");
        ConsumerThread consumerB = new ConsumerThread("consumerB");
        ConsumerThread consumerC = new ConsumerThread("consumerC");
        consumerA.start();
        consumerB.start();
        consumerC.start();
    }
}
