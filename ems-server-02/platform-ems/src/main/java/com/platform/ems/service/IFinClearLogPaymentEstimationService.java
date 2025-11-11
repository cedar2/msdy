package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogPaymentEstimation;

/**
 * 核销应付暂估日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogPaymentEstimationService extends IService<FinClearLogPaymentEstimation>{

    /**
     * 查询核销应付暂估日志列表
     *
     * @param finClearLogPaymentEstimation 核销应付暂估日志
     * @return 核销应付暂估日志集合
     */
    public List<FinClearLogPaymentEstimation> selectFinClearLogPaymentEstimationList(FinClearLogPaymentEstimation finClearLogPaymentEstimation);

}
