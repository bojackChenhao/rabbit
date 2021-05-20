package com.dongnao.james.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
//引入SpringBoot功能
@SpringBootApplication
public class OrderApplication{
    public static void main(String[] args) {
    	//启动Springboot容器,即启动orderService服务
        SpringApplication.run(OrderApplication.class, args);
    }
}