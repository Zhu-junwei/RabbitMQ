package com.zjw.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.zjw.rabbitmq.utils.QueueUtils;
import com.zjw.rabbitmq.utils.RabbitMqUtils;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认的三种方式：单条确认、批量确认、异步确认
 * @author 朱俊伟
 * @date 2022/03/20 16:45
 */
public class ConfirmMessage {

    public static final int MESSAGE_COUNT = 10000;

    public static void main(String[] args) throws Exception {
        //单条确认发布 7207ms
        System.out.println("单条发布确认测试开始。");
//        publishMessageIndividually();
        //批量确认发布 1187ms
        System.out.println("批量确认发布测试开始。");
//        publishMessageBatch();
        //异步确认发布 683ms
        System.out.println("异步确认发布测试开始。");
        publishMessageAsync();
    }

    /**
     * 单条发布确认
     * 同步等待确认，简单，但吞吐量非常有限。
     */
    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认
        channel.confirmSelect();
        channel.queueDeclare(QueueUtils.CONFIRM_QUEUE,false,false,false,null);
        //开始时间
        long startTime = System.currentTimeMillis();

        for(int i = 0; i < MESSAGE_COUNT; i++){
            channel.basicPublish("",QueueUtils.CONFIRM_QUEUE,null,(i+"").getBytes());
            //服务端返回 false 或超时时间内未返回，生产者可以消息重发
            boolean flag = channel.waitForConfirms();
            if(!flag){
                System.out.println("消息发送失败");
            }
        }
        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("单个确认耗时:"+(endTime-startTime)+"ms");

    }

    /**
     * 批量发布确认
     * 批量同步等待确认，简单，合理的吞吐量，一旦出现问题但很难推断出是哪条消息出现了问题
     */
    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认
        channel.confirmSelect();
        channel.queueDeclare(QueueUtils.CONFIRM_QUEUE,false,false,false,null);
        //开始时间
        long startTime = System.currentTimeMillis();
        int batchSize = 100;
        for(int i = 0; i < MESSAGE_COUNT; i++){
            channel.basicPublish("",QueueUtils.CONFIRM_QUEUE,null,(i+"").getBytes());
            if (i%batchSize == 0){
                //批量确认
                channel.waitForConfirms();
            }
        }
        channel.waitForConfirms();
        //结束时间
        long endTime = System.currentTimeMillis();
        System.out.println("批量确认耗时:"+(endTime-startTime)+"ms");

    }

    /**
     * 异步发布确认
     * 最佳性能和资源使用，在出现错误的情况下可以很好地控制，但是实现起来稍微难些
     */
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QueueUtils.CONFIRM_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        /*
         * 线程安全有序的一个哈希表，适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序列号
         * 3.支持并发访问
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        /*
         * 确认收到消息的一个回调
         * 1.消息序列号
         * 2.true 可以确认小于等于当前序列号的消息
         * false 确认当前序列号消息
         */
        //消息确认成功 回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if (multiple) {
                //返回的是小于等于当前序列号的未确认消息 是一个 map
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag, true);
                //清除该部分未确认消息
                confirmed.clear();
            }else{
                //只清除当前序列号的消息
                outstandingConfirms.remove(deliveryTag);
            }
        };
        //消息确认失败 回调函数
        ConfirmCallback nackCallback = (deliveryTag, multiple) ->{
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("发布的消息:"+message+"未被确认，序列号"+deliveryTag);
        };
        /*
         * 添加一个异步确认的监听器
         * 1.确认收到消息的回调
         * 2.未收到消息的回调
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++){
            String message = i + "";
            /*
             * channel.getNextPublishSeqNo()获取下一个消息的序列号
             * 通过序列号与消息体进行一个关联
             * 全部都是未确认的消息体
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", QueueUtils.CONFIRM_QUEUE, null, message.getBytes());

        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - begin) + "ms");
    }
}
