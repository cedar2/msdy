package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.dto.request.form.DevCategoryPlanItemFormRequest;
import com.platform.ems.domain.dto.response.form.DevCategoryPlanItemFormResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevCategoryPlanItem;

/**
 * 品类规划-明细Mapper接口
 *
 * @author chenkw
 * @date 2022-12-09
 */
public interface DevCategoryPlanItemMapper extends BaseMapper<DevCategoryPlanItem> {

    DevCategoryPlanItem selectDevCategoryPlanItemById(Long categoryPlanItemSid);

    List<DevCategoryPlanItem> selectDevCategoryPlanItemList(DevCategoryPlanItem devCategoryPlanItem);

    /**
     * 添加多个
     *
     * @param list List DevCategoryPlanItem
     * @return int
     */
    int inserts(@Param("list") List<DevCategoryPlanItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevCategoryPlanItem
     * @return int
     */
    int updateAllById(DevCategoryPlanItem entity);

    /**
     * 更新多个
     *
     * @param list List DevCategoryPlanItem
     * @return int
     */
    int updatesAllById(@Param("list") List<DevCategoryPlanItem> list);

    /**
     * 查询品类规划-明细报表
     *
     * @param request 品类规划-明细报表请求体
     * @return 品类规划-明细集合
     */
    public List<DevCategoryPlanItemFormResponse> selectDevCategoryPlanItemForm(DevCategoryPlanItemFormRequest request);

}
