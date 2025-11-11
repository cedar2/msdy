package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocTypeVendorFundsFreeze;
import com.platform.ems.plug.mapper.ConDocTypeVendorFundsFreezeMapper;
import com.platform.ems.plug.service.IConDocTypeVendorFundsFreezeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_供应商暂押款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeVendorFundsFreezeServiceImpl extends ServiceImpl<ConDocTypeVendorFundsFreezeMapper, ConDocTypeVendorFundsFreeze> implements IConDocTypeVendorFundsFreezeService {
    @Autowired
    private ConDocTypeVendorFundsFreezeMapper conDocTypeVendorFundsFreezeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_供应商暂押款";

    /**
     * 查询单据类型_供应商暂押款
     *
     * @param sid 单据类型_供应商暂押款ID
     * @return 单据类型_供应商暂押款
     */
    @Override
    public ConDocTypeVendorFundsFreeze selectConDocTypeVendorFundsFreezeById(Long sid) {
        ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze = conDocTypeVendorFundsFreezeMapper.selectConDocTypeVendorFundsFreezeById(sid);
        MongodbUtil.find(conDocTypeVendorFundsFreeze);
        return conDocTypeVendorFundsFreeze;
    }

    /**
     * 查询单据类型_供应商暂押款列表
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 单据类型_供应商暂押款
     */
    @Override
    public List<ConDocTypeVendorFundsFreeze> selectConDocTypeVendorFundsFreezeList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        return conDocTypeVendorFundsFreezeMapper.selectConDocTypeVendorFundsFreezeList(conDocTypeVendorFundsFreeze);
    }

    /**
     * 新增单据类型_供应商暂押款
     * 需要注意编码重复校验
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        List<ConDocTypeVendorFundsFreeze> codeList = conDocTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeVendorFundsFreeze>().lambda()
                .eq(ConDocTypeVendorFundsFreeze::getCode, conDocTypeVendorFundsFreeze.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeVendorFundsFreeze> nameList = conDocTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeVendorFundsFreeze>().lambda()
                .eq(ConDocTypeVendorFundsFreeze::getName, conDocTypeVendorFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeVendorFundsFreeze);
        int row = conDocTypeVendorFundsFreezeMapper.insert(conDocTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeVendorFundsFreeze.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeVendorFundsFreeze o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_供应商暂押款
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        ConDocTypeVendorFundsFreeze response = conDocTypeVendorFundsFreezeMapper.selectConDocTypeVendorFundsFreezeById(conDocTypeVendorFundsFreeze.getSid());
        int row = conDocTypeVendorFundsFreezeMapper.updateById(conDocTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorFundsFreeze.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeVendorFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_供应商暂押款
     *
     * @param conDocTypeVendorFundsFreeze 单据类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeVendorFundsFreeze(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        List<ConDocTypeVendorFundsFreeze> nameList = conDocTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeVendorFundsFreeze>().lambda()
                .eq(ConDocTypeVendorFundsFreeze::getName, conDocTypeVendorFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conDocTypeVendorFundsFreeze.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conDocTypeVendorFundsFreeze);
        conDocTypeVendorFundsFreeze.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConDocTypeVendorFundsFreeze response = conDocTypeVendorFundsFreezeMapper.selectConDocTypeVendorFundsFreezeById(conDocTypeVendorFundsFreeze.getSid());
        int row = conDocTypeVendorFundsFreezeMapper.updateAllById(conDocTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorFundsFreeze.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeVendorFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_供应商暂押款
     *
     * @param sids 需要删除的单据类型_供应商暂押款ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeVendorFundsFreezeByIds(List<Long> sids) {
        return conDocTypeVendorFundsFreezeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeVendorFundsFreeze
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        int row = 0;
        Long[] sids = conDocTypeVendorFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeVendorFundsFreezeMapper.update(null, new UpdateWrapper<ConDocTypeVendorFundsFreeze>().lambda().set(ConDocTypeVendorFundsFreeze::getStatus, conDocTypeVendorFundsFreeze.getStatus())
                    .in(ConDocTypeVendorFundsFreeze::getSid, sids));
            for (Long id : sids) {
                conDocTypeVendorFundsFreeze.setSid(id);
                row = conDocTypeVendorFundsFreezeMapper.updateById(conDocTypeVendorFundsFreeze);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeVendorFundsFreeze.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeVendorFundsFreeze.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeVendorFundsFreeze
     * @return
     */
    @Override
    public int check(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        int row = 0;
        Long[] sids = conDocTypeVendorFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeVendorFundsFreezeMapper.update(null, new UpdateWrapper<ConDocTypeVendorFundsFreeze>().lambda().set(ConDocTypeVendorFundsFreeze::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeVendorFundsFreeze::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_供应商暂押款下拉框列表
     */
    @Override
    public List<ConDocTypeVendorFundsFreeze> getList(ConDocTypeVendorFundsFreeze conDocTypeVendorFundsFreeze) {
        return conDocTypeVendorFundsFreezeMapper.getList(conDocTypeVendorFundsFreeze);
    }
}
