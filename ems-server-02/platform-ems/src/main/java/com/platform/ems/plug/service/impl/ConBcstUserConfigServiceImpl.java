package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBcstUserConfig;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import com.platform.ems.plug.mapper.ConBcstUserConfigMapper;
import com.platform.ems.plug.mapper.ConDataobjectCategoryMapper;
import com.platform.ems.plug.service.IConBcstUserConfigService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 通知用户配置Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-12
 */
@Service
@SuppressWarnings("all")
public class ConBcstUserConfigServiceImpl extends ServiceImpl<ConBcstUserConfigMapper, ConBcstUserConfig> implements IConBcstUserConfigService {
    @Autowired
    private ConBcstUserConfigMapper conBcstUserConfigMapper;
    @Autowired
    private ConDataobjectCategoryMapper conDataobjectCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "通知用户配置";

    /**
     * 查询通知用户配置
     *
     * @param sid 通知用户配置ID
     * @return 通知用户配置
     */
    @Override
    public ConBcstUserConfig selectConBcstUserConfigById(Long sid) {
        ConBcstUserConfig conBcstUserConfig = conBcstUserConfigMapper.selectConBcstUserConfigById(sid);
        MongodbUtil.find(conBcstUserConfig);
        return conBcstUserConfig;
    }

    /**
     * 查询通知用户配置列表
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 通知用户配置
     */
    @Override
    public List<ConBcstUserConfig> selectConBcstUserConfigList(ConBcstUserConfig conBcstUserConfig) {
        return conBcstUserConfigMapper.selectConBcstUserConfigList(conBcstUserConfig);
    }

    /**
     * 新增通知用户配置
     * 需要注意编码重复校验
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBcstUserConfig(ConBcstUserConfig conBcstUserConfig) {
        List<ConBcstUserConfig> conBcstUserConfigList = getConBcstUserConfigs(conBcstUserConfig);
        if (CollectionUtil.isNotEmpty(conBcstUserConfigList)) {
            throw new BaseException("已存在相同通知用户配置，请核实");
        }
        setCode(conBcstUserConfig);
        setConfirmInfo(conBcstUserConfig);
        int row = conBcstUserConfigMapper.insert(conBcstUserConfig);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBcstUserConfig.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    private void setCode(ConBcstUserConfig conBcstUserConfig) {
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conBcstUserConfig.getDataobjectCategorySid());
        conBcstUserConfig.setDataobjectCategoryCode(conDataobjectCategory.getCode());
    }

    /**
     * parms:数据对象类别sid、通知类型、通知人账号
     */
    private List<ConBcstUserConfig> getConBcstUserConfigs(ConBcstUserConfig conBcstUserConfig) {
        return conBcstUserConfigMapper.selectList(new QueryWrapper<ConBcstUserConfig>().lambda()
                .eq(ConBcstUserConfig::getDataobjectCategorySid, conBcstUserConfig.getDataobjectCategorySid())
                .eq(ConBcstUserConfig::getBcstType, conBcstUserConfig.getBcstType())
                .eq(ConBcstUserConfig::getUserAccount, conBcstUserConfig.getUserAccount()));
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConBcstUserConfig o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改通知用户配置
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBcstUserConfig(ConBcstUserConfig conBcstUserConfig) {
        checkUnique(conBcstUserConfig);
        setCode(conBcstUserConfig);
        setConfirmInfo(conBcstUserConfig);
        ConBcstUserConfig response = conBcstUserConfigMapper.selectConBcstUserConfigById(conBcstUserConfig.getSid());
        int row = conBcstUserConfigMapper.updateById(conBcstUserConfig);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBcstUserConfig.getSid(), BusinessType.UPDATE.getValue(), response, conBcstUserConfig, TITLE);
        }
        return row;
    }

    /**
     * 变更通知用户配置
     *
     * @param conBcstUserConfig 通知用户配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBcstUserConfig(ConBcstUserConfig conBcstUserConfig) {
        checkUnique(conBcstUserConfig);
        setCode(conBcstUserConfig);
        setConfirmInfo(conBcstUserConfig);
        conBcstUserConfig.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConBcstUserConfig response = conBcstUserConfigMapper.selectConBcstUserConfigById(conBcstUserConfig.getSid());
        int row = conBcstUserConfigMapper.updateAllById(conBcstUserConfig);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBcstUserConfig.getSid(), BusinessType.CHANGE.getValue(), response, conBcstUserConfig, TITLE);
        }
        return row;
    }

    private void checkUnique(ConBcstUserConfig conBcstUserConfig) {
        List<ConBcstUserConfig> conBcstUserConfigList = getConBcstUserConfigs(conBcstUserConfig);
        if (CollectionUtil.isNotEmpty(conBcstUserConfigList)) {
            conBcstUserConfigList.forEach(o -> {
                if (!o.getSid().equals(conBcstUserConfig.getSid())) {
                    throw new BaseException("已存在相同通知用户配置，请核实");
                }
            });
        }
    }

    /**
     * 批量删除通知用户配置
     *
     * @param sids 需要删除的通知用户配置ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBcstUserConfigByIds(List<Long> sids) {
        /*Integer count = conBcstUserConfigMapper.selectCount(new QueryWrapper<ConBcstUserConfig>().lambda()
                .eq(ConBcstUserConfig::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ConBcstUserConfig::getSid, sids));
        if (count != sids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }*/
        return conBcstUserConfigMapper.deleteBatchIds(sids);
    }

    /**
     * 更改确认状态
     *
     * @param conBcstUserConfig
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConBcstUserConfig conBcstUserConfig) {
        int row = 0;
        Long[] sids = conBcstUserConfig.getSidList();
        if (sids != null && sids.length > 0) {
            Integer count = conBcstUserConfigMapper.selectCount(new QueryWrapper<ConBcstUserConfig>().lambda()
                    .eq(ConBcstUserConfig::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(ConBcstUserConfig::getSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = conBcstUserConfigMapper.update(null, new UpdateWrapper<ConBcstUserConfig>().lambda()
                    .set(ConBcstUserConfig::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(ConBcstUserConfig::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(ConBcstUserConfig::getConfirmDate, new Date())
                    .in(ConBcstUserConfig::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
