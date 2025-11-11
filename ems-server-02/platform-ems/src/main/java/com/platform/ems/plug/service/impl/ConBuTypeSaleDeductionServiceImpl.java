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
import com.platform.ems.plug.domain.ConBuTypeSaleDeduction;
import com.platform.ems.plug.mapper.ConBuTypeSaleDeductionMapper;
import com.platform.ems.plug.service.IConBuTypeSaleDeductionService;
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
 * 业务类型_销售扣款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeSaleDeductionServiceImpl extends ServiceImpl<ConBuTypeSaleDeductionMapper,ConBuTypeSaleDeduction>  implements IConBuTypeSaleDeductionService {
    @Autowired
    private ConBuTypeSaleDeductionMapper conBuTypeSaleDeductionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_销售扣款单";
    /**
     * 查询业务类型_销售扣款单
     *
     * @param sid 业务类型_销售扣款单ID
     * @return 业务类型_销售扣款单
     */
    @Override
    public ConBuTypeSaleDeduction selectConBuTypeSaleDeductionById(Long sid) {
        ConBuTypeSaleDeduction conBuTypeSaleDeduction = conBuTypeSaleDeductionMapper.selectConBuTypeSaleDeductionById(sid);
        MongodbUtil.find(conBuTypeSaleDeduction);
        return  conBuTypeSaleDeduction;
    }

    /**
     * 查询业务类型_销售扣款单列表
     *
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 业务类型_销售扣款单
     */
    @Override
    public List<ConBuTypeSaleDeduction> selectConBuTypeSaleDeductionList(ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        return conBuTypeSaleDeductionMapper.selectConBuTypeSaleDeductionList(conBuTypeSaleDeduction);
    }

    /**
     * 新增业务类型_销售扣款单
     * 需要注意编码重复校验
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        List<ConBuTypeSaleDeduction> codeList = conBuTypeSaleDeductionMapper.selectList(new QueryWrapper<ConBuTypeSaleDeduction>().lambda()
                .eq(ConBuTypeSaleDeduction::getCode, conBuTypeSaleDeduction.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeSaleDeduction> nameList = conBuTypeSaleDeductionMapper.selectList(new QueryWrapper<ConBuTypeSaleDeduction>().lambda()
                .eq(ConBuTypeSaleDeduction::getName, conBuTypeSaleDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeSaleDeductionMapper.insert(conBuTypeSaleDeduction);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeSaleDeduction.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_销售扣款单
     *
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        ConBuTypeSaleDeduction response = conBuTypeSaleDeductionMapper.selectConBuTypeSaleDeductionById(conBuTypeSaleDeduction.getSid());
        int row=conBuTypeSaleDeductionMapper.updateById(conBuTypeSaleDeduction);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSaleDeduction.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeSaleDeduction,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_销售扣款单
     *
     * @param conBuTypeSaleDeduction 业务类型_销售扣款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeSaleDeduction(ConBuTypeSaleDeduction conBuTypeSaleDeduction) {
        List<ConBuTypeSaleDeduction> nameList = conBuTypeSaleDeductionMapper.selectList(new QueryWrapper<ConBuTypeSaleDeduction>().lambda()
                .eq(ConBuTypeSaleDeduction::getName, conBuTypeSaleDeduction.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeSaleDeduction.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeSaleDeduction.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeSaleDeduction response = conBuTypeSaleDeductionMapper.selectConBuTypeSaleDeductionById(conBuTypeSaleDeduction.getSid());
        int row = conBuTypeSaleDeductionMapper.updateAllById(conBuTypeSaleDeduction);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSaleDeduction.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeSaleDeduction, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_销售扣款单
     *
     * @param sids 需要删除的业务类型_销售扣款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeSaleDeductionByIds(List<Long> sids) {
        return conBuTypeSaleDeductionMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeSaleDeduction
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeSaleDeduction conBuTypeSaleDeduction){
        int row=0;
        Long[] sids=conBuTypeSaleDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSaleDeduction.setSid(id);
                row=conBuTypeSaleDeductionMapper.updateById( conBuTypeSaleDeduction);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeSaleDeduction.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeSaleDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeSaleDeduction
     * @return
     */
    @Override
    public int check(ConBuTypeSaleDeduction conBuTypeSaleDeduction){
        int row=0;
        Long[] sids=conBuTypeSaleDeduction.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSaleDeduction.setSid(id);
                row=conBuTypeSaleDeductionMapper.updateById( conBuTypeSaleDeduction);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeSaleDeduction.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
