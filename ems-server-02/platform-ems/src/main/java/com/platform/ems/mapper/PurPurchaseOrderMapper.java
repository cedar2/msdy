package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.dto.response.PurPurchaseOrderOutResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrder;
import org.apache.ibatis.annotations.Select;

/**
 * 采购订单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface PurPurchaseOrderMapper  extends BaseMapper<PurPurchaseOrder> {

    PurPurchaseOrder selectPurPurchaseOrderById(Long purchaseOrderSid);

    List<String> getCode(Long purchaseOrderCode);

    PurPurchaseOrderOutResponse getOutPurPurchaseOrderById(Long purchaseOrderSid);

    List<PurPurchaseOrder> selectPurPurchaseOrderList(PurPurchaseOrder purPurchaseOrder);

    int updatePrice(PurPurchaseOrder purPurchaseOrder);

    List<PurPurchaseOrderItem> getUpdatePrice(PurPurchaseOrder purPurchaseOrder);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<PurPurchaseOrder> list);

    /**
     * 全量更新
     */
    int updateAllById(PurPurchaseOrder entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrder> list);


    int countByDomain(PurPurchaseOrder params);

    int deletePurPurchaseOrderByIds(@Param("array")Long[] purchaseOrderSids);

    int confirm(PurPurchaseOrder purPurchaseOrder);

    PurPurchaseOrder selectPurPurchaseOrderByCode(Long purchaseOrderCode);

    /**
     * 若订单里的合同的“特殊用途”为“临时过渡”，合同确认的
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseOrder> selectListInterceptorIgnore(PurPurchaseOrder purchaseOrder);

    /**
     * 查询出所有处理状态为“已确认”且签收状态(纸质合同)为“未签收”的订单，
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseOrder> selectListContractPaperInterceptorIgnore(PurPurchaseOrder purchaseOrder);


    @Select("SELECT t.purchase_order_sid, t.purchase_order_code, t.in_out_stock_status \n" +
            "FROM s_pur_purchase_order t \n" +
            "WHERE t.in_out_stock_status IN ('BFCK','WCK') \n" +
            "        AND EXISTS (SELECT 1 FROM s_pur_purchase_order_item t1 \n" +
            "                WHERE t1.purchase_order_sid = t.purchase_order_sid) \n" +
            "        AND NOT EXISTS (SELECT 1 \n" +
            "                FROM s_pur_purchase_order_item t2 \n" +
            "                WHERE t2.purchase_order_sid = t.purchase_order_sid \n" +
            "                        AND (t2.in_out_stock_status IN ('BFCK','WCK') OR t2.in_out_stock_status IS NULL)) ")
    List<PurPurchaseOrder> selectInOutStockStatusCk();

    @Select("SELECT t.purchase_order_sid, t.purchase_order_code, t.in_out_stock_status \n" +
            "FROM s_pur_purchase_order t \n" +
            "WHERE t.in_out_stock_status IN ('BFRK','WRK') \n" +
            "        AND EXISTS (SELECT 1 FROM s_pur_purchase_order_item t1 \n" +
            "                WHERE t1.purchase_order_sid = t.purchase_order_sid) \n" +
            "        AND NOT EXISTS (SELECT 1 \n" +
            "                FROM s_pur_purchase_order_item t2 \n" +
            "                WHERE t2.purchase_order_sid = t.purchase_order_sid \n" +
            "                        AND (t2.in_out_stock_status IN ('BFRK','WRK') OR t2.in_out_stock_status IS NULL)) ")
    List<PurPurchaseOrder> selectInOutStockStatusRk();
}
