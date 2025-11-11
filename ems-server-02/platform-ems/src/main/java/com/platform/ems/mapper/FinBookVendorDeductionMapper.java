package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookVendorDeduction;

/**
 * 财务流水账-供应商扣款Mapper接口
 *
 * @author qhq
 * @date 2021-06-02
 */
public interface FinBookVendorDeductionMapper  extends BaseMapper<FinBookVendorDeduction> {

    FinBookVendorDeduction selectFinBookVendorDeductionById(Long bookDeductionSid);

    List<FinBookVendorDeduction> selectFinBookVendorDeductionList(FinBookVendorDeduction finBookVendorDeduction);

    /**
     * 添加多个
     * @param list List FinBookVendorDeduction
     * @return int
     */
    int inserts(@Param("list") List<FinBookVendorDeduction> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookVendorDeduction
    * @return int
    */
    int updateAllById(FinBookVendorDeduction entity);

    /**
     * 查報表
     * @param entity
     * @return
     */
    List<FinBookVendorDeduction> getReportForm(FinBookVendorDeduction entity);


}
