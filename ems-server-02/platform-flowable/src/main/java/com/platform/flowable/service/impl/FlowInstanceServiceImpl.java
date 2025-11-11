package com.platform.flowable.service.impl;

import com.platform.common.exception.base.BaseException;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.factory.FlowServiceFactory;
import com.platform.flowable.response.ProcessInstanceResponse;
import com.platform.flowable.service.IFlowInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>工作流流程实例管理<p>
 *
 * @author qhq
 */
@Service
@Slf4j
public class FlowInstanceServiceImpl extends FlowServiceFactory implements IFlowInstanceService {

	private static String BG = "_BG";

	@Override
	public List<Task> queryListByInstanceId (String instanceId) {
		List<Task> list = taskService.createTaskQuery().processInstanceId(instanceId).active().list();
		return list;
	}

	/**
	 * 结束流程实例
	 *
	 * @param vo
	 */
	@Override
	public void stopProcessInstance (FlowTaskVo vo) {
		String taskId = vo.getTaskId();

	}

	/**
	 * 激活或挂起流程实例
	 *
	 * @param state      状态
	 * @param instanceId 流程实例ID
	 */
	@Override
	public void updateState (Integer state, String instanceId) {

		// 激活
		if (state == 1) {
			runtimeService.activateProcessInstanceById(instanceId);
		}
		// 挂起
		if (state == 2) {
			runtimeService.suspendProcessInstanceById(instanceId);
		}
	}

	/**
	 * 删除流程实例ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete (Map<String, Object> variables) {
		String instanceId = "instanceId";
		String businessKey = "businesskey";
		String deleteReason = "deleteReason";
		// 查询历史数据
		HistoricProcessInstance historicProcessInstance = null;
		if (variables.get(businessKey).toString() == null) {
			historicProcessInstance = getHistoricProcessInstanceById(variables.get(instanceId).toString());
		} else {
			historicProcessInstance = getHistoricProcessInstanceByKey(variables.get(businessKey).toString());
		}
		if (historicProcessInstance.getEndTime() != null) {
			historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
			return;
		}
		// 删除流程实例
		runtimeService.deleteProcessInstance(historicProcessInstance.getId(), variables.get(deleteReason) == null ? null : variables.get(deleteReason).toString());
		// 删除历史流程实例
		historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());

	}

	/**
	 * 根据实例ID查询历史实例数据
	 *
	 * @param processInstanceId
	 * @return
	 */
	@Override
	public HistoricProcessInstance getHistoricProcessInstanceById (String processInstanceId) {
		HistoricProcessInstance historicProcessInstance =
				historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if (Objects.isNull(historicProcessInstance)) {
			throw new FlowableObjectNotFoundException("流程实例不存在: " + processInstanceId);
		}
		return historicProcessInstance;
	}

	/**
	 * 根据实例KEY查询历史实例数据
	 *
	 * @param businessKey
	 * @return
	 */
	public HistoricProcessInstance getHistoricProcessInstanceByKey (String businessKey) {
		HistoricProcessInstance historicProcessInstance =
				historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).singleResult();
		if (Objects.isNull(historicProcessInstance)) {
			throw new FlowableObjectNotFoundException("流程KEY不存在: " + businessKey);
		}
		return historicProcessInstance;
	}

	/**
	 * 根据流程定义ID启动流程实例
	 *
	 * @param procDefId 流程定义Id
	 * @param variables 流程变量
	 * @return
	 */
	@Override
	public ProcessInstanceResponse startProcessInstanceById (String procDefId, Map<String, Object> variables) {
		try {
			variables.put("startUserId", variables.get("startUserId").toString());
			identityService.setAuthenticatedUserId(variables.get("startUserId").toString());
			List<HistoricProcessInstance> hi = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(variables.get("businesskey").toString()).list();
			if (hi.size() > 0) {
				if (variables.get("formType").toString().contains(BG)) {
					hi.forEach(i -> {
						try {
							runtimeService.deleteProcessInstance(i.getId(), "");
						} catch (Exception ignored) {

						} finally {
							historyService.deleteHistoricProcessInstance(i.getId());
						}
					});
				} else {
					throw new BaseException("系统中存在相同单据编号提交的单据，请检查！");
				}
			}
			ProcessInstance instance = runtimeService.startProcessInstanceById(procDefId, variables.get("businesskey").toString(), variables);
			List<Task> taskList = taskService.createTaskQuery().processInstanceId(instance.getId()).list();
			if (taskList == null) {
				//有可能没及时创建好，线程等待
				Thread.sleep(500);
				taskList = taskService.createTaskQuery().processInstanceId(instance.getId()).list();
			}
			List<String> approvalNodes = new ArrayList<String>();
			List<String> approvalIds = new ArrayList<String>();
			for (Task task : taskList) {
				approvalIds.add(task.getAssignee());
				if (!approvalNodes.contains(task.getName())) {
					approvalNodes.add(task.getName());
				}
			}
			ProcessInstanceResponse response = new ProcessInstanceResponse();
			response.setBusinessKey(instance.getBusinessKey());
			response.setName(instance.getName());
			response.setProcessDefinitionId(instance.getProcessDefinitionId());
			response.setProcessInstanceId(instance.getProcessInstanceId());
			response.setStartUserId(instance.getStartUserId());
			response.setApprovalIds(approvalIds);
			response.setApprovalNodes(approvalNodes);
			return response;
			//return AjaxResult.success("流程启动成功", response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException("流程启动错误");
		}
	}
}
