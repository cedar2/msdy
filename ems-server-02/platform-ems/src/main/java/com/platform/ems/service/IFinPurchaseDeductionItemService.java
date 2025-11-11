package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseDeductionBillItem;

/**
 * 采购扣款单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface IFinPurchaseDeductionItemService extends IService<FinPurchaseDeductionBillItem>{
    /**
     * 查询采购扣款单-明细
     * 
     * @param purchaseDeductionItemSid 采购扣款单-明细ID
     * @return 采购扣款单-明细
     */
    public FinPurchaseDeductionBillItem selectFinPurchaseDeductionItemById(Long purchaseDeductionItemSid);

    /**
     * 查询采购扣款单-明细列表
     * 
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 采购扣款单-明细集合
     */
    public List<FinPurchaseDeductionBillItem> selectFinPurchaseDeductionItemList(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem);

    /**
     * 新增采购扣款单-明细
     * 
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 结果
     */
    public int insertFinPurchaseDeductionItem(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem);

    /**
     * 修改采购扣款单-明细
     * 
     * @param FinPurchaseDeductionBillItem 采购扣款单-明细
     * @return 结果
     */
    public int updateFinPurchaseDeductionItem(FinPurchaseDeductionBillItem FinPurchaseDeductionBillItem);

    /**
     * 批量删除采购扣款单-明细
     * 
     * @param purchaseDeductionItemSids 需要删除的采购扣款单-明细ID
     * @return 结果
     */
    public int deleteFinPurchaseDeductionItemByIds(List<Long> purchaseDeductionItemSids);

}
