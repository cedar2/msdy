package com.platform.ems.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.redis.RedisCache;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ConBarcodeRangeConfig;
import com.platform.ems.domain.ConMaterialCodeRangeConfig;
import com.platform.ems.mapper.ConBarcodeRangeConfigMapper;
import com.platform.ems.mapper.ConMaterialCodeRangeConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@EnableScheduling
@SuppressWarnings("all")
public class TimingTask {

    private static final Logger logger = LoggerFactory.getLogger(TimingTask.class);

    @Autowired
    private RedisCache redisService;

    @Autowired
    private ConBarcodeRangeConfigMapper conBarcodeRangeConfigMapper;
    @Autowired
    private ConMaterialCodeRangeConfigMapper conMaterialCodeRangeConfigMapper;

    @Scheduled(cron = "0 0 * * * *")
    public void sync(){
        logger.info("----定时任务开启：开始同步-----");
        List<ConBarcodeRangeConfig> barcodeRangeConfigList=conBarcodeRangeConfigMapper.selectList(new QueryWrapper<ConBarcodeRangeConfig>());
        if(barcodeRangeConfigList!=null){
            barcodeRangeConfigList.forEach(config->{
                String key="barcode:"+config.getClientId();
                Long value=redisService.getCacheObject(key);
                if(value!=null&&!value.equals(config.getRangeCodePresent())){
                    conBarcodeRangeConfigMapper.update(null,new UpdateWrapper<ConBarcodeRangeConfig>().lambda().set(ConBarcodeRangeConfig::getRangeCodePresent , value)
                    .eq(ConBarcodeRangeConfig::getClientId, config.getClientId()));
                }
            });
        }
        List<ConMaterialCodeRangeConfig> materialCodeRangeConfigList=conMaterialCodeRangeConfigMapper.selectList(new QueryWrapper<ConMaterialCodeRangeConfig>());
        if(materialCodeRangeConfigList!=null){
            materialCodeRangeConfigList.forEach(config->{
                String cateGory="";
                String materialCategory=config.getMaterialCategory();
                if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
                    cateGory="wl";
                }else if(ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)){
                    cateGory="sp";
                }else if(ConstantsEms.MATERIAL_CATEGORY_FW.equals(materialCategory)){
                    cateGory="fw";
                }
                String key="material_code:"+config.getClientId()+":"+cateGory;
                Long value=redisService.getCacheObject(key);
                if(value!=null&&!value.equals(config.getRangeCodePresent())){
                    conMaterialCodeRangeConfigMapper.update(null,new UpdateWrapper<ConMaterialCodeRangeConfig>().lambda().set(ConMaterialCodeRangeConfig::getRangeCodePresent , value)
                            .eq(ConMaterialCodeRangeConfig::getClientId, config.getClientId()).eq(ConMaterialCodeRangeConfig::getMaterialCategory, materialCategory));
                }
            });
        }
        logger.info("----同步完成-----");
    }
}
