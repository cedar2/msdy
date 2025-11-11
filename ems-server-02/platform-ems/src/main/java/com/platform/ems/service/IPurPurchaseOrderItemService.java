package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurPurchaseOrder;
import com.platform.ems.domain.PurPurchaseOrderDataSource;
import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.response.OrderProgressItemResponse;
import com.platform.ems.domain.dto.response.OrderProgressResponse;
import com.platform.ems.domain.dto.response.OrderTotalResponse;

/**
 * 采购订单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IPurPurchaseOrderItemService extends IService<PurPurchaseOrderItem>{
    /**
     * 查询采购订单-明细
     *
     * @param clientId 采购订单-明细ID
     * @return 采购订单-明细
     */
    public PurPurchaseOrderItem selectPurPurchaseOrderItemById(String clientId);

    /**
     * 查询采购订单-明细列表
     *
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 采购订单-明细集合
     */
    public List<PurPurchaseOrderItem> selectPurPurchaseOrderItemList(PurPurchaseOrderItem purPurchaseOrderItem);

    /**
     * 新增采购订单-明细
     *
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 结果
     */
    public int insertPurPurchaseOrderItem(PurPurchaseOrderItem purPurchaseOrderItem);

    /**
     * 修改采购订单-明细
     *
     * @param purPurchaseOrderItem 采购订单-明细
     * @return 结果
     */
    public int updatePurPurchaseOrderItem(PurPurchaseOrderItem purPurchaseOrderItem);

    /**
     * 批量删除采购订单-明细
     *
     * @param clientIds 需要删除的采购订单-明细ID
     * @return 结果
     */
    public int deletePurPurchaseOrderItemByIds(List<String> clientIds);

    /**
     * 采购订单明细报表
     */
    List<PurPurchaseOrderItem> getItemList(PurPurchaseOrderItem purPurchaseOrderItem);

    public List<PurPurchaseOrderItem> handleIndex(List<PurPurchaseOrderItem> itemList);
    public PurPurchaseOrder handleIndexDelievery(PurPurchaseOrder purPurchaseOrder);
    /**
     *采购状况交期报表
     */
    public List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request);
    /**
     *采购统计报表 主
     */
    public List<OrderTotalResponse> getTotal(OrderTotalRequest request);
    /**
     *采购统计报表 明细
     */
    public List<OrderTotalResponse> getTotalItem(OrderTotalRequest request);
    /**
     *采购状况交期报表 明细
     */
    public List<OrderProgressItemResponse>  getDeliveryProcessItem(OrderProgressRequest request);

    public List<OrderProgressItemResponse> sortProgressItem(List<OrderProgressItemResponse> salSalesOrderItemList);

    /**
     * 数据来源列表
     * @param dataSource
     * @return
     */
    public List<PurPurchaseOrderDataSource> selectPurPurchaseOrderDataSourceList(PurPurchaseOrderDataSource dataSource);

    /**
     * 数据来源列表修改
     * @param dataSourceList
     * @return
     */
    public EmsResultEntity updatePurPurchaseOrderDataSourceList(List<PurPurchaseOrderDataSource> dataSourceList, String keep);

    /**
     * 订单明细更新来源数量时得到新的订单量返回前端
     * @param dataSourceList
     * @return
     */
    public EmsResultEntity getPurPurchaseOrderItemQuantityByDataSource(List<PurPurchaseOrderDataSource> dataSourceList, String keep);

    /**
     * 移动端采购进度
     */
    public List<PurPurchaseOrderItem> selectMobProcessList(PurPurchaseOrderItem order);
}
