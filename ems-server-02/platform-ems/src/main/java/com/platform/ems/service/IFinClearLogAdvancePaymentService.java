package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogAdvancePayment;

/**
 * 核销供应商已预付款日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogAdvancePaymentService extends IService<FinClearLogAdvancePayment>{

    /**
     * 查询核销供应商已预付款日志列表
     *
     * @param finClearLogAdvancePayment 核销供应商已预付款日志
     * @return 核销供应商已预付款日志集合
     */
    public List<FinClearLogAdvancePayment> selectFinClearLogAdvancePaymentList(FinClearLogAdvancePayment finClearLogAdvancePayment);

}
