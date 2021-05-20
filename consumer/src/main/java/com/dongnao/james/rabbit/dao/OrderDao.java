package com.dongnao.james.rabbit.dao;


import com.dongnao.james.rabbit.model.Order;
 

public interface OrderDao {
	//查询方法
    Order queryOrderInfo(String orderid);
   
}