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
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptancePurchase;
import com.platform.ems.plug.mapper.ConDocTypeServiceAcceptancePurchaseMapper;
import com.platform.ems.plug.service.IConDocTypeServiceAcceptancePurchaseService;
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
 * 单据类型_服务采购验收单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeServiceAcceptancePurchaseServiceImpl extends ServiceImpl<ConDocTypeServiceAcceptancePurchaseMapper,ConDocTypeServiceAcceptancePurchase>  implements IConDocTypeServiceAcceptancePurchaseService {
    @Autowired
    private ConDocTypeServiceAcceptancePurchaseMapper conDocTypeServiceAcceptancePurchaseMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_服务采购验收单";
    /**
     * 查询单据类型_服务采购验收单
     *
     * @param sid 单据类型_服务采购验收单ID
     * @return 单据类型_服务采购验收单
     */
    @Override
    public ConDocTypeServiceAcceptancePurchase selectConDocTypeServiceAcceptancePurchaseById(Long sid) {
        ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase = conDocTypeServiceAcceptancePurchaseMapper.selectConDocTypeServiceAcceptancePurchaseById(sid);
        MongodbUtil.find(conDocTypeServiceAcceptancePurchase);
        return  conDocTypeServiceAcceptancePurchase;
    }

    /**
     * 查询单据类型_服务采购验收单列表
     *
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 单据类型_服务采购验收单
     */
    @Override
    public List<ConDocTypeServiceAcceptancePurchase> selectConDocTypeServiceAcceptancePurchaseList(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        return conDocTypeServiceAcceptancePurchaseMapper.selectConDocTypeServiceAcceptancePurchaseList(conDocTypeServiceAcceptancePurchase);
    }

    /**
     * 新增单据类型_服务采购验收单
     * 需要注意编码重复校验
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        List<ConDocTypeServiceAcceptancePurchase> codeList = conDocTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptancePurchase>().lambda()
                .eq(ConDocTypeServiceAcceptancePurchase::getCode, conDocTypeServiceAcceptancePurchase.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeServiceAcceptancePurchase> nameList = conDocTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptancePurchase>().lambda()
                .eq(ConDocTypeServiceAcceptancePurchase::getName, conDocTypeServiceAcceptancePurchase.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeServiceAcceptancePurchaseMapper.insert(conDocTypeServiceAcceptancePurchase);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptancePurchase.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_服务采购验收单
     *
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        ConDocTypeServiceAcceptancePurchase response = conDocTypeServiceAcceptancePurchaseMapper.selectConDocTypeServiceAcceptancePurchaseById(conDocTypeServiceAcceptancePurchase.getSid());
        int row=conDocTypeServiceAcceptancePurchaseMapper.updateById(conDocTypeServiceAcceptancePurchase);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptancePurchase.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeServiceAcceptancePurchase,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_服务采购验收单
     *
     * @param conDocTypeServiceAcceptancePurchase 单据类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeServiceAcceptancePurchase(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase) {
        List<ConDocTypeServiceAcceptancePurchase> nameList = conDocTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConDocTypeServiceAcceptancePurchase>().lambda()
                .eq(ConDocTypeServiceAcceptancePurchase::getName, conDocTypeServiceAcceptancePurchase.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeServiceAcceptancePurchase.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeServiceAcceptancePurchase.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeServiceAcceptancePurchase response = conDocTypeServiceAcceptancePurchaseMapper.selectConDocTypeServiceAcceptancePurchaseById(conDocTypeServiceAcceptancePurchase.getSid());
        int row = conDocTypeServiceAcceptancePurchaseMapper.updateAllById(conDocTypeServiceAcceptancePurchase);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeServiceAcceptancePurchase.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeServiceAcceptancePurchase, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_服务采购验收单
     *
     * @param sids 需要删除的单据类型_服务采购验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeServiceAcceptancePurchaseByIds(List<Long> sids) {
        return conDocTypeServiceAcceptancePurchaseMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeServiceAcceptancePurchase
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase){
        int row=0;
        Long[] sids=conDocTypeServiceAcceptancePurchase.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeServiceAcceptancePurchase.setSid(id);
                row=conDocTypeServiceAcceptancePurchaseMapper.updateById( conDocTypeServiceAcceptancePurchase);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeServiceAcceptancePurchase.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeServiceAcceptancePurchase.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeServiceAcceptancePurchase
     * @return
     */
    @Override
    public int check(ConDocTypeServiceAcceptancePurchase conDocTypeServiceAcceptancePurchase){
        int row=0;
        Long[] sids=conDocTypeServiceAcceptancePurchase.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeServiceAcceptancePurchase.setSid(id);
                row=conDocTypeServiceAcceptancePurchaseMapper.updateById( conDocTypeServiceAcceptancePurchase);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeServiceAcceptancePurchase.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
