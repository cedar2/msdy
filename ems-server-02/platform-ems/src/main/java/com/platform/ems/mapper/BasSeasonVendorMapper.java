package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasSeasonVendor;

/**
 * 季度供应商Mapper接口
 *
 * @author chenkw
 * @date 2023-04-13
 */
public interface BasSeasonVendorMapper extends BaseMapper<BasSeasonVendor> {

    BasSeasonVendor selectBasSeasonVendorById(Long seasonVendorSid);

    List<BasSeasonVendor> selectBasSeasonVendorList(BasSeasonVendor basSeasonVendor);

    /**
     * 添加多个
     *
     * @param list List BasSeasonVendor
     * @return int
     */
    int inserts(@Param("list") List<BasSeasonVendor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasSeasonVendor
     * @return int
     */
    int updateAllById(BasSeasonVendor entity);

    /**
     * 更新多个
     *
     * @param list List BasSeasonVendor
     * @return int
     */
    int updatesAllById(@Param("list") List<BasSeasonVendor> list);

}
