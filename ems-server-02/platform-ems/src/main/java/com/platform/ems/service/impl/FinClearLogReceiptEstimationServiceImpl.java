package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.FinClearLogReceiptEstimation;
import com.platform.ems.mapper.FinClearLogReceiptEstimationMapper;
import com.platform.ems.service.IFinClearLogReceiptEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核销应收暂估日志Service业务层处理
 *
 * @author platform
 * @date 2024-03-28
 */
@Service
@SuppressWarnings("all")
public class FinClearLogReceiptEstimationServiceImpl extends ServiceImpl<FinClearLogReceiptEstimationMapper, FinClearLogReceiptEstimation> implements IFinClearLogReceiptEstimationService {
    @Autowired
    private FinClearLogReceiptEstimationMapper finClearLogReceiptEstimationMapper;

    /**
     * 查询核销应收暂估日志列表
     *
     * @param finClearLogReceiptEstimation 核销应收暂估日志
     * @return 核销应收暂估日志
     */
    @Override
    public List<FinClearLogReceiptEstimation> selectFinClearLogReceiptEstimationList(FinClearLogReceiptEstimation finClearLogReceiptEstimation) {
        return finClearLogReceiptEstimationMapper.selectFinClearLogReceiptEstimationList(finClearLogReceiptEstimation);
    }

}
