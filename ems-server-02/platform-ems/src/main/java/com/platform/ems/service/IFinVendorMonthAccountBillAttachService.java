package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinVendorMonthAccountBillAttach;

/**
 * 供应商月对账单-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface IFinVendorMonthAccountBillAttachService extends IService<FinVendorMonthAccountBillAttach> {
    /**
     * 查询供应商月对账单-附件
     *
     * @param monthAccountBillAttachmentSid 供应商月对账单-附件ID
     * @return 供应商月对账单-附件
     */
    public FinVendorMonthAccountBillAttach selectFinVendorMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    /**
     * 查询供应商月对账单-附件列表
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 供应商月对账单-附件集合
     */
    public List<FinVendorMonthAccountBillAttach> selectFinVendorMonthAccountBillAttachList(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach);

    /**
     * 新增供应商月对账单-附件
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    public int insertFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach);

    /**
     * 修改供应商月对账单-附件
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    public int updateFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach);

    /**
     * 变更供应商月对账单-附件
     *
     * @param finVendorMonthAccountBillAttach 供应商月对账单-附件
     * @return 结果
     */
    public int changeFinVendorMonthAccountBillAttach(FinVendorMonthAccountBillAttach finVendorMonthAccountBillAttach);

    /**
     * 批量删除供应商月对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的供应商月对账单-附件ID
     * @return 结果
     */
    public int deleteFinVendorMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids);

}
