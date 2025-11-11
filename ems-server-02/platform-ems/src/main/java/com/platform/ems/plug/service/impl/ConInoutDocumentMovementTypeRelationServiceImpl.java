package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConInoutDocumentMovementTypeRelation;
import com.platform.ems.plug.mapper.ConInoutDocumentMovementTypeRelationMapper;
import com.platform.ems.plug.service.IConInoutDocumentMovementTypeRelationService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 出入库作业类型&单据作业类型对照Service业务层处理
 *
 * @author c
 * @date 2022-03-11
 */
@Service
@SuppressWarnings("all")
public class ConInoutDocumentMovementTypeRelationServiceImpl extends ServiceImpl<ConInoutDocumentMovementTypeRelationMapper, ConInoutDocumentMovementTypeRelation> implements IConInoutDocumentMovementTypeRelationService {
    @Autowired
    private ConInoutDocumentMovementTypeRelationMapper conInoutDocumentMovementTypeRelationMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "出入库作业类型&单据作业类型对照";

    /**
     * 查询出入库作业类型&单据作业类型对照
     *
     * @param sid 出入库作业类型&单据作业类型对照ID
     * @return 出入库作业类型&单据作业类型对照
     */
    @Override
    public ConInoutDocumentMovementTypeRelation selectConInoutDocumentMovementTypeRelationById(Long sid) {
        ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation = conInoutDocumentMovementTypeRelationMapper.selectConInoutDocumentMovementTypeRelationById(sid);
        MongodbUtil.find(conInoutDocumentMovementTypeRelation);
        return conInoutDocumentMovementTypeRelation;
    }

    /**
     * 查询出入库作业类型&单据作业类型对照列表
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 出入库作业类型&单据作业类型对照
     */
    @Override
    public List<ConInoutDocumentMovementTypeRelation> selectConInoutDocumentMovementTypeRelationList(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return conInoutDocumentMovementTypeRelationMapper.selectConInoutDocumentMovementTypeRelationList(conInoutDocumentMovementTypeRelation);
    }

    /**
     * 新增出入库作业类型&单据作业类型对照
     * 需要注意编码重复校验
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        List<ConInoutDocumentMovementTypeRelation> list = selectList(conInoutDocumentMovementTypeRelation);
        if (CollUtil.isNotEmpty(list)) {
            throw new BaseException("已存在相同配置档案，请核实！");
        }
        setConfirmInfo(conInoutDocumentMovementTypeRelation);
        int row = conInoutDocumentMovementTypeRelationMapper.insert(conInoutDocumentMovementTypeRelation);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conInoutDocumentMovementTypeRelation.getSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    private List<ConInoutDocumentMovementTypeRelation> selectList(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        return conInoutDocumentMovementTypeRelationMapper.selectList(new QueryWrapper<ConInoutDocumentMovementTypeRelation>().lambda()
                    .eq(ConInoutDocumentMovementTypeRelation::getDocumentMovementTypeCode, conInoutDocumentMovementTypeRelation.getDocumentMovementTypeCode())
                    .eq(ConInoutDocumentMovementTypeRelation::getInOutMovementTypeCode, conInoutDocumentMovementTypeRelation.getInOutMovementTypeCode()));
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConInoutDocumentMovementTypeRelation o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改出入库作业类型&单据作业类型对照
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        ConInoutDocumentMovementTypeRelation response = conInoutDocumentMovementTypeRelationMapper.selectConInoutDocumentMovementTypeRelationById(conInoutDocumentMovementTypeRelation.getSid());
        int row = conInoutDocumentMovementTypeRelationMapper.updateById(conInoutDocumentMovementTypeRelation);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInoutDocumentMovementTypeRelation.getSid(), BusinessType.UPDATE.ordinal(), response, conInoutDocumentMovementTypeRelation, TITLE);
        }
        return row;
    }

    /**
     * 变更出入库作业类型&单据作业类型对照
     *
     * @param conInoutDocumentMovementTypeRelation 出入库作业类型&单据作业类型对照
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInoutDocumentMovementTypeRelation(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        List<ConInoutDocumentMovementTypeRelation> list = selectList(conInoutDocumentMovementTypeRelation);
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(o ->{
                if (!o.getSid().equals(conInoutDocumentMovementTypeRelation.getSid())) {
                    throw new BaseException("已存在相同配置档案，请核实！");
                }
            });
        }
        ConInoutDocumentMovementTypeRelation response = conInoutDocumentMovementTypeRelationMapper.selectConInoutDocumentMovementTypeRelationById(conInoutDocumentMovementTypeRelation.getSid());
        conInoutDocumentMovementTypeRelation.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = conInoutDocumentMovementTypeRelationMapper.updateAllById(conInoutDocumentMovementTypeRelation);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInoutDocumentMovementTypeRelation.getSid(), BusinessType.CHANGE.ordinal(), response, conInoutDocumentMovementTypeRelation, TITLE);
        }
        return row;
    }

    /**
     * 批量删除出入库作业类型&单据作业类型对照
     *
     * @param sids 需要删除的出入库作业类型&单据作业类型对照ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInoutDocumentMovementTypeRelationByIds(List<Long> sids) {
        return conInoutDocumentMovementTypeRelationMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conInoutDocumentMovementTypeRelation
     * @return
     */
    @Override
    public int changeStatus(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        int row = 0;
        Long[] sids = conInoutDocumentMovementTypeRelation.getSidList();
        if (sids != null && sids.length > 0) {
            row = conInoutDocumentMovementTypeRelationMapper.update(null, new UpdateWrapper<ConInoutDocumentMovementTypeRelation>().lambda().set(ConInoutDocumentMovementTypeRelation::getStatus, conInoutDocumentMovementTypeRelation.getStatus())
                    .in(ConInoutDocumentMovementTypeRelation::getSid, sids));
            for (Long id : sids) {
                conInoutDocumentMovementTypeRelation.setSid(id);
                row = conInoutDocumentMovementTypeRelationMapper.updateById(conInoutDocumentMovementTypeRelation);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conInoutDocumentMovementTypeRelation.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conInoutDocumentMovementTypeRelation.getSid(), BusinessType.CHECK.ordinal(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conInoutDocumentMovementTypeRelation
     * @return
     */
    @Override
    public int check(ConInoutDocumentMovementTypeRelation conInoutDocumentMovementTypeRelation) {
        int row = 0;
        Long[] sids = conInoutDocumentMovementTypeRelation.getSidList();
        if (sids != null && sids.length > 0) {
            row = conInoutDocumentMovementTypeRelationMapper.update(null, new UpdateWrapper<ConInoutDocumentMovementTypeRelation>().lambda().set(ConInoutDocumentMovementTypeRelation::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConInoutDocumentMovementTypeRelation::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList, TITLE);
            }
        }
        return row;
    }


}
