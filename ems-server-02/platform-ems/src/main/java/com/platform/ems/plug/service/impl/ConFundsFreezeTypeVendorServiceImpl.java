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
import com.platform.ems.plug.domain.ConFundsFreezeTypeVendor;
import com.platform.ems.plug.mapper.ConFundsFreezeTypeVendorMapper;
import com.platform.ems.plug.service.IConFundsFreezeTypeVendorService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 暂押款类型_供应商Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConFundsFreezeTypeVendorServiceImpl extends ServiceImpl<ConFundsFreezeTypeVendorMapper, ConFundsFreezeTypeVendor> implements IConFundsFreezeTypeVendorService {
    @Autowired
    private ConFundsFreezeTypeVendorMapper conFundsFreezeTypeVendorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "暂押款类型_供应商";

    /**
     * 查询暂押款类型_供应商
     *
     * @param sid 暂押款类型_供应商ID
     * @return 暂押款类型_供应商
     */
    @Override
    public ConFundsFreezeTypeVendor selectConFundsFreezeTypeVendorById(Long sid) {
        ConFundsFreezeTypeVendor conFundsFreezeTypeVendor = conFundsFreezeTypeVendorMapper.selectConFundsFreezeTypeVendorById(sid);
        MongodbUtil.find(conFundsFreezeTypeVendor);
        return conFundsFreezeTypeVendor;
    }

    /**
     * 查询暂押款类型_供应商列表
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 暂押款类型_供应商
     */
    @Override
    public List<ConFundsFreezeTypeVendor> selectConFundsFreezeTypeVendorList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return conFundsFreezeTypeVendorMapper.selectConFundsFreezeTypeVendorList(conFundsFreezeTypeVendor);
    }

    /**
     * 新增暂押款类型_供应商
     * 需要注意编码重复校验
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        List<ConFundsFreezeTypeVendor> codeList = conFundsFreezeTypeVendorMapper.selectList(new QueryWrapper<ConFundsFreezeTypeVendor>().lambda()
                .eq(ConFundsFreezeTypeVendor::getCode, conFundsFreezeTypeVendor.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConFundsFreezeTypeVendor> nameList = conFundsFreezeTypeVendorMapper.selectList(new QueryWrapper<ConFundsFreezeTypeVendor>().lambda()
                .eq(ConFundsFreezeTypeVendor::getName, conFundsFreezeTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conFundsFreezeTypeVendor);
        int row = conFundsFreezeTypeVendorMapper.insert(conFundsFreezeTypeVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conFundsFreezeTypeVendor.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConFundsFreezeTypeVendor o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改暂押款类型_供应商
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        ConFundsFreezeTypeVendor response = conFundsFreezeTypeVendorMapper.selectConFundsFreezeTypeVendorById(conFundsFreezeTypeVendor.getSid());
        int row = conFundsFreezeTypeVendorMapper.updateById(conFundsFreezeTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFundsFreezeTypeVendor.getSid(), BusinessType.UPDATE.getValue(), response, conFundsFreezeTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更暂押款类型_供应商
     *
     * @param conFundsFreezeTypeVendor 暂押款类型_供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConFundsFreezeTypeVendor(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        List<ConFundsFreezeTypeVendor> nameList = conFundsFreezeTypeVendorMapper.selectList(new QueryWrapper<ConFundsFreezeTypeVendor>().lambda()
                .eq(ConFundsFreezeTypeVendor::getName, conFundsFreezeTypeVendor.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conFundsFreezeTypeVendor.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conFundsFreezeTypeVendor);
        conFundsFreezeTypeVendor.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConFundsFreezeTypeVendor response = conFundsFreezeTypeVendorMapper.selectConFundsFreezeTypeVendorById(conFundsFreezeTypeVendor.getSid());
        int row = conFundsFreezeTypeVendorMapper.updateAllById(conFundsFreezeTypeVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFundsFreezeTypeVendor.getSid(), BusinessType.CHANGE.getValue(), response, conFundsFreezeTypeVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除暂押款类型_供应商
     *
     * @param sids 需要删除的暂押款类型_供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConFundsFreezeTypeVendorByIds(List<Long> sids) {
        return conFundsFreezeTypeVendorMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conFundsFreezeTypeVendor
     * @return
     */
    @Override
    public int changeStatus(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        int row = 0;
        Long[] sids = conFundsFreezeTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFundsFreezeTypeVendorMapper.update(null, new UpdateWrapper<ConFundsFreezeTypeVendor>().lambda().set(ConFundsFreezeTypeVendor::getStatus, conFundsFreezeTypeVendor.getStatus())
                    .in(ConFundsFreezeTypeVendor::getSid, sids));
            for (Long id : sids) {
                conFundsFreezeTypeVendor.setSid(id);
                row = conFundsFreezeTypeVendorMapper.updateById(conFundsFreezeTypeVendor);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conFundsFreezeTypeVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conFundsFreezeTypeVendor.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conFundsFreezeTypeVendor
     * @return
     */
    @Override
    public int check(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        int row = 0;
        Long[] sids = conFundsFreezeTypeVendor.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFundsFreezeTypeVendorMapper.update(null, new UpdateWrapper<ConFundsFreezeTypeVendor>().lambda().set(ConFundsFreezeTypeVendor::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConFundsFreezeTypeVendor::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 暂押款类型_供应商下拉框列表
     */
    @Override
    public List<ConFundsFreezeTypeVendor> getList(ConFundsFreezeTypeVendor conFundsFreezeTypeVendor) {
        return conFundsFreezeTypeVendorMapper.getList(conFundsFreezeTypeVendor);
    }
}
