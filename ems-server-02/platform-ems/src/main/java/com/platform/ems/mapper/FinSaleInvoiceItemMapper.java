package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.FinPurchaseInvoiceItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinSaleInvoiceItem;

/**
 * 销售发票-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinSaleInvoiceItemMapper  extends BaseMapper<FinSaleInvoiceItem> {



    FinSaleInvoiceItem selectFinSaleInvoiceItemById(Long saleInvoiceItemSid);

    List<FinSaleInvoiceItem> selectFinSaleInvoiceItemList(FinSaleInvoiceItem finSaleInvoiceItem);

    List<FinSaleInvoiceItem> getReportForm(FinSaleInvoiceItem finSaleInvoiceItem);

    /**
     * 添加多个
     * @param list List FinSaleInvoiceItem
     * @return int
     */
    int inserts(@Param("list") List<FinSaleInvoiceItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity FinSaleInvoiceItem
     * @return int
     */
    int updateAllById(FinSaleInvoiceItem entity);

    /**
     * 更新多个
     * @param list List FinSaleInvoiceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleInvoiceItem> list);

}
