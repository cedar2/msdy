package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinClearLogReceiptEstimation;

/**
 * 核销应收暂估日志Service接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface IFinClearLogReceiptEstimationService extends IService<FinClearLogReceiptEstimation> {

    /**
     * 查询核销应收暂估日志列表
     *
     * @param finClearLogReceiptEstimation 核销应收暂估日志
     * @return 核销应收暂估日志集合
     */
    public List<FinClearLogReceiptEstimation> selectFinClearLogReceiptEstimationList(FinClearLogReceiptEstimation finClearLogReceiptEstimation);

}
