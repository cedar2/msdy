package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.SalSalesOrderItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesOrder;
import org.apache.ibatis.annotations.Select;

/**
 * 销售订单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface SalSalesOrderMapper  extends BaseMapper<SalSalesOrder> {

    SalSalesOrder selectSalSalesOrderById(Long salesOrderSid);

    List<String> getCode(Long salesOrderCode);

    List<SalSalesOrder> selectSalSalesOrderList(SalSalesOrder salSalesOrder);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<SalSalesOrder> list);

    /**
     * 全量更新
     */
    int updateAllById(SalSalesOrder entity);

    int updatePrice(SalSalesOrder entity);

    List<SalSalesOrderItem> getUpdatePrice(SalSalesOrder entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<SalSalesOrder> list);

    int countByDomain(SalSalesOrder params);

    int deleteSalSalesOrderByIds(@Param("array")Long[] salesOrderSids);

    int confirm(SalSalesOrder salSalesOrder);

    SalSalesOrder getName(SalSalesOrder salSalesOrder);

    SalSalesOrder selectSalSalesOrderByCode(Long salesOrderCode);

    /**
     * 若订单里的合同的“特殊用途”为“临时过渡”，合同确认的
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSalesOrder> selectListInterceptorIgnore(SalSalesOrder salSalesOrder);

    /**
     * 查询出所有处理状态为“已确认”且签收状态(纸质合同)为“未签收”的销售订单，
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSalesOrder> selectListContractPaperInterceptorIgnore(SalSalesOrder salSalesOrder);

    @Select("SELECT t.sales_order_sid, t.in_out_stock_status \n" +
            "FROM s_sal_sales_order t \n" +
            "WHERE t.in_out_stock_status IN ('BFCK','WCK') \n" +
            "        AND EXISTS (SELECT 1 FROM s_sal_sales_order_item t1 \n" +
            "                WHERE t1.sales_order_sid = t.sales_order_sid) \n" +
            "        AND NOT EXISTS (SELECT 1 \n" +
            "                FROM s_sal_sales_order_item t2 \n" +
            "                WHERE t2.sales_order_sid = t.sales_order_sid \n" +
            "                        AND (t2.in_out_stock_status IN ('BFCK','WCK') OR t2.in_out_stock_status IS NULL)) \n")
    List<SalSalesOrder> selectInOutStockStatusCk();

    @Select("SELECT t.sales_order_sid, t.in_out_stock_status \n" +
            "FROM s_sal_sales_order t \n" +
            "WHERE t.in_out_stock_status IN ('BFRK','WRK') \n" +
            "        AND EXISTS (SELECT 1 FROM s_sal_sales_order_item t1 \n" +
            "                WHERE t1.sales_order_sid = t.sales_order_sid) \n" +
            "        AND NOT EXISTS (SELECT 1 \n" +
            "                FROM s_sal_sales_order_item t2 \n" +
            "                WHERE t2.sales_order_sid = t.sales_order_sid \n" +
            "                        AND (t2.in_out_stock_status IN ('BFRK','WRK') OR t2.in_out_stock_status IS NULL)) ")
    List<SalSalesOrder> selectInOutStockStatusRk();

}
