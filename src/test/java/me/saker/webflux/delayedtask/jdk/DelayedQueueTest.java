package me.saker.webflux.delayedtask.jdk;


import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;

/**
 * 延迟任务实现： 使用jdk自带延迟队列
 *
 * @author 猎隼
 */
public class DelayedQueueTest {
    private final DelayQueue<Order> dq = new DelayQueue<>();

    /**
     * 消息生产者
     */
    public void produce(Order order) {
        dq.offer(order);
    }

    /**
     * 消息消费者
     */
    public void comsume() {
        while (true) {
            try {
                Order order = dq.take();
                System.out.println(order);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        DelayedQueueTest test = new DelayedQueueTest();
        test.produce(new Order(10000001L, LocalDateTime.now().plusSeconds(3)));
        test.produce(new Order(10000002L, LocalDateTime.now().plusSeconds(20)));
        Thread worker = new Thread(() -> {
            System.out.println("消费端开始就绪");
            test.comsume();
        }
        );
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
