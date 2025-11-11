package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogCustomerDeduction;
import com.platform.ems.mapper.FinClearLogCustomerDeductionMapper;
import com.platform.ems.service.IFinClearLogCustomerDeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销客户扣款日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogCustomerDeductionServiceImpl extends ServiceImpl<FinClearLogCustomerDeductionMapper, FinClearLogCustomerDeduction> implements IFinClearLogCustomerDeductionService {
    @Autowired
    private FinClearLogCustomerDeductionMapper finClearLogCustomerDeductionMapper;

    /**
     * 查询核销客户扣款日志列表
     *
     * @param finClearLogCustomerDeduction 核销客户扣款日志
     * @return 核销客户扣款日志
     */
    @Override
    public List<FinClearLogCustomerDeduction> selectFinClearLogCustomerDeductionList(FinClearLogCustomerDeduction finClearLogCustomerDeduction) {
        return finClearLogCustomerDeductionMapper.selectFinClearLogCustomerDeductionList(finClearLogCustomerDeduction);
    }

}
