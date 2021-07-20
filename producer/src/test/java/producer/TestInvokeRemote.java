package producer;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.dongnao.james.rabbit.UserApplication;

@SpringBootTest(classes = UserApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestInvokeRemote {

	RestTemplate restTemplate = new RestTemplate();
	// 并发量
	private static final int USER_NUM = 500;
	// 倒计时器，用于模拟高并发
	private static CountDownLatch cdl = new CountDownLatch(USER_NUM);
	private final String url = "http://127.0.0.1:8090/queryOrderInfo?orderId=123456";

	@Test
	public void testInvokeRemote() throws InterruptedException {
		// 循环实例化USER_NUM个并发请求（线程）
		for (int i = 0; i < USER_NUM; i++) {
			new Thread(new UserRequst()).start();
			cdl.countDown();// 倒计时器减一
			System.out.println(cdl.getCount());
		}
		Thread.currentThread().sleep(20000);
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
			String str = restTemplate.getForEntity(url, String.class).getBody();
			System.out.println(str);
		}
	}
}