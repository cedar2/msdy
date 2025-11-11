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
import com.platform.ems.plug.domain.ConFundsFreezeTypeCustomer;
import com.platform.ems.plug.mapper.ConFundsFreezeTypeCustomerMapper;
import com.platform.ems.plug.service.IConFundsFreezeTypeCustomerService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 暂押款类型_客户Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConFundsFreezeTypeCustomerServiceImpl extends ServiceImpl<ConFundsFreezeTypeCustomerMapper, ConFundsFreezeTypeCustomer> implements IConFundsFreezeTypeCustomerService {
    @Autowired
    private ConFundsFreezeTypeCustomerMapper conFundsFreezeTypeCustomerMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "暂押款类型_客户";

    /**
     * 查询暂押款类型_客户
     *
     * @param sid 暂押款类型_客户ID
     * @return 暂押款类型_客户
     */
    @Override
    public ConFundsFreezeTypeCustomer selectConFundsFreezeTypeCustomerById(Long sid) {
        ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer = conFundsFreezeTypeCustomerMapper.selectConFundsFreezeTypeCustomerById(sid);
        MongodbUtil.find(conFundsFreezeTypeCustomer);
        return conFundsFreezeTypeCustomer;
    }

    /**
     * 查询暂押款类型_客户列表
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 暂押款类型_客户
     */
    @Override
    public List<ConFundsFreezeTypeCustomer> selectConFundsFreezeTypeCustomerList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return conFundsFreezeTypeCustomerMapper.selectConFundsFreezeTypeCustomerList(conFundsFreezeTypeCustomer);
    }

    /**
     * 新增暂押款类型_客户
     * 需要注意编码重复校验
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        List<ConFundsFreezeTypeCustomer> codeList = conFundsFreezeTypeCustomerMapper.selectList(new QueryWrapper<ConFundsFreezeTypeCustomer>().lambda()
                .eq(ConFundsFreezeTypeCustomer::getCode, conFundsFreezeTypeCustomer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConFundsFreezeTypeCustomer> nameList = conFundsFreezeTypeCustomerMapper.selectList(new QueryWrapper<ConFundsFreezeTypeCustomer>().lambda()
                .eq(ConFundsFreezeTypeCustomer::getName, conFundsFreezeTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conFundsFreezeTypeCustomer);
        int row = conFundsFreezeTypeCustomerMapper.insert(conFundsFreezeTypeCustomer);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conFundsFreezeTypeCustomer.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConFundsFreezeTypeCustomer o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改暂押款类型_客户
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        ConFundsFreezeTypeCustomer response = conFundsFreezeTypeCustomerMapper.selectConFundsFreezeTypeCustomerById(conFundsFreezeTypeCustomer.getSid());
        int row = conFundsFreezeTypeCustomerMapper.updateById(conFundsFreezeTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFundsFreezeTypeCustomer.getSid(), BusinessType.UPDATE.getValue(), response, conFundsFreezeTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 变更暂押款类型_客户
     *
     * @param conFundsFreezeTypeCustomer 暂押款类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConFundsFreezeTypeCustomer(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        List<ConFundsFreezeTypeCustomer> nameList = conFundsFreezeTypeCustomerMapper.selectList(new QueryWrapper<ConFundsFreezeTypeCustomer>().lambda()
                .eq(ConFundsFreezeTypeCustomer::getName, conFundsFreezeTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conFundsFreezeTypeCustomer.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conFundsFreezeTypeCustomer);
        conFundsFreezeTypeCustomer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConFundsFreezeTypeCustomer response = conFundsFreezeTypeCustomerMapper.selectConFundsFreezeTypeCustomerById(conFundsFreezeTypeCustomer.getSid());
        int row = conFundsFreezeTypeCustomerMapper.updateAllById(conFundsFreezeTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conFundsFreezeTypeCustomer.getSid(), BusinessType.CHANGE.getValue(), response, conFundsFreezeTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除暂押款类型_客户
     *
     * @param sids 需要删除的暂押款类型_客户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConFundsFreezeTypeCustomerByIds(List<Long> sids) {
        return conFundsFreezeTypeCustomerMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conFundsFreezeTypeCustomer
     * @return
     */
    @Override
    public int changeStatus(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        int row = 0;
        Long[] sids = conFundsFreezeTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFundsFreezeTypeCustomerMapper.update(null, new UpdateWrapper<ConFundsFreezeTypeCustomer>().lambda().set(ConFundsFreezeTypeCustomer::getStatus, conFundsFreezeTypeCustomer.getStatus())
                    .in(ConFundsFreezeTypeCustomer::getSid, sids));
            for (Long id : sids) {
                conFundsFreezeTypeCustomer.setSid(id);
                row = conFundsFreezeTypeCustomerMapper.updateById(conFundsFreezeTypeCustomer);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conFundsFreezeTypeCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conFundsFreezeTypeCustomer.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conFundsFreezeTypeCustomer
     * @return
     */
    @Override
    public int check(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        int row = 0;
        Long[] sids = conFundsFreezeTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            row = conFundsFreezeTypeCustomerMapper.update(null, new UpdateWrapper<ConFundsFreezeTypeCustomer>().lambda().set(ConFundsFreezeTypeCustomer::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConFundsFreezeTypeCustomer::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 暂押款类型_客户下拉框列表
     */
    @Override
    public List<ConFundsFreezeTypeCustomer> getList(ConFundsFreezeTypeCustomer conFundsFreezeTypeCustomer) {
        return conFundsFreezeTypeCustomerMapper.getList(conFundsFreezeTypeCustomer);
    }
}
