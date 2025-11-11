package com.platform.ems.util.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

@Component
public class ConcumerPriceReceiver {
    @RabbitListener(queues="platform.ems.msg")
    public void ListenerQueue(Message message){
        System.out.println("message:"+message.getBody());
    }
}
