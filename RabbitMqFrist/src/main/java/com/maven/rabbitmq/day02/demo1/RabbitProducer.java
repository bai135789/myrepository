package com.maven.rabbitmq.day02.demo1;

import com.maven.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 生产者类
 */
public class RabbitProducer {

    private final static String EXCHANGE_NAME = "exchange demo";
    private final static String ROUTING_KEY = " routingkey demo";
    private final static String QUEUE_NAME = "queue demo ";


    public static void main(String args[]) throws Exception{
        //创建连接工厂
        Connection conn = ConnectionUtil.getConnection();
        //创建通道
        Channel channel = conn.createChannel();




        //备份交换器
        Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("a1ternate-exchange" , "myAe");
        channel.exchangeDeclare( "norma1Exchange" , "direct" , true , false , argsMap);
        channel.exchangeDeclare( "myAe " , "fanout" , true, false , null) ;

        channel.queueDeclare( "norma1Queue " , true , false , false , null);
        channel.queueDeclare( "unroutedQueue " , true , false , false , null);

        channel.queueBind( " norma1Queue " , " norma1Exchange" , " norma1Key");
        channel .queueBind( "unroutedQueue ", "myAe ", "");
        /**
         * 上面的代码中声明了两个交换器 nonnallixchange 和 myAe，分别绑定了 nonnalQueue 和
         * umoutedQueue 这两个队列，同时将 myAe 设置为 nonnallixchange 的备份交换器。注 意 myAe
         * 的交换器类型为 fanout。
         */




        //创建type=direct 持久化的，非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
        //创建一个持久化的、非排他的、非自动删除的队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        //将队列与交换器绑定， 路由键为 ROUNTING_KEY
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        String message = "Hello World";
        //发送普通消息
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        //发送AMQP.BasicProperties 消息
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
                    new AMQP.BasicProperties.Builder().contentType("text/plain")  //设置content-type为" text/plain" 可以自己设定消息的属性:
                .deliveryMode(2)   //设置消息的投递模式为2（消息会被持久化到服务器）
                .priority(1)       //设置消息的优先级为1
                .userId("hidden")
                .expiration("60000")   //设置过期时间
                .build(), message.getBytes()
        );

       // 发送一条带有 headers 的消息
        Map<String, Object> headers = new HashMap<String, Object>() ;
        headers.put( "loca1tion" , "here " );
        headers . put( " time " , " today" );
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, new AMQP.BasicProperties.Builder()
                .headers(headers)
                .build(),
                message.getBytes()) ;


        // 设置mandatory
        /**
         * 当 mandatory 参数设为 true 时，交换器无法根据自身的类型和路由键找到一个符合条件
         * 的队列，那么 RabbitMQ 会调用 Basic.Return 命令将消息返回给生产者 。当 mandatory 参
         * 数设置为 false 时，出现上述情形，则消息直接被丢弃 。
         */
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                "mandatory test".getBytes());

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
                                     AMQP.BasicProperties basicProperties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println( "Basic.Return 返回的结果是:" + message);
            }
        });

        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        conn.close();
    }
}


/***
 * exchangeDeclare方法参数解析
 *  exchange : 交换器的名称。
 *  type : 交换器的类型，常见的如 fanout、 direct 、 topic ，
 *  durable: 设置是否持久化 。 durab l e 设置为 true 表示持久化， 反之是非持久化 。持久化可以将交换器存盘，在服务器重启 的时候不会丢失相关信息。
 *  autoDelete : 设置是否自动删除。 autoDelete 设置为 true 则表示自动删除。自动删除的前提是至少有一个队列或者交换器与这个交换器绑定 ， 之后所有与这个交换器绑
 *        定的队列或者交换器都与此解绑。注意不能错误地把这个参数理解为 : "当与此交换器连接的客户端都断开时 ， RabbitMQ 会自动删除本交换器 "。
 *  internal : 设置是否是内置的。如果设置为 true ，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式。
 *  argument : 其他一些结构化参数
 *
 * queueDeclare 方法参数解析
 *     不带任何参数的 queueDeclare 方法默认创建一个由 RabbitMQ 命名的(类似这种amq.gen-LhQzlgv3GhDOv8PIDabOXA 名称，这种队列也称之为匿名队列〉、排他的、自动删除的、非持久化的队列。
 *  queue : 队列的名称。
 *  durable: 设置是否持久化。为 true 则设置队列为持久化。持久化的队列会存盘，在服务器重启的时候可以保证不丢失相关信息。
 *  exclusive : 设置是否排他。
 *  autoDelete: 设置是否自动删除。
 *  argurnents: 设置队列的其他一些参数
 * queueBind方法参数解析
 *   queue: 队列名称:
 *   exchange: 交换器的名称:
 *   routingKey: 用来绑定队列和交换器的路由键;
 *   argument: 定义绑定的一些参数。
 * exchangeBind方法参数解析
 *  可以将交换器与交换器绑定, 绑定之后消息从 source 交换器转发到 destination 交换器，某种程度上来说 destination 交换器可以看作一个队列
 * basicPublish方法参数解析
 *   exchange: 交换器的名称，指明消息需要发送到哪个交换器中 。 如果设置为空字符串，则消息会被发送到 RabbitMQ 默认的交换器中。
 *   routingKey : 路由键，交换器根据路由键将消息存储到相应的队列之中 。
 *   props : 消息的基本属性集
 *   byte[] body : 消息体 ( pay1oad ) ，真正需要发送的消息 。
 *   mandatory
 *   immediate
 * */