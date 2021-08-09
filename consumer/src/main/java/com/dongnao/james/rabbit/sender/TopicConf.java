package com.dongnao.james.rabbit.sender;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

//配置类，随系统启动时，根据需求创建交换器和队列， 用来接收服务端发送过来的数据
@Configuration
public class TopicConf{
    Logger log = LoggerFactory.getLogger(TopicConf.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean(name = "orderMessage")
    public Queue queueMessage() {
        //系统启动时：创建一个topic.orderReceive的队列到rabbitMQ
        return new Queue("topic.orderReceive");
    }

       /* @Bean(name="messages")
        public Queue queueMessages() {
            return new Queue("topic.messages");
        }*/

    @Bean
    public TopicExchange exchange() {
        //系统启动时：创建一个topic.orderReceive的队列到rabbitMQ
        return new TopicExchange("exchange");
    }

    @Bean
    Binding bindingExchangeMessage(@Qualifier("orderMessage") Queue queueMessage, TopicExchange exchange) {
        //将交换器与指定的队列绑定起来
        return BindingBuilder.bind(queueMessage).to(exchange).with("topic.orderReceive");
    }



    @PostConstruct
    public void initRabbitTemplate(){
        //设置服务器收到消息确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData   当前消息的唯一关键数据(id)  发送消息时可以指定UUID
             * @param ack   消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                //当消息成功抵达消息中间件后会调用
                //处理具体的业务
                System.out.println(correlationData.toString()+ack+cause);
            }
        });

        //设置消息正确抵达队列的回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列,就触发这个失败回调
             * @param message  消息的详细内容
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容
             * @param change    当时这个消息发给哪个交换机
             * @param routingKey    当时这个消息使用哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String change, String routingKey) {
                //只要消息没有投递给指定的队列,就触发这个失败回调
                //处理具体的业务
                System.out.println(message.toString()+replyCode+replyText+change+routingKey);
            }
        });
    }
//    @PostConstruct
//    public void init(){
//        rabbitTemplate.setReturnCallback(this);             //指定 ReturnCallback
//    }





       /* @Bean
        Binding bindingExchangeMessages(@Qualifier("messages") Queue queueMessages, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");//*表示一个词,#表示零个或多个词
        }*/

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        Logger log = LoggerFactory.getLogger(RabbitTemplate.class);
//
//
//        // 消息发送失败返回到队列中, yml需要配置 publisher-returns: true
//        rabbitTemplate.setMandatory(true);
//
//        // 消息返回, yml需要配置 publisher-returns: true
//        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
//            String correlationId = message.getMessageProperties().getCorrelationIdString();
//            log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
//        });
//
//        // 消息确认, yml需要配置 publisher-confirms: true
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                // log.debug("消息发送到exchange成功,id: {}", correlationData.getId());
//            } else {
//                log.debug("消息发送到exchange失败,原因: {}", cause);
//            }
//        });
//
//        return rabbitTemplate;
//    }
}
