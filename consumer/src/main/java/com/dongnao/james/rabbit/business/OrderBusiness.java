package com.dongnao.james.rabbit.business;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.dongnao.james.rabbit.body.FileBlockBody;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
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

    private FileChannel bufferfile = FileChannel.open(Paths.get("D:\\111.tar", ""), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

    public OrderBusiness() throws IOException {
    }


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
    @RabbitHandler
    public void process1(Message message, Channel channel) throws IOException {
        System.out.println("start");
//        FileBlockBody body = JSON.parseObject(message.getBody(), FileBlockBody.class);
//        try {
//            this.bufferfile.write(ByteBuffer.wrap(body.getContent(), 0, body.getLimit()), body.getStart());
//        } catch (IOException e) {
//            System.out.println("接收到文件块(编号."+"body.getBlockindex()"+")时文件已经被关闭。");
//
//            // 文件已经写入完成并关闭后，如果还收到数据块，此处会因文件已关闭而抛出异常，该异常可忽略
////            if (!isComplete(this.matrix)) {
////                throw e;
////            } else {
////                return;
////            }
//        }
//        System.out.println("");
        channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
    }


    @RabbitListener(queues="topic.file")
    @RabbitHandler
    public void fileReceive(Message message, Channel channel) throws IOException {
//        FileBlockBody body = JSON.parseObject(message.getBody(), FileBlockBody.class);
//        try {
//            this.bufferfile.write(ByteBuffer.wrap(body.getContent(), 0, body.getLimit()), body.getStart());
//        } catch (IOException e) {
//            System.out.println("接收到文件块(编号."+"body.getBlockindex()"+")时文件已经被关闭。");
//
//            // 文件已经写入完成并关闭后，如果还收到数据块，此处会因文件已关闭而抛出异常，该异常可忽略
////            if (!isComplete(this.matrix)) {
////                throw e;
////            } else {
////                return;
////            }
//        }
        System.out.println("shoudao");

    }

    private boolean isComplete(byte[] matrix) {
        if (matrix != null && matrix.length > 0) {
            for (byte b : matrix) {
                if (b == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}