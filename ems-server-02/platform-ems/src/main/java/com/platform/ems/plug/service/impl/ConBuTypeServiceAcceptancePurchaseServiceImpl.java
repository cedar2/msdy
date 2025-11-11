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
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptancePurchase;
import com.platform.ems.plug.mapper.ConBuTypeServiceAcceptancePurchaseMapper;
import com.platform.ems.plug.service.IConBuTypeServiceAcceptancePurchaseService;
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
 * 业务类型_服务采购验收单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeServiceAcceptancePurchaseServiceImpl extends ServiceImpl<ConBuTypeServiceAcceptancePurchaseMapper,ConBuTypeServiceAcceptancePurchase>  implements IConBuTypeServiceAcceptancePurchaseService {
    @Autowired
    private ConBuTypeServiceAcceptancePurchaseMapper conBuTypeServiceAcceptancePurchaseMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_服务采购验收单";
    /**
     * 查询业务类型_服务采购验收单
     *
     * @param sid 业务类型_服务采购验收单ID
     * @return 业务类型_服务采购验收单
     */
    @Override
    public ConBuTypeServiceAcceptancePurchase selectConBuTypeServiceAcceptancePurchaseById(Long sid) {
        ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase = conBuTypeServiceAcceptancePurchaseMapper.selectConBuTypeServiceAcceptancePurchaseById(sid);
        MongodbUtil.find(conBuTypeServiceAcceptancePurchase);
        return  conBuTypeServiceAcceptancePurchase;
    }

    /**
     * 查询业务类型_服务采购验收单列表
     *
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 业务类型_服务采购验收单
     */
    @Override
    public List<ConBuTypeServiceAcceptancePurchase> selectConBuTypeServiceAcceptancePurchaseList(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        return conBuTypeServiceAcceptancePurchaseMapper.selectConBuTypeServiceAcceptancePurchaseList(conBuTypeServiceAcceptancePurchase);
    }

    /**
     * 新增业务类型_服务采购验收单
     * 需要注意编码重复校验
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        List<ConBuTypeServiceAcceptancePurchase> codeList = conBuTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptancePurchase>().lambda()
                .eq(ConBuTypeServiceAcceptancePurchase::getCode, conBuTypeServiceAcceptancePurchase.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeServiceAcceptancePurchase> nameList = conBuTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptancePurchase>().lambda()
                .eq(ConBuTypeServiceAcceptancePurchase::getName, conBuTypeServiceAcceptancePurchase.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeServiceAcceptancePurchaseMapper.insert(conBuTypeServiceAcceptancePurchase);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptancePurchase.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_服务采购验收单
     *
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        ConBuTypeServiceAcceptancePurchase response = conBuTypeServiceAcceptancePurchaseMapper.selectConBuTypeServiceAcceptancePurchaseById(conBuTypeServiceAcceptancePurchase.getSid());
        int row=conBuTypeServiceAcceptancePurchaseMapper.updateById(conBuTypeServiceAcceptancePurchase);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptancePurchase.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeServiceAcceptancePurchase,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_服务采购验收单
     *
     * @param conBuTypeServiceAcceptancePurchase 业务类型_服务采购验收单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeServiceAcceptancePurchase(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase) {
        List<ConBuTypeServiceAcceptancePurchase> nameList = conBuTypeServiceAcceptancePurchaseMapper.selectList(new QueryWrapper<ConBuTypeServiceAcceptancePurchase>().lambda()
                .eq(ConBuTypeServiceAcceptancePurchase::getName, conBuTypeServiceAcceptancePurchase.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeServiceAcceptancePurchase.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeServiceAcceptancePurchase.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeServiceAcceptancePurchase response = conBuTypeServiceAcceptancePurchaseMapper.selectConBuTypeServiceAcceptancePurchaseById(conBuTypeServiceAcceptancePurchase.getSid());
        int row = conBuTypeServiceAcceptancePurchaseMapper.updateAllById(conBuTypeServiceAcceptancePurchase);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeServiceAcceptancePurchase.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeServiceAcceptancePurchase, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_服务采购验收单
     *
     * @param sids 需要删除的业务类型_服务采购验收单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeServiceAcceptancePurchaseByIds(List<Long> sids) {
        return conBuTypeServiceAcceptancePurchaseMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeServiceAcceptancePurchase
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase){
        int row=0;
        Long[] sids=conBuTypeServiceAcceptancePurchase.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeServiceAcceptancePurchase.setSid(id);
                row=conBuTypeServiceAcceptancePurchaseMapper.updateById( conBuTypeServiceAcceptancePurchase);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeServiceAcceptancePurchase.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeServiceAcceptancePurchase.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeServiceAcceptancePurchase
     * @return
     */
    @Override
    public int check(ConBuTypeServiceAcceptancePurchase conBuTypeServiceAcceptancePurchase){
        int row=0;
        Long[] sids=conBuTypeServiceAcceptancePurchase.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeServiceAcceptancePurchase.setSid(id);
                row=conBuTypeServiceAcceptancePurchaseMapper.updateById( conBuTypeServiceAcceptancePurchase);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeServiceAcceptancePurchase.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
