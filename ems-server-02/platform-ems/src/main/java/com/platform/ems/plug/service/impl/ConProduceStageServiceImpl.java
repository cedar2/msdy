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
import com.platform.ems.plug.domain.ConProduceStage;
import com.platform.ems.plug.mapper.ConProduceStageMapper;
import com.platform.ems.plug.service.IConProduceStageService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 所属生产阶段Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-26
 */
@Service
@SuppressWarnings("all")
public class ConProduceStageServiceImpl extends ServiceImpl<ConProduceStageMapper, ConProduceStage> implements IConProduceStageService {
    @Autowired
    private ConProduceStageMapper conProduceStageMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "所属生产阶段";

    /**
     * 查询所属生产阶段
     *
     * @param sid 所属生产阶段ID
     * @return 所属生产阶段
     */
    @Override
    public ConProduceStage selectConProduceStageById(Long sid) {
        ConProduceStage conProduceStage = conProduceStageMapper.selectConProduceStageById(sid);
        MongodbUtil.find(conProduceStage);
        return conProduceStage;
    }

    /**
     * 查询所属生产阶段列表
     *
     * @param conProduceStage 所属生产阶段
     * @return 所属生产阶段
     */
    @Override
    public List<ConProduceStage> selectConProduceStageList(ConProduceStage conProduceStage) {
        return conProduceStageMapper.selectConProduceStageList(conProduceStage);
    }

    /**
     * 新增所属生产阶段
     * 需要注意编码重复校验
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConProduceStage(ConProduceStage conProduceStage) {
        List<ConProduceStage> codeList = conProduceStageMapper.selectList(new QueryWrapper<ConProduceStage>().lambda()
                .eq(ConProduceStage::getCode, conProduceStage.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConProduceStage> nameList = conProduceStageMapper.selectList(new QueryWrapper<ConProduceStage>().lambda()
                .eq(ConProduceStage::getName, conProduceStage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        conProduceStage.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conProduceStageMapper.insert(conProduceStage);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conProduceStage.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改所属生产阶段
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConProduceStage(ConProduceStage conProduceStage) {
        ConProduceStage response = conProduceStageMapper.selectConProduceStageById(conProduceStage.getSid());
        int row = conProduceStageMapper.updateById(conProduceStage);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conProduceStage.getSid(), BusinessType.UPDATE.getValue(), response, conProduceStage, TITLE);
        }
        return row;
    }

    /**
     * 变更所属生产阶段
     *
     * @param conProduceStage 所属生产阶段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConProduceStage(ConProduceStage conProduceStage) {
        List<ConProduceStage> nameList = conProduceStageMapper.selectList(new QueryWrapper<ConProduceStage>().lambda()
                .eq(ConProduceStage::getName, conProduceStage.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conProduceStage.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conProduceStage.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConProduceStage response = conProduceStageMapper.selectConProduceStageById(conProduceStage.getSid());
        int row = conProduceStageMapper.updateAllById(conProduceStage);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conProduceStage.getSid(), BusinessType.CHANGE.getValue(), response, conProduceStage, TITLE);
        }
        return row;
    }

    /**
     * 批量删除所属生产阶段
     *
     * @param sids 需要删除的所属生产阶段ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConProduceStageByIds(List<Long> sids) {
        return conProduceStageMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conProduceStage
     * @return
     */
    @Override
    public int changeStatus(ConProduceStage conProduceStage) {
        int row = 0;
        Long[] sids = conProduceStage.getSidList();
        if (sids != null && sids.length > 0) {
            row = conProduceStageMapper.update(null, new UpdateWrapper<ConProduceStage>().lambda()
                                       .set(ConProduceStage::getStatus, conProduceStage.getStatus()).in(ConProduceStage::getSid, sids));
            if (row != sids.length) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            String remark = conProduceStage.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
            MongodbUtil.insertUserLog(conProduceStage.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conProduceStage
     * @return
     */
    @Override
    public int check(ConProduceStage conProduceStage) {
        int row = 0;
        Long[] sids = conProduceStage.getSidList();
        if (sids != null && sids.length > 0) {
            row = conProduceStageMapper.update(null, new UpdateWrapper<ConProduceStage>().lambda().set(ConProduceStage::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConProduceStage::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 所属生产阶段下拉框列表
     */
    @Override
    public List<ConProduceStage> getList(ConProduceStage conProduceStage) {
        return conProduceStageMapper.getList(conProduceStage);
    }
}
