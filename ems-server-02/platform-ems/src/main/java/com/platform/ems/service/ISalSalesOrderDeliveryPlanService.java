package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;

/**
 * 销售订单-发货计划Service接口
 * 
 * @author linhongwei
 * @date 2021-11-01
 */
public interface ISalSalesOrderDeliveryPlanService extends IService<SalSalesOrderDeliveryPlan>{
    /**
     * 查询销售订单-发货计划
     * 
     * @param salesOrderItemSid 销售订单-发货计划ID
     * @return 销售订单-发货计划
     */
    public List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanById(Long salesOrderItemSid);

    /**
     * 查询销售订单-发货计划列表
     * 
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 销售订单-发货计划集合
     */
    public List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanList(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

    /**
     * 新增销售订单-发货计划
     * 
     * @param salSalesOrderDeliveryPlans 销售订单-发货计划
     * @return 结果
     */
    public int insertSalSalesOrderDeliveryPlan(List<SalSalesOrderDeliveryPlan> salSalesOrderDeliveryPlans);

    /**
     * 修改销售订单-发货计划
     * 
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 结果
     */
    public int updateSalSalesOrderDeliveryPlan(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

    /**
     * 变更销售订单-发货计划
     *
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 结果
     */
    public int changeSalSalesOrderDeliveryPlan(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

    /**
     * 批量删除销售订单-发货计划
     * 
     * @param deliveryPlanSids 需要删除的销售订单-发货计划ID
     * @return 结果
     */
    public int deleteSalSalesOrderDeliveryPlanByIds(List<Long> deliveryPlanSids);

    /**
    * 启用/停用
    * @param salSalesOrderDeliveryPlan
    * @return
    */
    int changeStatus(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

    /**
     * 更改确认状态
     * @param salSalesOrderDeliveryPlan
     * @return
     */
    int check(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

}
