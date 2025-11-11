package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasVendorTagItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商标签(分组)明细Mapper接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface BasVendorTagItemMapper extends BaseMapper<BasVendorTagItem> {


    BasVendorTagItem selectBasVendorTagItemById(Long vendorTagItemSid);

    List<BasVendorTagItem> selectBasVendorTagItemList(BasVendorTagItem basVendorTagItem);

    /**
     * 添加多个
     *
     * @param list List BasVendorTagItem
     * @return int
     */
    int inserts(@Param("list") List<BasVendorTagItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorTagItem
     * @return int
     */
    int updateAllById(BasVendorTagItem entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorTagItem
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorTagItem> list);


}
