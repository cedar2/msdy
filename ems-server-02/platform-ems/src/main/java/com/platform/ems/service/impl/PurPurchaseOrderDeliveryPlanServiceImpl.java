package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.PurPurchaseOrderDeliveryPlanMapper;
import com.platform.ems.domain.PurPurchaseOrderDeliveryPlan;
import com.platform.ems.service.IPurPurchaseOrderDeliveryPlanService;

/**
 * 系统SID-采购订单明细的交货计划明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-11-11
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderDeliveryPlanServiceImpl extends ServiceImpl<PurPurchaseOrderDeliveryPlanMapper,PurPurchaseOrderDeliveryPlan>  implements IPurPurchaseOrderDeliveryPlanService {
    @Autowired
    private PurPurchaseOrderDeliveryPlanMapper purPurchaseOrderDeliveryPlanMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "系统SID-采购订单明细的交货计划明细";
    /**
     * 查询系统SID-采购订单明细的交货计划明细
     *
     * @param deliveryPlanSid 系统SID-采购订单明细的交货计划明细ID
     * @return 系统SID-采购订单明细的交货计划明细
     */
    @Override
    public PurPurchaseOrderDeliveryPlan selectPurPurchaseOrderDeliveryPlanById(Long purchaseOrderItemSid) {
        return  null;
    }

    /**
     * 查询系统SID-采购订单明细的交货计划明细列表
     *
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 系统SID-采购订单明细的交货计划明细
     */
    @Override
    public List<PurPurchaseOrderDeliveryPlan> selectPurPurchaseOrderDeliveryPlanList(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return purPurchaseOrderDeliveryPlanMapper.selectPurPurchaseOrderDeliveryPlanList(purPurchaseOrderDeliveryPlan);
    }

    /**
     * 新增系统SID-采购订单明细的交货计划明细
     * 需要注意编码重复校验
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrderDeliveryPlan(List<PurPurchaseOrderDeliveryPlan> purPurchaseOrderDeliveryPlans) {
        int row;
        if(CollectionUtil.isNotEmpty(purPurchaseOrderDeliveryPlans)){
            Long purchaseOrderItemSid = purPurchaseOrderDeliveryPlans.get(0).getPurchaseOrderItemSid();
            purPurchaseOrderDeliveryPlanMapper.delete( new QueryWrapper<PurPurchaseOrderDeliveryPlan>().lambda()
                    .eq(PurPurchaseOrderDeliveryPlan::getPurchaseOrderItemSid,purchaseOrderItemSid)
            );
            row= purPurchaseOrderDeliveryPlanMapper.inserts(purPurchaseOrderDeliveryPlans);
        }else{
            row=1;
        }
        return row;
    }

    /**
     * 修改系统SID-采购订单明细的交货计划明细
     *
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrderDeliveryPlan(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return 1;
    }

    /**
     * 变更系统SID-采购订单明细的交货计划明细
     *
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurPurchaseOrderDeliveryPlan(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return 1;
    }

    /**
     * 批量删除系统SID-采购订单明细的交货计划明细
     *
     * @param deliveryPlanSids 需要删除的系统SID-采购订单明细的交货计划明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderDeliveryPlanByIds(List<Long> deliveryPlanSids) {
        return purPurchaseOrderDeliveryPlanMapper.deleteBatchIds(deliveryPlanSids);
    }

    /**
    * 启用/停用
    * @param purPurchaseOrderDeliveryPlan
    * @return
    */
    @Override
    public int changeStatus(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan){
        int row=0;

        return row;
    }


    /**
     *更改确认状态
     * @param purPurchaseOrderDeliveryPlan
     * @return
     */
    @Override
    public int check(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan){
        int row=0;
        return row;
    }


}
