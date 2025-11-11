package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseInvoiceAttachment;

/**
 * 采购发票-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinPurchaseInvoiceAttachmentService extends IService<FinPurchaseInvoiceAttachment>{
    /**
     * 查询采购发票-附件
     * 
     * @param purchaseInvoiceAttachmentSid 采购发票-附件ID
     * @return 采购发票-附件
     */
    public FinPurchaseInvoiceAttachment selectFinPurchaseInvoiceAttachmentById(Long purchaseInvoiceAttachmentSid);

    /**
     * 查询采购发票-附件列表
     * 
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 采购发票-附件集合
     */
    public List<FinPurchaseInvoiceAttachment> selectFinPurchaseInvoiceAttachmentList(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment);

    /**
     * 新增采购发票-附件
     * 
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 结果
     */
    public int insertFinPurchaseInvoiceAttachment(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment);

    /**
     * 修改采购发票-附件
     * 
     * @param finPurchaseInvoiceAttachment 采购发票-附件
     * @return 结果
     */
    public int updateFinPurchaseInvoiceAttachment(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment);

    /**
     * 批量删除采购发票-附件
     * 
     * @param purchaseInvoiceAttachmentSids 需要删除的采购发票-附件ID
     * @return 结果
     */
    public int deleteFinPurchaseInvoiceAttachmentByIds(List<Long> purchaseInvoiceAttachmentSids);

}
