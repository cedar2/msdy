package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderDeliveryPlan;

/**
 * 系统SID-采购订单明细的交货计划明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-11-11
 */
public interface PurPurchaseOrderDeliveryPlanMapper  extends BaseMapper<PurPurchaseOrderDeliveryPlan> {


    List<PurPurchaseOrderDeliveryPlan> selectPurPurchaseOrderDeliveryPlanById(Long purchaseOrderItemSid);

    List<PurPurchaseOrderDeliveryPlan> selectPurPurchaseOrderDeliveryPlanList(PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan);

    /**
     * 添加多个
     * @param list List PurPurchaseOrderDeliveryPlan
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderDeliveryPlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseOrderDeliveryPlan
    * @return int
    */
    int updateAllById(PurPurchaseOrderDeliveryPlan entity);

    /**
     * 更新多个
     * @param list List PurPurchaseOrderDeliveryPlan
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderDeliveryPlan> list);


}
