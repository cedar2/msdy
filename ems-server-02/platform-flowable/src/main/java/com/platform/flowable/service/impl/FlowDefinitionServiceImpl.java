package com.platform.flowable.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.core.domain.AjaxResult;
import com.platform.system.domain.dto.FlowProcDefDto;
import com.platform.flowable.factory.FlowServiceFactory;
import com.platform.flowable.flow.FlowableUtils;
import com.platform.flowable.service.IFlowDefinitionService;
import com.platform.flowable.service.ISysDeployFormService;
import com.platform.system.domain.SysDeployForm;
import com.platform.system.domain.SysProcessTaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserClientId;

/**
 * 流程定义
 *
 * @author c
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class FlowDefinitionServiceImpl extends FlowServiceFactory implements IFlowDefinitionService {

    @Resource
    private ISysDeployFormService sysDeployFormService;
    @Resource
    private ISysDeployFormService sysInstanceFormService;

    private static final String BPMN_FILE_SUFFIX = ".bpmn";

    @Override
    public boolean exist(String processDefinitionKey) {
        ProcessDefinitionQuery processDefinitionQuery
                = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey);
        long count = processDefinitionQuery.count();
        return count > 0 ? true : false;
    }


    /**
     * 流程定义列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return 流程定义分页列表数据
     */
    @Override
    public Page<FlowProcDefDto> list(Map<String, String> map) {
        String clientId = getLoginUserClientId();

        String key = map.get("key");
        Integer pageNum = Integer.valueOf(map.getOrDefault("pageNum", "1"));
        Integer pageSize = Integer.valueOf(map.getOrDefault("pageSize", "20"));
        String name = map.get("name");

        // 查询流程定义列表
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                                                                         .latestVersion()
                                                                         .orderByProcessDefinitionKey()
                                                                         .asc();
        Page<FlowProcDefDto> page = new Page<>(pageNum, pageSize, processDefinitionQuery.count());
        if (Objects.nonNull(key)) {
            processDefinitionQuery.processDefinitionKey(key);
        }
        if (name != null) {
            processDefinitionQuery.processDefinitionNameLike("%" + name + "%");
        }

        // 判断租户，如果是管理员，不加租户条件
        if (!clientId.equals("10000")) {
            processDefinitionQuery.processDefinitionTenantId(clientId);
        }

        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(pageNum - 1, pageSize);

        List<FlowProcDefDto> dataList = new ArrayList<>();

        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();

            // 根据流程实例查流程定义
            Deployment deployment = repositoryService.createDeploymentQuery()
                                                     .deploymentId(deploymentId)
                                                     .singleResult();

            FlowProcDefDto reProcDef = new FlowProcDefDto();
            BeanUtils.copyProperties(processDefinition, reProcDef);
            // 流程定义时间
            reProcDef.setDeploymentTime(deployment.getDeploymentTime());
            reProcDef.setTenant_id(deployment.getTenantId());
            dataList.add(reProcDef);
        }

        page.setRecords(dataList);
        return page;
    }

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     * @return
     */
    @Override
    public AjaxResult importFile(String name, String category, InputStream in) {
        Deployment deploy = repositoryService.createDeployment()
                                             .addInputStream(name + BPMN_FILE_SUFFIX, in)
                                             .name(name)
                                             .category(category)
                                             .tenantId(getLoginUserClientId())
                                             .deploy();
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
        repositoryService.setProcessDefinitionCategory(definition.getId(), category);
        SysDeployForm sysDeployForm = new SysDeployForm();
        sysDeployForm.setKey(definition.getKey());
        sysDeployForm.setProcessDefintionId(definition.getId());
        sysDeployForm.setName(definition.getName());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definition.getId());
        List<Process> processes = bpmnModel.getProcesses();
        Collection<FlowElement> flowElements = processes.get(0).getFlowElements();
        HashMap<String, String> hashMap = new HashMap<>();
        flowElements.forEach(li -> {
            if (li.getName() != null) {
                hashMap.put(li.getName(), li.getId());
            }
        });
        List<SysProcessTaskConfig> sysProcessTaskConfigs = new ArrayList<>();
        hashMap.keySet().forEach(li -> {
            SysProcessTaskConfig sysProcessTaskConfig = new SysProcessTaskConfig();
            sysProcessTaskConfig.setProcessKey(definition.getKey())
                                .setTaskId(hashMap.get(li))
                                .setTaskName(li)
                                .setPropertiesId(3L);
            sysProcessTaskConfigs.add(sysProcessTaskConfig);
        });
        sysDeployForm.setTaskConfigList(sysProcessTaskConfigs);
        sysDeployForm.setClientId(getLoginUserClientId());
        return AjaxResult.success(sysDeployFormService.insertSysDeployForm(sysDeployForm));
    }

    /**
     * 读取xml
     *
     * @param deployId
     * @return
     */
    @Override
    public AjaxResult readXml(String deployId) throws IOException {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(definition.getDeploymentId(),
                                                                        definition.getResourceName());
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        return AjaxResult.success("", result);
    }

    /**
     * 读取xml
     *
     * @param deployId
     * @return
     */
    @Override
    public InputStream readImage(String deployId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 获得图片流
        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        // 输出为图片
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

    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    @Override
    public void updateState(Integer state, String deployId) {
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().deploymentId(deployId).singleResult();
        // 激活
        if (state == 1) {
            repositoryService.activateProcessDefinitionById(procDef.getId(), true, null);
        }
        // 挂起
        if (state == 2) {
            repositoryService.suspendProcessDefinitionById(procDef.getId(), true, null);
        }
    }


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    @Override
    public void delete(String deployId) {
        // true 允许级联删除 ,不设置会导致数据库外键关联异常
        repositoryService.deleteDeployment(deployId, true);
    }


    @Override
    public void getProcessDefitionByKey(String defKey, String businessKey) {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                                                   .processInstanceBusinessKey(businessKey, defKey)
                                                   .list();
        System.out.println(list.size());
    }

    @Override
    public List<Map<String, String>> getAllUserTask(String definitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(
                definitionId).singleResult();
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (FlowElement element : allElements) {
            if (element instanceof UserTask) {
                map = new HashMap<>();
                map.put(element.getName(), element.getId());
                mapList.add(map);
            }
        }
        return mapList;
    }
}
