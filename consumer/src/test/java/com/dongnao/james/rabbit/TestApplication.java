package com.dongnao.james.rabbit;

import com.dongnao.james.rabbit.model.Order;
import com.dongnao.james.rabbit.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenHao
 * @Classname Test
 * @Description TODO
 * @Date 2021/6/3 22:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestApplication {
    @Autowired
    private OrderService orderService; //加入OrderService bean

    @Test
    public void testInsert() throws Exception {
        Order order = new Order();

        for(int i = 123457; i < 400000;i++){
            order.setOrdermoney(new BigDecimal(i));
            order.setOrderstatus(String.valueOf(i));
            order.setOrdertime(new Date());
            order.setVersion(i);
            orderService.insertOrder(order);
        }
    }
}
