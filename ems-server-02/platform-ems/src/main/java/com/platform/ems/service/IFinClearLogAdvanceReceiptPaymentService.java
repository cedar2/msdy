package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogAdvanceReceiptPayment;

/**
 * 核销客户已预收款日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogAdvanceReceiptPaymentService extends IService<FinClearLogAdvanceReceiptPayment>{

    /**
     * 查询核销客户已预收款日志列表
     *
     * @param finClearLogAdvanceReceiptPayment 核销客户已预收款日志
     * @return 核销客户已预收款日志集合
     */
    public List<FinClearLogAdvanceReceiptPayment> selectFinClearLogAdvanceReceiptPaymentList(FinClearLogAdvanceReceiptPayment finClearLogAdvanceReceiptPayment);

}
