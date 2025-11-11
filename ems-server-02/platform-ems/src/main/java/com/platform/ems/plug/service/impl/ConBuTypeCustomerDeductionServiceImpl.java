package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConBusinessChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConBuTypeCustomerDeductionMapper;
import com.platform.ems.plug.domain.ConBuTypeCustomerDeduction;
import com.platform.ems.plug.service.IConBuTypeCustomerDeductionService;

/**
 * 业务类型_客户扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-03
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeCustomerDeductionServiceImpl extends ServiceImpl<ConBuTypeCustomerDeductionMapper, ConBuTypeCustomerDeduction> implements IConBuTypeCustomerDeductionService {
    @Autowired
    private ConBuTypeCustomerDeductionMapper conBuTypeCustomerDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_客户扣款单";

    /**
     * 查询业务类型_客户扣款单
     *
     * @param sid 业务类型_客户扣款单ID
     * @return 业务类型_客户扣款单
     */
    @Override
    public ConBuTypeCustomerDeduction selectConBuTypeCustomerDeductionById(Long sid) {
        ConBuTypeCustomerDeduction conBuTypeCustomerDeduction = conBuTypeCustomerDeductionMapper.selectConBuTypeCustomerDeductionById(sid);
        MongodbUtil.find(conBuTypeCustomerDeduction);
        return conBuTypeCustomerDeduction;
    }

    /**
     * 查询业务类型_客户扣款单列表
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 业务类型_客户扣款单
     */
    @Override
    public List<ConBuTypeCustomerDeduction> selectConBuTypeCustomerDeductionList(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        return conBuTypeCustomerDeductionMapper.selectConBuTypeCustomerDeductionList(conBuTypeCustomerDeduction);
    }

    /**
     * 新增业务类型_客户扣款单
     * 需要注意编码重复校验
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        List<ConBuTypeCustomerDeduction> codeList = conBuTypeCustomerDeductionMapper.selectList(new QueryWrapper<ConBuTypeCustomerDeduction>().lambda()
                .eq(ConBuTypeCustomerDeduction::getCode, conBuTypeCustomerDeduction.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeCustomerDeduction> nameList = conBuTypeCustomerDeductionMapper.selectList(new QueryWrapper<ConBuTypeCustomerDeduction>().lambda()
                .eq(ConBuTypeCustomerDeduction::getName, conBuTypeCustomerDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBuTypeCustomerDeductionMapper.insert(conBuTypeCustomerDeduction);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeCustomerDeduction.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_客户扣款单
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        ConBuTypeCustomerDeduction response = conBuTypeCustomerDeductionMapper.selectConBuTypeCustomerDeductionById(conBuTypeCustomerDeduction.getSid());
        int row = conBuTypeCustomerDeductionMapper.updateById(conBuTypeCustomerDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerDeduction.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeCustomerDeduction, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_客户扣款单
     *
     * @param conBuTypeCustomerDeduction 业务类型_客户扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeCustomerDeduction(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        List<ConBuTypeCustomerDeduction> nameList = conBuTypeCustomerDeductionMapper.selectList(new QueryWrapper<ConBuTypeCustomerDeduction>().lambda()
                .eq(ConBuTypeCustomerDeduction::getName, conBuTypeCustomerDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeCustomerDeduction.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeCustomerDeduction.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeCustomerDeduction response = conBuTypeCustomerDeductionMapper.selectConBuTypeCustomerDeductionById(conBuTypeCustomerDeduction.getSid());
        int row = conBuTypeCustomerDeductionMapper.updateAllById(conBuTypeCustomerDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerDeduction.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeCustomerDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_客户扣款单
     *
     * @param sids 需要删除的业务类型_客户扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeCustomerDeductionByIds(List<Long> sids) {
        return conBuTypeCustomerDeductionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerDeduction
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        int row = 0;
        Long[] sids = conBuTypeCustomerDeduction.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerDeductionMapper.update(null, new UpdateWrapper<ConBuTypeCustomerDeduction>().lambda().set(ConBuTypeCustomerDeduction::getStatus, conBuTypeCustomerDeduction.getStatus())
                    .in(ConBuTypeCustomerDeduction::getSid, sids));
            for (Long id : sids) {
                conBuTypeCustomerDeduction.setSid(id);
                row = conBuTypeCustomerDeductionMapper.updateById(conBuTypeCustomerDeduction);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeCustomerDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeCustomerDeduction.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerDeduction
     * @return
     */
    @Override
    public int check(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        int row = 0;
        Long[] sids = conBuTypeCustomerDeduction.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerDeductionMapper.update(null, new UpdateWrapper<ConBuTypeCustomerDeduction>().lambda().set(ConBuTypeCustomerDeduction::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeCustomerDeduction::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


    /**
     * 下拉框列表
     */
    @Override
    public List<ConBuTypeCustomerDeduction> getConBuTypeCustomerDeductionList() {
        return conBuTypeCustomerDeductionMapper.getConBuTypeCustomerDeductionList();
    }
}
