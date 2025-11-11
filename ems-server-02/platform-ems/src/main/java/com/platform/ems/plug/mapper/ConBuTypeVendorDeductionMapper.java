package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeVendorDeduction;

/**
 * 业务类型_供应商扣款单Mapper接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface ConBuTypeVendorDeductionMapper extends BaseMapper<ConBuTypeVendorDeduction> {


    ConBuTypeVendorDeduction selectConBuTypeVendorDeductionById(Long sid);

    List<ConBuTypeVendorDeduction> selectConBuTypeVendorDeductionList(ConBuTypeVendorDeduction conBuTypeVendorDeduction);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeVendorDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeVendorDeduction> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeVendorDeduction
     * @return int
     */
    int updateAllById(ConBuTypeVendorDeduction entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeVendorDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeVendorDeduction> list);

    /**
     * 下拉框列表
     */
    List<ConBuTypeVendorDeduction> getConBuTypeVendorDeductionList();
}
