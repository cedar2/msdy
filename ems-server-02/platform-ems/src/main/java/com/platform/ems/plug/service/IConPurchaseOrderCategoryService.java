package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPurchaseOrderCategory;

/**
 * 采购订单类别Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPurchaseOrderCategoryService extends IService<ConPurchaseOrderCategory>{
    /**
     * 查询采购订单类别
     * 
     * @param sid 采购订单类别ID
     * @return 采购订单类别
     */
    public ConPurchaseOrderCategory selectConPurchaseOrderCategoryById(Long sid);

    /**
     * 查询采购订单类别列表
     * 
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 采购订单类别集合
     */
    public List<ConPurchaseOrderCategory> selectConPurchaseOrderCategoryList(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 新增采购订单类别
     * 
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    public int insertConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 修改采购订单类别
     * 
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    public int updateConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 变更采购订单类别
     *
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    public int changeConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 批量删除采购订单类别
     * 
     * @param sids 需要删除的采购订单类别ID
     * @return 结果
     */
    public int deleteConPurchaseOrderCategoryByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPurchaseOrderCategory
    * @return
    */
    int changeStatus(ConPurchaseOrderCategory conPurchaseOrderCategory);

    /**
     * 更改确认状态
     * @param conPurchaseOrderCategory
     * @return
     */
    int check(ConPurchaseOrderCategory conPurchaseOrderCategory);

}
