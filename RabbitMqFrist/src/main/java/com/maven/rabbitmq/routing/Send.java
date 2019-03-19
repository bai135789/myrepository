package com.maven.rabbitmq.routing;

import com.maven.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class Send {

    private final static String EXCHANGE_NAME = "test_exchange_direct";

    public static void main(String args[]) throws Exception{
        //创建连接工厂
        Connection conn = ConnectionUtil.getConnection();
        //创建通道
        Channel channel = conn.createChannel();
        //声明队列
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String message = "删除商品";
        //发送消息
        channel.basicPublish(EXCHANGE_NAME, "delete", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        conn.close();
    }

}
