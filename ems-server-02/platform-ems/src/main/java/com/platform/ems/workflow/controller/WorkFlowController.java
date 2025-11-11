package com.platform.ems.workflow.controller;

import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureOutsourceSettle;
import com.platform.ems.enums.FormType;
import com.platform.ems.service.IManManufactureOutsourceSettleService;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.impl.WorkFlowServiceImpl;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.domain.vo.FlowTaskVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author qhq
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/workflow")
@Api(tags = "工作流相关公共接口")
public class WorkFlowController {

    static String SUBMIT_USER = "2";

    @Autowired
    WorkFlowServiceImpl workflowService;
    @Autowired
    IManManufactureOutsourceSettleService outsourceSettleService;

    /**
     * 提交单据
     *
     * @param variables 流程启动参数
     */
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody Map<String, Object> variables) {
        if (variables != null && FormType.OutsourceBill.getCode().equals(variables.get("formType")) && variables.get("formId") != null) {
            ManManufactureOutsourceSettle settle = new ManManufactureOutsourceSettle();
            settle.setManufactureOutsourceSettleSidList(new Long[]{Long.valueOf((String) variables.get("formId"))})
                    .setBusinessType(BusinessType.SUBMIT.getValue());
            return AjaxResult.success(outsourceSettleService.approval(settle));
        }
        return workflowService.submit(variables);
    }

    /**
     * 提交单据
     * 给不需要经过登录的权限
     *
     * @param variables 流程启动参数
     */
    @PostMapping("/submitByOut")
    public AjaxResult submitByOut(@RequestBody Map<String, Object> variables) {
        String formType = variables.get("formType").toString();
        // 景盛管理员的userId
        variables.put("startUserId", "3");
        FormType type = FormType.valueOf(formType);
        if ("1".equals(type.getStatus())) {
            return workflowService.submit(variables);
        }
        return AjaxResult.success();
    }

    /**
     * 提交单据明细
     *
     * @param submit
     */
    @PostMapping("/submitByItem")
    public AjaxResult submitByItem(@RequestBody Submit submit) {
        return workflowService.submitByItem(submit);
    }

    /**
     * 审批单据
     *
     * @param taskVo
     */
    @PostMapping("/approval")
    public AjaxResult approval(@RequestBody FlowTaskVo taskVo) {
        if (taskVo != null && FormType.OutsourceBill.getCode().equals(taskVo.getFormType())) {
            ManManufactureOutsourceSettle settle = new ManManufactureOutsourceSettle();
            settle.setManufactureOutsourceSettleSidList(new Long[]{Long.valueOf(String.valueOf(taskVo.getFormId()))})
                    .setBusinessType(BusinessType.APPROVED.getValue()).setComment(taskVo.getComment());
            return AjaxResult.success(outsourceSettleService.approval(settle));
        }
        return workflowService.approval(taskVo);
    }

    /**
     * 批量驳回
     *
     * @param submit
     */
    @PostMapping("/approvalList")
    public AjaxResult approvalList(@RequestBody Submit submit) {
        return workflowService.approvalList(submit);
    }

    /**
     * 变更单据提交
     */
    @PostMapping("/change")
    public AjaxResult change(@RequestBody Submit submit) {
        return workflowService.change(submit);
    }

    /**
     * 变更审批通过
     */
    @PostMapping("/changeApproval")
    public AjaxResult changeApproval(@RequestBody Submit submit) {
        return workflowService.changeAapproval(submit);
    }

    @PostMapping("/changeReject")
    public AjaxResult changeReject(@RequestBody FlowTaskVo taskVo) {
        return workflowService.changeReject(taskVo);
    }

    /**
     * 驳回到提交人
     *
     * @param taskVo
     * @author qhq
     */
    @PostMapping("/taskRejectToSubmit")
    public AjaxResult taskRejectToSubmit(@RequestBody FlowTaskVo taskVo) {
        return workflowService.taskRejectToSubmit(taskVo);
    }

    /**
     * 批量驳回到提交人
     *
     * @param taskVo
     */
    @PostMapping("/taskRejectList")
    public AjaxResult taskRejectListToSubmit(@RequestBody FlowTaskVo taskVo) {
        return workflowService.taskRejectListToSubmit(taskVo);
    }

    /**
     * 获取流程可退回节点
     *
     * @param taskVo
     * @author qhq
     */
    @PostMapping("/getReturnList")
    public AjaxResult getReturnList(@RequestBody FlowTaskVo taskVo) {
        return workflowService.getReturnList(taskVo);
    }


    /**
     * 退回节点
     *
     * @param taskVo
     * @author qhq
     */
    @PostMapping("/returnNode")
    public AjaxResult returnNode(@RequestBody FlowTaskVo taskVo) {
        if (SUBMIT_USER.equals(taskVo.getTargetKey())) {
            return workflowService.taskRejectToSubmit(taskVo);
        }
        return workflowService.returnNode(taskVo);
    }

    @PostMapping("/addApproval")
    public AjaxResult addApproval(@RequestBody FlowTaskVo taskVo) {
        return workflowService.addApproval(taskVo);
    }

    /**
     * 获取用户发起的流程情况
     * 传参：
     * Finished 是否为结束流程 true  false  all
     * FormType 单据类型
     * PageNum
     * PageSize
     * userId
     *
     * @param request
     * @author qhq
     */
    @PostMapping("/getMyProcess")
    public AjaxResult getMyProcess(@RequestBody FlowProcessRequest request) {
        return workflowService.getMyProcess(request);
    }

    /**
     * 获取用户审批的任务情况
     * 传参：
     * FormType 单据类型
     * Finished 任务是否完成 true  false  all
     * userId
     * PageNum
     * PageSize
     *
     * @param request
     * @author qhq
     */
    @PostMapping("/getMyApprovalTask")
    public AjaxResult myApprovalTask(@RequestBody FlowProcessRequest request) {
        return workflowService.getMyApprovalTask(request);
    }

    /**
     * 获取流程定义列表
     * 必传：pageNum、pageSize
     * 选填：key
     *
     * @param map
     */
    @SuppressWarnings("SpellCheckingInspection")
    @PostMapping("/getProdefList")
    public AjaxResult getProdefList(@RequestBody Map<String, String> map) {
        return workflowService.getProdefList(map);
    }

    @PostMapping("/removeProcess")
    public AjaxResult removeProcess(@RequestBody FlowTaskVo taskVo) {
        return workflowService.removeProcess(taskVo);
    }
}
