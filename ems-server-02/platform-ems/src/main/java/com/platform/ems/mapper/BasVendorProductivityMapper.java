package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorProductivity;

/**
 * 供应商的产能信息Mapper接口
 *
 * @author chenkw
 * @date 2022-01-06
 */
public interface BasVendorProductivityMapper extends BaseMapper<BasVendorProductivity> {


    BasVendorProductivity selectBasVendorProductivityById(Long vendorProductivitySid);

    List<BasVendorProductivity> selectBasVendorProductivityList(BasVendorProductivity basVendorProductivity);

    /**
     * 添加多个
     *
     * @param list List BasVendorProductivity
     * @return int
     */
    int inserts(@Param("list") List<BasVendorProductivity> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorProductivity
     * @return int
     */
    int updateAllById(BasVendorProductivity entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorProductivity
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorProductivity> list);


}
