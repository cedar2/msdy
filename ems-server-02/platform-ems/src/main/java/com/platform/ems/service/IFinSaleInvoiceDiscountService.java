package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleInvoiceDiscount;

/**
 * 销售发票-折扣Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinSaleInvoiceDiscountService extends IService<FinSaleInvoiceDiscount>{
    /**
     * 查询销售发票-折扣
     * 
     * @param saleInvoiceDiscountSid 销售发票-折扣ID
     * @return 销售发票-折扣
     */
    public FinSaleInvoiceDiscount selectFinSaleInvoiceDiscountById(Long saleInvoiceDiscountSid);

    /**
     * 查询销售发票-折扣列表
     * 
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 销售发票-折扣集合
     */
    public List<FinSaleInvoiceDiscount> selectFinSaleInvoiceDiscountList(FinSaleInvoiceDiscount finSaleInvoiceDiscount);

    /**
     * 新增销售发票-折扣
     * 
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 结果
     */
    public int insertFinSaleInvoiceDiscount(FinSaleInvoiceDiscount finSaleInvoiceDiscount);

    /**
     * 修改销售发票-折扣
     * 
     * @param finSaleInvoiceDiscount 销售发票-折扣
     * @return 结果
     */
    public int updateFinSaleInvoiceDiscount(FinSaleInvoiceDiscount finSaleInvoiceDiscount);

    /**
     * 批量删除销售发票-折扣
     * 
     * @param saleInvoiceDiscountSids 需要删除的销售发票-折扣ID
     * @return 结果
     */
    public int deleteFinSaleInvoiceDiscountByIds(List<Long> saleInvoiceDiscountSids);

}
