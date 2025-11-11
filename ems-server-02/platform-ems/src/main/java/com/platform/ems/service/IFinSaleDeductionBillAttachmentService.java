package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinSaleDeductionBillAttachment;

/**
 * 销售扣款单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-04-09
 */
public interface IFinSaleDeductionBillAttachmentService extends IService<FinSaleDeductionBillAttachment>{
    /**
     * 查询销售扣款单-附件
     * 
     * @param saleDeductionAttachmentSid 销售扣款单-附件ID
     * @return 销售扣款单-附件
     */
    public FinSaleDeductionBillAttachment selectFinSaleDeductionAttachmentById(Long saleDeductionAttachmentSid);

    /**
     * 查询销售扣款单-附件列表
     * 
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 销售扣款单-附件集合
     */
    public List<FinSaleDeductionBillAttachment> selectFinSaleDeductionAttachmentList(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment);

    /**
     * 新增销售扣款单-附件
     * 
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 结果
     */
    public int insertFinSaleDeductionAttachment(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment);

    /**
     * 修改销售扣款单-附件
     * 
     * @param FinSaleDeductionBillAttachment 销售扣款单-附件
     * @return 结果
     */
    public int updateFinSaleDeductionAttachment(FinSaleDeductionBillAttachment FinSaleDeductionBillAttachment);

    /**
     * 批量删除销售扣款单-附件
     * 
     * @param saleDeductionAttachmentSids 需要删除的销售扣款单-附件ID
     * @return 结果
     */
    public int deleteFinSaleDeductionAttachmentByIds(List<Long> saleDeductionAttachmentSids);

}
