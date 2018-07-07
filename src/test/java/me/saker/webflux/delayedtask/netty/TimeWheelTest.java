package me.saker.webflux.delayedtask.netty;

import io.netty.util.HashedWheelTimer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * netty时间轮算法实现延迟任务
 * @author 猎隼
 */
public class TimeWheelTest {

    public static void main(String[] main) {
        HashedWheelTimer wheel = new HashedWheelTimer(Executors.defaultThreadFactory(), 1, TimeUnit.SECONDS, 10);
        for (int i = 1; i <= 20; i++) {
            final int y = i;
            wheel.newTimeout((timeout) ->
                            System.out.println("延迟了" + y + "秒执行")
                    , y, TimeUnit.SECONDS);
        }
        wheel.start();
    }

}
