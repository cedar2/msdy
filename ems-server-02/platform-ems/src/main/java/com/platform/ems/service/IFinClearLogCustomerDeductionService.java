package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogCustomerDeduction;

/**
 * 核销客户扣款日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogCustomerDeductionService extends IService<FinClearLogCustomerDeduction>{

    /**
     * 查询核销客户扣款日志列表
     *
     * @param finClearLogCustomerDeduction 核销客户扣款日志
     * @return 核销客户扣款日志集合
     */
    public List<FinClearLogCustomerDeduction> selectFinClearLogCustomerDeductionList(FinClearLogCustomerDeduction finClearLogCustomerDeduction);

}
