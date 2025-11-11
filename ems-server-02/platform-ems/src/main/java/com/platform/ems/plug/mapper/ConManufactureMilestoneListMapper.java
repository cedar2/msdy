package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConManufactureMilestoneList;

/**
 * 生产里程碑清单Mapper接口
 *
 * @author platform
 * @date 2024-03-14
 */
public interface ConManufactureMilestoneListMapper extends BaseMapper<ConManufactureMilestoneList> {

    /**
     * 查询详情
     *
     * @param manufactureMilestoneListSid 单据sid
     * @return ConManufactureMilestoneList
     */
    ConManufactureMilestoneList selectConManufactureMilestoneListById(Long manufactureMilestoneListSid);

    /**
     * 查询列表
     *
     * @param conManufactureMilestoneList ConManufactureMilestoneList
     * @return List
     */
    List<ConManufactureMilestoneList> selectConManufactureMilestoneListList(ConManufactureMilestoneList conManufactureMilestoneList);

    /**
     * 添加多个
     *
     * @param list List ConManufactureMilestoneList
     * @return int
     */
    int inserts(@Param("list") List<ConManufactureMilestoneList> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConManufactureMilestoneList
     * @return int
     */
    int updateAllById(ConManufactureMilestoneList entity);

}
