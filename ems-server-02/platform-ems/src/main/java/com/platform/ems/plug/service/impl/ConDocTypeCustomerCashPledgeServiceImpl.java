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
import com.platform.ems.plug.domain.ConDocTypeCustomerCashPledge;
import com.platform.ems.plug.mapper.ConDocTypeCustomerCashPledgeMapper;
import com.platform.ems.plug.service.IConDocTypeCustomerCashPledgeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_客户押金Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeCustomerCashPledgeServiceImpl extends ServiceImpl<ConDocTypeCustomerCashPledgeMapper, ConDocTypeCustomerCashPledge> implements IConDocTypeCustomerCashPledgeService {
    @Autowired
    private ConDocTypeCustomerCashPledgeMapper conDocTypeCustomerCashPledgeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_客户押金";

    /**
     * 查询单据类型_客户押金
     *
     * @param sid 单据类型_客户押金ID
     * @return 单据类型_客户押金
     */
    @Override
    public ConDocTypeCustomerCashPledge selectConDocTypeCustomerCashPledgeById(Long sid) {
        ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge = conDocTypeCustomerCashPledgeMapper.selectConDocTypeCustomerCashPledgeById(sid);
        MongodbUtil.find(conDocTypeCustomerCashPledge);
        return conDocTypeCustomerCashPledge;
    }

    /**
     * 查询单据类型_客户押金列表
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 单据类型_客户押金
     */
    @Override
    public List<ConDocTypeCustomerCashPledge> selectConDocTypeCustomerCashPledgeList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return conDocTypeCustomerCashPledgeMapper.selectConDocTypeCustomerCashPledgeList(conDocTypeCustomerCashPledge);
    }

    /**
     * 新增单据类型_客户押金
     * 需要注意编码重复校验
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        List<ConDocTypeCustomerCashPledge> codeList = conDocTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeCustomerCashPledge>().lambda()
                .eq(ConDocTypeCustomerCashPledge::getCode, conDocTypeCustomerCashPledge.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeCustomerCashPledge> nameList = conDocTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeCustomerCashPledge>().lambda()
                .eq(ConDocTypeCustomerCashPledge::getName, conDocTypeCustomerCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeCustomerCashPledge);
        int row = conDocTypeCustomerCashPledgeMapper.insert(conDocTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeCustomerCashPledge.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeCustomerCashPledge o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_客户押金
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        ConDocTypeCustomerCashPledge response = conDocTypeCustomerCashPledgeMapper.selectConDocTypeCustomerCashPledgeById(conDocTypeCustomerCashPledge.getSid());
        int row = conDocTypeCustomerCashPledgeMapper.updateById(conDocTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerCashPledge.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeCustomerCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_客户押金
     *
     * @param conDocTypeCustomerCashPledge 单据类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeCustomerCashPledge(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        List<ConDocTypeCustomerCashPledge> nameList = conDocTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeCustomerCashPledge>().lambda()
                .eq(ConDocTypeCustomerCashPledge::getName, conDocTypeCustomerCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conDocTypeCustomerCashPledge.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conDocTypeCustomerCashPledge);
        conDocTypeCustomerCashPledge.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConDocTypeCustomerCashPledge response = conDocTypeCustomerCashPledgeMapper.selectConDocTypeCustomerCashPledgeById(conDocTypeCustomerCashPledge.getSid());
        int row = conDocTypeCustomerCashPledgeMapper.updateAllById(conDocTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerCashPledge.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeCustomerCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_客户押金
     *
     * @param sids 需要删除的单据类型_客户押金ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeCustomerCashPledgeByIds(List<Long> sids) {
        return conDocTypeCustomerCashPledgeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeCustomerCashPledge
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        int row = 0;
        Long[] sids = conDocTypeCustomerCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeCustomerCashPledgeMapper.update(null, new UpdateWrapper<ConDocTypeCustomerCashPledge>().lambda().set(ConDocTypeCustomerCashPledge::getStatus, conDocTypeCustomerCashPledge.getStatus())
                    .in(ConDocTypeCustomerCashPledge::getSid, sids));
            for (Long id : sids) {
                conDocTypeCustomerCashPledge.setSid(id);
                row = conDocTypeCustomerCashPledgeMapper.updateById(conDocTypeCustomerCashPledge);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeCustomerCashPledge.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeCustomerCashPledge.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeCustomerCashPledge
     * @return
     */
    @Override
    public int check(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        int row = 0;
        Long[] sids = conDocTypeCustomerCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeCustomerCashPledgeMapper.update(null, new UpdateWrapper<ConDocTypeCustomerCashPledge>().lambda().set(ConDocTypeCustomerCashPledge::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeCustomerCashPledge::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_客户押金下拉框列表
     */
    @Override
    public List<ConDocTypeCustomerCashPledge> getList(ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return conDocTypeCustomerCashPledgeMapper.getList(conDocTypeCustomerCashPledge);
    }
}
