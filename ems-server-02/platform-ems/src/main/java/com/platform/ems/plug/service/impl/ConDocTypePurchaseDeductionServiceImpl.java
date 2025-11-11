package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocTypePurchaseDeduction;
import com.platform.ems.plug.mapper.ConDocTypePurchaseDeductionMapper;
import com.platform.ems.plug.service.IConDocTypePurchaseDeductionService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_采购扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypePurchaseDeductionServiceImpl extends ServiceImpl<ConDocTypePurchaseDeductionMapper,ConDocTypePurchaseDeduction>  implements IConDocTypePurchaseDeductionService {
    @Autowired
    private ConDocTypePurchaseDeductionMapper conDocTypePurchaseDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_采购扣款单";
    /**
     * 查询单据类型_采购扣款单
     *
     * @param sid 单据类型_采购扣款单ID
     * @return 单据类型_采购扣款单
     */
    @Override
    public ConDocTypePurchaseDeduction selectConDocTypePurchaseDeductionById(Long sid) {
        ConDocTypePurchaseDeduction conDocTypePurchaseDeduction = conDocTypePurchaseDeductionMapper.selectConDocTypePurchaseDeductionById(sid);
        MongodbUtil.find(conDocTypePurchaseDeduction);
        return  conDocTypePurchaseDeduction;
    }

    /**
     * 查询单据类型_采购扣款单列表
     *
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 单据类型_采购扣款单
     */
    @Override
    public List<ConDocTypePurchaseDeduction> selectConDocTypePurchaseDeductionList(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction) {
        return conDocTypePurchaseDeductionMapper.selectConDocTypePurchaseDeductionList(conDocTypePurchaseDeduction);
    }

    /**
     * 新增单据类型_采购扣款单
     * 需要注意编码重复校验
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction) {
        List<ConDocTypePurchaseDeduction> codeList = conDocTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConDocTypePurchaseDeduction>().lambda()
                .eq(ConDocTypePurchaseDeduction::getCode, conDocTypePurchaseDeduction.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypePurchaseDeduction> nameList = conDocTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConDocTypePurchaseDeduction>().lambda()
                .eq(ConDocTypePurchaseDeduction::getName, conDocTypePurchaseDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypePurchaseDeductionMapper.insert(conDocTypePurchaseDeduction);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypePurchaseDeduction.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_采购扣款单
     *
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction) {
        ConDocTypePurchaseDeduction response = conDocTypePurchaseDeductionMapper.selectConDocTypePurchaseDeductionById(conDocTypePurchaseDeduction.getSid());
        int row=conDocTypePurchaseDeductionMapper.updateById(conDocTypePurchaseDeduction);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseDeduction.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypePurchaseDeduction,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_采购扣款单
     *
     * @param conDocTypePurchaseDeduction 单据类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypePurchaseDeduction(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction) {
        List<ConDocTypePurchaseDeduction> nameList = conDocTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConDocTypePurchaseDeduction>().lambda()
                .eq(ConDocTypePurchaseDeduction::getName, conDocTypePurchaseDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypePurchaseDeduction.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypePurchaseDeduction.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypePurchaseDeduction response = conDocTypePurchaseDeductionMapper.selectConDocTypePurchaseDeductionById(conDocTypePurchaseDeduction.getSid());
        int row = conDocTypePurchaseDeductionMapper.updateAllById(conDocTypePurchaseDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseDeduction.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypePurchaseDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_采购扣款单
     *
     * @param sids 需要删除的单据类型_采购扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypePurchaseDeductionByIds(List<Long> sids) {
        return conDocTypePurchaseDeductionMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypePurchaseDeduction
    * @return
    */
    @Override
    public int changeStatus(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction){
        int row=0;
        Long[] sids=conDocTypePurchaseDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseDeduction.setSid(id);
                row=conDocTypePurchaseDeductionMapper.updateById( conDocTypePurchaseDeduction);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypePurchaseDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypePurchaseDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypePurchaseDeduction
     * @return
     */
    @Override
    public int check(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction){
        int row=0;
        Long[] sids=conDocTypePurchaseDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseDeduction.setSid(id);
                row=conDocTypePurchaseDeductionMapper.updateById( conDocTypePurchaseDeduction);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypePurchaseDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
