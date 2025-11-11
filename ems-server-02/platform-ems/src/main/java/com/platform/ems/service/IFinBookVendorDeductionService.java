package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookVendorDeduction;

/**
 * 财务流水账-供应商扣款Service接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface IFinBookVendorDeductionService extends IService<FinBookVendorDeduction>{

    /**
     * 查報表
     * @param entity
     * @return
     */
    List<FinBookVendorDeduction> getReportForm(FinBookVendorDeduction entity);
}
