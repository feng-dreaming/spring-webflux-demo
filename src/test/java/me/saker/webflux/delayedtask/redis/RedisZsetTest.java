package me.saker.webflux.delayedtask.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * redis 有序集合实现延迟任务，弊端是高并发场景下由于内存占用太大效率反而降低</br>
 * 局限性：同一个分数下只能存储一次
 *
 * @author 猎隼
 */
public class RedisZsetTest {
    private static final String DELAYED_TASK_QUEUE_KEY = "order:close:queue:task";

    private final JedisPool jp = this.initJedisPool();

    private Jedis jedis = jp.getResource();

    public JedisPool initJedisPool() {
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxIdle(1000);
        jpc.setMaxTotal(100);
        jpc.setMaxWaitMillis(300);
        jpc.setTestOnBorrow(true);
        jpc.setTestOnReturn(true);
        JedisPool jp = new JedisPool(jpc, "localhost");
        return jp;
    }

    /**
     * 消息生产者<br/>
     * 同样score的排序，此时redis使用字典排序 <br/>
     * https://blog.csdn.net/u011250882/article/details/48665703 <br/>
     */
    public void produce() {
        try {
            Jedis jedis = jp.getResource();
            Thread.sleep(5000);
            //score用当前时间，消费是用消费的当前时间和这里的时间对比
            long timePoint1 = System.currentTimeMillis();
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint1, "1001");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint1, "1002");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint1, "1003");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint1, "1004");
            System.out.println("1001+2==" + timePoint1);
            long timePoint2 = timePoint1 + 5000;
            System.out.println("1003+4==" + timePoint2);
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint2, "2001");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint2, "2002");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint2, "2003");
            jedis.zadd(DELAYED_TASK_QUEUE_KEY, timePoint2, "2004");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息消费者<br/>
     */
    public void comsume() {
        while (jedis.isConnected()) {
            Set<Tuple> items = jedis.zrangeWithScores(DELAYED_TASK_QUEUE_KEY, 0, 1);
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
                        Long deletedCount = jedis.zrem(DELAYED_TASK_QUEUE_KEY, p.getElement());
                        if (deletedCount != null && deletedCount > 0) {
                            System.out.println(p.getElement() + "订单已关闭" + (long) p.getScore() + "当前时间" + System.currentTimeMillis());
                        }
                    }
            );
        }
    }

    public void shutdown() {
        //释放连接
        jedis.close();
        jp.close();
    }

    public static void main(String[] args) {
        RedisZsetTest zsetTest = new RedisZsetTest();
        zsetTest.produce();
        System.out.println("=============");
        Thread worker = new Thread(() -> zsetTest.comsume());
        worker.start();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-------------------");
        zsetTest.shutdown();
        System.out.println("-------------------");
    }
}
