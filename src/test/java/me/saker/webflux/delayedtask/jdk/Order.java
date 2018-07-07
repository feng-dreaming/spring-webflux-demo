package me.saker.webflux.delayedtask.jdk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 模拟待关闭的订单
 *
 * @author 猎隼
 */
@Data
@Builder
@AllArgsConstructor
public class Order implements Delayed, Serializable {

    private Long orderId;
    private LocalDateTime createDate;

    @Override
    public long getDelay(TimeUnit unit) {
        long orderTime = createDate.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        return orderTime - now;
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == null || !(o instanceof Order)) return 1;
        if (o == this) return 0;
        Order s = (Order) o;
        return this.createDate.compareTo(s.getCreateDate());
    }
}
