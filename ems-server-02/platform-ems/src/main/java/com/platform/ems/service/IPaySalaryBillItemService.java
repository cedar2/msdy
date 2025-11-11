package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.PaySalaryBillItem;
import com.platform.ems.domain.dto.request.PaySalaryBillItemRequest;
import com.platform.ems.domain.dto.response.PaySalaryBillItemExResponse;

import java.util.List;

/**
 * 工资单-明细Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPaySalaryBillItemService extends IService<PaySalaryBillItem> {
    /**
     * 查询工资单-明细
     *
     * @param billItemSid 工资单-明细ID
     * @return 工资单-明细
     */
    PaySalaryBillItem selectPaySalaryBillItemById(Long billItemSid);

    /**
     * 查询工资单-明细列表
     *
     * @param paySalaryBillItemRequest 工资单-明细
     * @return 工资单-明细集合
     */
    public List<PaySalaryBillItemExResponse> getReport(PaySalaryBillItemRequest paySalaryBillItemRequest);

    /**
     * 新增工资单-明细
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    int insertPaySalaryBillItem(PaySalaryBillItem paySalaryBillItem);

    /**
     * 修改工资单-明细
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    int updatePaySalaryBillItem(PaySalaryBillItem paySalaryBillItem);

    /**
     * 变更工资单-明细
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    int changePaySalaryBillItem(PaySalaryBillItem paySalaryBillItem);

    /**
     * 批量删除工资单-明细
     *
     * @param billItemSids 需要删除的工资单-明细ID
     * @return 结果
     */
    int deletePaySalaryBillItemByIds(List<Long> billItemSids);

    /**
     * 查询工资单-明细列表
     * @param staffList 员工列表
     * @param paySalaryBillItem 工资单-明细
     * @return 工资单-明细集合
     */
    List<PaySalaryBillItem> getProcessStepCompleteWage(List<BasStaff> staffList, PaySalaryBillItem paySalaryBillItem);

    /**
     * 工资单明细报表设置工资成本分摊
     * @param paySalaryBillItem 工资单-明细
     * @return 工资单-明细集合
     */
    int setSalaryCostAllocateType(PaySalaryBillItem paySalaryBillItem);

}
