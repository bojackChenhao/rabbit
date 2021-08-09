package com.dongnao.james.rabbit.controller;

import com.dongnao.james.rabbit.sender.RabbitSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenHao
 * @Classname RabbitTestController
 * @Description TODO
 * @Date 2021/7/21 21:47
 */
@RestController
@RequestMapping(path = "/rabbit")
public class RabbitTestController {
    @Autowired
    private RabbitSender rabbitSender;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/confirmTest")
    public String confirmTest(@RequestParam("name") String name){
        String exchange = "exchange";
        String queueName = "topic.order";
//        String orderId = "123456";
//        rabbitSender.sendTopic(exchange,queueName,name);
        rabbitTemplate.convertAndSend(exchange,queueName,name);
        System.out.println(name);
        return "success";
    }
}
