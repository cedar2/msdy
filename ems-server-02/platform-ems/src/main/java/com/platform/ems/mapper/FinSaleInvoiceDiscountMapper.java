package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinSaleInvoiceDiscount;

/**
 * 销售发票-折扣Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinSaleInvoiceDiscountMapper  extends BaseMapper<FinSaleInvoiceDiscount> {


    FinSaleInvoiceDiscount selectFinSaleInvoiceDiscountById(Long saleInvoiceDiscountSid);

    List<FinSaleInvoiceDiscount> selectFinSaleInvoiceDiscountList(FinSaleInvoiceDiscount finSaleInvoiceDiscount);

    /**
     * 添加多个
     * @param list List FinSaleInvoiceDiscount
     * @return int
     */
    int inserts(@Param("list") List<FinSaleInvoiceDiscount> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleInvoiceDiscount
    * @return int
    */
    int updateAllById(FinSaleInvoiceDiscount entity);

    /**
     * 更新多个
     * @param list List FinSaleInvoiceDiscount
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleInvoiceDiscount> list);


    void deleteFinSaleInvoiceDiscountByIds(@Param("array") Long[] saleInvoiceSids);
}
