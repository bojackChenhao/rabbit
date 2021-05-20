package com.dongnao.james.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//引入SpringBoot功能
@SpringBootApplication
public class UserApplication{
    public static void main(String[] args) {
    	//启动Springboot容器,即启动orderService服务
        SpringApplication.run(UserApplication.class, args);
    }
}