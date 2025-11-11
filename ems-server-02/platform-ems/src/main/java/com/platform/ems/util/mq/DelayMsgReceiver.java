package com.platform.ems.util.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 延迟消息的处理者
 * Created by c
 */
@Component
@RabbitListener(queues = "platform.ems.delay")
public class DelayMsgReceiver {

    private static Logger LOGGER = LoggerFactory.getLogger(MsgReceiver.class);

    @RabbitHandler
    public void handle(Long id){
        LOGGER.info("receive delay message id:{}",id);
    }
}
