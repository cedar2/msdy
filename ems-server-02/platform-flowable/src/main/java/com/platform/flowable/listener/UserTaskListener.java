package com.platform.flowable.listener;

import com.platform.common.security.utils.SpringBeanUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.service.delegate.DelegateTask;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Xuan xuan
 * @date 2021/4/20
 */
public class UserTaskListener implements TaskListener{

    protected TaskService taskService;
    @Resource
    protected HistoryService historyService;
    @Resource
    protected RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        if(historyService==null){
            historyService= SpringBeanUtil.getBean(HistoryService.class);
        }
        if(runtimeService==null){
            runtimeService=SpringBeanUtil.getBean(RuntimeService.class);
        }
        HistoricProcessInstance history = historyService.createHistoricProcessInstanceQuery().includeProcessVariables().processInstanceId(delegateTask.getProcessInstanceId()).singleResult();
        Map<String,Object> map=history.getProcessVariables();
    }

}
