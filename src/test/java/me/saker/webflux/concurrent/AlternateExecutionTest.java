package me.saker.webflux.concurrent;

/**
 * 两个线程交替执行，两个线程的代码逻辑是最后实行wait方法，最终线程无法停止
 *
 * @author 猎隼
 */
public class AlternateExecutionTest {
    public static void main(String[] args) {
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    System.out.println("aa");
                    //notify方法不会阻塞
                    lock.notifyAll();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "a");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                synchronized (lock) {
                    System.out.println("bb");
                    lock.notifyAll();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "bb");

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
