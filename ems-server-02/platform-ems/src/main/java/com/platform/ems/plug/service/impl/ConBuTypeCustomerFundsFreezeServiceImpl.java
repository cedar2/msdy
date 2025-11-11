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
import com.platform.ems.plug.domain.ConBuTypeCustomerFundsFreeze;
import com.platform.ems.plug.mapper.ConBuTypeCustomerFundsFreezeMapper;
import com.platform.ems.plug.service.IConBuTypeCustomerFundsFreezeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_客户暂押款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeCustomerFundsFreezeServiceImpl extends ServiceImpl<ConBuTypeCustomerFundsFreezeMapper, ConBuTypeCustomerFundsFreeze> implements IConBuTypeCustomerFundsFreezeService {
    @Autowired
    private ConBuTypeCustomerFundsFreezeMapper conBuTypeCustomerFundsFreezeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_客户暂押款";

    /**
     * 查询业务类型_客户暂押款
     *
     * @param sid 业务类型_客户暂押款ID
     * @return 业务类型_客户暂押款
     */
    @Override
    public ConBuTypeCustomerFundsFreeze selectConBuTypeCustomerFundsFreezeById(Long sid) {
        ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze = conBuTypeCustomerFundsFreezeMapper.selectConBuTypeCustomerFundsFreezeById(sid);
        MongodbUtil.find(conBuTypeCustomerFundsFreeze);
        return conBuTypeCustomerFundsFreeze;
    }

    /**
     * 查询业务类型_客户暂押款列表
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 业务类型_客户暂押款
     */
    @Override
    public List<ConBuTypeCustomerFundsFreeze> selectConBuTypeCustomerFundsFreezeList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return conBuTypeCustomerFundsFreezeMapper.selectConBuTypeCustomerFundsFreezeList(conBuTypeCustomerFundsFreeze);
    }

    /**
     * 新增业务类型_客户暂押款
     * 需要注意编码重复校验
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        List<ConBuTypeCustomerFundsFreeze> codeList = conBuTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeCustomerFundsFreeze>().lambda()
                .eq(ConBuTypeCustomerFundsFreeze::getCode, conBuTypeCustomerFundsFreeze.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeCustomerFundsFreeze> nameList = conBuTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeCustomerFundsFreeze>().lambda()
                .eq(ConBuTypeCustomerFundsFreeze::getName, conBuTypeCustomerFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        conBuTypeCustomerFundsFreeze.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conBuTypeCustomerFundsFreezeMapper.insert(conBuTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeCustomerFundsFreeze.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_客户暂押款
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        ConBuTypeCustomerFundsFreeze response = conBuTypeCustomerFundsFreezeMapper.selectConBuTypeCustomerFundsFreezeById(conBuTypeCustomerFundsFreeze.getSid());
        int row = conBuTypeCustomerFundsFreezeMapper.updateById(conBuTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerFundsFreeze.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeCustomerFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_客户暂押款
     *
     * @param conBuTypeCustomerFundsFreeze 业务类型_客户暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeCustomerFundsFreeze(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        List<ConBuTypeCustomerFundsFreeze> nameList = conBuTypeCustomerFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeCustomerFundsFreeze>().lambda()
                .eq(ConBuTypeCustomerFundsFreeze::getName, conBuTypeCustomerFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypeCustomerFundsFreeze.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeCustomerFundsFreeze.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeCustomerFundsFreeze response = conBuTypeCustomerFundsFreezeMapper.selectConBuTypeCustomerFundsFreezeById(conBuTypeCustomerFundsFreeze.getSid());
        int row = conBuTypeCustomerFundsFreezeMapper.updateAllById(conBuTypeCustomerFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerFundsFreeze.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeCustomerFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_客户暂押款
     *
     * @param sids 需要删除的业务类型_客户暂押款ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeCustomerFundsFreezeByIds(List<Long> sids) {
        return conBuTypeCustomerFundsFreezeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerFundsFreeze
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        int row = 0;
        Long[] sids = conBuTypeCustomerFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerFundsFreezeMapper.update(null, new UpdateWrapper<ConBuTypeCustomerFundsFreeze>().lambda().set(ConBuTypeCustomerFundsFreeze::getStatus, conBuTypeCustomerFundsFreeze.getStatus())
                    .in(ConBuTypeCustomerFundsFreeze::getSid, sids));
            for (Long id : sids) {
                conBuTypeCustomerFundsFreeze.setSid(id);
                row = conBuTypeCustomerFundsFreezeMapper.updateById(conBuTypeCustomerFundsFreeze);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeCustomerFundsFreeze.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeCustomerFundsFreeze.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerFundsFreeze
     * @return
     */
    @Override
    public int check(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        int row = 0;
        Long[] sids = conBuTypeCustomerFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerFundsFreezeMapper.update(null, new UpdateWrapper<ConBuTypeCustomerFundsFreeze>().lambda().set(ConBuTypeCustomerFundsFreeze::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeCustomerFundsFreeze::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_客户暂押款下拉框列表
     */
    @Override
    public List<ConBuTypeCustomerFundsFreeze> getList(ConBuTypeCustomerFundsFreeze conBuTypeCustomerFundsFreeze) {
        return conBuTypeCustomerFundsFreezeMapper.getList(conBuTypeCustomerFundsFreeze);
    }
}
