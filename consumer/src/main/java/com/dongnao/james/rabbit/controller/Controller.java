package com.dongnao.james.rabbit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dongnao.james.rabbit.model.Order;
import com.dongnao.james.rabbit.service.OrderService;
//定义为RESTFUL的控制类
@RestController
public class Controller {

	@Autowired
	private OrderService orderService; //加入OrderService bean
	
	//对外开放的接口，地址为：http://127.0.0.1:8090/queryOrderInfo?orderId=123456
	@RequestMapping("/queryOrderInfo")
	public Order queryOrderInfo(@RequestParam(required = false) String orderId){
		try {
			//调用订单查询的service，并将查询结果返回给调用方(userService)
			return orderService.queryOrderInfo(orderId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
