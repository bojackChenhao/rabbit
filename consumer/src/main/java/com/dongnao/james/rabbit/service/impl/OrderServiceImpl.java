
package com.dongnao.james.rabbit.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dongnao.james.rabbit.dao.OrderDao;
import com.dongnao.james.rabbit.model.Order;
import com.dongnao.james.rabbit.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;

	@Override
	public Order queryOrderInfo(String orderId) throws Exception {
		// TODO Auto-generated method stub
		return orderDao.queryOrderInfo(orderId);
	}

	@Override
	public int insertOrder(Order order) throws Exception {
		return orderDao.insertOrder(order);
	}


}

