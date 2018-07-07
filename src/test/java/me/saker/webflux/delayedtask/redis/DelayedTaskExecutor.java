package me.saker.webflux.delayedtask.redis;

/**
 * 延迟任务队列顶层接口
 */
public interface DelayedTaskExecutor {
    /**
     * 提交延迟任务到队列
     *
     * @param task
     */
    public void execute(DelayedTask task);

    /**
     * 回收线程资源
     */
    public void shutdown();
}
