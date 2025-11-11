package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.request.PurchaseOrderProgressRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.PurPurchaseOrderProcessTracking;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseOrderItem;
import org.apache.ibatis.annotations.Select;

/**
 * 采购订单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface PurPurchaseOrderItemMapper  extends BaseMapper<PurPurchaseOrderItem> {


    PurPurchaseOrderItem selectPurPurchaseOrderItemById(Long purchaseOrderItemSid);

    List<PurPurchaseOrderItemOutResponse> getOutPurPurchaseOrderItemById(Long purchaseOrderSid);

    List<Long> judgeOrderAndMaterial(PurPurchaseOrderItem purPurchaseOrderItem);

    List<PurPurchaseOrderItem> selectPurPurchaseOrderItemList(PurPurchaseOrderItem purPurchaseOrderItem);

    /**
     * 添加多个
     * @param list List PurPurchaseOrderItem
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseOrderItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseOrderItem
    * @return int
    */
    int updateAllById(PurPurchaseOrderItem entity);

    /**
     * 更新多个
     * @param list List PurPurchaseOrderItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseOrderItem> list);


    void deletePurPurchaseOrderItemByIds(@Param("array")Long[] purchaseOrderSids);

    /**
     * 采购订单明细报表
     */
    List<PurPurchaseOrderItem> getItemList(PurPurchaseOrderItem purPurchaseOrderItem);

    @InterceptorIgnore(tenantLine = "true")
    List<PurchaseOrderProgressResponse> getProcessHead(PurchaseOrderProgressRequest request);

    @InterceptorIgnore(tenantLine = "true")
    List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request);

    @InterceptorIgnore(tenantLine = "true")
    List<PurchaseOrderProgressItemResponse> getProcessItem(PurchaseOrderProgressRequest request);

    @InterceptorIgnore(tenantLine = "true")
    List<OrderProgressItemResponse> getDeliveryProcessItem(OrderProgressRequest request);


    List<OrderTotalResponse> getTotal(OrderTotalRequest request);

    List<OrderTotalResponse> getTotalItem(OrderTotalRequest request);

    /**
     * 采购入库进度跟踪报表
     *
     * @param purPurchaseOrder 采购订单
     * @return 采购订单集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseOrderProcessTracking> selectPurPurchaseProcessTrackingList(PurPurchaseOrderProcessTracking purPurchaseOrder);

    /**
     * 查询即将到期的订单明细
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseOrderItem> getToexpireBusiness();

    /**
     * 查询已逾期的订单明细
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PurPurchaseOrderItem> getOverdueBusiness();

    /**
     * 移动端采购进度
     */
    public List<PurPurchaseOrderItem> selectMobProcessList(PurPurchaseOrderItem order);

    /**
     * 查找对应处理状态订单主表的明细
     */
    @Select({
            "<script>",
            "select t.*",
            "from s_pur_purchase_order_item t",
            "left join s_pur_purchase_order t1 on t.purchase_order_sid = t1.purchase_order_sid",
            "where t.purchase_order_item_sid in",
            "<foreach collection='purchaseOrderItemSidList' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "and t1.handle_status = #{handleStatus} ",
            ";",
            "</script>"
    })
    public List<PurPurchaseOrderItem> selectOrderItemListBy(PurPurchaseOrderItem order);
}
