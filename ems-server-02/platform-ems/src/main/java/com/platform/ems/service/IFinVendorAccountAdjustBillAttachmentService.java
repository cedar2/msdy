package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountAdjustBillAttachment;

/**
 * 供应商调账单-附件Service接口
 * 
 * @author qhq
 * @date 2021-05-26
 */
public interface IFinVendorAccountAdjustBillAttachmentService extends IService<FinVendorAccountAdjustBillAttachment>{
    /**
     * 查询供应商调账单-附件
     * 
     * @param adjustBillAttachmentSid 供应商调账单-附件ID
     * @return 供应商调账单-附件
     */
    public FinVendorAccountAdjustBillAttachment selectFinVendorAccountAdjustBillAttachmentById(Long adjustBillAttachmentSid);

    /**
     * 查询供应商调账单-附件列表
     * 
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 供应商调账单-附件集合
     */
    public List<FinVendorAccountAdjustBillAttachment> selectFinVendorAccountAdjustBillAttachmentList(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 新增供应商调账单-附件
     * 
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    public int insertFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 修改供应商调账单-附件
     * 
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    public int updateFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 变更供应商调账单-附件
     *
     * @param finVendorAccountAdjustBillAttachment 供应商调账单-附件
     * @return 结果
     */
    public int changeFinVendorAccountAdjustBillAttachment(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 批量删除供应商调账单-附件
     * 
     * @param adjustBillAttachmentSids 需要删除的供应商调账单-附件ID
     * @return 结果
     */
    public int deleteFinVendorAccountAdjustBillAttachmentByIds(List<Long>  adjustBillAttachmentSids);

    /**
    * 启用/停用
    * @param finVendorAccountAdjustBillAttachment
    * @return
    */
    int changeStatus(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

    /**
     * 更改确认状态
     * @param finVendorAccountAdjustBillAttachment
     * @return
     */
    int check(FinVendorAccountAdjustBillAttachment finVendorAccountAdjustBillAttachment);

}
