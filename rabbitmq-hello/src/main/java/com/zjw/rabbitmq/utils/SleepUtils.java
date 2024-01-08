package com.zjw.rabbitmq.utils;

/**
 * @author 朱俊伟
 * @since 2022/03/20 14:00
 */
public class SleepUtils {

    /**
     * 程序睡眠
     * @param second 睡眠时长：单位(秒)
     */
    public static void sleep(int second){
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
