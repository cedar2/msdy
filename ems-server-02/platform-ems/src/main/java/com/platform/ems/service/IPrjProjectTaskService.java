package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PrjProject;
import com.platform.ems.domain.PrjProjectTask;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.form.PrjProjectTaskFormRequest;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskFormResponse;
import com.platform.ems.domain.dto.response.form.PrjProjectTaskPreCondition;

/**
 * 项目档案-任务Service接口
 *
 * @author chenkw
 * @date 2022-12-15
 */
public interface IPrjProjectTaskService extends IService<PrjProjectTask> {
    /**
     * 查询项目档案-任务
     *
     * @param projectTaskSid 项目档案-任务ID
     * @return 项目档案-任务
     */
    public PrjProjectTask selectPrjProjectTaskById(Long projectTaskSid);

    /**
     * 查询项目档案-任务列表
     *
     * @param prjProjectTask 项目档案-任务
     * @return 项目档案-任务集合
     */
    public List<PrjProjectTask> selectPrjProjectTaskList(PrjProjectTask prjProjectTask);

    /**
     * 新增项目档案-任务
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    public int insertPrjProjectTask(PrjProjectTask prjProjectTask);

    /**
     * 修改项目档案-任务
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    public int updatePrjProjectTask(PrjProjectTask prjProjectTask);

    /**
     * 变更项目档案-任务
     *
     * @param prjProjectTask 项目档案-任务
     * @return 结果
     */
    public int changePrjProjectTask(PrjProjectTask prjProjectTask);

    /**
     * 批量删除项目档案-任务
     *
     * @param projectTaskSids 需要删除的项目档案-任务ID
     * @return 结果
     */
    public int deletePrjProjectTaskByIds(List<Long> projectTaskSids);

    /**
     * 获取岗位名称
     *
     * @param projectTask 任务节点
     * @return 结果
     */
    public void getPosition(PrjProjectTask projectTask);

    /**
     * 查询项目档案-任务明细
     *
     * @param projectSid 项目档案-主表ID
     * @return 项目档案-任务-明细
     */
    public List<PrjProjectTask> selectPrjProjectTaskListById(Long projectSid);

    /**
     * 批量新增项目档案-任务明细
     *
     * @param project 项目档案
     * @return 结果
     */
    public int insertPrjProjectTaskList(PrjProject project);

    /**
     * 批量修改项目档案-任务明细
     *
     * @param project 项目档案
     * @return 结果
     */
    public int updatePrjProjectTaskList(PrjProject project);

    /**
     * 批量删除项目档案-任务明细
     *
     * @param itemList 需要删除的项目档案-任务列表
     * @return 结果
     */
    public int deletePrjProjectTaskByList(List<PrjProjectTask> itemList);

    /**
     * 批量删除项目档案-任务明细 根据主表sids
     *
     * @param prjProjectSidList 需要删除的项目档案sids
     * @return 结果
     */
    public int deletePrjProjectTaskByProject(List<Long> prjProjectSidList);

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
     * 设置即将到期提醒天数
     * @param projectTask
     * @return
     */
    int setToexpireDays(PrjProjectTask projectTask);

    /**
     * 项目任务执行提醒天数
     * @param projectTask
     * @return
     */
    int setToexpireNoticeDays(PrjProjectTask projectTask);

    /**
     * 设置任务状态
     * @param projectTask
     * @return
     */
    EmsResultEntity setTaskStatus(PrjProjectTask projectTask);

    /**
     * 设置计划日期
     * @param projectTask
     * @return
     */
    int setTaskPlanDate(PrjProjectTask projectTask);

    /**
     * 项目任务明细报表分配任务处理人
     * @param projectTask 入参
     * @return 出参
     */
    int setTaskHandler(PrjProjectTask projectTask);

    /**
     * 设置项目任务优先级
     * @param projectTask 入参
     * @return 出餐
     */
    public int setTaskPriority(PrjProjectTask projectTask);
}
