package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogAdvancePayment;
import com.platform.ems.mapper.FinClearLogAdvancePaymentMapper;
import com.platform.ems.service.IFinClearLogAdvancePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销供应商已预付款日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogAdvancePaymentServiceImpl extends ServiceImpl<FinClearLogAdvancePaymentMapper, FinClearLogAdvancePayment> implements IFinClearLogAdvancePaymentService {
    @Autowired
    private FinClearLogAdvancePaymentMapper finClearLogAdvancePaymentMapper;

    /**
     * 查询核销供应商已预付款日志列表
     *
     * @param finClearLogAdvancePayment 核销供应商已预付款日志
     * @return 核销供应商已预付款日志
     */
    @Override
    public List<FinClearLogAdvancePayment> selectFinClearLogAdvancePaymentList(FinClearLogAdvancePayment finClearLogAdvancePayment) {
        return finClearLogAdvancePaymentMapper.selectFinClearLogAdvancePaymentList(finClearLogAdvancePayment);
    }

}
