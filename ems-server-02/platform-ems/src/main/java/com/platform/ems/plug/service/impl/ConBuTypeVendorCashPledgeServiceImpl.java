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
import com.platform.ems.plug.domain.ConBuTypeVendorCashPledge;
import com.platform.ems.plug.mapper.ConBuTypeVendorCashPledgeMapper;
import com.platform.ems.plug.service.IConBuTypeVendorCashPledgeService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_供应商押金Service业务层处理
 *
 * @author c
 * @date 2021-09-27
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeVendorCashPledgeServiceImpl extends ServiceImpl<ConBuTypeVendorCashPledgeMapper, ConBuTypeVendorCashPledge> implements IConBuTypeVendorCashPledgeService {
    @Autowired
    private ConBuTypeVendorCashPledgeMapper conBuTypeVendorCashPledgeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_供应商押金";

    /**
     * 查询业务类型_供应商押金
     *
     * @param sid 业务类型_供应商押金ID
     * @return 业务类型_供应商押金
     */
    @Override
    public ConBuTypeVendorCashPledge selectConBuTypeVendorCashPledgeById(Long sid) {
        ConBuTypeVendorCashPledge conBuTypeVendorCashPledge = conBuTypeVendorCashPledgeMapper.selectConBuTypeVendorCashPledgeById(sid);
        MongodbUtil.find(conBuTypeVendorCashPledge);
        return conBuTypeVendorCashPledge;
    }

    /**
     * 查询业务类型_供应商押金列表
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 业务类型_供应商押金
     */
    @Override
    public List<ConBuTypeVendorCashPledge> selectConBuTypeVendorCashPledgeList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return conBuTypeVendorCashPledgeMapper.selectConBuTypeVendorCashPledgeList(conBuTypeVendorCashPledge);
    }

    /**
     * 新增业务类型_供应商押金
     * 需要注意编码重复校验
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        List<ConBuTypeVendorCashPledge> codeList = conBuTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeVendorCashPledge>().lambda()
                .eq(ConBuTypeVendorCashPledge::getCode, conBuTypeVendorCashPledge.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeVendorCashPledge> nameList = conBuTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeVendorCashPledge>().lambda()
                .eq(ConBuTypeVendorCashPledge::getName, conBuTypeVendorCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        conBuTypeVendorCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conBuTypeVendorCashPledgeMapper.insert(conBuTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeVendorCashPledge.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_供应商押金
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        ConBuTypeVendorCashPledge response = conBuTypeVendorCashPledgeMapper.selectConBuTypeVendorCashPledgeById(conBuTypeVendorCashPledge.getSid());
        int row = conBuTypeVendorCashPledgeMapper.updateById(conBuTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorCashPledge.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeVendorCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_供应商押金
     *
     * @param conBuTypeVendorCashPledge 业务类型_供应商押金
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeVendorCashPledge(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        List<ConBuTypeVendorCashPledge> nameList = conBuTypeVendorCashPledgeMapper.selectList(new QueryWrapper<ConBuTypeVendorCashPledge>().lambda()
                .eq(ConBuTypeVendorCashPledge::getName, conBuTypeVendorCashPledge.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypeVendorCashPledge.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeVendorCashPledge.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeVendorCashPledge response = conBuTypeVendorCashPledgeMapper.selectConBuTypeVendorCashPledgeById(conBuTypeVendorCashPledge.getSid());
        int row = conBuTypeVendorCashPledgeMapper.updateAllById(conBuTypeVendorCashPledge);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeVendorCashPledge.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeVendorCashPledge, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_供应商押金
     *
     * @param sids 需要删除的业务类型_供应商押金ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeVendorCashPledgeByIds(List<Long> sids) {
        return conBuTypeVendorCashPledgeMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypeVendorCashPledge
     * @return
     */
    @Override
    public int changeStatus(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        int row = 0;
        Long[] sids = conBuTypeVendorCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorCashPledgeMapper.update(null, new UpdateWrapper<ConBuTypeVendorCashPledge>().lambda().set(ConBuTypeVendorCashPledge::getStatus, conBuTypeVendorCashPledge.getStatus())
                    .in(ConBuTypeVendorCashPledge::getSid, sids));
            for (Long id : sids) {
                conBuTypeVendorCashPledge.setSid(id);
                row = conBuTypeVendorCashPledgeMapper.updateById(conBuTypeVendorCashPledge);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypeVendorCashPledge.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypeVendorCashPledge.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypeVendorCashPledge
     * @return
     */
    @Override
    public int check(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        int row = 0;
        Long[] sids = conBuTypeVendorCashPledge.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeVendorCashPledgeMapper.update(null, new UpdateWrapper<ConBuTypeVendorCashPledge>().lambda().set(ConBuTypeVendorCashPledge::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeVendorCashPledge::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_供应商押金下拉框列表
     */
    @Override
    public List<ConBuTypeVendorCashPledge> getList(ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return conBuTypeVendorCashPledgeMapper.getList(conBuTypeVendorCashPledge);
    }
}
