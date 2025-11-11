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
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptanceSale;
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
import com.platform.ems.plug.mapper.ConBuTypeVendorDeductionMapper;
import com.platform.ems.plug.domain.ConBuTypeVendorDeduction;
import com.platform.ems.plug.service.IConBuTypeVendorDeductionService;

/**
 * 业务类型_供应商扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-03
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeVendorDeductionServiceImpl extends ServiceImpl<ConBuTypeVendorDeductionMapper, ConBuTypeVendorDeduction> implements IConBuTypeVendorDeductionService {
    @Autowired
    private ConBuTypeVendorDeductionMapper conBuTypeVendorDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_供应商扣款单";

    /**
     * 查询业务类型_供应商扣款单
     *
     * @param sid 业务类型_供应商扣款单ID
     * @return 业务类型_供应商扣款单
     */
    @Override
    public ConBuTypeVendorDeduction selectConBuTypeVendorDeductionById(Long sid) {
        ConBuTypeVendorDeduction conBuTypeVendorDeduction = conBuTypeVendorDeductionMapper.selectConBuTypeVendorDeductionById(sid);
        MongodbUtil.find(conBuTypeVendorDeduction);
        return conBuTypeVendorDeduction;
    }

    /**
     * 查询业务类型_供应商扣款单列表
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 业务类型_供应商扣款单
     */
    @Override
    public List<ConBuTypeVendorDeduction> selectConBuTypeVendorDeductionList(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        return conBuTypeVendorDeductionMapper.selectConBuTypeVendorDeductionList(conBuTypeVendorDeduction);
    }

    /**
     * 新增业务类型_供应商扣款单
     * 需要注意编码重复校验
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        List<ConBuTypeVendorDeduction> codeList = conBuTypeVendorDeductionMapper.selectList(new QueryWrapper<ConBuTypeVendorDeduction>().lambda()
                .eq(ConBuTypeVendorDeduction::getCode, conBuTypeVendorDeduction.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeVendorDeduction> nameList = conBuTypeVendorDeductionMapper.selectList(new QueryWrapper<ConBuTypeVendorDeduction>().lambda()
                .eq(ConBuTypeVendorDeduction::getName, conBuTypeVendorDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBuTypeVendorDeductionMapper.insert(conBuTypeVendorDeduction);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeVendorDeduction.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_供应商扣款单
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        ConBuTypeVendorDeduction response = conBuTypeVendorDeductionMapper.selectConBuTypeVendorDeductionById(conBuTypeVendorDeduction.getSid());
        int row = conBuTypeVendorDeductionMapper.updateById(conBuTypeVendorDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorDeduction.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeVendorDeduction, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_供应商扣款单
     *
     * @param conBuTypeVendorDeduction 业务类型_供应商扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeVendorDeduction(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        List<ConBuTypeVendorDeduction> nameList = conBuTypeVendorDeductionMapper.selectList(new QueryWrapper<ConBuTypeVendorDeduction>().lambda()
                .eq(ConBuTypeVendorDeduction::getName, conBuTypeVendorDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeVendorDeduction.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeVendorDeduction.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeVendorDeduction response = conBuTypeVendorDeductionMapper.selectConBuTypeVendorDeductionById(conBuTypeVendorDeduction.getSid());
        int row = conBuTypeVendorDeductionMapper.updateAllById(conBuTypeVendorDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorDeduction.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeVendorDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_供应商扣款单
     *
     * @param sids 需要删除的业务类型_供应商扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeVendorDeductionByIds(List<Long> sids) {
        return conBuTypeVendorDeductionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorDeduction
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        int row = 0;
        Long[] sids = conBuTypeVendorDeduction.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorDeductionMapper.update(null, new UpdateWrapper<ConBuTypeVendorDeduction>().lambda().set(ConBuTypeVendorDeduction::getStatus, conBuTypeVendorDeduction.getStatus())
                    .in(ConBuTypeVendorDeduction::getSid, sids));
            for (Long id : sids) {
                conBuTypeVendorDeduction.setSid(id);
                row = conBuTypeVendorDeductionMapper.updateById(conBuTypeVendorDeduction);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeVendorDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeVendorDeduction.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorDeduction
     * @return
     */
    @Override
    public int check(ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        int row = 0;
        Long[] sids = conBuTypeVendorDeduction.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorDeductionMapper.update(null, new UpdateWrapper<ConBuTypeVendorDeduction>().lambda().set(ConBuTypeVendorDeduction::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeVendorDeduction::getSid, sids));
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
    public List<ConBuTypeVendorDeduction> getConBuTypeVendorDeductionList() {
        return conBuTypeVendorDeductionMapper.getConBuTypeVendorDeductionList();
    }
}
