package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.form.DevDevelopPlanForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevDevelopPlan;

/**
 * 开发计划Mapper接口
 * 
 * @author chenkw
 * @date 2022-12-08
 */
public interface DevDevelopPlanMapper  extends BaseMapper<DevDevelopPlan> {


    DevDevelopPlan selectDevDevelopPlanById(Long developPlanSid);

    List<DevDevelopPlan> selectDevDevelopPlanList(DevDevelopPlan devDevelopPlan);

    @SqlParser(filter=true)
    List<DevDevelopPlan> selectDevDevelopPlanListByCode(DevDevelopPlan devDevelopPlan);

    /**
     * 添加多个
     * @param list List DevDevelopPlan
     * @return int
     */
    int inserts(@Param("list") List<DevDevelopPlan> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DevDevelopPlan
    * @return int
    */
    int updateAllById(DevDevelopPlan entity);

    /**
     * 更新多个
     * @param list List DevDevelopPlan
     * @return int
     */
    int updatesAllById(@Param("list") List<DevDevelopPlan> list);

    /**
     * 查询开发计划报表
     *
     * @param devDevelopPlanForm 开发计划
     * @return 开发计划集合
     */
    List<DevDevelopPlanForm> selectDevDevelopPlanForm(DevDevelopPlanForm devDevelopPlanForm);

}
