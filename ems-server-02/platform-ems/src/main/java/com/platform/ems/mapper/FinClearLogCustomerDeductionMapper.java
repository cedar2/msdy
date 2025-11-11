package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogCustomerDeduction;

/**
 * 核销客户扣款日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogCustomerDeductionMapper  extends BaseMapper<FinClearLogCustomerDeduction> {

    /**
     * 查询详情
     * @param clearLogCustomerDeductionSid 单据sid
     * @return FinClearLogCustomerDeduction
     */
    FinClearLogCustomerDeduction selectFinClearLogCustomerDeductionById(Long clearLogCustomerDeductionSid);

    /**
     * 查询列表
     * @param finClearLogCustomerDeduction FinClearLogCustomerDeduction
     * @return List
     */
    List<FinClearLogCustomerDeduction> selectFinClearLogCustomerDeductionList(FinClearLogCustomerDeduction finClearLogCustomerDeduction);

    /**
     * 添加多个
     * @param list List FinClearLogCustomerDeduction
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogCustomerDeduction> list);

}
