package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinSaleInvoiceAttachment;

/**
 * 销售发票-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface FinSaleInvoiceAttachmentMapper  extends BaseMapper<FinSaleInvoiceAttachment> {


    FinSaleInvoiceAttachment selectFinSaleInvoiceAttachmentById(Long saleInvoiceAttachmentSid);

    List<FinSaleInvoiceAttachment> selectFinSaleInvoiceAttachmentList(FinSaleInvoiceAttachment finSaleInvoiceAttachment);

    /**
     * 添加多个
     * @param list List FinSaleInvoiceAttachment
     * @return int
     */
    int inserts(@Param("list") List<FinSaleInvoiceAttachment> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinSaleInvoiceAttachment
    * @return int
    */
    int updateAllById(FinSaleInvoiceAttachment entity);

    /**
     * 更新多个
     * @param list List FinSaleInvoiceAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<FinSaleInvoiceAttachment> list);


    void deleteFinSaleInvoiceAttachmentByIds(@Param("array") Long[] saleInvoiceSids);
}
