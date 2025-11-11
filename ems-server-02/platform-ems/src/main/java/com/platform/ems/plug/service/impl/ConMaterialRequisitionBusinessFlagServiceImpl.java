package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.plug.domain.ConBuStockBusinessFlag;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.plug.mapper.ConMaterialRequisitionBusinessFlagMapper;
import com.platform.ems.plug.domain.ConMaterialRequisitionBusinessFlag;
import com.platform.ems.plug.service.IConMaterialRequisitionBusinessFlagService;

/**
 * 业务标识_领退料Service业务层处理
 *
 * @author platform
 * @date 2024-11-10
 */
@Service
@SuppressWarnings("all" )
public class ConMaterialRequisitionBusinessFlagServiceImpl extends ServiceImpl<ConMaterialRequisitionBusinessFlagMapper,ConMaterialRequisitionBusinessFlag> implements IConMaterialRequisitionBusinessFlagService {
    @Autowired
    private ConMaterialRequisitionBusinessFlagMapper conMaterialRequisitionBusinessFlagMapper;

    private static final String TITLE = "业务标识_领退料" ;

    /**
     * 查询业务标识_领退料
     *
     * @param sid 业务标识_领退料ID
     * @return 业务标识_领退料
     */
    @Override
    public ConMaterialRequisitionBusinessFlag selectConMaterialRequisitionBusinessFlagById(Long sid) {
        ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag =conMaterialRequisitionBusinessFlagMapper.selectConMaterialRequisitionBusinessFlagById(sid);
        MongodbUtil.find(conMaterialRequisitionBusinessFlag);
        return conMaterialRequisitionBusinessFlag;
    }

    /**
     * 查询业务标识_领退料列表
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 业务标识_领退料
     */
    @Override
    public List<ConMaterialRequisitionBusinessFlag> selectConMaterialRequisitionBusinessFlagList(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        return conMaterialRequisitionBusinessFlagMapper.selectConMaterialRequisitionBusinessFlagList(conMaterialRequisitionBusinessFlag);
    }

    /**
     * 新增业务标识_领退料
     * 需要注意编码重复校验
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {

        List<ConMaterialRequisitionBusinessFlag> checkCode = conMaterialRequisitionBusinessFlagMapper.selectList(new QueryWrapper<ConMaterialRequisitionBusinessFlag>().lambda()
                .eq(ConMaterialRequisitionBusinessFlag::getCode, conMaterialRequisitionBusinessFlag.getCode()));
        if (CollectionUtil.isNotEmpty(checkCode)) {
            throw new CustomException("业务标识编码已存在，请核实！");
        }

        List<ConMaterialRequisitionBusinessFlag> checkName = conMaterialRequisitionBusinessFlagMapper.selectList(new QueryWrapper<ConMaterialRequisitionBusinessFlag>().lambda()
                .eq(ConMaterialRequisitionBusinessFlag::getName, conMaterialRequisitionBusinessFlag.getName()));
        if (CollectionUtil.isNotEmpty(checkCode)) {
            throw new CustomException("业务标识名称已存在，请核实！");
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(conMaterialRequisitionBusinessFlag.getHandleStatus())) {
            conMaterialRequisitionBusinessFlag.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }

        int row = conMaterialRequisitionBusinessFlagMapper.insert(conMaterialRequisitionBusinessFlag);
        if (row > 0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConMaterialRequisitionBusinessFlag(), conMaterialRequisitionBusinessFlag);
            MongodbDeal.insert(conMaterialRequisitionBusinessFlag.getSid(), conMaterialRequisitionBusinessFlag.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改业务标识_领退料
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        ConMaterialRequisitionBusinessFlag original = conMaterialRequisitionBusinessFlagMapper.selectConMaterialRequisitionBusinessFlagById(conMaterialRequisitionBusinessFlag.getSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(conMaterialRequisitionBusinessFlag.getHandleStatus())) {
            conMaterialRequisitionBusinessFlag.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, conMaterialRequisitionBusinessFlag);
        if (CollectionUtil.isNotEmpty(msgList)) {
            conMaterialRequisitionBusinessFlag.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }

        int row = conMaterialRequisitionBusinessFlagMapper.updateAllById(conMaterialRequisitionBusinessFlag);
        if (row > 0){
            //插入日志
            MongodbDeal.update(conMaterialRequisitionBusinessFlag.getSid(), original.getHandleStatus(),
            conMaterialRequisitionBusinessFlag.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更业务标识_领退料
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        List<ConMaterialRequisitionBusinessFlag> checkName = conMaterialRequisitionBusinessFlagMapper.selectList(new QueryWrapper<ConMaterialRequisitionBusinessFlag>().lambda()
                .eq(ConMaterialRequisitionBusinessFlag::getName, conMaterialRequisitionBusinessFlag.getName()));
        if (CollectionUtil.isNotEmpty(checkName) && !checkName.get(0).getSid().equals(conMaterialRequisitionBusinessFlag.getSid())) {
            throw new CustomException("业务标识名称已存在，请核实！");
        }else{
            ConMaterialRequisitionBusinessFlag response = conMaterialRequisitionBusinessFlagMapper.selectConMaterialRequisitionBusinessFlagById(conMaterialRequisitionBusinessFlag.getSid());
            // 更新人更新日期
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(response, conMaterialRequisitionBusinessFlag);
            if (CollectionUtil.isNotEmpty(msgList)) {
                conMaterialRequisitionBusinessFlag.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            }
            int row = conMaterialRequisitionBusinessFlagMapper.updateAllById(conMaterialRequisitionBusinessFlag);
            if (row > 0){
                //插入日志
                MongodbUtil.insertUserLog(conMaterialRequisitionBusinessFlag.getSid(), BusinessType.CHANGE.getValue(), response, conMaterialRequisitionBusinessFlag, TITLE);
            }
            return row;
        }
    }

    /**
     * 批量删除业务标识_领退料
     *
     * @param sids 需要删除的业务标识_领退料ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMaterialRequisitionBusinessFlagByIds(List<Long> sids) {
        List<ConMaterialRequisitionBusinessFlag> list = conMaterialRequisitionBusinessFlagMapper.selectList(new QueryWrapper<ConMaterialRequisitionBusinessFlag>()
                .lambda().in(ConMaterialRequisitionBusinessFlag::getSid, sids));
        int row = conMaterialRequisitionBusinessFlagMapper.deleteBatchIds(sids);
        if (row > 0){
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConMaterialRequisitionBusinessFlag());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     * @param conMaterialRequisitionBusinessFlag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        int row = 0;
        Long[] sids =conMaterialRequisitionBusinessFlag.getSidList();
        if (sids != null && sids.length > 0) {
            row = conMaterialRequisitionBusinessFlagMapper.update(null, new UpdateWrapper<ConMaterialRequisitionBusinessFlag>().lambda()
                    .set(ConMaterialRequisitionBusinessFlag::getStatus,conMaterialRequisitionBusinessFlag.getStatus() )
                    .in(ConMaterialRequisitionBusinessFlag::getSid, sids));
            if (row == 0) {
                throw new CheckedException("更改状态失败,请联系管理员" );
            }
            for (Long id : sids) {
                //插入日志
                String remark = conMaterialRequisitionBusinessFlag.getStatus().equals(com.platform.ems.constant.ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, conMaterialRequisitionBusinessFlag.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     *更改确认状态
     * @param conMaterialRequisitionBusinessFlag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag) {
        Long[] sids =conMaterialRequisitionBusinessFlag.getSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<ConMaterialRequisitionBusinessFlag> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ConMaterialRequisitionBusinessFlag::getSid, sids);
        updateWrapper.set(ConMaterialRequisitionBusinessFlag::getHandleStatus, conMaterialRequisitionBusinessFlag.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(conMaterialRequisitionBusinessFlag.getHandleStatus())) {
            updateWrapper.set(ConMaterialRequisitionBusinessFlag::getConfirmDate, new Date());
            updateWrapper.set(ConMaterialRequisitionBusinessFlag::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = conMaterialRequisitionBusinessFlagMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, conMaterialRequisitionBusinessFlag.getHandleStatus(), null, TITLE, null);
            }
        }else{
            throw new CustomException("确认失败,请联系管理员");
        }
        return row;
    }

}
