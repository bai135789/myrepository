package com.maven.rabbitmq.day01.work;

import com.maven.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private final static String QUEUE_NAME = "test_queue_work";

    public static void main(String args[]) throws Exception{
        //创建连接工厂
        Connection conn = ConnectionUtil.getConnection();
        //创建通道
        Channel channel = conn.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        for (int i = 0; i < 100; i++) {
            // 消息内容
            String message = "" + i;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

            Thread.sleep(i * 3);
        }
        channel.close();
        conn.close();
    }
}
