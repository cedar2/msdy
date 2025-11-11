package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookCustomerDeduction;

/**
 * 财务流水账-客户扣款Service接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinBookCustomerDeductionService extends IService<FinBookCustomerDeduction> {

    List<FinBookCustomerDeduction> getReportForm(FinBookCustomerDeduction entity);

}
