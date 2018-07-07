package me.saker.webflux.delayedtask.redis;

/**
 * 回调函数
 *
 * @author 猎隼
 */
public interface Callable<DelayedTask> {

    public void call(DelayedTask task);
}
