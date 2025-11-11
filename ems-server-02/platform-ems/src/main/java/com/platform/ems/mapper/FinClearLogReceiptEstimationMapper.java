package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogReceiptEstimation;

/**
 * 核销应收暂估日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogReceiptEstimationMapper  extends BaseMapper<FinClearLogReceiptEstimation> {

    /**
     * 查询详情
     * @param clearLogReceiptEstimationSid 单据sid
     * @return FinClearLogReceiptEstimation
     */
    FinClearLogReceiptEstimation selectFinClearLogReceiptEstimationById(Long clearLogReceiptEstimationSid);

    /**
     * 查询列表
     * @param finClearLogReceiptEstimation FinClearLogReceiptEstimation
     * @return List
     */
    List<FinClearLogReceiptEstimation> selectFinClearLogReceiptEstimationList(FinClearLogReceiptEstimation finClearLogReceiptEstimation);

    /**
     * 添加多个
     * @param list List FinClearLogReceiptEstimation
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogReceiptEstimation> list);

}
