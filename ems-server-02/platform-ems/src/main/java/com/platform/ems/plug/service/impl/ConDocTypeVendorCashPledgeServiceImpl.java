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
import com.platform.ems.plug.domain.ConDocTypeVendorCashPledge;
import com.platform.ems.plug.mapper.ConDocTypeVendorCashPledgeMapper;
import com.platform.ems.plug.service.IConDocTypeVendorCashPledgeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_供应商押金Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeVendorCashPledgeServiceImpl extends ServiceImpl<ConDocTypeVendorCashPledgeMapper, ConDocTypeVendorCashPledge> implements IConDocTypeVendorCashPledgeService {
    @Autowired
    private ConDocTypeVendorCashPledgeMapper conDocTypeVendorCashPledgeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_供应商押金";

    /**
     * 查询单据类型_供应商押金
     *
     * @param sid 单据类型_供应商押金ID
     * @return 单据类型_供应商押金
     */
    @Override
    public ConDocTypeVendorCashPledge selectConDocTypeVendorCashPledgeById(Long sid) {
        ConDocTypeVendorCashPledge conDocTypeVendorCashPledge = conDocTypeVendorCashPledgeMapper.selectConDocTypeVendorCashPledgeById(sid);
        MongodbUtil.find(conDocTypeVendorCashPledge);
        return conDocTypeVendorCashPledge;
    }

    /**
     * 查询单据类型_供应商押金列表
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 单据类型_供应商押金
     */
    @Override
    public List<ConDocTypeVendorCashPledge> selectConDocTypeVendorCashPledgeList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return conDocTypeVendorCashPledgeMapper.selectConDocTypeVendorCashPledgeList(conDocTypeVendorCashPledge);
    }

    /**
     * 新增单据类型_供应商押金
     * 需要注意编码重复校验
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        List<ConDocTypeVendorCashPledge> codeList = conDocTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeVendorCashPledge>().lambda()
                .eq(ConDocTypeVendorCashPledge::getCode, conDocTypeVendorCashPledge.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeVendorCashPledge> nameList = conDocTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeVendorCashPledge>().lambda()
                .eq(ConDocTypeVendorCashPledge::getName, conDocTypeVendorCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeVendorCashPledge);
        int row = conDocTypeVendorCashPledgeMapper.insert(conDocTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeVendorCashPledge.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeVendorCashPledge o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_供应商押金
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        ConDocTypeVendorCashPledge response = conDocTypeVendorCashPledgeMapper.selectConDocTypeVendorCashPledgeById(conDocTypeVendorCashPledge.getSid());
        int row = conDocTypeVendorCashPledgeMapper.updateById(conDocTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorCashPledge.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeVendorCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_供应商押金
     *
     * @param conDocTypeVendorCashPledge 单据类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeVendorCashPledge(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        List<ConDocTypeVendorCashPledge> nameList = conDocTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConDocTypeVendorCashPledge>().lambda()
                .eq(ConDocTypeVendorCashPledge::getName, conDocTypeVendorCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conDocTypeVendorCashPledge.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conDocTypeVendorCashPledge);
        conDocTypeVendorCashPledge.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        ConDocTypeVendorCashPledge response = conDocTypeVendorCashPledgeMapper.selectConDocTypeVendorCashPledgeById(conDocTypeVendorCashPledge.getSid());
        int row = conDocTypeVendorCashPledgeMapper.updateAllById(conDocTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeVendorCashPledge.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeVendorCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_供应商押金
     *
     * @param sids 需要删除的单据类型_供应商押金ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeVendorCashPledgeByIds(List<Long> sids) {
        return conDocTypeVendorCashPledgeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeVendorCashPledge
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        int row = 0;
        Long[] sids = conDocTypeVendorCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeVendorCashPledgeMapper.update(null, new UpdateWrapper<ConDocTypeVendorCashPledge>().lambda().set(ConDocTypeVendorCashPledge::getStatus, conDocTypeVendorCashPledge.getStatus())
                    .in(ConDocTypeVendorCashPledge::getSid, sids));
            for (Long id : sids) {
                conDocTypeVendorCashPledge.setSid(id);
                row = conDocTypeVendorCashPledgeMapper.updateById(conDocTypeVendorCashPledge);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeVendorCashPledge.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeVendorCashPledge.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeVendorCashPledge
     * @return
     */
    @Override
    public int check(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        int row = 0;
        Long[] sids = conDocTypeVendorCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocTypeVendorCashPledgeMapper.update(null, new UpdateWrapper<ConDocTypeVendorCashPledge>().lambda().set(ConDocTypeVendorCashPledge::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocTypeVendorCashPledge::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_供应商押金下拉框列表
     */
    @Override
    public List<ConDocTypeVendorCashPledge> getList(ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return conDocTypeVendorCashPledgeMapper.getList(conDocTypeVendorCashPledge);
    }
}
