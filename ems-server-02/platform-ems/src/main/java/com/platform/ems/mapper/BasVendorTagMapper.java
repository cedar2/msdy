package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasVendorTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商标签(分组)Mapper接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface BasVendorTagMapper extends BaseMapper<BasVendorTag> {


    BasVendorTag selectBasVendorTagById(Long vendorTagSid);

    List<BasVendorTag> selectBasVendorTagList(BasVendorTag basVendorTag);

    /**
     * 添加多个
     *
     * @param list List BasVendorTag
     * @return int
     */
    int inserts(@Param("list") List<BasVendorTag> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorTag
     * @return int
     */
    int updateAllById(BasVendorTag entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorTag
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorTag> list);


}
