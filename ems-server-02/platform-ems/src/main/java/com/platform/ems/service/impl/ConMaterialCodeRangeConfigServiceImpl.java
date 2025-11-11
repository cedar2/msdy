package com.platform.ems.service.impl;

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
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.util.mq.MsgSender;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.ConMaterialCodeRangeConfigMapper;
import com.platform.ems.domain.ConMaterialCodeRangeConfig;
import com.platform.ems.service.IConMaterialCodeRangeConfigService;

/**
 * 物料/商品/服务编码号码段配置Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-03
 */
@Service
@SuppressWarnings("all")
public class ConMaterialCodeRangeConfigServiceImpl extends ServiceImpl<ConMaterialCodeRangeConfigMapper,ConMaterialCodeRangeConfig>  implements IConMaterialCodeRangeConfigService {
    @Autowired
    private ConMaterialCodeRangeConfigMapper conMaterialCodeRangeConfigMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static RedisCache redisService;
    @Autowired
    private MsgSender msgSender;
    @Autowired
    RedissonClient redissonClient;
    private static ExecutorService executor = ThreadUtil.newExecutor(5);

    private static final String TITLE = "物料/商品/服务编码号码段配置";

    private RedisCache getRedisService(){
        if(redisService==null){
            redisService= SpringBeanUtil.getBean(RedisCache.class);
        }
        return redisService;
    }

    @Override
    public ConMaterialCodeRangeConfig getNext(String category){
        ConMaterialCodeRangeConfig config = conMaterialCodeRangeConfigMapper.selectOne(new QueryWrapper<ConMaterialCodeRangeConfig>().lambda()
        .eq(ConMaterialCodeRangeConfig::getMaterialCategory, category));
        return config;
    }

    /**
     * 查询物料/商品/服务编码号码段配置
     *
     * @param rangeConfigSid 物料/商品/服务编码号码段配置ID
     * @return 物料/商品/服务编码号码段配置
     */
    @Override
    public ConMaterialCodeRangeConfig selectConMaterialCodeRangeConfigById(Long rangeConfigSid) {
        ConMaterialCodeRangeConfig conMaterialCodeRangeConfig = conMaterialCodeRangeConfigMapper.selectConMaterialCodeRangeConfigById(rangeConfigSid);
        MongodbUtil.find(conMaterialCodeRangeConfig);
        return  conMaterialCodeRangeConfig;
    }

    @Override
    public synchronized Long nextId(String materialCategory){
        String clientId= SecurityUtils.getClientId();
        if(StrUtil.isEmpty(clientId)){
            throw new BaseException("当前用户不允许操作业务");
        }
        String cateGory="";
        String CACHE_KEY="material_code:"+ clientId+":";
        String lockKey="getMaterialCode:"+clientId+":";

        if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
            cateGory="wl";
        }else if(ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)){
            cateGory="sp";
        }else if(ConstantsEms.MATERIAL_CATEGORY_FW.equals(materialCategory)){
            cateGory="fw";
        }else {
            throw new BaseException("类型错误,请联系管理员");
        }
        CACHE_KEY+=cateGory;
        lockKey+=cateGory;
        RLock lock = redissonClient.getLock(lockKey);
        Long nextCode=null;
        //获取当前code
        lock.lock(10L,TimeUnit.SECONDS);
        try {
            nextCode=getRedisService().getCacheObject(CACHE_KEY);
            if(nextCode==null){
                ConMaterialCodeRangeConfig config= conMaterialCodeRangeConfigMapper.selectOne(new QueryWrapper<ConMaterialCodeRangeConfig>().lambda()
                        .eq(ConMaterialCodeRangeConfig::getMaterialCategory, materialCategory));
                if(config==null){
                    throw new BaseException("编码配置不存在,请联系管理员");
                }
                nextCode=config.getRangeCodePresent();
            }
            //校验code是否重复
            BasMaterial queryResult=basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getMaterialCode, nextCode));
            String key=CACHE_KEY;
            Long id=nextCode;
            if(queryResult!=null){
                updateCode(key,materialCategory,id,clientId);
                throw new BaseException("编码配置重复,请联系管理员");
            }
            updateCode(key,materialCategory,id,clientId);
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }finally {
            lock.unlock();
        }
        return nextCode;
    }

    public  synchronized void  updateCode(String CACHE_KEY,String materialCategory,Long code,String clientId){
        //更新配置
        String uuid= UUID.randomUUID().toString();
        JSONObject msg=new JSONObject();
        msg.put("type", AutoIdField.materialCode_key);
        msg.put("materialCategory", materialCategory);
        msg.put("clientId", clientId);
        msg.put("uuid",uuid);
        msgSender.sendMessage(msg);
        String key=AutoIdField.materialCode_key+":"+uuid;
        getRedisService().setCacheObject(key, uuid,10L, TimeUnit.MINUTES);
        //更新redis中的值
        getRedisService().setCacheObject(CACHE_KEY, code+1);
    }

    /**
     * 查询物料/商品/服务编码号码段配置列表
     *
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 物料/商品/服务编码号码段配置
     */
    @Override
    public List<ConMaterialCodeRangeConfig> selectConMaterialCodeRangeConfigList(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig) {
        return conMaterialCodeRangeConfigMapper.selectConMaterialCodeRangeConfigList(conMaterialCodeRangeConfig);
    }

    /**
     * 新增物料/商品/服务编码号码段配置
     * 需要注意编码重复校验
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig) {
        int row= conMaterialCodeRangeConfigMapper.insert(conMaterialCodeRangeConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conMaterialCodeRangeConfig.getRangeConfigSid(), BusinessType.INSERT.ordinal(), null,TITLE);
        }
        return row;
    }

    /**
     * 修改物料/商品/服务编码号码段配置
     *
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig) {
        ConMaterialCodeRangeConfig response = conMaterialCodeRangeConfigMapper.selectConMaterialCodeRangeConfigById(conMaterialCodeRangeConfig.getRangeConfigSid());
        int row=conMaterialCodeRangeConfigMapper.updateById(conMaterialCodeRangeConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conMaterialCodeRangeConfig.getRangeConfigSid(), BusinessType.UPDATE.ordinal(), response,conMaterialCodeRangeConfig,TITLE);
        }
        return row;
    }

    /**
     * 变更物料/商品/服务编码号码段配置
     *
     * @param conMaterialCodeRangeConfig 物料/商品/服务编码号码段配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMaterialCodeRangeConfig(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig) {
        ConMaterialCodeRangeConfig response = conMaterialCodeRangeConfigMapper.selectConMaterialCodeRangeConfigById(conMaterialCodeRangeConfig.getRangeConfigSid());
                                                                            int row=conMaterialCodeRangeConfigMapper.updateAllById(conMaterialCodeRangeConfig);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conMaterialCodeRangeConfig.getRangeConfigSid(), BusinessType.CHANGE.ordinal(), response,conMaterialCodeRangeConfig,TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料/商品/服务编码号码段配置
     *
     * @param rangeConfigSids 需要删除的物料/商品/服务编码号码段配置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMaterialCodeRangeConfigByIds(List<Long> rangeConfigSids) {
        return conMaterialCodeRangeConfigMapper.deleteBatchIds(rangeConfigSids);
    }

    /**
    * 启用/停用
    * @param conMaterialCodeRangeConfig
    * @return
    */
    @Override
    public int changeStatus(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig){
        int row=0;
        Long[] sids=conMaterialCodeRangeConfig.getRangeConfigSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conMaterialCodeRangeConfig.setRangeConfigSid(id);
                row=conMaterialCodeRangeConfigMapper.updateById( conMaterialCodeRangeConfig);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                String remark=conMaterialCodeRangeConfig.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conMaterialCodeRangeConfig.getRangeConfigSid(), BusinessType.CHECK.ordinal(), null,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conMaterialCodeRangeConfig
     * @return
     */
    @Override
    public int check(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig){
        int row=0;
        Long[] sids=conMaterialCodeRangeConfig.getRangeConfigSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conMaterialCodeRangeConfig.setRangeConfigSid(id);
                row=conMaterialCodeRangeConfigMapper.updateById( conMaterialCodeRangeConfig);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                MongodbUtil.insertUserLog(conMaterialCodeRangeConfig.getRangeConfigSid(), BusinessType.CHECK.ordinal(),TITLE);
            }
        }
        return row;
    }


}
