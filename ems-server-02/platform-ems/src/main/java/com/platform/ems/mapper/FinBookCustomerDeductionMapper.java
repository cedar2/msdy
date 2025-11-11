package com.platform.ems.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookCustomerDeduction;

/**
 * 财务流水账-客户扣款Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-08
 */
public interface FinBookCustomerDeductionMapper extends BaseMapper<FinBookCustomerDeduction> {

    FinBookCustomerDeduction selectFinBookCustomerDeductionById(Long bookDeductionSid);

    List<FinBookCustomerDeduction> selectFinBookCustomerDeductionList(FinBookCustomerDeduction finBookCustomerDeduction);

    /**
     * 添加多个
     *
     * @param list List FinBookCustomerDeduction
     * @return int
     */
    int inserts(@Param("list") List<FinBookCustomerDeduction> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinBookCustomerDeduction
     * @return int
     */
    int updateAllById(FinBookCustomerDeduction entity);

    List<FinBookCustomerDeduction> getReportForm(FinBookCustomerDeduction entity);

}
