package producer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dongnao.james.rabbit.body.FileBlockBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dongnao.james.rabbit.UserApplication;
import com.dongnao.james.rabbit.sender.RabbitSender;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRabbitMQ {

	// 并发量
	private static final int USER_NUM = 100;
	// 倒计时器，用于模拟高并发
	private static CountDownLatch cdl = new CountDownLatch(USER_NUM);

	public static final int FILEQUEUE_BLOCKSIZE = 64 * 1024;
	
	
	@Autowired
	private RabbitSender rabbitSender;

	@Autowired
	private RabbitTemplate rabbitTemplate;


	@Test
	public void testRabbit() {
		String queueName = "queue";
		String orderId = "123456";
		//rabbitSender.send(queueName,orderId);
	}
	@Test
	public void testRabbitObject() {
		String exchange = "exchange";
		String queueName = "topic.order";
		String orderId = "123456";
		rabbitSender.sendTopic(exchange,queueName,orderId);
	}
	@Test
	public void testRabbitTopic() {
		String exchange = "exchange";
		String queueName = "topic.file";
		String orderId = "123456";
		rabbitSender.sendTopic(exchange,queueName,orderId);
	}
	
	
    //并发模拟
	@Test
	public void testStressRabbitTopic() throws InterruptedException {
		long start = System.currentTimeMillis();
		// 循环实例化USER_NUM个并发请求（线程）
		for (int i = 0; i < USER_NUM; i++) {
			new Thread(new UserRequst()).start();
			cdl.countDown();// 倒计时器减一
		}
		cdl.await();

		System.out.println("执行时间" + (System.currentTimeMillis()-start));
		Thread.sleep(60000);
	}
	
	// 内部类继承线程接口，用于模拟用户请求
	public class UserRequst implements Runnable {
		@Override
		public void run() {
			try {
				cdl.await();// 当前线程等待，等所以线程实例化完成后，同时停止等待后调用接口代码
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String exchange = "exchange";
			String queueName = "topic.order";
			String orderId = "123456";
			rabbitSender.sendTopic(exchange,queueName,orderId);
		}
	}

	@Test
	public void testFile() {
		String exchange = "exchange";
		String queueName = "topic.file";
		File file = new File("E:\\all.tar");
		RandomAccessFile raf = null;
		try {
			long index = 0L;
			raf = new RandomAccessFile(file, "r");
			byte[] bytes = new byte[FILEQUEUE_BLOCKSIZE];
			int readLen = 0;
			long start = 0L;
			while ((readLen = raf.read(bytes)) > 0) {
				final long blockstart = start;
				final int blocklength = readLen;
				final long blockindex = index;
				//文件块信息不会缓存,
				// 一方面因为大,
				// 另外一方面如果文件发送失败重试时还是要根据文件ID找到对应的块来进行重发的 不需要从缓存里面读

				FileBlockBody fileblockBody = new FileBlockBody();
//				fileblockBody.setFileId(filebody.getFileId());
//				fileblockBody.setFileTargetPath(filebody.getFileTargetPath());
				fileblockBody.setBlockindex(blockindex);
				fileblockBody.setStart(blockstart);
				fileblockBody.setLimit(blocklength);
				fileblockBody.setFilelength(file.length());
				fileblockBody.setContent(bytes);
				rabbitTemplate.convertAndSend(exchange, queueName, JSON.toJSONString(fileblockBody, SerializerFeature.WriteClassName));
				bytes = new byte[FILEQUEUE_BLOCKSIZE];
				index++;
				start += FILEQUEUE_BLOCKSIZE;
				raf.seek(start);
			}
		} catch (Exception e) {
			System.out.println("error1");

		} finally {
			try {
				if (raf != null) {
					raf.close();
				}
			} catch (IOException e) {
				System.out.println("error2");
			}
		}
//		FileBlockBody
		return;
	}

}