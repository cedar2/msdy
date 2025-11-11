package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleInvoiceAttachment;

/**
 * 销售发票-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface IFinSaleInvoiceAttachmentService extends IService<FinSaleInvoiceAttachment>{
    /**
     * 查询销售发票-附件
     * 
     * @param saleInvoiceAttachmentSid 销售发票-附件ID
     * @return 销售发票-附件
     */
    public FinSaleInvoiceAttachment selectFinSaleInvoiceAttachmentById(Long saleInvoiceAttachmentSid);

    /**
     * 查询销售发票-附件列表
     * 
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 销售发票-附件集合
     */
    public List<FinSaleInvoiceAttachment> selectFinSaleInvoiceAttachmentList(FinSaleInvoiceAttachment finSaleInvoiceAttachment);

    /**
     * 新增销售发票-附件
     * 
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 结果
     */
    public int insertFinSaleInvoiceAttachment(FinSaleInvoiceAttachment finSaleInvoiceAttachment);

    /**
     * 修改销售发票-附件
     * 
     * @param finSaleInvoiceAttachment 销售发票-附件
     * @return 结果
     */
    public int updateFinSaleInvoiceAttachment(FinSaleInvoiceAttachment finSaleInvoiceAttachment);

    /**
     * 批量删除销售发票-附件
     * 
     * @param saleInvoiceAttachmentSids 需要删除的销售发票-附件ID
     * @return 结果
     */
    public int deleteFinSaleInvoiceAttachmentByIds(List<Long> saleInvoiceAttachmentSids);

}
