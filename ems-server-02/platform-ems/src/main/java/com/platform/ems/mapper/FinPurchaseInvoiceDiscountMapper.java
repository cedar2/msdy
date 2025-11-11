package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;

/**
 * 采购发票-折扣Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinPurchaseInvoiceDiscountMapper  extends BaseMapper<FinPurchaseInvoiceDiscount> {


    FinPurchaseInvoiceDiscount selectFinPurchaseInvoiceDiscountById(Long purchaseInvoiceDiscountSid);

    List<FinPurchaseInvoiceDiscount> selectFinPurchaseInvoiceDiscountList(FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount);

    /**
     * 添加多个
     * @param list List FinPurchaseInvoiceDiscount
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseInvoiceDiscount> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseInvoiceDiscount
    * @return int
    */
    int updateAllById(FinPurchaseInvoiceDiscount entity);

    /**
     * 更新多个
     * @param list List FinPurchaseInvoiceDiscount
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseInvoiceDiscount> list);


    void deleteFinPurchaseInvoiceDiscountByIds(@Param("array") Long[] purchaseInvoiceSids);
}
