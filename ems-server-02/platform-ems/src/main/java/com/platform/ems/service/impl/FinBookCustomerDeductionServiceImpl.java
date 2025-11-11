package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookCustomerDeduction;
import com.platform.ems.mapper.FinBookCustomerDeductionMapper;
import com.platform.ems.service.IFinBookCustomerDeductionService;

/**
 * 财务流水账-客户扣款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-08
 */
@Service
@SuppressWarnings("all")
public class FinBookCustomerDeductionServiceImpl extends ServiceImpl<FinBookCustomerDeductionMapper, FinBookCustomerDeduction> implements IFinBookCustomerDeductionService {
    @Autowired
    private FinBookCustomerDeductionMapper finBookCustomerDeductionMapper;

    @Override
    public List<FinBookCustomerDeduction> getReportForm(FinBookCustomerDeduction entity) {
        List<FinBookCustomerDeduction> responseList = finBookCustomerDeductionMapper.getReportForm(entity);
        return responseList;
    }
}
