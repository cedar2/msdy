package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;

/**
 * 销售订单-发货计划Mapper接口
 * 
 * @author linhongwei
 * @date 2021-11-01
 */
public interface SalSalesOrderDeliveryPlanMapper  extends BaseMapper<SalSalesOrderDeliveryPlan> {

    List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanById(Long salesOrderItemSid);

    List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanList(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan);

    /**
     * 添加多个
     * @param list List SalSalesOrderDeliveryPlan
     * @return int
     */
    int inserts(@Param("list") List<SalSalesOrderDeliveryPlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSalesOrderDeliveryPlan
    * @return int
    */
    int updateAllById(SalSalesOrderDeliveryPlan entity);

    /**
     * 更新多个
     * @param list List SalSalesOrderDeliveryPlan
     * @return int
     */
    int updatesAllById(@Param("list") List<SalSalesOrderDeliveryPlan> list);


}
