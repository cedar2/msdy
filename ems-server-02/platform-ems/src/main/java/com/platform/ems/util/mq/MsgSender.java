package com.platform.ems.util.mq;

import com.alibaba.fastjson.JSONObject;
import com.platform.ems.enums.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 * @author c
 */
@Component
public class MsgSender {

    private static Logger LOGGER = LoggerFactory.getLogger(MsgSender.class);
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(JSONObject msg){
        try {
            amqpTemplate.convertAndSend(QueueEnum.QUEUE_MSG.getExchange(), QueueEnum.QUEUE_MSG.getRouteKey(), msg);
            LOGGER.info("send message {}",msg);
        }catch (Exception e){
            LOGGER.info("send error {}",e.getMessage());
        }
    }
}
