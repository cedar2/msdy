package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseDeductionBill;

/**
 * 采购扣款单Service接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface IFinPurchaseDeductionBillService extends IService<FinPurchaseDeductionBill>{
    /**
     * 查询采购扣款单
     * 
     * @param purchaseDeductionSid 采购扣款单ID
     * @return 采购扣款单
     */
    public FinPurchaseDeductionBill selectFinPurchaseDeductionById(Long purchaseDeductionSid);

    /**
     * 查询采购扣款单列表
     * 
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 采购扣款单集合
     */
    public List<FinPurchaseDeductionBill> selectFinPurchaseDeductionList(FinPurchaseDeductionBill finPurchaseDeductionBill);

    /**
     * 新增采购扣款单
     * 
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 结果
     */
    public int insertFinPurchaseDeduction(FinPurchaseDeductionBill finPurchaseDeductionBill);

    /**
     * 修改采购扣款单
     * 
     * @param FinPurchaseDeductionBill 采购扣款单
     * @return 结果
     */
    public int updateFinPurchaseDeduction(FinPurchaseDeductionBill finPurchaseDeductionBill);

    /**
     * 批量删除采购扣款单
     * 
     * @param purchaseDeductionSids 需要删除的采购扣款单ID
     * @return 结果
     */
    public int deleteFinPurchaseDeductionByIds(Long[] purchaseDeductionSids);

    /**
     * 采购扣款单确认
     */
    int confirm(FinPurchaseDeductionBill finPurchaseDeductionBill);

    /**
     * 采购扣款单变更
     */
    int change(FinPurchaseDeductionBill finPurchaseDeductionBill);
}
