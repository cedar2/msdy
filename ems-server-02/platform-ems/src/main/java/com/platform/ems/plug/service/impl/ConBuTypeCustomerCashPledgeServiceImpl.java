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
import com.platform.ems.plug.domain.ConBuTypeCustomerCashPledge;
import com.platform.ems.plug.mapper.ConBuTypeCustomerCashPledgeMapper;
import com.platform.ems.plug.service.IConBuTypeCustomerCashPledgeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_客户押金Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeCustomerCashPledgeServiceImpl extends ServiceImpl<ConBuTypeCustomerCashPledgeMapper, ConBuTypeCustomerCashPledge> implements IConBuTypeCustomerCashPledgeService {
    @Autowired
    private ConBuTypeCustomerCashPledgeMapper conBuTypeCustomerCashPledgeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_客户押金";

    /**
     * 查询业务类型_客户押金
     *
     * @param sid 业务类型_客户押金ID
     * @return 业务类型_客户押金
     */
    @Override
    public ConBuTypeCustomerCashPledge selectConBuTypeCustomerCashPledgeById(Long sid) {
        ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge = conBuTypeCustomerCashPledgeMapper.selectConBuTypeCustomerCashPledgeById(sid);
        MongodbUtil.find(conBuTypeCustomerCashPledge);
        return conBuTypeCustomerCashPledge;
    }

    /**
     * 查询业务类型_客户押金列表
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 业务类型_客户押金
     */
    @Override
    public List<ConBuTypeCustomerCashPledge> selectConBuTypeCustomerCashPledgeList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return conBuTypeCustomerCashPledgeMapper.selectConBuTypeCustomerCashPledgeList(conBuTypeCustomerCashPledge);
    }

    /**
     * 新增业务类型_客户押金
     * 需要注意编码重复校验
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        List<ConBuTypeCustomerCashPledge> codeList = conBuTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeCustomerCashPledge>().lambda()
                .eq(ConBuTypeCustomerCashPledge::getCode, conBuTypeCustomerCashPledge.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeCustomerCashPledge> nameList = conBuTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeCustomerCashPledge>().lambda()
                .eq(ConBuTypeCustomerCashPledge::getName, conBuTypeCustomerCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        conBuTypeCustomerCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conBuTypeCustomerCashPledgeMapper.insert(conBuTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeCustomerCashPledge.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_客户押金
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        ConBuTypeCustomerCashPledge response = conBuTypeCustomerCashPledgeMapper.selectConBuTypeCustomerCashPledgeById(conBuTypeCustomerCashPledge.getSid());
        int row = conBuTypeCustomerCashPledgeMapper.updateById(conBuTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerCashPledge.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeCustomerCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_客户押金
     *
     * @param conBuTypeCustomerCashPledge 业务类型_客户押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeCustomerCashPledge(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        List<ConBuTypeCustomerCashPledge> nameList = conBuTypeCustomerCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeCustomerCashPledge>().lambda()
                .eq(ConBuTypeCustomerCashPledge::getName, conBuTypeCustomerCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypeCustomerCashPledge.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeCustomerCashPledge.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeCustomerCashPledge response = conBuTypeCustomerCashPledgeMapper.selectConBuTypeCustomerCashPledgeById(conBuTypeCustomerCashPledge.getSid());
        int row = conBuTypeCustomerCashPledgeMapper.updateAllById(conBuTypeCustomerCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerCashPledge.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeCustomerCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_客户押金
     *
     * @param sids 需要删除的业务类型_客户押金ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeCustomerCashPledgeByIds(List<Long> sids) {
        return conBuTypeCustomerCashPledgeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeCustomerCashPledge
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        int row = 0;
        Long[] sids = conBuTypeCustomerCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerCashPledgeMapper.update(null, new UpdateWrapper<ConBuTypeCustomerCashPledge>().lambda().set(ConBuTypeCustomerCashPledge::getStatus, conBuTypeCustomerCashPledge.getStatus())
                    .in(ConBuTypeCustomerCashPledge::getSid, sids));
            for (Long id : sids) {
                conBuTypeCustomerCashPledge.setSid(id);
                row = conBuTypeCustomerCashPledgeMapper.updateById(conBuTypeCustomerCashPledge);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeCustomerCashPledge.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeCustomerCashPledge.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeCustomerCashPledge
     * @return
     */
    @Override
    public int check(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        int row = 0;
        Long[] sids = conBuTypeCustomerCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeCustomerCashPledgeMapper.update(null, new UpdateWrapper<ConBuTypeCustomerCashPledge>().lambda().set(ConBuTypeCustomerCashPledge::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeCustomerCashPledge::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_客户押金下拉框列表
     */
    @Override
    public List<ConBuTypeCustomerCashPledge> getList(ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return conBuTypeCustomerCashPledgeMapper.getList(conBuTypeCustomerCashPledge);
    }
}
