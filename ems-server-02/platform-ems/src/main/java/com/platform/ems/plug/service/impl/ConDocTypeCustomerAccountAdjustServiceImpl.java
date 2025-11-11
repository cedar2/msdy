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
import com.platform.ems.plug.domain.ConDocTypeCustomerAccountAdjust;
import com.platform.ems.plug.mapper.ConDocTypeCustomerAccountAdjustMapper;
import com.platform.ems.plug.service.IConDocTypeCustomerAccountAdjustService;
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
 * 单据类型_客户调账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeCustomerAccountAdjustServiceImpl extends ServiceImpl<ConDocTypeCustomerAccountAdjustMapper,ConDocTypeCustomerAccountAdjust>  implements IConDocTypeCustomerAccountAdjustService {
    @Autowired
    private ConDocTypeCustomerAccountAdjustMapper conDocTypeCustomerAccountAdjustMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_客户调账单";
    /**
     * 查询单据类型_客户调账单
     *
     * @param sid 单据类型_客户调账单ID
     * @return 单据类型_客户调账单
     */
    @Override
    public ConDocTypeCustomerAccountAdjust selectConDocTypeCustomerAccountAdjustById(Long sid) {
        ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust = conDocTypeCustomerAccountAdjustMapper.selectConDocTypeCustomerAccountAdjustById(sid);
        MongodbUtil.find(conDocTypeCustomerAccountAdjust);
        return  conDocTypeCustomerAccountAdjust;
    }

    /**
     * 查询单据类型_客户调账单列表
     *
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 单据类型_客户调账单
     */
    @Override
    public List<ConDocTypeCustomerAccountAdjust> selectConDocTypeCustomerAccountAdjustList(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust) {
        return conDocTypeCustomerAccountAdjustMapper.selectConDocTypeCustomerAccountAdjustList(conDocTypeCustomerAccountAdjust);
    }

    /**
     * 新增单据类型_客户调账单
     * 需要注意编码重复校验
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust) {
        List<ConDocTypeCustomerAccountAdjust> codeList = conDocTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeCustomerAccountAdjust>().lambda()
                .eq(ConDocTypeCustomerAccountAdjust::getCode, conDocTypeCustomerAccountAdjust.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeCustomerAccountAdjust> nameList = conDocTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeCustomerAccountAdjust>().lambda()
                .eq(ConDocTypeCustomerAccountAdjust::getName, conDocTypeCustomerAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeCustomerAccountAdjustMapper.insert(conDocTypeCustomerAccountAdjust);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeCustomerAccountAdjust.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_客户调账单
     *
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust) {
        ConDocTypeCustomerAccountAdjust response = conDocTypeCustomerAccountAdjustMapper.selectConDocTypeCustomerAccountAdjustById(conDocTypeCustomerAccountAdjust.getSid());
        int row=conDocTypeCustomerAccountAdjustMapper.updateById(conDocTypeCustomerAccountAdjust);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerAccountAdjust.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeCustomerAccountAdjust,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_客户调账单
     *
     * @param conDocTypeCustomerAccountAdjust 单据类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeCustomerAccountAdjust(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust) {
        List<ConDocTypeCustomerAccountAdjust> nameList = conDocTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConDocTypeCustomerAccountAdjust>().lambda()
                .eq(ConDocTypeCustomerAccountAdjust::getName, conDocTypeCustomerAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeCustomerAccountAdjust.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeCustomerAccountAdjust.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeCustomerAccountAdjust response = conDocTypeCustomerAccountAdjustMapper.selectConDocTypeCustomerAccountAdjustById(conDocTypeCustomerAccountAdjust.getSid());
        int row = conDocTypeCustomerAccountAdjustMapper.updateAllById(conDocTypeCustomerAccountAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeCustomerAccountAdjust.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeCustomerAccountAdjust, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_客户调账单
     *
     * @param sids 需要删除的单据类型_客户调账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeCustomerAccountAdjustByIds(List<Long> sids) {
        return conDocTypeCustomerAccountAdjustMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeCustomerAccountAdjust
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust){
        int row=0;
        Long[] sids=conDocTypeCustomerAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeCustomerAccountAdjust.setSid(id);
                row=conDocTypeCustomerAccountAdjustMapper.updateById( conDocTypeCustomerAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeCustomerAccountAdjust.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeCustomerAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeCustomerAccountAdjust
     * @return
     */
    @Override
    public int check(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust){
        int row=0;
        Long[] sids=conDocTypeCustomerAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeCustomerAccountAdjust.setSid(id);
                row=conDocTypeCustomerAccountAdjustMapper.updateById( conDocTypeCustomerAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeCustomerAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
