package com.platform.ems.enums;

import lombok.Getter;

@Getter
public enum QueueEnum {

    QUEUE_MSG("platform.ems.msg", "platform.ems.msg", "platform.ems.msg"),
    /**
     * 延迟消息通知队列
     */
    QUEUE_DELAY("platform.ems.direct", "platform.ems.delay", "platform.ems.delay"),
    /**
     * 延迟消息通知ttl队列
     */
    QUEUE_TTL_DELAY("platform.ems.direct.ttl", "platform.ems.delay.ttl", "platform.ems.delay.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
