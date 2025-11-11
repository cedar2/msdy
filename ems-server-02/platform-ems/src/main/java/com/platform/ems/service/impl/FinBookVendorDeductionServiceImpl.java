package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinBookVendorDeduction;
import com.platform.ems.mapper.FinBookVendorDeductionMapper;
import com.platform.ems.service.IFinBookVendorDeductionService;

/**
 * 财务流水账-供应商扣款Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-02
 */
@Service
@SuppressWarnings("all")
public class FinBookVendorDeductionServiceImpl extends ServiceImpl<FinBookVendorDeductionMapper,FinBookVendorDeduction>  implements IFinBookVendorDeductionService {
    @Autowired
    private FinBookVendorDeductionMapper finBookVendorDeductionMapper;

    /**
     * 查報表
     * @param entity
     * @return
     */
    @Override
    public List<FinBookVendorDeduction> getReportForm(FinBookVendorDeduction entity){
        List<FinBookVendorDeduction> response = finBookVendorDeductionMapper.getReportForm(entity);
        return response;
    }
}
