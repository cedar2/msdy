package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.SalSalesOrderItem;
import com.platform.ems.domain.dto.request.OrderBestSellingRequest;
import com.platform.ems.domain.dto.request.OrderProgressRequest;
import com.platform.ems.domain.dto.request.OrderTotalRequest;
import com.platform.ems.domain.dto.request.SaleOrderProgressRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.SalSaleProductCostForm;

/**
 * 销售订单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface ISalSalesOrderItemService extends IService<SalSalesOrderItem>{

    /**
     * 查询销售订单-明细
     *
     * @param
     * @return 销售订单-明细
     */
    public SalSalesOrderItem selectSalSalesOrderItemById(Long salesOrderItemSid);

    /**
     * 查询销售订单-明细列表
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 销售订单-明细集合
     */
    public List<SalSalesOrderItem> selectSalSalesOrderItemList(SalSalesOrderItem salSalesOrderItem);

    /**
     *销售状况交期报表 主
     */
    public List<OrderProgressResponse>  getDeliveryProcess(OrderProgressRequest request);

    /**
     *销售状况交期报表 明细
     */
    public List<OrderProgressItemResponse>  getDeliveryProcessItem(OrderProgressRequest request);

    public List<OrderProgressItemResponse> sortProgressItem(List<OrderProgressItemResponse> salSalesOrderItemList);

    /**
     *销售统计报表 主
     */
    public List<OrderTotalResponse> getTotal(OrderTotalRequest request);

    /**
     *销售畅销报表 主
     */
    List<OrderBestSellingResponse> getBestSell(OrderBestSellingRequest request);

    /**
     *销售畅销报表 明细
     */
    public List<OrderBestSellingResponse> getBestSellItem(OrderBestSellingRequest request);

    /**
     *销售统计报表 明细
     */
    public List<OrderTotalResponse> getTotalItem(OrderTotalRequest request);

    /**
     * 新增销售订单-明细
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 结果
     */
    public int insertSalSalesOrderItem(SalSalesOrderItem salSalesOrderItem);

    /**
     * 修改销售订单-明细
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 结果
     */
    public int updateSalSalesOrderItem(SalSalesOrderItem salSalesOrderItem);

    /**
     * 销售订单进度报表 主
     */
    List<SaleOrderProgressResponse> getProcessHead(SaleOrderProgressRequest request);

    /**
     * 销售订单进度报表 明细
     */
    List<SaleOrderProgressItemResponse> getProcessItem(SaleOrderProgressRequest request);

    /**
     * 批量删除销售订单-明细
     *
     * @param clientIds 需要删除的销售订单-明细ID
     * @return 结果
     */
    public int deleteSalSalesOrderItemByIds(List<String> clientIds);

    /**
     * 销售订单排产明细报表
     */
    public List<SalSalesOrderItem> getItemListProduct(SalSalesOrderItem salSalesOrderItem);

    /**
     * 销售订单排产明细报表
     */
    public List<SalSalesOrderItem> mobPaichan(SalSalesOrderItem salSalesOrderItem);

    /**
     * 销售订单排采明细报表
     */
    public List<SalSalesOrderItem> getItemListPC(SalSalesOrderItem salSalesOrderItem);

    /**
     * 销售订单明细报表
     */
    List<SalSalesOrderItem> getItemList(SalSalesOrderItem salSalesOrderItem);
    public List<SalSalesOrderItem> handleIndex(List<SalSalesOrderItem> itemList);
    public List<SalSalesOrderItem> handleIndexPro(List<SalSalesOrderItem> itemList);
    public SalSalesOrder handleIndexDelievery(SalSalesOrder salSalesOrder);

    /**
     * 查询商品销售成本报表
     *
     * @param salSalesOrderItem 销售订单-明细
     * @return 销售订单-明细集合
     */
    List<SalSaleProductCostForm> selectSalSalesProductCostList(SalSaleProductCostForm salSalesOrderItem);

    /**
     * 移动端销售进度
     */
    public List<SalSalesOrderItem> selectMobProcessList(SalSalesOrderItem order);

}
