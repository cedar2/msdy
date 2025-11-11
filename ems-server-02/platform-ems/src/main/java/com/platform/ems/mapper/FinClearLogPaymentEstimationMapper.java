package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogPaymentEstimation;

/**
 * 核销应付暂估日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogPaymentEstimationMapper  extends BaseMapper<FinClearLogPaymentEstimation> {

    /**
     * 查询详情
     * @param clearLogPaymentEstimationSid 单据sid
     * @return FinClearLogPaymentEstimation
     */
    FinClearLogPaymentEstimation selectFinClearLogPaymentEstimationById(Long clearLogPaymentEstimationSid);

    /**
     * 查询列表
     * @param finClearLogPaymentEstimation FinClearLogPaymentEstimation
     * @return List
     */
    List<FinClearLogPaymentEstimation> selectFinClearLogPaymentEstimationList(FinClearLogPaymentEstimation finClearLogPaymentEstimation);

    /**
     * 添加多个
     * @param list List FinClearLogPaymentEstimation
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogPaymentEstimation> list);

}
