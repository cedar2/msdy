package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookPayment;
import com.platform.ems.domain.FinBookPaymentItem;

/**
 * 财务流水账-明细-付款Service接口
 *
 * @author linhongwei
 * @date 2021-06-07
 */
public interface IFinBookPaymentItemService extends IService<FinBookPaymentItem>{

    /**
     * 新增
     */
    int insertFinBookPaymentItemItem(FinBookPaymentItem finBookPaymentItem);

    /**
     * 批量新增
     */
    int insertByList(FinBookPayment payment);

    /**
     * 修改核销中金额已核销金额
     */
    int updateByAmountTax(FinBookPaymentItem finBookPaymentItem);
}
