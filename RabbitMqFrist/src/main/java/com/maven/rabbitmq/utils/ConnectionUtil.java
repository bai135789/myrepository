package com.maven.rabbitmq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionUtil {

    public static Connection getConnection()throws Exception{
        //定义连接工厂
        ConnectionFactory factory =  new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory.newConnection();
    }

    public static Connection getConnByURI() throws Exception{
        ConnectionFactory factory =  new ConnectionFactory();
        factory.setUri("amqp://guest:guest@localhost:5672");
        return factory.newConnection();
    }

    public static void main(String args[]) throws Exception{
        System.out.println(getConnByURI());
    }

}
