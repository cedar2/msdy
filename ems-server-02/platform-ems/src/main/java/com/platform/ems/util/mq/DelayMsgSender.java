package com.platform.ems.util.mq;

import com.platform.ems.enums.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟消息生产者
 * @author c
 */
@Component
public class DelayMsgSender {

    private static Logger LOGGER =LoggerFactory.getLogger(DelayMsgSender.class);
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Long id,final long delayTimes){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_DELAY.getExchange(), QueueEnum.QUEUE_TTL_DELAY.getRouteKey(), id, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //给消息设置延迟毫秒值
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
        LOGGER.info("send delay message id:{}",id);
    }
}
