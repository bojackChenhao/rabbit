package com.dongnao.james.rabbit.business;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dongnao.james.rabbit.model.Order;
import com.dongnao.james.rabbit.sender.RabbitSender;
import com.dongnao.james.rabbit.service.OrderService;
//实现接收有关订单信息的处理类，由此类从MQ取订单相关的数据，如果orderId,
@Component
public class OrderBusiness {
    @Autowired
	private OrderService orderService;
    @Autowired
	private RabbitSender rabbitSender;//将处理结果发送数据到队列
    
    //监听器监听指定的Queue
    @RabbitListener(queues="queue")    
    public void processC(String orderId) {
        System.out.println("Receive orderId===:"+orderId);
        try {
			orderService.queryOrderInfo(orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    //监听指定queueObject队列，获取的数据为Map对象
    @RabbitListener(queues="queueObject")   
    public void process1(Map user) {    
        System.out.println("Receive Object===:"+user);
    }
    //监听指定的topic.order队列，当此队列有数据时，数据就会被取走
    @RabbitListener(queues="topic.order")    
    public void process1(String orderId, Message message, Channel channel) throws IOException {
        Order order = null;
        try {
            System.out.println(System.currentTimeMillis()+"====Receive from topic.order orderId is========:" + orderId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            throw new Exception();
            //根据从队列里获取到的orderId, 查询出订单信息
//			order = orderService.queryOrderInfo(orderId);
//
//			if(null != order){
//				//将查询出来的订单信息结果发送到topic.orderReceive队列，等待userService来获取
//				rabbitSender.send("topic.orderReceive", order.getOrderid()+"~"+order.getOrdermoney()+"~"+order.getOrderstatus()+"~"+order.getOrdertime());
//			}
		} catch (Exception e) {
			e.printStackTrace();
            System.out.println("失败确认");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		}
        
    }
    /*@RabbitListener(queues="topic.messages")    //监听器监听指定的Queue
    public void process2(String orderId) {
        System.out.println("orderId:"+orderId);
    }*/
}