package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseDeductionBillAttachment;

/**
 * 采购扣款单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface IFinPurchaseDeductionAttachmentService extends IService<FinPurchaseDeductionBillAttachment>{
    /**
     * 查询采购扣款单-附件
     * 
     * @param purchaseDeductionAttachmentSid 采购扣款单-附件ID
     * @return 采购扣款单-附件
     */
    public FinPurchaseDeductionBillAttachment selectFinPurchaseDeductionAttachmentById(Long purchaseDeductionAttachmentSid);

    /**
     * 查询采购扣款单-附件列表
     * 
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 采购扣款单-附件集合
     */
    public List<FinPurchaseDeductionBillAttachment> selectFinPurchaseDeductionAttachmentList(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment);

    /**
     * 新增采购扣款单-附件
     * 
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 结果
     */
    public int insertFinPurchaseDeductionAttachment(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment);

    /**
     * 修改采购扣款单-附件
     * 
     * @param FinPurchaseDeductionBillAttachment 采购扣款单-附件
     * @return 结果
     */
    public int updateFinPurchaseDeductionAttachment(FinPurchaseDeductionBillAttachment FinPurchaseDeductionBillAttachment);

    /**
     * 批量删除采购扣款单-附件
     * 
     * @param purchaseDeductionAttachmentSids 需要删除的采购扣款单-附件ID
     * @return 结果
     */
    public int deleteFinPurchaseDeductionAttachmentByIds(List<Long> purchaseDeductionAttachmentSids);

}
