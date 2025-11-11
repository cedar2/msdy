package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleInvoiceItem;

/**
 * 销售发票-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinSaleInvoiceItemService extends IService<FinSaleInvoiceItem>{
    /**
     * 查询销售发票-明细
     * 
     * @param saleInvoiceItemSid 销售发票-明细ID
     * @return 销售发票-明细
     */
    public FinSaleInvoiceItem selectFinSaleInvoiceItemById(Long saleInvoiceItemSid);

    /**
     * 查询销售发票-明细列表
     * 
     * @param finSaleInvoiceItem 销售发票-明细
     * @return 销售发票-明细集合
     */
    public List<FinSaleInvoiceItem> selectFinSaleInvoiceItemList(FinSaleInvoiceItem finSaleInvoiceItem);


    /**
     * 查询销售发票-明细报表
     *
     * @param finSaleInvoiceItem 销售发票-明细报表
     * @return 采购发票-明细集合
     */
    public List<FinSaleInvoiceItem> getReportForm(FinSaleInvoiceItem finSaleInvoiceItem);
}
