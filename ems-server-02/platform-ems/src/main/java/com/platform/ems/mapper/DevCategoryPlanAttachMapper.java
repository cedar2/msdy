package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevCategoryPlanAttach;

/**
 * 品类规划-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-12-09
 */
public interface DevCategoryPlanAttachMapper extends BaseMapper<DevCategoryPlanAttach> {

    DevCategoryPlanAttach selectDevCategoryPlanAttachById(Long categoryPlanAttachSid);

    List<DevCategoryPlanAttach> selectDevCategoryPlanAttachList(DevCategoryPlanAttach devCategoryPlanAttach);

    /**
     * 添加多个
     *
     * @param list List DevCategoryPlanAttach
     * @return int
     */
    int inserts(@Param("list") List<DevCategoryPlanAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevCategoryPlanAttach
     * @return int
     */
    int updateAllById(DevCategoryPlanAttach entity);

    /**
     * 更新多个
     *
     * @param list List DevCategoryPlanAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevCategoryPlanAttach> list);

}
