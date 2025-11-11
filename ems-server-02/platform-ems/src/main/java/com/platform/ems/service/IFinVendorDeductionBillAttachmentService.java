package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorDeductionBillAttachment;

/**
 * 供应商扣款单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-05-31
 */
public interface IFinVendorDeductionBillAttachmentService extends IService<FinVendorDeductionBillAttachment>{
    /**
     * 查询供应商扣款单-附件
     * 
     * @param deductionBillAttachmentSid 供应商扣款单-附件ID
     * @return 供应商扣款单-附件
     */
    public FinVendorDeductionBillAttachment selectFinVendorDeductionBillAttachmentById(Long deductionBillAttachmentSid);

    /**
     * 查询供应商扣款单-附件列表
     * 
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 供应商扣款单-附件集合
     */
    public List<FinVendorDeductionBillAttachment> selectFinVendorDeductionBillAttachmentList(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 新增供应商扣款单-附件
     * 
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    public int insertFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 修改供应商扣款单-附件
     * 
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    public int updateFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 变更供应商扣款单-附件
     *
     * @param finVendorDeductionBillAttachment 供应商扣款单-附件
     * @return 结果
     */
    public int changeFinVendorDeductionBillAttachment(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 批量删除供应商扣款单-附件
     * 
     * @param deductionBillAttachmentSids 需要删除的供应商扣款单-附件ID
     * @return 结果
     */
    public int deleteFinVendorDeductionBillAttachmentByIds(List<Long>  deductionBillAttachmentSids);

    /**
    * 启用/停用
    * @param finVendorDeductionBillAttachment
    * @return
    */
    int changeStatus(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

    /**
     * 更改确认状态
     * @param finVendorDeductionBillAttachment
     * @return
     */
    int check(FinVendorDeductionBillAttachment finVendorDeductionBillAttachment);

}
