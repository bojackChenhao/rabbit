package com.dongnao.james.rabbit.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

//配置类，随系统启动时，根据需求创建交换器和队列， 用来接收服务端发送过来的数据
@Configuration
public class TopicConf {

    Logger log = LoggerFactory.getLogger(TopicConf.class);

    @Resource
    private RabbitTemplate rabbitTemplate;

    //系统启动时：创建一个message的队列到rabbitMQ
    @Bean(name = "message")
    public Queue queueMessage() {
        return new Queue("topic.order");
    }

    /* @Bean(name="messages")
     public Queue queueMessages() {
         return new Queue("topic.messages");
     }*/
    //系统启动时：创建一个exchange的交换器到rabbitMQ
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("exchange");
    }

    //系统启动时：将exchange的交换器与队列绑定
    @Bean
    Binding bindingExchangeMessage(@Qualifier("message") Queue queueMessage, TopicExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange).with("topic.order");
    }

    @Bean
    @ConditionalOnMissingBean(value = RabbitTemplate.ConfirmCallback.class)
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            // do something ...
            System.out.println("失败");
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
}
