package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.FinPurchaseInvoice;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinSaleInvoice;

/**
 * 销售发票Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinSaleInvoiceMapper  extends BaseMapper<FinSaleInvoice> {


    FinSaleInvoice selectFinSaleInvoiceById(Long saleInvoiceSid);

    List<FinSaleInvoice> selectFinSaleInvoiceList(FinSaleInvoice finSaleInvoice);

    /**
     * 添加多个
     * @param list List FinSaleInvoice
     * @return int
     */
    int inserts(@Param("list") List<FinSaleInvoice> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleInvoice
    * @return int
    */
    int updateAllById(FinSaleInvoice entity);

    /**
     * 更新多个
     * @param list List FinSaleInvoice
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleInvoice> list);


    int countByDomain(FinSaleInvoice params);

    int deleteFinSaleInvoiceByIds(@Param("array") Long[] saleInvoiceSids);

    int confirm(FinSaleInvoice finSaleInvoice);
}
