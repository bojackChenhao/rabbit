

package com.dongnao.james.rabbit.service;


import com.dongnao.james.rabbit.model.Order;

public interface OrderService {
	
	Order queryOrderInfo(String orderId) throws Exception;
}