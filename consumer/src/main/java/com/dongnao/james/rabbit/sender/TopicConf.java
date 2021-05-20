package com.dongnao.james.rabbit.sender;

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

        @Bean(name="orderMessage")
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

       /* @Bean
        Binding bindingExchangeMessages(@Qualifier("messages") Queue queueMessages, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");//*表示一个词,#表示零个或多个词
        }*/
}
