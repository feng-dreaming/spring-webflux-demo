package me.saker.webflux.delayedtask.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * redis实现的延迟任务执行器
 *
 * @author 猎隼
 */
public class RedisDelayedTaskExecutor implements DelayedTaskExecutor {

    private final String queueKey;

    private Jedis jedis;

    private Thread worker;

    private Callable<DelayedTask> callback;

    public RedisDelayedTaskExecutor(String queueKey, Jedis jedis, Callable<DelayedTask> callback) {
        this.queueKey = queueKey;
        this.jedis = jedis;
        worker = new Thread(() -> {
            System.out.println("启动消费");
            this.consume();
        });
        worker.start();
    }

    /**
     * 提交至延迟队列
     *
     * @param task
     */
    @Override
    public void execute(DelayedTask task) {
        jedis.zadd(queueKey, task.getPublishTime(), task.getMessage());
    }

    /**
     * 开始执行延迟任务
     */
    public void consume() {
        while (jedis.isConnected() && !Thread.interrupted()) {
            Set<Tuple> items = jedis.zrangeWithScores(queueKey, 0, 1);
            if (items == null || items.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            //过滤取得符合超时的订单
            items.stream().filter(p -> System.currentTimeMillis() >= p.getScore()).forEach(p -> {
                        //从zset移除对应的memeber,返回数值大于0表示有删除member
                        Long deletedCount = jedis.zrem(queueKey, p.getElement());
                        if (deletedCount != null && deletedCount > 0) {
                            System.out.println(p.getElement() + "订单已关闭");
                            callback.call(new DelayedTask((long) p.getScore(), p.getElement()));
                        }
                    }
            );
        }
    }

    @Override
    public void shutdown() {
        jedis.close();
    }
}
