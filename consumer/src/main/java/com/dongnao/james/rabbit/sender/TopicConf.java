package com.dongnao.james.rabbit.sender;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

//配置类，随系统启动时，根据需求创建交换器和队列， 用来接收服务端发送过来的数据
@Configuration
public class TopicConf {
    Logger log = LoggerFactory.getLogger(TopicConf.class);

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

    @Bean
    @ConditionalOnMissingBean(value = RabbitTemplate.ConfirmCallback.class)
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            // do something ...
            log.debug("消息发送到exchange失败,原因: {}", cause);
        };
    }


    @Bean
    @ConditionalOnMissingBean(value = RabbitTemplate.ReturnCallback.class)
    public RabbitTemplate.ReturnCallback returnCallback() {
        return (message, replyCode, replyText, exchange, routingKey) -> {
            // do something ...
            String correlationId = message.getMessageProperties().getCorrelationIdString();
            log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);

        };
    }

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
