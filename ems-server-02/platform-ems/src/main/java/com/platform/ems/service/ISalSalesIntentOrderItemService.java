package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalSalesIntentOrder;
import com.platform.ems.domain.SalSalesIntentOrderItem;

/**
 * 销售意向单-明细Service接口
 *
 * @author chenkw
 * @date 2022-10-17
 */
public interface ISalSalesIntentOrderItemService extends IService<SalSalesIntentOrderItem> {
    /**
     * 查询销售意向单-明细
     *
     * @param salesIntentOrderItemSid 销售意向单-明细ID
     * @return 销售意向单-明细
     */
    public SalSalesIntentOrderItem selectSalSalesIntentOrderItemById(Long salesIntentOrderItemSid);

    /**
     * 查询销售意向单-明细  根据主表sid
     *
     * @param salesIntentOrderSid 销售意向单-明细ID
     * @return 销售意向单-明细
     */
    public List<SalSalesIntentOrderItem> selectSalSalesIntentOrderItemByOrderId(Long salesIntentOrderSid);

    /**
     * 查询销售意向单-明细列表
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 销售意向单-明细集合
     */
    public List<SalSalesIntentOrderItem> selectSalSalesIntentOrderItemList(SalSalesIntentOrderItem salSalesIntentOrderItem);

    /**
     * 新增销售意向单-明细
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    public int insertSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem);

    /**
     * 批量新增销售意向单-明细
     *
     * @param order 销售意向单
     * @return 结果
     */
    public int insertSalSalesIntentOrderItemList(SalSalesIntentOrder order);

    /**
     * 修改销售意向单-明细
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    public int updateSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem);

    /**
     * 批量修改销售意向单-明细
     *
     * @param order 销售意向单
     * @return 结果
     */
    public int updateSalSalesIntentOrderItemList(SalSalesIntentOrder order);

    /**
     * 变更销售意向单-明细
     *
     * @param salSalesIntentOrderItem 销售意向单-明细
     * @return 结果
     */
    public int changeSalSalesIntentOrderItem(SalSalesIntentOrderItem salSalesIntentOrderItem);

    /**
     * 批量删除销售意向单-明细
     *
     * @param salesIntentOrderItemSids 需要删除的销售意向单-明细ID
     * @return 结果
     */
    public int deleteSalSalesIntentOrderItemByIds(List<Long> salesIntentOrderItemSids);

    /**
     * 批量删除销售意向单-明细
     *
     * @param itemList 需要删除的销售意向单-明细列表
     * @return 结果
     */
    public int deleteSalSalesIntentOrderItemList(List<SalSalesIntentOrderItem> itemList);

    /**
     * 批量删除销售意向单-明细 根据主表sids
     *
     * @param orderSids 需要删除的销售意向单sids
     * @return 结果
     */
    public int deleteSalSalesIntentOrderItemListByOrder(List<Long> orderSids);

    /**
     *  按照“商品/物料编码+SKU1序号+SKU1名称+SKU2序号+SKU2名称”升序排列
     * （SKU1序号、SKU2序号，取对应商品/物料档案的“SKU1”、“SKU2”页签中的“序号”清单列的值）
     */
    public List<SalSalesIntentOrderItem> newSort(List<SalSalesIntentOrderItem> itemList);

}
