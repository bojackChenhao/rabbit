package com.dongnao.james.rabbit.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//配置类，随系统启动时，根据需求创建交换器和队列， 用来接收服务端发送过来的数据
@Configuration
public class TopicConf {

	    //系统启动时：创建一个message的队列到rabbitMQ
        @Bean(name="message")
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

       /* @Bean
        Binding bindingExchangeMessages(@Qualifier("messages") Queue queueMessages, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");//*表示一个词,#表示零个或多个词
        }*/
}
