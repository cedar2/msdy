package com.platform.flowable.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.stereotype.Service;

import com.platform.flowable.factory.FlowServiceFactory;
import com.platform.flowable.service.IFlowBpmnService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FlowBpmnServiceImpl extends FlowServiceFactory implements IFlowBpmnService{

	public InputStream readImage(String businesskey,String definitionKey) {
		List<ProcessInstance>  list = runtimeService.createProcessInstanceQuery()
    			.processInstanceBusinessKey(businesskey, definitionKey)
    			.list();
		if(list==null||list.size()==0) {
			HistoricProcessInstance historyPorcess =  historyService.createHistoricProcessInstanceQuery().processDefinitionKey(definitionKey).processInstanceBusinessKey(businesskey).singleResult();
			return readImageByHistory(historyPorcess.getDeploymentId());
		}
		ProcessInstance instance = list.get(0);
		String processDefinitionId = instance.getProcessDefinitionId();
		String processInstanceId = instance.getProcessInstanceId();
		List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

		List<String> activityIds = new ArrayList<>();
	    List<String> flows = new ArrayList<>();
	    //获取流程图
	    for (HistoricActivityInstance hi : highLightedActivitList) {
	        String activityType = hi.getActivityType();
	        if (activityType.equals("sequenceFlow") || activityType.equals("exclusiveGateway")) {
	            flows.add(hi.getActivityId());
	        } else if (activityType.equals("userTask") || activityType.equals("startEvent")) {
	            activityIds.add(hi.getActivityId());
	        }
	    }
	    DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
	    return diagramGenerator.generateDiagram(
                bpmnModel,
                "png",
                activityIds,
                flows,
                "宋体",
                "宋体",
                "宋体",
                null,
                1.0,
                false);

	}

	/**
	 * 读取流程图-基本
	 * @param deployId
	 * @return
	 */
	public InputStream readImageByHistory(String deployId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        //获得图片流
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //输出为图片
        return diagramGenerator.generateDiagram(
                bpmnModel,
                "png",
                Collections.emptyList(),
                Collections.emptyList(),
                "宋体",
                "宋体",
                "宋体",
                null,
                1.0,
                false);
    }


}
