package com.platform.ems.util.mq;

import com.alibaba.fastjson.JSONObject;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.SystemBusyException;
import com.platform.common.core.redis.RedisCache;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.domain.ConMaterialCodeRangeConfig;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.mapper.ConBarcodeRangeConfigMapper;
import com.platform.ems.mapper.ConMaterialCodeRangeConfigMapper;
import com.platform.ems.mapper.SalSalesOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 消息的处理者
 *
 * @author Created by c
 */
@Component
@RabbitListener(queues = "platform.ems.msg")
@SuppressWarnings("all")
@Slf4j
public class MsgReceiver {
    private static Logger LOGGER = LoggerFactory.getLogger(MsgReceiver.class);

    @Autowired
    private ConBarcodeRangeConfigMapper conBarcodeRangeConfigMapper;
    @Autowired
    private ConMaterialCodeRangeConfigMapper conMaterialCodeRangeConfigMapper;
    @Autowired
    private RedisCache redisService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;

    @RabbitHandler
    public void handle(JSONObject msg) {
        LOGGER.info("receive message {}", msg.toJSONString());
        String type = msg.getString("type");
        String uuid = msg.getString("uuid");
        if (AutoIdField.barCode_key.equals(type)) {
            String key = AutoIdField.barCode_key + ":" + uuid;
            String lockKey = AutoIdField.barCode_key + "_lock:" + uuid;
            RLock lock = redissonClient.getLock(lockKey);
            try {
                lock.lock();
                if (redisService.getCacheObject(key) != null) {
                    String clientId = msg.getString("clientId");
                    conBarcodeRangeConfigMapper.updateCode(clientId);
                    redisService.deleteObject(key);
                    System.out.println("删除：" + key);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new SystemBusyException();
            } finally {
                lock.unlock();
            }
        }
        if (AutoIdField.materialCode_key.equals(type)) {
            String key = AutoIdField.materialCode_key + ":" + uuid;
            String lockKey = AutoIdField.materialCode_key + "_lock:" + uuid;
            RLock lock = redissonClient.getLock(lockKey);
            try {
                lock.lock();
                if (redisService.getCacheObject(key) != null) {
                    String materialCategory = msg.getString("materialCategory");
                    String clientId = msg.getString("clientId");
                    conMaterialCodeRangeConfigMapper.updateCode(materialCategory, clientId);
                    redisService.deleteObject(key);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new SystemBusyException();
            } finally {
                lock.unlock();
            }
        }
        //审批后价格的获取
        if(AutoIdField.change_sale_price_key.equals(type)){
            Date startDate = msg.getDate("startDate");
            Date endDate = msg.getDate("endDate");
            if(startDate.getTime()<=new Date().getTime()
                    &&new Date().getTime()<=endDate.getTime()
            ){
                SalSalesOrder salSalesOrder = msg.toJavaObject(SalSalesOrder.class);
                salSalesOrderMapper.updatePrice(salSalesOrder);
            }
        }
    }

}
