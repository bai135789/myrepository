package com.maven.rabbitmq.day02.demo1;

import com.maven.rabbitmq.utils.ConnectionUtil;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 消费者类
 */
public class RabbitConsumer {

    private final static String QUEUE_NAME = "queue demo ";

    public static void main(String args[]) throws Exception{
        //创建连接工厂
        Connection conn = ConnectionUtil.getConnection();
        //创建通道
        final Channel channel = conn.createChannel();

        channel.basicQos(1); //设置客户端最多接受违背ack的个数


        //一下为推模式
        //可以使用QueueingConsurner来创建消费者，但是不推荐
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                System.out.println("接受到的消息" + new String(body));
                try{
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                //设置手动确认消息
                channel.basicAck(deliveryTag, false); //可以防止消 息不必要地丢失。
            }
        };


        //---一下为拉模式
        GetResponse response = channel.basicGet(QUEUE_NAME, false);
        System.out.println(new String(response.getBody()));
        channel .basicAck(response.getEnvelope() .getDeliveryTag() , false);

        channel.basicConsume(QUEUE_NAME, consumer);
        //等待函数执行完之后关闭资源。
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        conn.close();
    }
}
/********
 * basicConsume方法参数解析
 *  queue : 队列的名称:
 *  autoAck : 设置是否自动确认。建议设成 false ，即不自动确认:
 *  consumerTag: 消费者标签，用来区分多个消费者:
 *  noLocal : 设置为 true 则表示不能将同一个 Connectio口中生产者发送的消息传送给这个 Connection 中的消费者:
 *  exclusive : 设置是否排他 :
 *  arguments : 设置消费者的其他参数:
 *  callback : 设置消费者的回调函数。用来处理 RabbitMQ 推送过来的消息，比如DefaultConsumer ， 使用时需要客户端重写 (override) 其中的方法。
 *
 *
 *  Basic.Consume 将信道 (Channel) 直为接收模式，直到取消队列的订阅为止。在接收式期间， RabbitMQ 会不断地推送消息给消费者，当然推送消息的个数还是会受到
 *   Basic.Qos的限制.如果只想从队列获得单条消息而不是持续订阅，建议还是使用 Basic.Get 进行消费.但不能将 Basic.Get 放在一个循环里来代替 Basic.Consume ，这样做会严重影响 RabbitMQ
 *   的性能.如果要实现高吞吐量，消费者理应使用 Basic.Consume 方法。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * ****************/
