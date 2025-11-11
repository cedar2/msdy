package com.platform.ems.config;

import com.platform.ems.enums.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author c
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 延迟消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange delayMsgDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_DELAY.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 普通消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange msgDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_MSG.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange delayMsgTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_DELAY.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 实际消费队列
     */
    @Bean
    public Queue delayMsgQueue() {
        return new Queue(QueueEnum.QUEUE_DELAY.getName());
    }

    /**
     * 普通消费队列
     */
    @Bean
    public Queue msgQueue() {
        return new Queue(QueueEnum.QUEUE_MSG.getName());
    }

    /**
     * 延迟队列（死信队列）
     */
    @Bean
    public Queue delayMsgTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_DELAY.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_DELAY.getExchange())
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_DELAY.getRouteKey())
                .build();
    }

    /**
     * 将队列绑定到交换机
     */
    @Bean
    Binding delayMsgBinding(DirectExchange delayMsgDirect, Queue delayMsgQueue){
        return BindingBuilder
                .bind(delayMsgQueue)
                .to(delayMsgDirect)
                .with(QueueEnum.QUEUE_DELAY.getRouteKey());
    }

    /**
     * 将队列绑定到交换机
     */
    @Bean
    Binding msgBinding(DirectExchange msgDirect, Queue msgQueue){
        return BindingBuilder
                .bind(msgQueue)
                .to(msgDirect)
                .with(QueueEnum.QUEUE_MSG.getRouteKey());
    }

    /**
     * 将延迟队列绑定到交换机
     */
    @Bean
    Binding delayMsgTtlBinding(DirectExchange delayMsgTtlDirect,Queue delayMsgTtlQueue){
        return BindingBuilder
                .bind(delayMsgTtlQueue)
                .to(delayMsgTtlDirect)
                .with(QueueEnum.QUEUE_TTL_DELAY.getRouteKey());
    }
}
