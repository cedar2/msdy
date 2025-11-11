package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinClearLogVendorDeduction;

/**
 * 核销供应商扣款日志Mapper接口
 *
 * @author platform
 * @date 2024-03-28
 */
public interface FinClearLogVendorDeductionMapper  extends BaseMapper<FinClearLogVendorDeduction> {

    /**
     * 查询详情
     * @param clearLogVendorDeductionSid 单据sid
     * @return FinClearLogVendorDeduction
     */
    FinClearLogVendorDeduction selectFinClearLogVendorDeductionById(Long clearLogVendorDeductionSid);

    /**
     * 查询列表
     * @param finClearLogVendorDeduction FinClearLogVendorDeduction
     * @return List
     */
    List<FinClearLogVendorDeduction> selectFinClearLogVendorDeductionList(FinClearLogVendorDeduction finClearLogVendorDeduction);

    /**
     * 添加多个
     * @param list List FinClearLogVendorDeduction
     * @return int
     */
    int inserts(@Param("list") List<FinClearLogVendorDeduction> list);
}
