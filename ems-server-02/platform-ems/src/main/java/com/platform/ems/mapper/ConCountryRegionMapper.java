package com.platform.ems.mapper;

import java.util.List;

import com.platform.common.core.domain.entity.ConCountryRegion;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 国家区域Mapper接口
 *
 * @author qhq
 * @date 2021-03-26
 */
public interface ConCountryRegionMapper  extends BaseMapper<ConCountryRegion> {

    ConCountryRegion selectConCountryRegionById(Long countryRegionSid);

    List<ConCountryRegion> selectConCountryRegionList(ConCountryRegion conCountryRegion);

    /**
     * 添加多个
     * @param list List ConCountryRegion
     * @return int
     */
    int inserts(@Param("list") List<ConCountryRegion> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConCountryRegion
     * @return int
     */
    int updateAllById(ConCountryRegion entity);

    /**
     * 更新多个
     * @param list List ConCountryRegion
     * @return int
     */
    int updatesAllById(@Param("list") List<ConCountryRegion> list);


}
