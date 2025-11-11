package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookReceiptEstimation;

/**
 * 财务流水账-应收暂估Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookReceiptEstimationMapper  extends BaseMapper<FinBookReceiptEstimation> {

    FinBookReceiptEstimation selectFinBookReceiptEstimationById(Long bookReceiptEstimationSid);

    List<FinBookReceiptEstimation> selectFinBookReceiptEstimationList(FinBookReceiptEstimation finBookReceiptEstimation);

    /**
     * 添加多个
     * @param list List FinBookReceiptEstimation
     * @return int
     */
    int inserts(@Param("list") List<FinBookReceiptEstimation> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookReceiptEstimation
    * @return int
    */
    int updateAllById(FinBookReceiptEstimation entity);

    /**
     * 查报表
     * @param entity
     * @return
     */
    List<FinBookReceiptEstimation> getReportForm(FinBookReceiptEstimation entity);
}
