package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorAccountBalanceBillAttachment;

/**
 * 供应商账互抵单-附件Service接口
 * 
 * @author linhongwei
 * @date 2021-05-27
 */
public interface IFinVendorAccountBalanceBillAttachmentService extends IService<FinVendorAccountBalanceBillAttachment>{
    /**
     * 查询供应商账互抵单-附件
     * 
     * @param vendorAccountBalanceBillAttachmentSid 供应商账互抵单-附件ID
     * @return 供应商账互抵单-附件
     */
    public FinVendorAccountBalanceBillAttachment selectFinVendorAccountBalanceBillAttachmentById(Long vendorAccountBalanceBillAttachmentSid);

    /**
     * 查询供应商账互抵单-附件列表
     * 
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 供应商账互抵单-附件集合
     */
    public List<FinVendorAccountBalanceBillAttachment> selectFinVendorAccountBalanceBillAttachmentList(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 新增供应商账互抵单-附件
     * 
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    public int insertFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 修改供应商账互抵单-附件
     * 
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    public int updateFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 变更供应商账互抵单-附件
     *
     * @param finVendorAccountBalanceBillAttachment 供应商账互抵单-附件
     * @return 结果
     */
    public int changeFinVendorAccountBalanceBillAttachment(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 批量删除供应商账互抵单-附件
     * 
     * @param vendorAccountBalanceBillAttachmentSids 需要删除的供应商账互抵单-附件ID
     * @return 结果
     */
    public int deleteFinVendorAccountBalanceBillAttachmentByIds(List<Long>  vendorAccountBalanceBillAttachmentSids);

    /**
    * 启用/停用
    * @param finVendorAccountBalanceBillAttachment
    * @return
    */
    int changeStatus(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

    /**
     * 更改确认状态
     * @param finVendorAccountBalanceBillAttachment
     * @return
     */
    int check(FinVendorAccountBalanceBillAttachment finVendorAccountBalanceBillAttachment);

}
