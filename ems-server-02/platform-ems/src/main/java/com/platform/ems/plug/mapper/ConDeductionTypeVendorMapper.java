package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDeductionTypeVendor;

/**
 * 扣款类型_采购Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDeductionTypeVendorMapper extends BaseMapper<ConDeductionTypeVendor> {


    ConDeductionTypeVendor selectConDeductionTypeVendorById(Long sid);

    List<ConDeductionTypeVendor> selectConDeductionTypeVendorList(ConDeductionTypeVendor conDeductionTypeVendor);

    /**
     * 添加多个
     *
     * @param list List ConDeductionTypeVendor
     * @return int
     */
    int inserts(@Param("list") List<ConDeductionTypeVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDeductionTypeVendor
     * @return int
     */
    int updateAllById(ConDeductionTypeVendor entity);

    /**
     * 更新多个
     *
     * @param list List ConDeductionTypeVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDeductionTypeVendor> list);

    /**
     * 获取下拉列表
     */
    List<ConDeductionTypeVendor> getConDeductionTypeVendorList();
}
