package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.domain.BasMaterialBarcode;
import com.platform.ems.mapper.BasMaterialBarcodeMapper;
import com.platform.ems.util.mq.MsgSender;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.ConBarcodeRangeConfigMapper;
import com.platform.ems.domain.ConBarcodeRangeConfig;
import com.platform.ems.service.IConBarcodeRangeConfigService;

/**
 * 物料/商品/服务条码号码段配置Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-03
 */
@Service
@SuppressWarnings("all")
public class ConBarcodeRangeConfigServiceImpl extends ServiceImpl<ConBarcodeRangeConfigMapper, ConBarcodeRangeConfig> implements IConBarcodeRangeConfigService {
    @Autowired
    private ConBarcodeRangeConfigMapper conBarcodeRangeConfigMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisCache redisService;
    @Autowired
    private MsgSender msgSender;
    @Autowired
    RedissonClient redissonClient;

    private static ExecutorService executor = ThreadUtil.newExecutor(5);

    private static final String TITLE = "物料/商品/服务条码号码段配置";

    /**
     * 查询物料/商品/服务条码号码段配置
     *
     * @param rangeConfigSid 物料/商品/服务条码号码段配置ID
     * @return 物料/商品/服务条码号码段配置
     */
    @Override
    public ConBarcodeRangeConfig selectConBarcodeRangeConfigById(Long rangeConfigSid) {
        ConBarcodeRangeConfig conBarcodeRangeConfig = conBarcodeRangeConfigMapper.selectConBarcodeRangeConfigById(rangeConfigSid);
        MongodbUtil.find(conBarcodeRangeConfig);
        return conBarcodeRangeConfig;
    }

    @Override
    public synchronized Long nextId() {
        //获取当前编码
        String clientId = SecurityUtils.getClientId();
        if(StrUtil.isEmpty(clientId)){
            throw new BaseException("当前用户不允许操作业务");
        }
        String lockKey="getBarcode:"+clientId;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(10L,TimeUnit.SECONDS);
        Long nextCode=null;
        try {
            String barcode_cache_key = "barcode:" + clientId;
            nextCode  = redisService.getCacheObject(barcode_cache_key);
            if (nextCode == null) {
                ConBarcodeRangeConfig config = conBarcodeRangeConfigMapper.selectOne(new QueryWrapper<ConBarcodeRangeConfig>());
                if (config != null) {
                    nextCode = config.getRangeCodePresent();
                } else {
                    throw new BaseException("商品条码起始配置不存在,请联系管理员");
                }
            }
            //校验code是否重复
            BasMaterialBarcode barcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                    .eq(BasMaterialBarcode::getBarcode, nextCode));
            Long id = nextCode;
            if (barcode != null) {
                updateCode(barcode_cache_key, id, clientId);
                throw new BaseException("商品条码配置重复,请联系管理员");
            }
            //更新配置
            updateCode(barcode_cache_key, id, clientId);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }finally {
            lock.unlock();
        }
        return nextCode;
    }

    private synchronized void updateCode(String barcode_cache_key, Long nextCode, String clientId) {
        String uuid = UUID.randomUUID().toString();
        JSONObject msg = new JSONObject();
        msg.put("type", AutoIdField.barCode_key);
        msg.put("clientId", clientId);
        msg.put("uuid", uuid);
        msgSender.sendMessage(msg);
        //conBarcodeRangeConfigMapper.updateCode(clientId);
        String key = AutoIdField.barCode_key + ":" + uuid;
        redisService.setCacheObject(key, uuid, 10L, TimeUnit.MINUTES);
        redisService.setCacheObject(barcode_cache_key, nextCode + 1);
    }

    /**
     * 查询物料/商品/服务条码号码段配置列表
     *
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 物料/商品/服务条码号码段配置
     */
    @Override
    public List<ConBarcodeRangeConfig> selectConBarcodeRangeConfigList(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        return conBarcodeRangeConfigMapper.selectConBarcodeRangeConfigList(conBarcodeRangeConfig);
    }

    /**
     * 新增物料/商品/服务条码号码段配置
     * 需要注意编码重复校验
     *
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        int row = conBarcodeRangeConfigMapper.insert(conBarcodeRangeConfig);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBarcodeRangeConfig.getRangeConfigSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料/商品/服务条码号码段配置
     *
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        ConBarcodeRangeConfig response = conBarcodeRangeConfigMapper.selectConBarcodeRangeConfigById(conBarcodeRangeConfig.getRangeConfigSid());
        int row = conBarcodeRangeConfigMapper.updateById(conBarcodeRangeConfig);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBarcodeRangeConfig.getRangeConfigSid(), BusinessType.UPDATE.ordinal(), response, conBarcodeRangeConfig, TITLE);
        }
        return row;
    }

    /**
     * 变更物料/商品/服务条码号码段配置
     *
     * @param conBarcodeRangeConfig 物料/商品/服务条码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBarcodeRangeConfig(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        ConBarcodeRangeConfig response = conBarcodeRangeConfigMapper.selectConBarcodeRangeConfigById(conBarcodeRangeConfig.getRangeConfigSid());
        int row = conBarcodeRangeConfigMapper.updateAllById(conBarcodeRangeConfig);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBarcodeRangeConfig.getRangeConfigSid(), BusinessType.CHANGE.ordinal(), response, conBarcodeRangeConfig, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料/商品/服务条码号码段配置
     *
     * @param rangeConfigSids 需要删除的物料/商品/服务条码号码段配置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBarcodeRangeConfigByIds(List<Long> rangeConfigSids) {
        return conBarcodeRangeConfigMapper.deleteBatchIds(rangeConfigSids);
    }

    /**
     * 启用/停用
     *
     * @param conBarcodeRangeConfig
     * @return
     */
    @Override
    public int changeStatus(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        int row = 0;
        Long[] sids = conBarcodeRangeConfig.getRangeConfigSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBarcodeRangeConfig.setRangeConfigSid(id);
                row = conBarcodeRangeConfigMapper.updateById(conBarcodeRangeConfig);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBarcodeRangeConfig.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBarcodeRangeConfig.getRangeConfigSid(), BusinessType.CHECK.ordinal(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBarcodeRangeConfig
     * @return
     */
    @Override
    public int check(ConBarcodeRangeConfig conBarcodeRangeConfig) {
        int row = 0;
        Long[] sids = conBarcodeRangeConfig.getRangeConfigSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBarcodeRangeConfig.setRangeConfigSid(id);
                row = conBarcodeRangeConfigMapper.updateById(conBarcodeRangeConfig);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBarcodeRangeConfig.getRangeConfigSid(), BusinessType.CHECK.ordinal(), msgList, TITLE);
            }
        }
        return row;
    }


}
