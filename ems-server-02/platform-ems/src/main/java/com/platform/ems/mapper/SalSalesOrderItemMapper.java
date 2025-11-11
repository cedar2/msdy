package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.SalSaleContract;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.dto.request.OrderBestSellingRequest;
import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.request.SaleOrderProgressRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.SalSaleOrderProcessTracking;
import com.platform.ems.domain.dto.response.form.SalSaleProductCostForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalSalesOrderItem;
import org.apache.ibatis.annotations.Select;

/**
 * 销售订单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface SalSalesOrderItemMapper  extends BaseMapper<SalSalesOrderItem> {


    SalSalesOrderItem selectSalSalesOrderItemById(Long salesOrderItemSid);

    List<SalSalesOrderItem> selectSalSalesOrderItemList(SalSalesOrderItem salSalesOrderItem);
    List<Long> judgeOrderAndMaterial(SalSalesOrderItem salSalesOrderItem);

    /**
     * 添加多个
     * @param list List SalSalesOrderItem
     * @return int
     */
    int inserts(@Param("list") List<SalSalesOrderItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalSalesOrderItem
    * @return int
    */
    int updateAllById(SalSalesOrderItem entity);

    /**
     * 更新多个
     * @param list List SalSalesOrderItem
     * @return int
     */
    int updatesFl(@Param("list") List<SalSalesOrderItem> list);

    int updatesMl(@Param("list") List<SalSalesOrderItem> list);


    void deleteSalSalesOrderItemByIds(@Param("array")Long[] salesOrderSids);

    /**
     * 销售订单明细报表
     */
    List<SalSalesOrderItem> getItemList(SalSalesOrderItem salSalesOrderItem);

    List<SalSalesOrderItem>  getItemProductList(SalSalesOrderItem salSalesOrderItem);

    List<SalSalesOrderItem>  mobPaichan(SalSalesOrderItem salSalesOrderItem);

    List<SalSalesOrderItem>  getItemPCList(SalSalesOrderItem salSalesOrderItem);

    /**
     * 销售订单进度报表 主
     */
    @InterceptorIgnore(tenantLine = "true")
    @SqlParser(filter=true)
    List<SaleOrderProgressResponse> getProcessHead(SaleOrderProgressRequest request);
    /**
     * 销售订单进度报表 明细
     */
    List<SaleOrderProgressItemResponse> getProcessItem(SaleOrderProgressRequest request);

    @InterceptorIgnore(tenantLine = "true")
    List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request);

    @InterceptorIgnore(tenantLine = "true")
    List<OrderProgressItemResponse> getDeliveryProcessItem(OrderProgressRequest request);

    List<OrderTotalResponse> getTotal(OrderTotalRequest request);
    List<OrderTotalResponse> getTotalItem(OrderTotalRequest request);

    List<OrderBestSellingResponse> getBestSell(OrderBestSellingRequest request);

    List<OrderBestSellingResponse> getBestSellItem(OrderBestSellingRequest request);

    /**
     * 订单号+商品编码+合同交期 汇总订单量和销售金额含税
     *
     * @param salSaleContract 销售合同信息
     * @return 销售合同信息集合
     */
    List<SalSalesOrder> selectSalSalesOrderItemGroupList(SalSaleContract salSaleContract);

    /**
     * 销售出库进度跟踪报表
     *
     * @param salSaleOrder 销售订单
     * @return 销售订单集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSaleOrderProcessTracking> selectSalSaleProcessTrackingList(SalSaleOrderProcessTracking salSaleOrder);

    /**
     * 查询即将到期的订单明细
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSalesOrderItem> getToexpireBusiness();

    /**
     * 查询已逾期的订单明细
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSalesOrderItem> getOverdueBusiness();

    /**
     * 查询商品销售成本报表
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 销售订单-明细集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SalSaleProductCostForm> selectSalSalesProductCostList(SalSaleProductCostForm salSalesOrderItem);

    /**
     * 移动端销售进度
     */
    public List<SalSalesOrderItem> selectMobProcessList(SalSalesOrderItem order);

    /**
     * 查找对应处理状态订单主表的明细
     */
    @Select({
            "<script>",
            "select t.*",
            "from s_sal_sales_order_item t",
            "left join s_sal_sales_order t1 on t.sales_order_sid = t1.sales_order_sid",
            "where t.sales_order_item_sid in",
            "<foreach collection='salesOrderItemSidList' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "and t1.handle_status = #{handleStatus} ",
            ";",
            "</script>"
    })
    public List<SalSalesOrderItem> selectOrderItemListBy(SalSalesOrderItem order);
}
