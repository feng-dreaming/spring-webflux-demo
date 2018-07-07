package me.saker.webflux.concurrent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 3个线程按顺序执行
 *
 * @author 猎隼
 */
public class ThreeThreadsAlternateExecutionTest {
    public static void main(String[] args) {
        //默认先执行t1
        Monitor lock = new Monitor("a");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    //一直循环+等待，直到满足条件通知其他线程竞争监控器
                    while (!"a".equals(lock.getTName())) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("a");
                    lock.setTName("b");
                    //notify方法不会阻塞
                    lock.notifyAll();
                }
            }
        }, "a");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    while (!"b".equals(lock.getTName())) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("b");
                    lock.setTName("c");
                    //notify方法不会阻塞
                    lock.notifyAll();
                }
            }
        }, "b");

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    while (!"c".equals(lock.getTName())) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("c");
                    lock.setTName("a");
                    //notify方法不会阻塞
                    lock.notifyAll();
                }
            }
        }, "c");

        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Builder
@Data
@AllArgsConstructor
class Monitor {
    //下一个要执行逻辑的线程名字
    private volatile String tName;

}
