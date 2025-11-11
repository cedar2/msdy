package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseInvoiceItem;

/**
 * 采购发票-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinPurchaseInvoiceItemService extends IService<FinPurchaseInvoiceItem>{
    /**
     * 查询采购发票-明细
     * 
     * @param purchaseInvoiceItemSid 采购发票-明细ID
     * @return 采购发票-明细
     */
    public FinPurchaseInvoiceItem selectFinPurchaseInvoiceItemById(Long purchaseInvoiceItemSid);

    /**
     * 查询采购发票-明细列表
     * 
     * @param finPurchaseInvoiceItem 采购发票-明细
     * @return 采购发票-明细集合
     */
    public List<FinPurchaseInvoiceItem> selectFinPurchaseInvoiceItemList(FinPurchaseInvoiceItem finPurchaseInvoiceItem);


    /**
     * 查询采购发票-明细报表
     *
     * @param finPurchaseInvoiceItem 采购发票-明细报表
     * @return 采购发票-明细集合
     */
    public List<FinPurchaseInvoiceItem> getReportForm(FinPurchaseInvoiceItem finPurchaseInvoiceItem);
}
