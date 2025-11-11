package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPurchaseInvoice;

/**
 * 采购发票Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinPurchaseInvoiceMapper  extends BaseMapper<FinPurchaseInvoice> {


    FinPurchaseInvoice selectFinPurchaseInvoiceById(Long purchaseInvoiceSid);

    List<FinPurchaseInvoice> selectFinPurchaseInvoiceList(FinPurchaseInvoice finPurchaseInvoice);

    /**
     * 添加多个
     * @param list List FinPurchaseInvoice
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseInvoice> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseInvoice
    * @return int
    */
    int updateAllById(FinPurchaseInvoice entity);

    /**
     * 更新多个
     * @param list List FinPurchaseInvoice
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseInvoice> list);


    int countByDomain(FinPurchaseInvoice params);

    int deleteFinPurchaseInvoiceByIds(@Param("array") Long[] purchaseInvoiceSids);

    int confirm(FinPurchaseInvoice finPurchaseInvoice);
}
