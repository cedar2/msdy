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
import com.platform.ems.plug.domain.ConCashPledgeTypeCustomer;
import com.platform.ems.plug.mapper.ConCashPledgeTypeCustomerMapper;
import com.platform.ems.plug.service.IConCashPledgeTypeCustomerService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 押金类型_客户Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConCashPledgeTypeCustomerServiceImpl extends ServiceImpl<ConCashPledgeTypeCustomerMapper, ConCashPledgeTypeCustomer> implements IConCashPledgeTypeCustomerService {
    @Autowired
    private ConCashPledgeTypeCustomerMapper conCashPledgeTypeCustomerMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "押金类型_客户";

    /**
     * 查询押金类型_客户
     *
     * @param sid 押金类型_客户ID
     * @return 押金类型_客户
     */
    @Override
    public ConCashPledgeTypeCustomer selectConCashPledgeTypeCustomerById(Long sid) {
        ConCashPledgeTypeCustomer conCashPledgeTypeCustomer = conCashPledgeTypeCustomerMapper.selectConCashPledgeTypeCustomerById(sid);
        MongodbUtil.find(conCashPledgeTypeCustomer);
        return conCashPledgeTypeCustomer;
    }

    /**
     * 查询押金类型_客户列表
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 押金类型_客户
     */
    @Override
    public List<ConCashPledgeTypeCustomer> selectConCashPledgeTypeCustomerList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return conCashPledgeTypeCustomerMapper.selectConCashPledgeTypeCustomerList(conCashPledgeTypeCustomer);
    }

    /**
     * 新增押金类型_客户
     * 需要注意编码重复校验
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        List<ConCashPledgeTypeCustomer> codeList = conCashPledgeTypeCustomerMapper.selectList(new QueryWrapper<ConCashPledgeTypeCustomer>().lambda()
                .eq(ConCashPledgeTypeCustomer::getCode, conCashPledgeTypeCustomer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConCashPledgeTypeCustomer> nameList = conCashPledgeTypeCustomerMapper.selectList(new QueryWrapper<ConCashPledgeTypeCustomer>().lambda()
                .eq(ConCashPledgeTypeCustomer::getName, conCashPledgeTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conCashPledgeTypeCustomer);
        int row = conCashPledgeTypeCustomerMapper.insert(conCashPledgeTypeCustomer);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conCashPledgeTypeCustomer.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConCashPledgeTypeCustomer o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }


    /**
     * 修改押金类型_客户
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        ConCashPledgeTypeCustomer response = conCashPledgeTypeCustomerMapper.selectConCashPledgeTypeCustomerById(conCashPledgeTypeCustomer.getSid());
        int row = conCashPledgeTypeCustomerMapper.updateById(conCashPledgeTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCashPledgeTypeCustomer.getSid(), BusinessType.UPDATE.getValue(), response, conCashPledgeTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 变更押金类型_客户
     *
     * @param conCashPledgeTypeCustomer 押金类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCashPledgeTypeCustomer(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        List<ConCashPledgeTypeCustomer> nameList = conCashPledgeTypeCustomerMapper.selectList(new QueryWrapper<ConCashPledgeTypeCustomer>().lambda()
                .eq(ConCashPledgeTypeCustomer::getName, conCashPledgeTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conCashPledgeTypeCustomer.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conCashPledgeTypeCustomer);
        conCashPledgeTypeCustomer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConCashPledgeTypeCustomer response = conCashPledgeTypeCustomerMapper.selectConCashPledgeTypeCustomerById(conCashPledgeTypeCustomer.getSid());
        int row = conCashPledgeTypeCustomerMapper.updateAllById(conCashPledgeTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCashPledgeTypeCustomer.getSid(), BusinessType.CHANGE.getValue(), response, conCashPledgeTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除押金类型_客户
     *
     * @param sids 需要删除的押金类型_客户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCashPledgeTypeCustomerByIds(List<Long> sids) {
        return conCashPledgeTypeCustomerMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conCashPledgeTypeCustomer
     * @return
     */
    @Override
    public int changeStatus(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        int row = 0;
        Long[] sids = conCashPledgeTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            row = conCashPledgeTypeCustomerMapper.update(null, new UpdateWrapper<ConCashPledgeTypeCustomer>().lambda().set(ConCashPledgeTypeCustomer::getStatus, conCashPledgeTypeCustomer.getStatus())
                    .in(ConCashPledgeTypeCustomer::getSid, sids));
            for (Long id : sids) {
                conCashPledgeTypeCustomer.setSid(id);
                row = conCashPledgeTypeCustomerMapper.updateById(conCashPledgeTypeCustomer);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conCashPledgeTypeCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conCashPledgeTypeCustomer.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conCashPledgeTypeCustomer
     * @return
     */
    @Override
    public int check(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        int row = 0;
        Long[] sids = conCashPledgeTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            row = conCashPledgeTypeCustomerMapper.update(null, new UpdateWrapper<ConCashPledgeTypeCustomer>().lambda().set(ConCashPledgeTypeCustomer::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConCashPledgeTypeCustomer::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 押金类型_客户下拉框列表
     */
    @Override
    public List<ConCashPledgeTypeCustomer> getList(ConCashPledgeTypeCustomer conCashPledgeTypeCustomer) {
        return conCashPledgeTypeCustomerMapper.getList(conCashPledgeTypeCustomer);
    }
}
