package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConManufactureMilestoneListItem;

/**
 * 生产里程碑清单-明细Mapper接口
 *
 * @author platform
 * @date 2024-03-14
 */
public interface ConManufactureMilestoneListItemMapper extends BaseMapper<ConManufactureMilestoneListItem> {

    /**
     * 查询详情
     *
     * @param manufactureMilestoneListItemSid 单据sid
     * @return ConManufactureMilestoneListItem
     */
    ConManufactureMilestoneListItem selectConManufactureMilestoneListItemById(Long manufactureMilestoneListItemSid);

    /**
     * 查询列表
     *
     * @param conManufactureMilestoneListItem ConManufactureMilestoneListItem
     * @return List
     */
    List<ConManufactureMilestoneListItem> selectConManufactureMilestoneListItemList(ConManufactureMilestoneListItem conManufactureMilestoneListItem);

    /**
     * 添加多个
     *
     * @param list List ConManufactureMilestoneListItem
     * @return int
     */
    int inserts(@Param("list") List<ConManufactureMilestoneListItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConManufactureMilestoneListItem
     * @return int
     */
    int updateAllById(ConManufactureMilestoneListItem entity);

    /**
     * 更新多个
     *
     * @param list List ConManufactureMilestoneListItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ConManufactureMilestoneListItem> list);

}
