package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalCustomerMonthAccountBillAttach;

import java.util.List;

/**
 * 客户对账单-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-22
 */
public interface ISalCustomerMonthAccountBillAttachService extends IService<SalCustomerMonthAccountBillAttach> {
    /**
     * 查询客户对账单-附件
     *
     * @param monthAccountBillAttachmentSid 客户对账单-附件ID
     * @return 客户对账单-附件
     */
    public SalCustomerMonthAccountBillAttach selectSalCustomerMonthAccountBillAttachById(Long monthAccountBillAttachmentSid);

    /**
     * 查询客户对账单-附件列表
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 客户对账单-附件集合
     */
    public List<SalCustomerMonthAccountBillAttach> selectSalCustomerMonthAccountBillAttachList(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach);

    /**
     * 新增客户对账单-附件
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    public int insertSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach);

    /**
     * 修改客户对账单-附件
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    public int updateSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach);

    /**
     * 变更客户对账单-附件
     *
     * @param salCustomerMonthAccountBillAttach 客户对账单-附件
     * @return 结果
     */
    public int changeSalCustomerMonthAccountBillAttach(SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach);

    /**
     * 批量删除客户对账单-附件
     *
     * @param monthAccountBillAttachmentSids 需要删除的客户对账单-附件ID
     * @return 结果
     */
    public int deleteSalCustomerMonthAccountBillAttachByIds(List<Long> monthAccountBillAttachmentSids);

}
