package com.platform.api.service;

import com.platform.common.core.domain.AjaxResult;
import com.platform.flowable.domain.dto.FlowTaskDto;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.response.ProcessInstanceResponse;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author qhq
 */
@SuppressWarnings("all")
public interface RemoteFlowableService {

	/**
	 * 启动流程
	 * @author qhq
	 */
    ProcessInstanceResponse start(String procDefId, Map<String, Object> variables);

    /**
     * 审批
     * @author qhq
     */
    AjaxResult approval(FlowTaskVo task);

    /**
     * 驳回任务到提交人
     * @author qhq
     * @param flowTaskVo
     * @return
     */
    AjaxResult taskRejectToSubmit(FlowTaskVo flowTaskVo);

    /**
     * 根据businessKey获取任务
     * @author qhq
     * @param businessKey
     * @return
     */
    List<FlowTaskDto> findTask(FlowTaskVo task);

    /**
     * 查询用户发起的流程
     * @author qhq
     */
    AjaxResult getMyProcess(FlowProcessRequest request);

    /**
     * 查询用户审批的任务
     * @author qhq
     */
    AjaxResult getMyApprovalTask(FlowProcessRequest request);

    /**
     * 获取当前可退回节点
     * @author qhq
     * @param task
     * @return
     */
    AjaxResult getReturnList(FlowTaskVo task);

    /**
     * 退回节点
     * @author qhq
     * @param task
     * @return
     */
    AjaxResult returnNode(FlowTaskVo task);

    public AjaxResult getNextFlowNode (FlowTaskVo flowTaskVo);

    AjaxResult getUserTaskList(FlowTaskVo task);

    AjaxResult getProcessAllUserTask(String definitionId);

    AjaxResult getProdefList(Map<String,String> map);

    /**
     * 删除流程
     * @param instanceId 流程ID
     * @return
     */
    AjaxResult deleteProcessById(Map<String, Object> variables);

    public AjaxResult addTaskAssignee(Map<String, Object> instance);

    AjaxResult removeProcess(FlowTaskVo task);

}
