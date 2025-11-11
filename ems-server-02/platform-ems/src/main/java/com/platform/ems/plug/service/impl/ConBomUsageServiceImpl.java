package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBomUsage;
import com.platform.ems.plug.mapper.ConBomUsageMapper;
import com.platform.ems.plug.service.IConBomUsageService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * BOM用途Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBomUsageServiceImpl extends ServiceImpl<ConBomUsageMapper, ConBomUsage> implements IConBomUsageService {
    @Autowired
    private ConBomUsageMapper conBomUsageMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "BOM用途";

    /**
     * 查询BOM用途
     *
     * @param sid BOM用途ID
     * @return BOM用途
     */
    @Override
    public ConBomUsage selectConBomUsageById(Long sid) {
        ConBomUsage conBomUsage = conBomUsageMapper.selectConBomUsageById(sid);
        MongodbUtil.find(conBomUsage);
        return conBomUsage;
    }

    /**
     * 查询BOM用途列表
     *
     * @param conBomUsage BOM用途
     * @return BOM用途
     */
    @Override
    public List<ConBomUsage> selectConBomUsageList(ConBomUsage conBomUsage) {
        return conBomUsageMapper.selectConBomUsageList(conBomUsage);
    }

    /**
     * 新增BOM用途
     * 需要注意编码重复校验
     *
     * @param conBomUsage BOM用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBomUsage(ConBomUsage conBomUsage) {
        List<ConBomUsage> codeList = conBomUsageMapper.selectList(new QueryWrapper<ConBomUsage>().lambda()
                .eq(ConBomUsage::getCode, conBomUsage.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBomUsage> nameList = conBomUsageMapper.selectList(new QueryWrapper<ConBomUsage>().lambda()
                .eq(ConBomUsage::getName, conBomUsage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBomUsageMapper.insert(conBomUsage);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBomUsage.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改BOM用途
     *
     * @param conBomUsage BOM用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBomUsage(ConBomUsage conBomUsage) {
        ConBomUsage response = conBomUsageMapper.selectConBomUsageById(conBomUsage.getSid());
        int row = conBomUsageMapper.updateById(conBomUsage);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBomUsage.getSid(), BusinessType.UPDATE.getValue(), response, conBomUsage, TITLE);
        }
        return row;
    }

    /**
     * 变更BOM用途
     *
     * @param conBomUsage BOM用途
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBomUsage(ConBomUsage conBomUsage) {
        List<ConBomUsage> nameList = conBomUsageMapper.selectList(new QueryWrapper<ConBomUsage>().lambda()
                .eq(ConBomUsage::getName, conBomUsage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBomUsage.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBomUsage.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBomUsage response = conBomUsageMapper.selectConBomUsageById(conBomUsage.getSid());
        int row = conBomUsageMapper.updateAllById(conBomUsage);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBomUsage.getSid(), BusinessType.CHANGE.getValue(), response, conBomUsage, TITLE);
        }
        return row;
    }

    /**
     * 批量删除BOM用途
     *
     * @param sids 需要删除的BOM用途ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBomUsageByIds(List<Long> sids) {
        return conBomUsageMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBomUsage
     * @return
     */
    @Override
    public int changeStatus(ConBomUsage conBomUsage) {
        int row = 0;
        Long[] sids = conBomUsage.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBomUsage.setSid(id);
                row = conBomUsageMapper.updateById(conBomUsage);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBomUsage.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBomUsage.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBomUsage
     * @return
     */
    @Override
    public int check(ConBomUsage conBomUsage) {
        int row = 0;
        Long[] sids = conBomUsage.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBomUsage.setSid(id);
                row = conBomUsageMapper.updateById(conBomUsage);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBomUsage.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
