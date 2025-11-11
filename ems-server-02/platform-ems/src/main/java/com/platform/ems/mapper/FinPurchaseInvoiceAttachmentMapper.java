package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinPurchaseInvoiceAttachment;

/**
 * 采购发票-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinPurchaseInvoiceAttachmentMapper  extends BaseMapper<FinPurchaseInvoiceAttachment> {


    FinPurchaseInvoiceAttachment selectFinPurchaseInvoiceAttachmentById(Long purchaseInvoiceAttachmentSid);

    List<FinPurchaseInvoiceAttachment> selectFinPurchaseInvoiceAttachmentList(FinPurchaseInvoiceAttachment finPurchaseInvoiceAttachment);

    /**
     * 添加多个
     * @param list List FinPurchaseInvoiceAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinPurchaseInvoiceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinPurchaseInvoiceAttachment
    * @return int
    */
    int updateAllById(FinPurchaseInvoiceAttachment entity);

    /**
     * 更新多个
     * @param list List FinPurchaseInvoiceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinPurchaseInvoiceAttachment> list);


    void deleteFinPurchaseInvoiceAttachmentByIds(@Param("array") Long[] purchaseInvoiceSids);
}
