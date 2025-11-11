package com.platform.ems.workflow.service;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.SysFormProcess;
import com.platform.ems.workflow.domain.Submit;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.domain.vo.FlowTaskVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 工作流相关公共接口
 * @author qhq
 */
public interface IWorkFlowService {

	/**
	 * 只提交单据
	 * @param variables 流程启动参数
	 * @return
	 */
	public AjaxResult submitOnly(Map<String, Object> variables);

	/**
	 * 只负责审批
	 * @param taskVo
	 * @return
	 */
	public SysFormProcess approvalOnly(FlowTaskVo taskVo);

	/**
	 * 只负责退回
	 * @param task
	 * @return
	 */
	public SysFormProcess returnOnly(@RequestBody FlowTaskVo task);

	/**
	 * 提交单据
	 * @param variables 流程启动参数
	 * @return
	 */
	public AjaxResult submit(Map<String, Object> variables);

	/**
	 * 提交主表明细行
	 * @param submit
	 * @return
	 */
	public AjaxResult submitByItem(Submit submit);

	/**
	 * 审批单据
	 * @param taskVo
	 * @return
	 */
	public AjaxResult approval(FlowTaskVo taskVo);

	/**
	 * 批量审批
	 * @return
	 */
	public AjaxResult approvalList(Submit submit);

	/**
	 * 变更提交
	 * @param submit
	 * @return
	 */
	public AjaxResult change(Submit submit);

	/**
	 * 变更审批
	 * @param submit
	 * @return
	 */
	public AjaxResult changeAapproval(Submit submit);

	/**
	 * 变更驳回
	 * @param taskVo
	 * @return
	 */
	public AjaxResult changeReject(FlowTaskVo taskVo);

	/**
	 * 驳回到提交人
	 * @author qhq
	 * @param taskVo
	 * @return
	 */
	public AjaxResult taskRejectToSubmit(FlowTaskVo taskVo);


	public AjaxResult taskRejectListToSubmit(FlowTaskVo taskVo);

	/**
	 * 获取用户发起的流程情况
	 * 传参：
	 * Finished 是否为结束流程 true  false  all
	 * FormType 单据类型
	 * PageNum
	 * PageSize
	 * userId
	 * @author qhq
	 * @param request
	 * @return
	 */
	public AjaxResult getMyProcess(FlowProcessRequest request);

	/**
	 * 获取用户审批的任务情况
	 * 传参：
	 * FormType 单据类型
	 * Finished 任务是否完成 true  false  all
	 * userId
	 * PageNum
	 * PageSize
	 * @author qhq
	 * @param request
	 * @return
	 */
	public AjaxResult getMyApprovalTask(FlowProcessRequest request);

	/**
	 * 获取流程可退回节点
	 * @param taskVo
	 * @author qhq
	 * @return
	 */
	public AjaxResult getReturnList(FlowTaskVo taskVo);

	/**
	 * 退回节点
	 * @param task
	 * @return
	 */
	public AjaxResult returnNode(@RequestBody FlowTaskVo task);

	/**
	 * 获取流程定义列表
	 * 必传：pageNum、pageSize
	 * 选填：key
	 * @param map
	 * @return
	 */
	public AjaxResult getProdefList(Map<String,String> map);

	public AjaxResult addApproval(FlowTaskVo taskVo);

	public AjaxResult removeProcess(FlowTaskVo taskVo);

}
