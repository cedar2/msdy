package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookVendorDeductionItem;

/**
 * 财务流水账-明细-供应商扣款Service接口
 *
 * @author linhongwei
 * @date 2021-06-02
 */
public interface IFinBookVendorDeductionItemService extends IService<FinBookVendorDeductionItem>{

    /**
     * 修改 核销中金额  已核销金额
     */
    int updateByAmountTax(FinBookVendorDeductionItem finBookVendorDeductionItem);
}
