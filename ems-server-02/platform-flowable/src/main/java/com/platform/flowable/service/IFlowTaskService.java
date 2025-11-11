package com.platform.flowable.service;

import com.platform.common.core.domain.AjaxResult;
import com.platform.flowable.domain.dto.FlowTaskDto;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.request.FlowProcessRequest;
import org.flowable.task.api.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author c
 */
public interface IFlowTaskService {
	/**
	 * 查找任务
	 *
	 * @param task
	 * @return
	 */
	public List<FlowTaskDto> getTaskByBusinessKey (FlowTaskVo task);

	/**
	 * 审批任务
	 *
	 * @param task 请求实体参数
	 */
	AjaxResult complete (FlowTaskVo task);

	/**
	 * 驳回任务
	 *
	 * @param flowTaskVo
	 * @return
	 */
	AjaxResult taskRejectToSubmit (FlowTaskVo flowTaskVo);


	/**
	 * 退回任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	AjaxResult taskReturn (FlowTaskVo flowTaskVo);

	/**
	 * 退回任务到上一级
	 *
	 * @param flowTaskVo
	 * @return
	 */
	AjaxResult returnToParent (FlowTaskVo flowTaskVo);

	/**
	 * 获取所有可回退的节点
	 *
	 * @param flowTaskVo
	 * @return
	 */
	AjaxResult findReturnTaskList (FlowTaskVo flowTaskVo);


	/**
	 * 获取用户待办任务
	 *
	 * @return
	 */
	AjaxResult getUserTaskList (FlowTaskVo flowTaskVo);


	/**
	 * 删除任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	void deleteTask (FlowTaskVo flowTaskVo);

	/**
	 * 认领/签收任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	void claim (FlowTaskVo flowTaskVo);

	/**
	 * 取消认领/签收任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	void unClaim (FlowTaskVo flowTaskVo);

	/**
	 * 委派任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	void delegateTask (FlowTaskVo flowTaskVo);


	/**
	 * 转办任务
	 *
	 * @param flowTaskVo 请求实体参数
	 */
	void assignTask (FlowTaskVo flowTaskVo);

	/**
	 * 我发起的流程
	 *
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	AjaxResult myProcess (Integer pageNum, Integer pageSize);

	/**
	 * 我发起的流程
	 *
	 * @return
	 */
	AjaxResult myProcess (FlowProcessRequest request);

	/**
	 * 取消申请
	 *
	 * @param flowTaskVo
	 * @return
	 */
	AjaxResult stopProcess (FlowTaskVo flowTaskVo);

	/**
	 * 撤回流程
	 *
	 * @param flowTaskVo
	 * @return
	 */
	AjaxResult revokeProcess (FlowTaskVo flowTaskVo);


	/**
	 * 代办任务列表
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页条数
	 * @return
	 */
	AjaxResult todoList (Integer pageNum, Integer pageSize);


	/**
	 * 已办任务列表
	 *
	 * @param pageNum  当前页码
	 * @param pageSize 每页条数
	 * @return
	 */
	AjaxResult finishedList (Integer pageNum, Integer pageSize, String userId);

	AjaxResult myApprovalTask (FlowProcessRequest request);

	/**
	 * 流程历史流转记录
	 *
	 * @param procInsId 流程实例Id
	 * @return
	 */
	AjaxResult flowRecord (String procInsId, String deployId);

	/**
	 * 根据任务ID查询挂载的表单信息
	 *
	 * @param taskId 任务Id
	 * @return
	 */
	Task getTaskForm (String taskId);

	/**
	 * 获取流程过程图
	 *
	 * @param processId
	 * @return
	 */
	InputStream diagram (String processId);

	/**
	 * 获取流程执行过程
	 *
	 * @param procInsId
	 * @return
	 */
	AjaxResult getFlowViewer (String procInsId);

	/**
	 * 获取流程变量
	 *
	 * @param taskId
	 * @return
	 */
	AjaxResult processVariables (String taskId);

	/**
	 * 获取下一节点
	 *
	 * @param flowTaskVo 任务
	 * @return
	 */
	AjaxResult getNextFlowNode (FlowTaskVo flowTaskVo);

	String getParentTaskId (String instanceId);

	/**
	 * 给任务设置审批人
	 *
	 * @param instance
	 */
	public void addTaskAssignee (Map<String, Object> instance);

	/**
	 * 删除流程（全部记录）
	 * @param flowTaskVo
	 */
	public AjaxResult removeProcess (FlowTaskVo flowTaskVo);

	AjaxResult isUserAssignee(Long userId, String formId);
}
