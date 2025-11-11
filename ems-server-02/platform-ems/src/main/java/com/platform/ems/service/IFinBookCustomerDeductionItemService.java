package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookCustomerDeductionItem;

/**
 * s_fin_book_customer_deduction_itemService接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface IFinBookCustomerDeductionItemService extends IService<FinBookCustomerDeductionItem>{

    /**
     * 修改 核销中金额  已核销金额
     */
    int updateByAmountTax(FinBookCustomerDeductionItem finBookCustomerDeductionItem);

}
