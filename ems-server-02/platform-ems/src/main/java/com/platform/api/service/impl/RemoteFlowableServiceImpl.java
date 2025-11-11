package com.platform.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.platform.api.service.RemoteFlowableService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.security.utils.SpringBeanUtil;
import com.platform.ems.util.CommonUtil;
import com.platform.flowable.controller.FlowDefinitionController;
import com.platform.flowable.controller.FlowInstanceController;
import com.platform.flowable.controller.FlowTaskController;
import com.platform.flowable.domain.dto.FlowTaskDto;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.response.ProcessInstanceResponse;
import com.platform.flowable.service.IFlowInstanceService;
import com.platform.flowable.service.IFlowTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author xfzz
 */
@Service
@SuppressWarnings("all")
public class RemoteFlowableServiceImpl implements RemoteFlowableService {

    @Autowired
    private FlowInstanceController flowInstanceController;
    @Autowired
    private FlowTaskController flowTaskController;
    @Autowired
    private FlowDefinitionController flowDefinitionController;
    @Autowired
    private IFlowInstanceService flowInstanceService;
    @Autowired
    private IFlowTaskService flowTaskService;

    @Override
    public ProcessInstanceResponse start(String procDefId, Map<String, Object> variables) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowInstanceController = context.getBean(FlowInstanceController.class);
        return flowInstanceService.startProcessInstanceById(procDefId, variables);
    }

    @Override
    public AjaxResult approval(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.complete(task);
    }

    @Override
    public AjaxResult taskRejectToSubmit(FlowTaskVo flowTaskVo) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.taskRejectToSubmit(flowTaskVo);
    }

    @Override
    public List<FlowTaskDto> findTask(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        List<FlowTaskDto> ajaxResult = flowTaskService.getTaskByBusinessKey(task);
        return ajaxResult;
    }

    @Override
    public AjaxResult getMyProcess(FlowProcessRequest request) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.myProcess(request);
    }

    @Override
    public AjaxResult getMyApprovalTask(FlowProcessRequest request) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.myApprovalTask(request);
    }

    @Override
    public AjaxResult getReturnList(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.findReturnTaskList(task);
    }

    @Override
    public AjaxResult returnNode(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.taskReturn(task);
    }

    @Override
    public AjaxResult getNextFlowNode(FlowTaskVo flowTaskVo) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.getNextFlowNode(flowTaskVo);
    }

    @Override
    public AjaxResult getUserTaskList(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.getUserTaskList(task);
    }

    @Override
    public AjaxResult getProcessAllUserTask(String definitionId) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowDefinitionController = context.getBean(FlowDefinitionController.class);
        return AjaxResult.success(flowDefinitionController.getProcessAllUserTask(definitionId));
    }

    @Override
    public AjaxResult getProdefList(Map<String, String> map) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowDefinitionController = context.getBean(FlowDefinitionController.class);
        return flowDefinitionController.list(map);
    }

    @Override
    public AjaxResult deleteProcessById(Map<String, Object> variables) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowInstanceController = context.getBean(FlowInstanceController.class);
        return flowInstanceController.delete(variables);
    }

    @Override
    public AjaxResult addTaskAssignee(Map<String, Object> instance) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        flowTaskController.addTaskAssignee(instance);
        return AjaxResult.success();
    }

    @Override
    public AjaxResult removeProcess(FlowTaskVo task) {
        ApplicationContext context = SpringBeanUtil.getApplicationContext();
        flowTaskController = context.getBean(FlowTaskController.class);
        return flowTaskController.removeProcess(task);
    }

}
