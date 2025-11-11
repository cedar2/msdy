package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourcePurchaseOrderItem;

/**
 * 外发加工单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IPurOutsourcePurchaseOrderItemService extends IService<PurOutsourcePurchaseOrderItem>{
    /**
     * 查询外发加工单-明细
     * 
     * @param outsourcePurchaseOrderItemSid 外发加工单-明细ID
     * @return 外发加工单-明细
     */
    public PurOutsourcePurchaseOrderItem selectPurOutsourcePurchaseOrderItemById(Long outsourcePurchaseOrderItemSid);

    /**
     * 查询外发加工单-明细列表
     * 
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 外发加工单-明细集合
     */
    public List<PurOutsourcePurchaseOrderItem> selectPurOutsourcePurchaseOrderItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 新增外发加工单-明细
     * 
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    public int insertPurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 修改外发加工单-明细
     * 
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    public int updatePurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 变更外发加工单-明细
     *
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    public int changePurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 批量删除外发加工单-明细
     * 
     * @param outsourcePurchaseOrderItemSids 需要删除的外发加工单-明细ID
     * @return 结果
     */
    public int deletePurOutsourcePurchaseOrderItemByIds(List<Long> outsourcePurchaseOrderItemSids);

    /**
     * 更改确认状态
     * @param purOutsourcePurchaseOrderItem
     * @return
     */
    int check(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);

    /**
     * 外发加工单明细报表
     */
    List<PurOutsourcePurchaseOrderItem> getItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem);
}
