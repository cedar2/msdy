package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManSchedulingInfoMapper;
import com.platform.ems.domain.ManSchedulingInfo;
import com.platform.ems.service.IManSchedulingInfoService;

/**
 * 生产排程信息Service业务层处理
 *
 * @author chenkw
 * @date 2023-05-24
 */
@Service
@SuppressWarnings("all")
public class ManSchedulingInfoServiceImpl extends ServiceImpl<ManSchedulingInfoMapper, ManSchedulingInfo> implements IManSchedulingInfoService {
    @Autowired
    private ManSchedulingInfoMapper manSchedulingInfoMapper;

    private static final String TITLE = "生产排程信息";

    /**
     * 查询生产排程信息
     *
     * @param schedulingInfoSid 生产排程信息ID
     * @return 生产排程信息
     */
    @Override
    public ManSchedulingInfo selectManSchedulingInfoById(Long schedulingInfoSid) {
        ManSchedulingInfo manSchedulingInfo = manSchedulingInfoMapper.selectManSchedulingInfoById(schedulingInfoSid);
        MongodbUtil.find(manSchedulingInfo);
        return manSchedulingInfo;
    }

    /**
     * 查询生产排程信息列表
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 生产排程信息
     */
    @Override
    public List<ManSchedulingInfo> selectManSchedulingInfoList(ManSchedulingInfo manSchedulingInfo) {
        return manSchedulingInfoMapper.selectManSchedulingInfoList(manSchedulingInfo);
    }

    /**
     * 新增生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManSchedulingInfo(ManSchedulingInfo manSchedulingInfo) {
        int row = manSchedulingInfoMapper.insert(manSchedulingInfo);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManSchedulingInfo(), manSchedulingInfo);
            MongodbDeal.insert(manSchedulingInfo.getSchedulingInfoSid(), manSchedulingInfo.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 新增生产排程信息 根据商品道序
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManSchedulingInfoByProcessStep(ManSchedulingInfo manSchedulingInfo) {
        int row = 0;
        List<ManSchedulingInfo> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(manSchedulingInfo.getProcessStepItemList())) {
            list = BeanCopyUtils.copyListProperties(manSchedulingInfo.getProcessStepItemList(), ManSchedulingInfo::new);
            list.forEach(item->{
                item.setHandleStatus(manSchedulingInfo.getHandleStatus()).setWorkCenterSid(manSchedulingInfo.getWorkCenterSid())
                        .setWorkCenterCode(manSchedulingInfo.getWorkCenterCode()).setWorkCenterName(manSchedulingInfo.getWorkCenterName())
                        .setWorkstationSid(manSchedulingInfo.getWorkstationSid()).setWorkstationCode(manSchedulingInfo.getWorkstationCode())
                        .setWorkstationName(manSchedulingInfo.getWorkstationName())
                        .setProductDimension(manSchedulingInfo.getProductDimension()).setProductSid(manSchedulingInfo.getProductSid())
                        .setProductCode(manSchedulingInfo.getProductCode()).setProductName(manSchedulingInfo.getProductName())
                        .setSku1Sid(manSchedulingInfo.getSku1Sid()).setSku1Code(manSchedulingInfo.getSku1Code())
                        .setSku1Name(manSchedulingInfo.getSku1Name()).setSku1Type(manSchedulingInfo.getSku1Type())
                        .setSku2Sid(manSchedulingInfo.getSku2Sid()).setSku1Code(manSchedulingInfo.getSku2Code())
                        .setSku2Name(manSchedulingInfo.getSku2Name()).setSku1Type(manSchedulingInfo.getSku2Type())
                        .setFenpeiQuantity(manSchedulingInfo.getFenpeiQuantity()).setPlanStartDate(manSchedulingInfo.getPlanStartDate())
                        .setPlanEndDate(manSchedulingInfo.getPlanEndDate()).setPaichanBatch(manSchedulingInfo.getPaichanBatch());
            });
            row = manSchedulingInfoMapper.inserts(list);
        }
        if (row > 0) {
            list.forEach(item->{
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new ManSchedulingInfo(), item);
                MongodbDeal.insert(item.getSchedulingInfoSid(), item.getHandleStatus(), msgList, TITLE, null);
            });
        }
        return row;
    }

    /**
     * 修改生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManSchedulingInfo(ManSchedulingInfo manSchedulingInfo) {
        ManSchedulingInfo original = manSchedulingInfoMapper.selectManSchedulingInfoById(manSchedulingInfo.getSchedulingInfoSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(manSchedulingInfo.getHandleStatus())) {
            manSchedulingInfo.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, manSchedulingInfo);
        if (CollectionUtil.isNotEmpty(msgList)) {
            manSchedulingInfo.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = manSchedulingInfoMapper.updateAllById(manSchedulingInfo);
        if (row > 0) {
            //插入日志
            MongodbDeal.update(manSchedulingInfo.getSchedulingInfoSid(), original.getHandleStatus(), manSchedulingInfo.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产排程信息
     *
     * @param manSchedulingInfo 生产排程信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManSchedulingInfo(ManSchedulingInfo manSchedulingInfo) {
        ManSchedulingInfo response = manSchedulingInfoMapper.selectManSchedulingInfoById(manSchedulingInfo.getSchedulingInfoSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, manSchedulingInfo);
        if (CollectionUtil.isNotEmpty(msgList)) {
            manSchedulingInfo.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = manSchedulingInfoMapper.updateAllById(manSchedulingInfo);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manSchedulingInfo.getSchedulingInfoSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产排程信息
     *
     * @param schedulingInfoSids 需要删除的生产排程信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManSchedulingInfoByIds(List<Long> schedulingInfoSids) {
        List<ManSchedulingInfo> list = manSchedulingInfoMapper.selectList(new QueryWrapper<ManSchedulingInfo>()
                .lambda().in(ManSchedulingInfo::getSchedulingInfoSid, schedulingInfoSids));
        int row = manSchedulingInfoMapper.deleteBatchIds(schedulingInfoSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManSchedulingInfo());
                MongodbUtil.insertUserLog(o.getSchedulingInfoSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manSchedulingInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManSchedulingInfo manSchedulingInfo) {
        int row = 0;
        Long[] sids = manSchedulingInfo.getSchedulingInfoSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManSchedulingInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManSchedulingInfo::getSchedulingInfoSid, sids);
            updateWrapper.set(ManSchedulingInfo::getHandleStatus, manSchedulingInfo.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manSchedulingInfo.getHandleStatus())) {
                updateWrapper.set(ManSchedulingInfo::getConfirmDate, new Date());
                updateWrapper.set(ManSchedulingInfo::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manSchedulingInfoMapper.update(null, updateWrapper);
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, manSchedulingInfo.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

}
