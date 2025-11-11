package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinPurchaseInvoiceItem;

/**
 * 采购发票-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinPurchaseInvoiceItemMapper  extends BaseMapper<FinPurchaseInvoiceItem> {


    FinPurchaseInvoiceItem selectFinPurchaseInvoiceItemById(Long purchaseInvoiceItemSid);

    List<FinPurchaseInvoiceItem> selectFinPurchaseInvoiceItemList(FinPurchaseInvoiceItem finPurchaseInvoiceItem);

    List<FinPurchaseInvoiceItem> getReportForm(FinPurchaseInvoiceItem finPurchaseInvoiceItem);

    /**
     * 添加多个
     * @param list List FinPurchaseInvoiceItem
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseInvoiceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseInvoiceItem
    * @return int
    */
    int updateAllById(FinPurchaseInvoiceItem entity);

    /**
     * 更新多个
     * @param list List FinPurchaseInvoiceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseInvoiceItem> list);

}
