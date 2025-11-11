package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogVendorDeduction;

/**
 * 核销供应商扣款日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogVendorDeductionService extends IService<FinClearLogVendorDeduction> {

    /**
     * 查询核销供应商扣款日志列表
     *
     * @param finClearLogVendorDeduction 核销供应商扣款日志
     * @return 核销供应商扣款日志集合
     */
    public List<FinClearLogVendorDeduction> selectFinClearLogVendorDeductionList(FinClearLogVendorDeduction finClearLogVendorDeduction);


}
