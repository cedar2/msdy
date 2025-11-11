package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookReceiptPayment;
import com.platform.ems.domain.FinBookReceiptPaymentItem;

/**
 * 财务流水账-明细-收款Service接口
 *
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IFinBookReceiptPaymentItemService extends IService<FinBookReceiptPaymentItem>{

    /**
     * 新增
     */
    int insertFinBookReceiptPaymentItemItem(FinBookReceiptPaymentItem finBookReceiptPaymentItem);

    /**
     * 批量新增
     */
    int insertByList(FinBookReceiptPayment payment);

    /**
     * 修改核销中金额已核销金额
     */
    int updateByAmountTax(FinBookReceiptPaymentItem finBookReceiptPaymentItem);
}
