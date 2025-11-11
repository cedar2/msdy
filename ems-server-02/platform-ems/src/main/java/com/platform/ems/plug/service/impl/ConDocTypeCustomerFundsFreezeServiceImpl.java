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
import com.platform.ems.plug.domain.ConDocTypeCustomerFundsFreeze;
import com.platform.ems.plug.mapper.ConDocTypeCustomerFundsFreezeMapper;
import com.platform.ems.plug.service.IConDocTypeCustomerFundsFreezeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_客户暂押款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeCustomerFundsFreezeServiceImpl extends ServiceImpl<ConDocTypeCustomerFundsFreezeMapper, ConDocTypeCustomerFundsFreeze> implements IConDocTypeCustomerFundsFreezeService {
    @Autowired
    private ConDocTypeCustomerFundsFreezeMapper conDocTypeCustomerFundsFreezeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_客户暂押款";

    /**
     * 查询单据类型_客户暂押款
     *
     * @param sid 单据类型_客户暂押款ID
     * @return 单据类型_客户暂押款
     */
    @Override
    public ConDocTypeCustomerFundsFreeze selectConDocTypeCustomerFundsFreezeById(Long sid) {
        ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze = conDocTypeCustomerFundsFreezeMapper.selectConDocTypeCustomerFundsFreezeById(sid);
        MongodbUtil.find(conDocTypeCustomerFundsFreeze);
        return conDocTypeCustomerFundsFreeze;
    }

    /**
     * 查询单据类型_客户暂押款列表
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 单据类型_客户暂押款
     */
    @Override
    public List<ConDocTypeCustomerFundsFreeze> selectConDocTypeCustomerFundsFreezeList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return conDocTypeCustomerFundsFreezeMapper.selectConDocTypeCustomerFundsFreezeList(conDocTypeCustomerFundsFreeze);
    }

    /**
     * 新增单据类型_客户暂押款
     * 需要注意编码重复校验
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        List<ConDocTypeCustomerFundsFreeze> codeList = conDocTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeCustomerFundsFreeze>().lambda()
                .eq(ConDocTypeCustomerFundsFreeze::getCode, conDocTypeCustomerFundsFreeze.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeCustomerFundsFreeze> nameList = conDocTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeCustomerFundsFreeze>().lambda()
                .eq(ConDocTypeCustomerFundsFreeze::getName, conDocTypeCustomerFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeCustomerFundsFreeze);
        int row = conDocTypeCustomerFundsFreezeMapper.insert(conDocTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeCustomerFundsFreeze.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeCustomerFundsFreeze o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_客户暂押款
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        ConDocTypeCustomerFundsFreeze response = conDocTypeCustomerFundsFreezeMapper.selectConDocTypeCustomerFundsFreezeById(conDocTypeCustomerFundsFreeze.getSid());
        int row = conDocTypeCustomerFundsFreezeMapper.updateById(conDocTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerFundsFreeze.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeCustomerFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_客户暂押款
     *
     * @param conDocTypeCustomerFundsFreeze 单据类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeCustomerFundsFreeze(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        List<ConDocTypeCustomerFundsFreeze> nameList = conDocTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConDocTypeCustomerFundsFreeze>().lambda()
                .eq(ConDocTypeCustomerFundsFreeze::getName, conDocTypeCustomerFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conDocTypeCustomerFundsFreeze.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conDocTypeCustomerFundsFreeze);
        conDocTypeCustomerFundsFreeze.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConDocTypeCustomerFundsFreeze response = conDocTypeCustomerFundsFreezeMapper.selectConDocTypeCustomerFundsFreezeById(conDocTypeCustomerFundsFreeze.getSid());
        int row = conDocTypeCustomerFundsFreezeMapper.updateAllById(conDocTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerFundsFreeze.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeCustomerFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_客户暂押款
     *
     * @param sids 需要删除的单据类型_客户暂押款ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeCustomerFundsFreezeByIds(List<Long> sids) {
        return conDocTypeCustomerFundsFreezeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeCustomerFundsFreeze
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        int row = 0;
        Long[] sids = conDocTypeCustomerFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeCustomerFundsFreezeMapper.update(null, new UpdateWrapper<ConDocTypeCustomerFundsFreeze>().lambda().set(ConDocTypeCustomerFundsFreeze::getStatus, conDocTypeCustomerFundsFreeze.getStatus())
                    .in(ConDocTypeCustomerFundsFreeze::getSid, sids));
            for (Long id : sids) {
                conDocTypeCustomerFundsFreeze.setSid(id);
                row = conDocTypeCustomerFundsFreezeMapper.updateById(conDocTypeCustomerFundsFreeze);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeCustomerFundsFreeze.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeCustomerFundsFreeze.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeCustomerFundsFreeze
     * @return
     */
    @Override
    public int check(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        int row = 0;
        Long[] sids = conDocTypeCustomerFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeCustomerFundsFreezeMapper.update(null, new UpdateWrapper<ConDocTypeCustomerFundsFreeze>().lambda().set(ConDocTypeCustomerFundsFreeze::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeCustomerFundsFreeze::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_客户暂押款下拉框列表
     */
    @Override
    public List<ConDocTypeCustomerFundsFreeze> getList(ConDocTypeCustomerFundsFreeze conDocTypeCustomerFundsFreeze) {
        return conDocTypeCustomerFundsFreezeMapper.getList(conDocTypeCustomerFundsFreeze);
    }
}
