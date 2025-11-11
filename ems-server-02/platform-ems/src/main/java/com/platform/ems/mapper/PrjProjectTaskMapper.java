package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.HashMap;
import java.util.List;

import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.dto.request.form.PrjProjectTaskFormRequest;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskFormResponse;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskPreCondition;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PrjProjectTask;

/**
 * 项目档案-任务Mapper接口
 *
 * @author chenkw
 * @date 2022-12-15
 */
public interface PrjProjectTaskMapper extends BaseMapper<PrjProjectTask> {

    PrjProjectTask selectPrjProjectTaskById(Long projectTaskSid);

    List<PrjProjectTask> selectPrjProjectTaskList(PrjProjectTask prjProjectTask);

    @InterceptorIgnore(tenantLine = "true")
    List<PrjProjectTask> selectPrjProjectTaskListAll(PrjProjectTask prjProjectTask);

    /**
     * 添加多个
     *
     * @param list List PrjProjectTask
     * @return int
     */
    int inserts(@Param("list") List<PrjProjectTask> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjProjectTask
     * @return int
     */
    int updateAllById(PrjProjectTask entity);

    /**
     * 更新多个
     *
     * @param list List PrjProjectTask
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjProjectTask> list);

    /**
     * 查询项目任务明细报表
     *
     * @param prjProjectTask 项目任务明细报表请求
     * @return 项目任务明细报表返回
     */
    public List<PrjProjectTaskFormResponse> selectPrjProjectTaskForm(PrjProjectTaskFormRequest prjProjectTask);

    /**
     * 查询项目前置任务完成状况报表
     *
     * @param prjProjectTask 查询项目前置任务完成状况报表请求
     * @return 查询项目前置任务完成状况报表返回
     */
    public List<PrjProjectTaskPreCondition> selectPrjProjectTaskPreCondition(PrjProjectTaskPreCondition prjProjectTask);

    /**
     * 根据主表sid查出对应主表下的明细数量
     *
     * @param entity Long[]
     * @return int
     */
    @MapKey("projectSid")
    HashMap<Long, PrjProject> selectTaskCountGroupByProjectSid(PrjProjectTask entity);

    /**
     * 查询即将到期的项目档案任务明细
     * offset : 设置到期前的天数才算即将到期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProjectTask> getToexpireBusiness(@Param("entity") PrjProjectTask entity, @Param("offset") int offset);

    /**
     * 查询已逾期的项目档案任务明细
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProjectTask> getOverdueBusiness(PrjProjectTask entity);

    /**
     * 任务所属的项目状态为进行中且已确认，且任务状态为未开始，且当前日期<计划开始日期的同时，当前日期+任务执行提醒天数>=计划完成日期
     */
    @InterceptorIgnore(tenantLine = "true")
    List<PrjProjectTask> getNotYetStartTaskList(@Param("entity") PrjProjectTask entity);

}
