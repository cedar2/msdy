package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConDocTypeMaterialRequisitionMapper;
import com.platform.ems.plug.domain.ConDocTypeMaterialRequisition;
import com.platform.ems.plug.service.IConDocTypeMaterialRequisitionService;

/**
 * 单据类型_领退料单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeMaterialRequisitionServiceImpl extends ServiceImpl<ConDocTypeMaterialRequisitionMapper, ConDocTypeMaterialRequisition> implements IConDocTypeMaterialRequisitionService {
    @Autowired
    private ConDocTypeMaterialRequisitionMapper conDocTypeMaterialRequisitionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_领退料单";

    /**
     * 查询单据类型_领退料单
     *
     * @param sid 单据类型_领退料单ID
     * @return 单据类型_领退料单
     */
    @Override
    public ConDocTypeMaterialRequisition selectConDocTypeMaterialRequisitionById(Long sid) {
        ConDocTypeMaterialRequisition conDocTypeMaterialRequisition = conDocTypeMaterialRequisitionMapper.selectConDocTypeMaterialRequisitionById(sid);
        MongodbUtil.find(conDocTypeMaterialRequisition);
        return conDocTypeMaterialRequisition;
    }

    @Override
    public List<ConDocTypeMaterialRequisition> getList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return conDocTypeMaterialRequisitionMapper.getList(conDocTypeMaterialRequisition);
    }

    /**
     * 查询单据类型_领退料单列表
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 单据类型_领退料单
     */
    @Override
    public List<ConDocTypeMaterialRequisition> selectConDocTypeMaterialRequisitionList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        return conDocTypeMaterialRequisitionMapper.selectConDocTypeMaterialRequisitionList(conDocTypeMaterialRequisition);
    }

    /**
     * 新增单据类型_领退料单
     * 需要注意编码重复校验
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        List<ConDocTypeMaterialRequisition> codeList = conDocTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConDocTypeMaterialRequisition>().lambda()
                .eq(ConDocTypeMaterialRequisition::getCode, conDocTypeMaterialRequisition.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeMaterialRequisition> nameList = conDocTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConDocTypeMaterialRequisition>().lambda()
                .eq(ConDocTypeMaterialRequisition::getName, conDocTypeMaterialRequisition.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conDocTypeMaterialRequisitionMapper.insert(conDocTypeMaterialRequisition);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeMaterialRequisition.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_领退料单
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        ConDocTypeMaterialRequisition response = conDocTypeMaterialRequisitionMapper.selectConDocTypeMaterialRequisitionById(conDocTypeMaterialRequisition.getSid());
        List<ConDocTypeMaterialRequisition> nameList = conDocTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConDocTypeMaterialRequisition>().lambda()
                .eq(ConDocTypeMaterialRequisition::getName, conDocTypeMaterialRequisition.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeMaterialRequisition.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        int row = conDocTypeMaterialRequisitionMapper.updateById(conDocTypeMaterialRequisition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeMaterialRequisition.getSid(), BusinessType.UPDATE.getValue(), response, conDocTypeMaterialRequisition, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_领退料单
     *
     * @param conDocTypeMaterialRequisition 单据类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeMaterialRequisition(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        ConDocTypeMaterialRequisition response = conDocTypeMaterialRequisitionMapper.selectConDocTypeMaterialRequisitionById(conDocTypeMaterialRequisition.getSid());
        List<ConDocTypeMaterialRequisition> nameList = conDocTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConDocTypeMaterialRequisition>().lambda()
                .eq(ConDocTypeMaterialRequisition::getName, conDocTypeMaterialRequisition.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeMaterialRequisition.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeMaterialRequisition.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conDocTypeMaterialRequisitionMapper.updateAllById(conDocTypeMaterialRequisition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeMaterialRequisition.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeMaterialRequisition, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_领退料单
     *
     * @param sids 需要删除的单据类型_领退料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeMaterialRequisitionByIds(List<Long> sids) {
        return conDocTypeMaterialRequisitionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocTypeMaterialRequisition
     * @return
     */
    @Override
    public int changeStatus(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        int row = 0;
        Long[] sids = conDocTypeMaterialRequisition.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDocTypeMaterialRequisition.setSid(id);
                row = conDocTypeMaterialRequisitionMapper.updateById(conDocTypeMaterialRequisition);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocTypeMaterialRequisition.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocTypeMaterialRequisition.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocTypeMaterialRequisition
     * @return
     */
    @Override
    public int check(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition) {
        int row = 0;
        Long[] sids = conDocTypeMaterialRequisition.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDocTypeMaterialRequisition.setSid(id);
                row = conDocTypeMaterialRequisitionMapper.updateById(conDocTypeMaterialRequisition);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeMaterialRequisition.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
