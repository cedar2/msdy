package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogVendorDeduction;
import com.platform.ems.mapper.FinClearLogVendorDeductionMapper;
import com.platform.ems.service.IFinClearLogVendorDeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销供应商扣款日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogVendorDeductionServiceImpl extends ServiceImpl<FinClearLogVendorDeductionMapper, FinClearLogVendorDeduction> implements IFinClearLogVendorDeductionService {
    @Autowired
    private FinClearLogVendorDeductionMapper finClearLogVendorDeductionMapper;

    /**
     * 查询核销供应商扣款日志列表
     *
     * @param finClearLogVendorDeduction 核销供应商扣款日志
     * @return 核销供应商扣款日志
     */
    @Override
    public List<FinClearLogVendorDeduction> selectFinClearLogVendorDeductionList(FinClearLogVendorDeduction finClearLogVendorDeduction) {
        return finClearLogVendorDeductionMapper.selectFinClearLogVendorDeductionList(finClearLogVendorDeduction);
    }

}
