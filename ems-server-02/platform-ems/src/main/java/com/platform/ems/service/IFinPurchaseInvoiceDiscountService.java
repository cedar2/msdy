package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;

/**
 * 采购发票-折扣Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinPurchaseInvoiceDiscountService extends IService<FinPurchaseInvoiceDiscount>{
    /**
     * 查询采购发票-折扣
     * 
     * @param purchaseInvoiceDiscountSid 采购发票-折扣ID
     * @return 采购发票-折扣
     */
    public FinPurchaseInvoiceDiscount selectFinPurchaseInvoiceDiscountById(Long purchaseInvoiceDiscountSid);

    /**
     * 查询采购发票-折扣列表
     * 
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 采购发票-折扣集合
     */
    public List<FinPurchaseInvoiceDiscount> selectFinPurchaseInvoiceDiscountList(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount);

    /**
     * 新增采购发票-折扣
     * 
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 结果
     */
    public int insertFinPurchaseInvoiceDiscount(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount);

    /**
     * 修改采购发票-折扣
     * 
     * @param finPurchaseInvoiceDiscount 采购发票-折扣
     * @return 结果
     */
    public int updateFinPurchaseInvoiceDiscount(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount);

    /**
     * 批量删除采购发票-折扣
     * 
     * @param purchaseInvoiceDiscountSids 需要删除的采购发票-折扣ID
     * @return 结果
     */
    public int deleteFinPurchaseInvoiceDiscountByIds(List<Long> purchaseInvoiceDiscountSids);

}
