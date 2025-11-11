package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevCategoryPlan;

/**
 * 品类规划Mapper接口
 *
 * @author chenkw
 * @date 2022-12-09
 */
public interface DevCategoryPlanMapper extends BaseMapper<DevCategoryPlan> {

    DevCategoryPlan selectDevCategoryPlanById(Long categoryPlanSid);

    List<DevCategoryPlan> selectDevCategoryPlanList(DevCategoryPlan devCategoryPlan);

    /**
     * 添加多个
     *
     * @param list List DevCategoryPlan
     * @return int
     */
    int inserts(@Param("list") List<DevCategoryPlan> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevCategoryPlan
     * @return int
     */
    int updateAllById(DevCategoryPlan entity);

    /**
     * 更新多个
     *
     * @param list List DevCategoryPlan
     * @return int
     */
    int updatesAllById(@Param("list") List<DevCategoryPlan> list);

}
