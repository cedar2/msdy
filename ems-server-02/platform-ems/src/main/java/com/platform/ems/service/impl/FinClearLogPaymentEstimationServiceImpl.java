package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogPaymentEstimation;
import com.platform.ems.mapper.FinClearLogPaymentEstimationMapper;
import com.platform.ems.service.IFinClearLogPaymentEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销应付暂估日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogPaymentEstimationServiceImpl extends ServiceImpl<FinClearLogPaymentEstimationMapper, FinClearLogPaymentEstimation> implements IFinClearLogPaymentEstimationService {
    @Autowired
    private FinClearLogPaymentEstimationMapper finClearLogPaymentEstimationMapper;


    /**
     * 查询核销应付暂估日志列表
     *
     * @param finClearLogPaymentEstimation 核销应付暂估日志
     * @return 核销应付暂估日志
     */
    @Override
    public List<FinClearLogPaymentEstimation> selectFinClearLogPaymentEstimationList(FinClearLogPaymentEstimation finClearLogPaymentEstimation) {
        return finClearLogPaymentEstimationMapper.selectFinClearLogPaymentEstimationList(finClearLogPaymentEstimation);
    }


}
