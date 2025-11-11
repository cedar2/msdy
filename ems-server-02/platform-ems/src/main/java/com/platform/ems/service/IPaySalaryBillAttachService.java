package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PaySalaryBillAttach;

import java.util.List;

/**
 * 工资单-附件Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPaySalaryBillAttachService extends IService<PaySalaryBillAttach> {
    /**
     * 查询工资单-附件
     *
     * @param attachmentSid 工资单-附件ID
     * @return 工资单-附件
     */
    public PaySalaryBillAttach selectPaySalaryBillAttachById(Long attachmentSid);

    /**
     * 查询工资单-附件列表
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 工资单-附件集合
     */
    public List<PaySalaryBillAttach> selectPaySalaryBillAttachList(PaySalaryBillAttach paySalaryBillAttach);

    /**
     * 新增工资单-附件
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    public int insertPaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach);

    /**
     * 修改工资单-附件
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    public int updatePaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach);

    /**
     * 变更工资单-附件
     *
     * @param paySalaryBillAttach 工资单-附件
     * @return 结果
     */
    public int changePaySalaryBillAttach(PaySalaryBillAttach paySalaryBillAttach);

    /**
     * 批量删除工资单-附件
     *
     * @param attachmentSids 需要删除的工资单-附件ID
     * @return 结果
     */
    public int deletePaySalaryBillAttachByIds(List<Long> attachmentSids);

}
