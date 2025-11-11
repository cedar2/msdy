package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeCustomerDeduction;

/**
 * 业务类型_客户扣款单Mapper接口
 *
 * @author chenkw
 * @date 2021-08-03
 */
public interface ConBuTypeCustomerDeductionMapper extends BaseMapper<ConBuTypeCustomerDeduction> {


    ConBuTypeCustomerDeduction selectConBuTypeCustomerDeductionById(Long sid);

    List<ConBuTypeCustomerDeduction> selectConBuTypeCustomerDeductionList(ConBuTypeCustomerDeduction conBuTypeCustomerDeduction);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeCustomerDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeCustomerDeduction> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeCustomerDeduction
     * @return int
     */
    int updateAllById(ConBuTypeCustomerDeduction entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeCustomerDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeCustomerDeduction> list);

    /**
     * 下拉框列表
     */
    List<ConBuTypeCustomerDeduction> getConBuTypeCustomerDeductionList();
}
