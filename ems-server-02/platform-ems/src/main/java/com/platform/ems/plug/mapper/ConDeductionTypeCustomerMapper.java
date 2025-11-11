package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDeductionTypeCustomer;

/**
 * 扣款类型_销售Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDeductionTypeCustomerMapper extends BaseMapper<ConDeductionTypeCustomer> {


    ConDeductionTypeCustomer selectConDeductionTypeCustomerById(Long sid);

    List<ConDeductionTypeCustomer> selectConDeductionTypeCustomerList(ConDeductionTypeCustomer conDeductionTypeCustomer);

    /**
     * 添加多个
     *
     * @param list List ConDeductionTypeCustomer
     * @return int
     */
    int inserts(@Param("list") List<ConDeductionTypeCustomer> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDeductionTypeCustomer
     * @return int
     */
    int updateAllById(ConDeductionTypeCustomer entity);

    /**
     * 更新多个
     *
     * @param list List ConDeductionTypeCustomer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDeductionTypeCustomer> list);

    /**
     * 获取下拉列表
     */
    List<ConDeductionTypeCustomer> getConDeductionTypeCustomerList();

}
