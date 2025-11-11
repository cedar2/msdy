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
import com.platform.ems.plug.domain.ConBuTypePurchaseDeduction;
import com.platform.ems.plug.mapper.ConBuTypePurchaseDeductionMapper;
import com.platform.ems.plug.service.IConBuTypePurchaseDeductionService;
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
 * 业务类型_采购扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypePurchaseDeductionServiceImpl extends ServiceImpl<ConBuTypePurchaseDeductionMapper,ConBuTypePurchaseDeduction>  implements IConBuTypePurchaseDeductionService {
    @Autowired
    private ConBuTypePurchaseDeductionMapper conBuTypePurchaseDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_采购扣款单";
    /**
     * 查询业务类型_采购扣款单
     *
     * @param sid 业务类型_采购扣款单ID
     * @return 业务类型_采购扣款单
     */
    @Override
    public ConBuTypePurchaseDeduction selectConBuTypePurchaseDeductionById(Long sid) {
        ConBuTypePurchaseDeduction conBuTypePurchaseDeduction = conBuTypePurchaseDeductionMapper.selectConBuTypePurchaseDeductionById(sid);
        MongodbUtil.find(conBuTypePurchaseDeduction);
        return  conBuTypePurchaseDeduction;
    }

    /**
     * 查询业务类型_采购扣款单列表
     *
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 业务类型_采购扣款单
     */
    @Override
    public List<ConBuTypePurchaseDeduction> selectConBuTypePurchaseDeductionList(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction) {
        return conBuTypePurchaseDeductionMapper.selectConBuTypePurchaseDeductionList(conBuTypePurchaseDeduction);
    }

    /**
     * 新增业务类型_采购扣款单
     * 需要注意编码重复校验
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction) {
        List<ConBuTypePurchaseDeduction> codeList = conBuTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConBuTypePurchaseDeduction>().lambda()
                .eq(ConBuTypePurchaseDeduction::getCode, conBuTypePurchaseDeduction.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypePurchaseDeduction> nameList = conBuTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConBuTypePurchaseDeduction>().lambda()
                .eq(ConBuTypePurchaseDeduction::getName, conBuTypePurchaseDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypePurchaseDeductionMapper.insert(conBuTypePurchaseDeduction);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypePurchaseDeduction.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_采购扣款单
     *
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction) {
        ConBuTypePurchaseDeduction response = conBuTypePurchaseDeductionMapper.selectConBuTypePurchaseDeductionById(conBuTypePurchaseDeduction.getSid());
        int row=conBuTypePurchaseDeductionMapper.updateById(conBuTypePurchaseDeduction);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseDeduction.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypePurchaseDeduction,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_采购扣款单
     *
     * @param conBuTypePurchaseDeduction 业务类型_采购扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypePurchaseDeduction(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction) {
        List<ConBuTypePurchaseDeduction> nameList = conBuTypePurchaseDeductionMapper.selectList(new QueryWrapper<ConBuTypePurchaseDeduction>().lambda()
                .eq(ConBuTypePurchaseDeduction::getName, conBuTypePurchaseDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypePurchaseDeduction.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypePurchaseDeduction.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypePurchaseDeduction response = conBuTypePurchaseDeductionMapper.selectConBuTypePurchaseDeductionById(conBuTypePurchaseDeduction.getSid());
        int row = conBuTypePurchaseDeductionMapper.updateAllById(conBuTypePurchaseDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseDeduction.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypePurchaseDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_采购扣款单
     *
     * @param sids 需要删除的业务类型_采购扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypePurchaseDeductionByIds(List<Long> sids) {
        return conBuTypePurchaseDeductionMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypePurchaseDeduction
    * @return
    */
    @Override
    public int changeStatus(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction){
        int row=0;
        Long[] sids=conBuTypePurchaseDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseDeduction.setSid(id);
                row=conBuTypePurchaseDeductionMapper.updateById( conBuTypePurchaseDeduction);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypePurchaseDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypePurchaseDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypePurchaseDeduction
     * @return
     */
    @Override
    public int check(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction){
        int row=0;
        Long[] sids=conBuTypePurchaseDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseDeduction.setSid(id);
                row=conBuTypePurchaseDeductionMapper.updateById( conBuTypePurchaseDeduction);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypePurchaseDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
