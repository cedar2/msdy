package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseOrderDeliveryPlan;

/**
 * 系统SID-采购订单明细的交货计划明细Service接口
 * 
 * @author linhongwei
 * @date 2021-11-11
 */
public interface IPurPurchaseOrderDeliveryPlanService extends IService<PurPurchaseOrderDeliveryPlan>{
    /**
     * 查询系统SID-采购订单明细的交货计划明细
     * 
     * @param deliveryPlanSid 系统SID-采购订单明细的交货计划明细ID
     * @return 系统SID-采购订单明细的交货计划明细
     */
    public PurPurchaseOrderDeliveryPlan selectPurPurchaseOrderDeliveryPlanById(Long deliveryPlanSid);

    /**
     * 查询系统SID-采购订单明细的交货计划明细列表
     * 
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 系统SID-采购订单明细的交货计划明细集合
     */
    public List<PurPurchaseOrderDeliveryPlan> selectPurPurchaseOrderDeliveryPlanList(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

    /**
     * 新增系统SID-采购订单明细的交货计划明细
     * 
     * @param purPurchaseOrderDeliveryPlans 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    public int insertPurPurchaseOrderDeliveryPlan(List<PurPurchaseOrderDeliveryPlan> purPurchaseOrderDeliveryPlans);

    /**
     * 修改系统SID-采购订单明细的交货计划明细
     * 
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    public int updatePurPurchaseOrderDeliveryPlan(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

    /**
     * 变更系统SID-采购订单明细的交货计划明细
     *
     * @param purPurchaseOrderDeliveryPlan 系统SID-采购订单明细的交货计划明细
     * @return 结果
     */
    public int changePurPurchaseOrderDeliveryPlan(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

    /**
     * 批量删除系统SID-采购订单明细的交货计划明细
     * 
     * @param deliveryPlanSids 需要删除的系统SID-采购订单明细的交货计划明细ID
     * @return 结果
     */
    public int deletePurPurchaseOrderDeliveryPlanByIds(List<Long> deliveryPlanSids);

    /**
    * 启用/停用
    * @param purPurchaseOrderDeliveryPlan
    * @return
    */
    int changeStatus(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

    /**
     * 更改确认状态
     * @param purPurchaseOrderDeliveryPlan
     * @return
     */
    int check(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

}
