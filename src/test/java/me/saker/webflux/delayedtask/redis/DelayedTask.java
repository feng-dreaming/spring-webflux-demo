package me.saker.webflux.delayedtask.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 延迟任务对象，基于向队列添加的时间
 *
 * @author 猎隼
 */
@Data
@Builder
@AllArgsConstructor
public class DelayedTask {

    //提交队列时间，毫秒数
    public long publishTime;
    //队列数据信息
    public String message;
}
