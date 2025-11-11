package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogAdvanceReceiptPayment;
import com.platform.ems.mapper.FinClearLogAdvanceReceiptPaymentMapper;
import com.platform.ems.service.IFinClearLogAdvanceReceiptPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销客户已预收款日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogAdvanceReceiptPaymentServiceImpl extends ServiceImpl<FinClearLogAdvanceReceiptPaymentMapper, FinClearLogAdvanceReceiptPayment> implements IFinClearLogAdvanceReceiptPaymentService {
    @Autowired
    private FinClearLogAdvanceReceiptPaymentMapper finClearLogAdvanceReceiptPaymentMapper;

    /**
     * 查询核销客户已预收款日志列表
     *
     * @param finClearLogAdvanceReceiptPayment 核销客户已预收款日志
     * @return 核销客户已预收款日志
     */
    @Override
    public List<FinClearLogAdvanceReceiptPayment> selectFinClearLogAdvanceReceiptPaymentList(FinClearLogAdvanceReceiptPayment finClearLogAdvanceReceiptPayment) {
        return finClearLogAdvanceReceiptPaymentMapper.selectFinClearLogAdvanceReceiptPaymentList(finClearLogAdvanceReceiptPayment);
    }

}
