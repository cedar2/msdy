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
import com.platform.ems.plug.domain.ConBuTypeVendorFundsFreeze;
import com.platform.ems.plug.mapper.ConBuTypeVendorFundsFreezeMapper;
import com.platform.ems.plug.service.IConBuTypeVendorFundsFreezeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_供应商暂押款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeVendorFundsFreezeServiceImpl extends ServiceImpl<ConBuTypeVendorFundsFreezeMapper, ConBuTypeVendorFundsFreeze> implements IConBuTypeVendorFundsFreezeService {
    @Autowired
    private ConBuTypeVendorFundsFreezeMapper conBuTypeVendorFundsFreezeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_供应商暂押款";

    /**
     * 查询业务类型_供应商暂押款
     *
     * @param sid 业务类型_供应商暂押款ID
     * @return 业务类型_供应商暂押款
     */
    @Override
    public ConBuTypeVendorFundsFreeze selectConBuTypeVendorFundsFreezeById(Long sid) {
        ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze = conBuTypeVendorFundsFreezeMapper.selectConBuTypeVendorFundsFreezeById(sid);
        MongodbUtil.find(conBuTypeVendorFundsFreeze);
        return conBuTypeVendorFundsFreeze;
    }

    /**
     * 查询业务类型_供应商暂押款列表
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 业务类型_供应商暂押款
     */
    @Override
    public List<ConBuTypeVendorFundsFreeze> selectConBuTypeVendorFundsFreezeList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return conBuTypeVendorFundsFreezeMapper.selectConBuTypeVendorFundsFreezeList(conBuTypeVendorFundsFreeze);
    }

    /**
     * 新增业务类型_供应商暂押款
     * 需要注意编码重复校验
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        List<ConBuTypeVendorFundsFreeze> codeList = conBuTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeVendorFundsFreeze>().lambda()
                .eq(ConBuTypeVendorFundsFreeze::getCode, conBuTypeVendorFundsFreeze.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeVendorFundsFreeze> nameList = conBuTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeVendorFundsFreeze>().lambda()
                .eq(ConBuTypeVendorFundsFreeze::getName, conBuTypeVendorFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        conBuTypeVendorFundsFreeze.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conBuTypeVendorFundsFreezeMapper.insert(conBuTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeVendorFundsFreeze.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_供应商暂押款
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        ConBuTypeVendorFundsFreeze response = conBuTypeVendorFundsFreezeMapper.selectConBuTypeVendorFundsFreezeById(conBuTypeVendorFundsFreeze.getSid());
        int row = conBuTypeVendorFundsFreezeMapper.updateById(conBuTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorFundsFreeze.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeVendorFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_供应商暂押款
     *
     * @param conBuTypeVendorFundsFreeze 业务类型_供应商暂押款
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeVendorFundsFreeze(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        List<ConBuTypeVendorFundsFreeze> nameList = conBuTypeVendorFundsFreezeMapper.selectList(new QueryWrapper<ConBuTypeVendorFundsFreeze>().lambda()
                .eq(ConBuTypeVendorFundsFreeze::getName, conBuTypeVendorFundsFreeze.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypeVendorFundsFreeze.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeVendorFundsFreeze.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeVendorFundsFreeze response = conBuTypeVendorFundsFreezeMapper.selectConBuTypeVendorFundsFreezeById(conBuTypeVendorFundsFreeze.getSid());
        int row = conBuTypeVendorFundsFreezeMapper.updateAllById(conBuTypeVendorFundsFreeze);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorFundsFreeze.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeVendorFundsFreeze, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_供应商暂押款
     *
     * @param sids 需要删除的业务类型_供应商暂押款ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeVendorFundsFreezeByIds(List<Long> sids) {
        return conBuTypeVendorFundsFreezeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorFundsFreeze
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        int row = 0;
        Long[] sids = conBuTypeVendorFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorFundsFreezeMapper.update(null, new UpdateWrapper<ConBuTypeVendorFundsFreeze>().lambda().set(ConBuTypeVendorFundsFreeze::getStatus, conBuTypeVendorFundsFreeze.getStatus())
                    .in(ConBuTypeVendorFundsFreeze::getSid, sids));
            for (Long id : sids) {
                conBuTypeVendorFundsFreeze.setSid(id);
                row = conBuTypeVendorFundsFreezeMapper.updateById(conBuTypeVendorFundsFreeze);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeVendorFundsFreeze.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeVendorFundsFreeze.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorFundsFreeze
     * @return
     */
    @Override
    public int check(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        int row = 0;
        Long[] sids = conBuTypeVendorFundsFreeze.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorFundsFreezeMapper.update(null, new UpdateWrapper<ConBuTypeVendorFundsFreeze>().lambda().set(ConBuTypeVendorFundsFreeze::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeVendorFundsFreeze::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_供应商暂押款下拉框列表
     */
    @Override
    public List<ConBuTypeVendorFundsFreeze> getList(ConBuTypeVendorFundsFreeze conBuTypeVendorFundsFreeze) {
        return conBuTypeVendorFundsFreezeMapper.getList(conBuTypeVendorFundsFreeze);
    }
}
