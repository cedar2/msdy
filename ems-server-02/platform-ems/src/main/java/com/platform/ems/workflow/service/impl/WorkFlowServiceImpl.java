package com.platform.ems.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.platform.api.service.RemoteFlowableService;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.PurPurchaseOrderMapper;
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.ems.mapper.SysFormProcessMapper;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.WorkbenchUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.ems.workflow.util.WorkFlowUtil;
import com.platform.flowable.domain.dto.FlowTaskDto;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.response.ProcessInstanceResponse;
import com.platform.flowable.service.IFlowTaskService;
import com.platform.flowable.service.ISysDeployFormService;
import com.platform.system.domain.SysDeployForm;
import com.platform.system.domain.SysProcessTaskConfig;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysProcessTaskConfigMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.workflow.factory.WorkFlowFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("all")
public class WorkFlowServiceImpl extends WorkFlowFactory implements IWorkFlowService {

	@Autowired
	private ISysFormProcessService formProcessService;
	@Autowired
	private ISysDeployFormService sysDeployService;
	@Autowired
	private IFlowTaskService flowTaskService;
	@Autowired
	private RemoteFlowableService flowableService;
	@Autowired
	private ISystemUserService userService;
	@Autowired
	private SysTodoTaskMapper todoTaskMapper;
	@Autowired
	private SysFormProcessMapper sysFormProcessMapper;
	@Autowired
	private SysProcessTaskConfigMapper taskConfigMapper;
	@Autowired
	private PurPurchaseOrderMapper purPurchaseOrderMapper;
	@Autowired
	private SalSalesOrderMapper salSalesOrderMapper;
	@Autowired
	private RemoteMenuService remoteMenuService;

	@Autowired
	private SysDefaultSettingClientMapper defaultSettingClientMapper;

	private static String BG = "_BG";

	/**
	 * 只提交单据
	 * @param variables 流程启动参数
	 * @return
	 */
	@Override
	public AjaxResult submitOnly(Map<String, Object> variables) {
		// 校验
		if (Objects.isNull(variables.get("startUserId"))) {
			throw new BaseException("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(variables.get("formId"))) {
			throw new BaseException("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(variables.get("formType"))) {
			throw new BaseException("未获取到单据类型，请联系管理员！");
		}
		String formId = variables.get("formId").toString();
		String formCode = variables.get("formCode").toString();
		String formType = variables.get("formType").toString();
		String startUserId = variables.get("startUserId").toString();
		String erpCode = variables.get("erpCode") == null ? "" : variables.get("erpCode").toString();
		String dataObject = variables.get("dataObject") == null ? "" : variables.get("dataObject").toString();
		// 是否已提交
		QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("form_id", formId);
		queryWrapper.eq("form_type", formType);
		List<SysFormProcess> fpList = formProcessService.list(queryWrapper);
		if (fpList.size() > 0) {
			throw new BaseException("该单据已提交！");
		}

		// 是否有配置
		SysDeployForm deploy = new SysDeployForm();
		deploy.setKey(formType);
		List<SysDeployForm> depList = sysDeployService.selectSysDeployFormList(deploy);
		if (null != depList && depList.size() > 0) {
			deploy = depList.get(0);
			variables.put("businesskey", formId);
			variables.put("formType", formType);
			variables.put("deleteReason", null);
		}
		else {
			throw new BaseException("未查询到单据关联的流程，请联系管理员检查！");
		}
		if(deploy.getProcessDefintionId()==null){
			throw new BaseException("启动流程失败:审批流未配置，请联系系统管理员！");
		}

		// 启动流程
		ProcessInstanceResponse response =null;
		try{
			response = flowableService.start(deploy.getProcessDefintionId(), variables);
		} catch (BaseException e){
			return AjaxResult.error(e.getDefaultMessage());
		} catch (Exception e){
			throw new BaseException("启动流程失败！");
		}

		// 当前审批人
		List<String> userIdList = new ArrayList<>();
		for (String approvalId : response.getApprovalIds()) {
			userIdList.addAll(Arrays.asList(approvalId.split(",")));
		}

		String[] userIdstring = userIdList.stream().toArray(String[]::new);
		Long[] userIds = new Long[userIdstring.length];
		for (int i = 0; i < userIdstring.length; i++) {
			userIds[i] = Long.parseLong(userIdstring[i]);
		}
		List<SysUser> userList = userService.selectSysUserList(new SysUser().setUserIdList(userIds));

		// 记录关联表
		this.inertFormProcess(formType, Long.parseLong(formId), startUserId, response, userList);

		// 生成待批
		this.insertTodoTask(dataObject, formType, formCode, Long.parseLong(formId), userIds, erpCode);

		return AjaxResult.success("流程启动成功！");
	}

	/**
	 * 记录到关联表
	 * @param formType formCode formId userIds
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	private int inertFormProcess(String formType, Long formId, String startUserId,
								 ProcessInstanceResponse instance, List<SysUser> userList) {
		if (userList == null || userList.size() == 0) {
			return 0;
		}
		SysFormProcess formProcess = new SysFormProcess();
		formProcess.setProcessInstanceId(instance.getProcessInstanceId());
		formProcess.setFormId(formId);
		formProcess.setFormType(formType);
		formProcess.setFormStatus("1");
		formProcess.setCreateById(startUserId);
		// 当前审批节点
		List<String> node = new ArrayList<>();
		for (String approvalNode : instance.getApprovalNodes()) {
			node.addAll(Arrays.asList(approvalNode.split(",")));
		}

		formProcess.setApprovalNode(String.join(",", node));
		// 当前审批人
		List<String> userIdList = new ArrayList<>();
		List<String> nickNameList = new ArrayList<>();
		userList.forEach(user->{
			userIdList.add(user.getUserId().toString());
			nickNameList.add(user.getNickName());
		});
		// id
		String approvalUserId = userIdList.toString();
		approvalUserId = approvalUserId.substring(1, approvalUserId.length() - 1);
		formProcess.setApprovalUserId(approvalUserId);
		// 昵称
		String approvalUserName = nickNameList.toString();
		approvalUserName = approvalUserName.substring(1, approvalUserName.length() - 1);
		formProcess.setApprovalUserName(approvalUserName);
		return formProcessService.insertSysFormProcess(formProcess);
	}

	/**
	 * 提交成功生成待批通知
	 * @param formType formCode formId userIds
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	private void insertTodoTask(String dataObject, String formType, String formCode, Long formId, Long[] userIds, String erpCode) {
		if (userIds == null || userIds.length == 0) {
			return;
		}
		if (dataObject == null || dataObject == "") {
			dataObject = formType;
		}
		// 记录待批
		SysTodoTask todoTask = new SysTodoTask();
		String infoName = WorkFlowUtil.backNameByFormType(dataObject);
		if (infoName == null) { infoName = ""; }
		String erp = erpCode == null ? "" : erpCode;
		todoTask.setTitle(erp + infoName + ":" + formCode + "待审批!");
		todoTask.setDocumentSid(formId);
		todoTask.setDocumentCode(formCode);
		todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DP);
		todoTask.setTableName(WorkFlowUtil.backTableByFormType(dataObject));
		todoTask.setNoticeDate(new Date());
		// 获取菜单id
		SysMenu menu = new SysMenu();
		menu.setMenuName(WorkbenchUtil.backMenuByFormType(dataObject));
		if (menu.getMenuName() != null) {
			menu = remoteMenuService.getInfoByName(menu).getData();
			if (menu != null && menu.getMenuId() != null) {
				todoTask.setMenuId(menu.getMenuId());
			}
		}
		// 写入通知人
		List<SysTodoTask> todoTaskList = new ArrayList<>();
		for (int i = 0; i < userIds.length; i++) {
			SysTodoTask task = new SysTodoTask();
			BeanCopyUtils.copyProperties(todoTask, task);
			task.setUserId(userIds[i]);
			todoTaskList.add(task);
		}
		if (CollectionUtil.isNotEmpty(todoTaskList)) {
			todoTaskMapper.inserts(todoTaskList);
		}
	}

	/**
	 * 审批通过生成待批通知
	 * @param formType formCode formId userIds
	 * @return
	 */
	public void insertApprovalTodoTask(List<FlowTaskDto> taskList, FlowTaskVo taskVo) {
		Long formId = Long.parseLong(String.valueOf(String.valueOf(taskVo.getFormId())));
		String formCode = taskVo.getFormCode();
		String dataObject = taskVo.getDataObject() == null ? taskVo.getFormType() : taskVo.getDataObject();
		for (FlowTaskDto task : taskList) {
			List<SysTodoTask> todoTaskList = new ArrayList<>();
			SysTodoTask todo = new SysTodoTask();
			String erpCode = taskVo.getErpCode() == null ? "" : taskVo.getErpCode();
			String titel = erpCode + WorkFlowUtil.backNameByFormType(dataObject) + ":" + formCode + "待审批!";
			todo.setTitle(titel);
			todo.setTaskCategory(ConstantsEms.TODO_TASK_DP);
			todo.setDocumentCode(formCode);
			todo.setDocumentSid(formId);
			todo.setNoticeDate(new Date());
			todo.setTableName(WorkFlowUtil.backTableByFormType(dataObject));
			// 获取菜单id
			SysMenu menu = new SysMenu();
			menu.setMenuName(WorkbenchUtil.backMenuByFormType(dataObject));
			if (menu.getMenuName() != null) {
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todo.setMenuId(menu.getMenuId());
				}
			}

			List<String> ids = Arrays.asList(task.getAssigneeId().split(","));
			for (String id : ids) {
				SysTodoTask one = new SysTodoTask();
				BeanCopyUtils.copyProperties(todo, one);
				one.setUserId(Long.valueOf(id));
				todoTaskList.add(one);
			}
			todoTaskMapper.inserts(todoTaskList);
		}
	}

	/**
	 * 只负责审批
	 * @param taskVo
	 * @return
	 */
	@Override
	public SysFormProcess approvalOnly(FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			throw new BaseException("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getType())) {
			throw new BaseException("未获取到审批类型，请联系管理员！");
		}
		if (Objects.isNull(String.valueOf(taskVo.getFormId()))) {
			throw new BaseException("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			throw new BaseException("未获取到单据类型，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormCode())) {
			throw new BaseException("未获取到单据编码，请联系管理员！");
		}
		if (taskVo.getDataObject() == null || taskVo.getDataObject() == "") {
			taskVo.setDataObject(taskVo.getFormType());
		}
		String formId = String.valueOf(String.valueOf(taskVo.getFormId()));
		String formType = taskVo.getFormType();
		String formCode = taskVo.getFormCode();
		String userId = taskVo.getUserId();
		String type = taskVo.getType();
		String comment = "";
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			comment = taskVo.getComment();
		}
		List<FlowTaskDto> getName = flowableService.findTask(taskVo);
		String taskName = getName.get(0).getTaskName();
		String remake = null;
		if (StringUtils.isNotEmpty(comment)) {
			remake = taskName + "通过：" + comment;
		} else {
			remake = taskName + "通过";
		}
		AjaxResult approvalRes = flowableService.approval(taskVo);
		if (approvalRes.get("msg").equals("当前用户不是审批人，操作失败！")) {
			throw new BaseException(approvalRes.get("msg").toString());
		}
		// 查询任务状态，回写实例表
		List<FlowTaskDto> taskList = new ArrayList<>();
		try {
			taskList = flowableService.findTask(taskVo);
		} catch (Exception e) {
			if ("任务不存在!".equals(e.getMessage()) && !approvalRes.get("msg").equals("操作成功！")) {
				throw new BaseException("任务不存在!");
			}
		}
		// 记录关联表
		SysFormProcess formProcess = new SysFormProcess();
		formProcess.setFormId(Long.valueOf(formId));
		formProcess.setFormType(formType);
		if (null == taskList || taskList.size() == 0) {
			formProcess.setApprovalNode("流程已完成");
			formProcess.setApprovalUserName("流程已完成");
			formProcess.setFormStatus("2");
		} else {
			setApprovalInfo(formProcess, taskList);
			formProcess.setFormStatus("1");
			insertApprovalTodoTask(taskList, taskVo);
		}
		formProcessService.insertSysFormProcess(formProcess);
		// 审批的操作日志和说明
		formProcess.setRemark(remake);
		return formProcess;
	}

	/**
	 * 只负责退回
	 * @param task
	 * @return
	 */
	@Override
	public SysFormProcess returnOnly(@RequestBody FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			throw new BaseException("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getTargetKey())) {
			throw new BaseException("未获取到任务ID，请联系管理员！");
		}
		if (Objects.isNull(String.valueOf(taskVo.getFormId()))) {
			throw new BaseException("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			throw new BaseException("未获取到单据类型，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormCode())) {
			throw new BaseException("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getComment())) {
			throw new BaseException("未获取到审批意见，请联系管理员！");
		}
		//查询任务状态，回写实例表
		List<FlowTaskDto> taskList2 = flowableService.findTask(taskVo);
		String taskName = taskList2.get(0).getTaskName();
		String remake = null;
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			remake = taskName + "驳回：" + taskVo.getComment();
		} else {
			remake = taskName + "驳回";
		}
		AjaxResult ajaxResult = flowableService.returnNode(taskVo);
		// 操作关联表和生成待办
		SysFormProcess process = new SysFormProcess();
		if (ajaxResult.get("msg").equals("操作成功")) {
			// 待办信息
			SysTodoTask todoTask = new SysTodoTask();
			String dataObject = taskVo.getDataObject() == null ? taskVo.getFormType() : taskVo.getDataObject();
			todoTask.setTableName(WorkFlowUtil.backTableByFormType(dataObject));
			todoTask.setDocumentSid(Long.parseLong(String.valueOf(taskVo.getFormId())));
			todoTask.setDocumentCode(taskVo.getFormCode());
			todoTask.setNoticeDate(new Date());
			// 设置菜单ID(工作台跳转详情)
			SysMenu menu = new SysMenu();
			menu.setMenuName(WorkbenchUtil.backMenuByFormType(dataObject));
			if (menu.getMenuName() != null) {
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todoTask.setMenuId(menu.getMenuId());
				}
			}
			// 获取关联
			process.setFormId(Long.valueOf(String.valueOf(taskVo.getFormId())));
			List<SysFormProcess> list = formProcessService.selectSysFormProcessList(process);
			// 查询是否还有任务存在
			List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
			// 无关联任务
			if (CollectionUtil.isEmpty(taskList)) {
				todoTask.setUserId(Long.valueOf(list.get(0).getCreateById()));
				todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB);
				String erpCode = taskVo.getErpCode() == null ? "" : taskVo.getErpCode();
				String titel = erpCode + WorkFlowUtil.backNameByFormType(dataObject) + ":" + taskVo.getFormCode() + "被驳回!";
				todoTask.setTitle(titel);
				todoTaskMapper.insert(todoTask);
				// 删除关联表
				sysFormProcessMapper.delete(new QueryWrapper<SysFormProcess>()
						.lambda().eq(SysFormProcess::getFormId, Long.parseLong(String.valueOf(taskVo.getFormId()))));
			}
			else {
				// 还有进程任务
				process.setFormStatus("1");
				for (SysFormProcess s : list) {
					setApprovalInfo(s, taskList);
					s.setFormStatus("1");
					sysFormProcessMapper.update(s, new UpdateWrapper<SysFormProcess>().lambda()
							.eq(SysFormProcess::getFormId, String.valueOf(taskVo.getFormId())));
					String approvalUserId = s.getApprovalUserId();
					String[] arrs = approvalUserId.split(",");
					// 待办信息
					todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DP);
					String erpCode = taskVo.getErpCode() == null ? "" : taskVo.getErpCode();
					String titel = erpCode + WorkFlowUtil.backNameByFormType(dataObject) + ":" + taskVo.getFormCode() + "待审批!";
					todoTask.setTitle(titel);
					for (String arr : arrs) {
						//生成代办任务
						todoTask.setUserId(Long.valueOf(arr));
						todoTaskMapper.insert(todoTask);
					}
				}
			}
		}
		process.setRemark(remake);
		return process;
	}

	/**
	 * 提交
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public AjaxResult submit (Map<String, Object> variables) {
		if (Objects.isNull(variables.get("startUserId"))) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(variables.get("formId"))) {
			return AjaxResult.error("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(variables.get("formType"))) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		if(!FormType.Bom.getCode().equals(variables.get("formType"))){
			if (Objects.isNull(variables.get("formCode"))) {
				return AjaxResult.error("未获取到单据编码，请联系管理员！");
			}
			String formCode = variables.get("formCode").toString();
		}
		String formId = variables.get("formId").toString();
		String formType = variables.get("formType").toString();
		String startUserId = variables.get("startUserId").toString();

		// 盘点单/样品盘点
		if (FormType.InventorySheet.getCode().equals(formType)||FormType.YPPD.getCode().equals(formType)) {
			SysDefaultSettingClient setting = defaultSettingClientMapper.selectOne(new UpdateWrapper<SysDefaultSettingClient>()
					.lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
			if (setting != null && !ConstantsEms.YES.equals(setting.getIsPandianApproval())) {
				getIInvInventorySheetService().update(new InvInventorySheet(), new UpdateWrapper<InvInventorySheet>().lambda()
						.eq(InvInventorySheet::getInventorySheetSid, Long.parseLong(formId))
						.set(InvInventorySheet::getHandleStatus, ConstantsEms.CHECK_STATUS));
				// 清待办
				todoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
						.eq(SysTodoTask::getDocumentSid, Long.parseLong(formId)));
				// 操作日志
				MongodbUtil.insertUserLog(Long.valueOf(formId), BusinessType.SUBMIT.getValue(), null, "盘点单", "提交并确认");
				return AjaxResult.success("提交并确认成功！");
			}
		}

		QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<SysFormProcess>();
		queryWrapper.eq("form_id", formId);
		queryWrapper.eq("form_type", formType);
		List<SysFormProcess> fpList = formProcessService.list(queryWrapper);
		if (fpList.size() > 0) {
			return AjaxResult.error("该单据已提交！");
		}
		SysDeployForm deploy = new SysDeployForm();
		deploy.setKey(formType);
		List<SysDeployForm> depList = sysDeployService.selectSysDeployFormList(deploy);
		if (null != depList && depList.size() > 0) {
			deploy = depList.get(0);
			variables.put("businesskey", formId);
			variables.put("formType", formType);
			variables.put("deleteReason", null);
		} else {
			return AjaxResult.error("未查询到单据关联的流程，请联系管理员检查！");
		}
		if(deploy.getProcessDefintionId()==null){
			return AjaxResult.error("启动流程失败:审批流未配置，请联系系统管理员");
		}
		ProcessInstanceResponse result =null;
		try{
			 result = flowableService.start(deploy.getProcessDefintionId(), variables);
		}catch (BaseException e){
			return AjaxResult.error(e.getDefaultMessage());
		}catch (Exception e){
			return AjaxResult.error("启动流程失败:审批流未配置，请联系系统管理员");
		}

		//记录到关联单据表
		List<String> userIds = recordFormProcess(formId, formType, startUserId, result);
		//提交后修改单据状态
		try {
			updateStatus(formType, Long.valueOf(formId), HandleStatus.SUBMIT.getCode(), null,null,null);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			flowableService.deleteProcessById(variables);
			e.printStackTrace();
			return AjaxResult.error(e.getMessage());
		}
		//发起待办推送
		String parentId = variables.get("parentId") == null ? null : variables.get("parentId").toString();
		pushTodoTask(formType, userIds, variables, formId, parentId);
		//插入日志
		List<OperMsg> msgList = new ArrayList<>();
		if (!isExistType(formType)) {
			MongodbUtil.insertUserLog(Long.valueOf(formId), BusinessType.SUBMIT.getValue(), msgList, "提交");
		} else {
			if (FormType.Bom.getCode().equals(formType)) {
				TecBomHead bom = new TecBomHead();
				bom.setMaterialSid(Long.parseLong(formId));
				List<TecBomHead> bomList = getBomService().selectTecBomHeadList(bom);
				for (TecBomHead b : bomList) {
					MongodbUtil.insertUserLog(Long.valueOf(b.getBomSid()), BusinessType.SUBMIT.getValue(), msgList, "提交");
				}
			}
		}
		return AjaxResult.success("流程启动成功！");
	}

	/**
	 * 提交主表明细行
	 *
	 * @param variables
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public AjaxResult submitByItem (Submit submit) {
		if (Objects.isNull(submit.getStartUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		List<FormParameter> parameterList = submit.getFormParameters();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		List<String> formIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
			if (!formIds.contains(p.getFormId())) {
				formIds.add(p.getFormId());
			}
		});
		List<String> errorFormIds = new ArrayList<>();
		for (String formId : formIds) {
			QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<SysFormProcess>();
			queryWrapper.eq("form_id", formId);
			queryWrapper.eq("form_type", submit.getFormType());
			List<SysFormProcess> fpList = formProcessService.list(queryWrapper);
			if (fpList.size() > 0) {
				errorFormIds.add(formId);
			}
		}
		if (errorFormIds != null && 0 < errorFormIds.size()) {
			return AjaxResult.error("单据号：" + errorFormIds.toString() + " 已提交！");
		}
		SysDeployForm deploy = new SysDeployForm();
		deploy.setKey(submit.getFormType());
		List<SysDeployForm> depList = sysDeployService.selectSysDeployFormList(deploy);
		deploy = depList.get(0);
		for (FormParameter parameter : parameterList) {
			Map<String, Object> variables = new HashMap<>();
			if (null != depList && 0 < depList.size()) {
				variables.put("startUserId", submit.getStartUserId());
				variables.put("businesskey", parameter.getFormId());
				variables.put("formType", submit.getFormType());
				variables.put("formCode", parameter.getFormCode());
				variables.put("formId", parameter.getFormId());
				variables.put("deleteReason", null);
				if (Strings.isNotEmpty(submit.getSubmitType())) {
					variables.put("submitType", submit.getSubmitType());
				}
				variables.put("isApproval", parameter.getIsApproval());
			} else {
				return AjaxResult.error("未查询到单据关联的流程，请联系管理员检查！");
			}
			String subValidation = submitValidation(submit.getFormType(), Long.valueOf(parameter.getFormId()));
			if(subValidation!=null){
				return AjaxResult.error(subValidation);
			}
			if(deploy.getProcessDefintionId()==null){
				return AjaxResult.error("启动流程失败:审批流未配置，请联系系统管理员");
			}
			ProcessInstanceResponse instance=null;
			try {
				instance = flowableService.start(deploy.getProcessDefintionId(), variables);
            }catch (BaseException e){
				return AjaxResult.error(e.getDefaultMessage());
			}catch (Exception e){
                return AjaxResult.error("启动流程失败:审批流未配置，请联系系统管理员");
            }
			//提交后修改单据状态
			try {
				String handleStatus=HandleStatus.SUBMIT.getCode();
				if(ConstantsEms.NO.equals(parameter.getIsApproval())){
					handleStatus=ConstantsEms.CHECK_STATUS;
				}
				HashMap<String,String> map = new HashMap<>();
				if(parameter.getIsApproval()!=null){
					map.put("isApproval", parameter.getIsApproval());
				}
				updateStatus(submit.getFormType(), Long.valueOf(parameter.getFormId()), handleStatus, null,null,map);
			} catch (Exception e) {
				flowableService.deleteProcessById(variables);
				e.printStackTrace();
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return AjaxResult.error(e.getMessage());
			}
			if(ConstantsEms.NO.equals(parameter.getIsApproval())){
				SysFormProcess formProcess = new SysFormProcess();
				formProcess.setFormId(Long.valueOf(parameter.getFormId()));
				formProcess.setFormType(submit.getFormType());
				formProcess.setApprovalNode("流程已完成");
				formProcess.setApprovalUserName("流程已完成");
				formProcess.setFormStatus("2");
				formProcessService.insertSysFormProcess(formProcess);
			}else{
				//记录到关联单据表
				List<String> userIds = recordFormProcess(parameter.getFormId(), submit.getFormType(), submit.getStartUserId(), instance);
				//发起待办推送
				pushTodoTask(submit.getFormType(), userIds, variables, parameter.getFormId(), parameter.getParentId());
			}
		}
		//插入日志
		List<OperMsg> msgList = new ArrayList<>();
		if (!isExistType(submit.getFormType())) {
			parentIds.forEach(p -> {
				MongodbUtil.insertUserLog(Long.valueOf(p), BusinessType.SUBMIT.getValue(), msgList, "提交");
			});
		}
		return AjaxResult.success("提交成功！");
	}

	/**
	 * 审批通过
	 */
	@Override
	public AjaxResult approval (FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getType())) {
			return AjaxResult.error("未获取到审批类型，请联系管理员！");
		}
		if (Objects.isNull(String.valueOf(taskVo.getFormId()))) {
			return AjaxResult.error("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormCode())) {
			return AjaxResult.error("未获取到单据编码，请联系管理员！");
		}
		String formId = String.valueOf(taskVo.getFormId());
		String formType = taskVo.getFormType();
		String formCode = taskVo.getFormCode();
		String userId = taskVo.getUserId();
		String type = taskVo.getType();
		String comment = "";
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			comment = taskVo.getComment();
		}
		List<FlowTaskDto> taskNameList = flowableService.findTask(taskVo);
		String taskName = taskNameList.get(0).getTaskName();
		String remake = null;
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			remake = taskName + "通过：" + taskVo.getComment();
		} else {
			remake = taskName + "通过";
		}
		AjaxResult approvalRes = flowableService.approval(taskVo);
		if (approvalRes.get("msg").equals("当前用户不是审批人，操作失败！")) {
			return AjaxResult.error("当前用户不是审批人，操作失败！");
		}
		//查询任务状态，回写实例表
		List<FlowTaskDto> taskList = new ArrayList<>();
		try {
			taskList = flowableService.findTask(taskVo);
		} catch (Exception e) {
			if ("任务不存在!".equals(e.getMessage()) && !approvalRes.get("msg").equals("操作成功！")) {
				return AjaxResult.error("任务不存在!");
			}
		}
		SysFormProcess formProcess = new SysFormProcess();
		formProcess.setFormId(Long.valueOf(formId));
		formProcess.setFormType(formType);
		if (null == taskList || taskList.size() == 0) {
			formProcess.setApprovalNode("流程已完成");
			formProcess.setApprovalUserName("流程已完成");
			formProcess.setFormStatus("2");
			//流程结束后修改单据状态
			updateStatus(formProcess.getFormType(), formProcess.getFormId(), HandleStatus.CONFIRMED.getCode(), comment, null, null);
		} else {
			setApprovalInfo(formProcess, taskList);
			formProcess.setFormStatus("1");
			QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
			todowrapper.eq("document_sid", formId);
			todoTaskMapper.delete(todowrapper);
			SysTodoTask todoTask = new SysTodoTask();
			setTodoInfo(todoTask, taskList, formId, taskVo.getFormCode(), taskVo.getFormType(), formId);
		}
		formProcessService.insertSysFormProcess(formProcess);
		//插入日志
		if (!isExistType(formType)) {
			MongodbUtil.insertApprovalLog(Long.valueOf(formId), BusinessType.APPROVAL.getValue(), remake);
		} else {
			setApprovalLog(formId, formType, remake);
		}
		return AjaxResult.success("审批成功！");
	}

	@Override
	public AjaxResult approvalList (Submit submit) {
		if (Objects.isNull(submit.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(submit.getFormType())) {
			return AjaxResult.error("未获取到审批类型，请联系管理员！");
		}
		List<FormParameter> parameterList = submit.getFormParameters();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
		});
		FlowTaskVo taskVo = new FlowTaskVo();
		taskVo.setUserId(submit.getUserId());
		taskVo.setType("1");
		taskVo.setFormType(submit.getFormType());
		taskVo.setComment(submit.getComment());
		String flag = "";   //流程结束标志(2: 已完成  1：继续)

		for (FormParameter parameter : parameterList) {
			String subValidation = submitValidation(submit.getFormType(),Long.parseLong(parameter.getFormId()));
			if(subValidation!=null){
				return AjaxResult.error(subValidation);
			}
			taskVo.setFormId(Long.parseLong(parameter.getFormId()));
			taskVo.setFormCode(parameter.getFormCode());
			List<FlowTaskDto> taskNameList = flowableService.findTask(taskVo);
			String taskName = taskNameList.get(0).getTaskName();
			String remake = null;
			if (StringUtils.isNotEmpty(taskVo.getComment())) {
				remake = taskName + "通过：" + taskVo.getComment();
			} else {
				remake = taskName + "通过";
			}
			AjaxResult approvalRes = flowableService.approval(taskVo);
			if (approvalRes.get("msg").equals("当前用户不是审批人，操作失败！")) {
				return AjaxResult.error("当前用户不是审批人，操作失败！");
			}
			//查询任务状态，回写实例表
			List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
			SysFormProcess formProcess = new SysFormProcess();
			formProcess.setFormId(Long.valueOf(parameter.getFormId()));
			formProcess.setFormType(taskVo.getFormType());
			QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
			todowrapper.eq("document_item_sid", parameter.getFormId());
			todoTaskMapper.delete(todowrapper);
			//有的明细表sid放在document_sid,有的明细表sid放在document_item_sid,能不能统一一下，到底放哪里？
			QueryWrapper<SysTodoTask> todowrapper2 = new QueryWrapper<>();
			todowrapper2.eq("document_sid", parameter.getFormId());
			todoTaskMapper.delete(todowrapper2);
			if (null == taskList || taskList.size() == 0) {
				formProcess.setApprovalNode("流程已完成");
				formProcess.setApprovalUserName("流程已完成");
				formProcess.setFormStatus("2");
                flag = "2";
				//流程结束后修改单据状态
				updateStatus(formProcess.getFormType(), formProcess.getFormId(), HandleStatus.CONFIRMED.getCode(), remake,null,null);
			} else {
				//流程审批中需要对对应的单据进行修改内容
				updateStatusApproval(formProcess.getFormType(), formProcess.getFormId(), BusinessType.APPROVAL.getValue(), remake,null);
				setApprovalInfo(formProcess, taskList);
				formProcess.setFormStatus("1");
                flag = "1";
				SysTodoTask todoTask = new SysTodoTask();
				setTodoInfo(todoTask, taskList, parameter.getFormId(), taskVo.getFormCode(), taskVo.getFormType(), parameter.getParentId());
			}
			//插入日志
			if (!isExistType(taskVo.getFormType())) {
				MongodbUtil.insertApprovalLog(Long.valueOf(parameter.getFormId()), BusinessType.APPROVAL.getValue(), remake);
			} else {
				setApprovalLog(parameter.getFormId(), submit.getFormType(), remake);
			}
			formProcessService.insertSysFormProcess(formProcess);
		}
		if ("1".equals(flag)){
            return AjaxResult.success("审批成功");
        }else{
            return AjaxResult.success("流程审批完成");
        }
	}

	@Override
	public AjaxResult change (Submit submit) {
		if (StringUtils.isEmpty(submit.getStartUserId())) {
			return AjaxResult.error("未获取到用户信息，请联系管理员！");
		}
		if (StringUtils.isEmpty(submit.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		List<FormParameter> parameterList = submit.getFormParameters();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		List<String> formIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
			if (!formIds.contains(p.getFormId())) {
				formIds.add(p.getFormId());
			}
		});
		for (String formId : formIds) {
			QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<SysFormProcess>();
			queryWrapper.eq("form_id", formId);
			queryWrapper.eq("form_type", formTypeChange(submit.getFormType()));
			List<SysFormProcess> fpList = formProcessService.list(queryWrapper);
			if (fpList.size() > 0) {
				for (SysFormProcess fp : fpList) {
					if (fp.getFormStatus().equals("1")) {
						return AjaxResult.error("该单据正在进行变更处理，请勿重复提交！");
					} else {
						formProcessService.deleteSysFormProcessByIds(Arrays.asList(fp.getId()));
					}
				}
			}
		}
		SysDeployForm deploy = new SysDeployForm();
		deploy.setKey(submit.getFormType());
		List<SysDeployForm> depList = sysDeployService.selectSysDeployFormList(deploy);
		deploy = depList.get(0);
		for (FormParameter parameter : parameterList) {
			Map<String, Object> variables = new HashMap<>();
			if (null != depList && 0 < depList.size()) {
				variables.put("startUserId", submit.getStartUserId());
				variables.put("businesskey", parameter.getFormId());
				variables.put("formType", submit.getFormType());
				variables.put("formCode", parameter.getFormCode());
				variables.put("formId", parameter.getFormId());
				variables.put("deleteReason", null);
			} else {
				return AjaxResult.error("未查询到单据关联的流程，请联系管理员检查！");
			}
			ProcessInstanceResponse instance = null;
			try {
				instance = flowableService.start(deploy.getProcessDefintionId(), variables);
			}catch (BaseException e){
				return AjaxResult.error(e.getDefaultMessage());
			}catch (Exception e){
				return AjaxResult.error("启动流程失败:审批流未配置，请联系系统管理员");
			}

			//记录到关联单据表
			List<String> userIds = recordFormProcess(parameter.getFormId(), formTypeChange(submit.getFormType()), submit.getStartUserId(), instance);
			//提交后修改单据状态
			try {
				updateStatus(formTypeChange(submit.getFormType()), Long.valueOf(parameter.getFormId()), HandleStatus.CHANGEAPPROVAL.getCode(), null,null,null);
			} catch (Exception e) {
				flowableService.deleteProcessById(variables);
				e.printStackTrace();
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return AjaxResult.error(e.getMessage());
			}
			//发起待办推送
			pushTodoTask(formTypeChange(submit.getFormType()), userIds, variables, parameter.getFormId(), parameter.getParentId());
		}
		return AjaxResult.success("提交成功！");
	}

	@Override
	public AjaxResult changeAapproval (Submit submit) {
		if (Objects.isNull(submit.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(submit.getFormType())) {
			return AjaxResult.error("未获取到审批类型，请联系管理员！");
		}
		List<FormParameter> parameterList = submit.getFormParameters();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
		});
		FlowTaskVo taskVo = new FlowTaskVo();
		taskVo.setUserId(submit.getUserId());
		taskVo.setType("1");
		taskVo.setFormType(submit.getFormType());
		taskVo.setComment(submit.getComment());
		for (FormParameter parameter : parameterList) {
			taskVo.setFormId(Long.valueOf(parameter.getFormId()));
			taskVo.setFormCode(parameter.getFormCode());
			List<FlowTaskDto> taskNameList = flowableService.findTask(taskVo);
			String taskName = taskNameList.get(0).getTaskName();
			String remake = null;
			if (StringUtils.isNotEmpty(taskVo.getComment())) {
				remake = taskName + "通过：" + taskVo.getComment();
			} else {
				remake = taskName + "通过";
			}
			AjaxResult approvalRes = flowableService.approval(taskVo);
			if (approvalRes.get("msg").equals("当前用户不是审批人，操作失败！")) {
				return AjaxResult.error("当前用户不是审批人，操作失败！");
			}
			//查询任务状态，回写实例表
			List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
			SysFormProcess formProcess = new SysFormProcess();
			formProcess.setFormId(Long.valueOf(parameter.getFormId()));
			formProcess.setFormType(formTypeChange(submit.getFormType()));
			if (null == taskList || taskList.size() == 0) {
				formProcess.setApprovalNode("流程已完成");
				formProcess.setApprovalUserName("流程已完成");
				formProcess.setFormStatus("2");
				//流程结束后修改单据状态
				updateStatus(formProcess.getFormType(), formProcess.getFormId(), HandleStatus.CONFIRMED.getCode(), submit.getComment(),null,null);
			} else {
				setApprovalInfo(formProcess, taskList);
				formProcess.setFormStatus("1");
				QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
				todowrapper.eq("document_sid", parameter.getFormId());
				todoTaskMapper.delete(todowrapper);
				SysTodoTask todoTask = new SysTodoTask();
				setTodoInfo(todoTask, taskList, parameter.getFormId(), taskVo.getFormCode(), taskVo.getFormType(), parameter.getParentId());
			}
			//插入日志
			if (!isExistType(taskVo.getFormType())) {
				MongodbUtil.insertApprovalLog(Long.valueOf(parameter.getFormId()), BusinessType.APPROVAL.getValue(), remake);
			} else {
				setApprovalLog(parameter.getFormId(), submit.getFormType(), remake);
			}
			formProcessService.insertSysFormProcess(formProcess);
		}
		return AjaxResult.success("审批成功！");
	}

	@Override
	public AjaxResult changeReject (FlowTaskVo taskVo) {
		if (StringUtils.isEmpty(taskVo.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (StringUtils.isEmpty(taskVo.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		List<com.platform.flowable.domain.vo.FormParameter> parameterList = taskVo.getParameterList();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
		});
		for (com.platform.flowable.domain.vo.FormParameter parameter : parameterList) {
			taskVo.setFormId(Long.valueOf(parameter.getFormId()));
			taskVo.setFormCode(parameter.getFormCode());
			if (StringUtils.isEmpty(taskVo.getComment())) {
				taskVo.setComment("批量驳回.");
			}
			//查询任务状态，回写实例表
			List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
			String taskName = taskList.get(0).getTaskName();
			AjaxResult taskResult = flowableService.taskRejectToSubmit(taskVo);
			if (taskResult.get("msg").equals("任务不存在!")) {
				return AjaxResult.error("任务不存在!");
			}
			if (taskResult.get("msg").equals("当前用户不是该任务审批人，操作失败！")) {
				return AjaxResult.error("当前用户不是该任务审批人，操作失败！");
			}
			String remake = null;
			if (StringUtils.isNotEmpty(taskVo.getComment())) {
				remake = taskName + "驳回：" + taskVo.getComment();
			} else {
				remake = taskName + "驳回";
			}
			//修改单据状态为已退回
			updateStatus(taskVo.getFormType(), Long.valueOf(String.valueOf(taskVo.getFormId())), HandleStatus.RETURNED.getCode(), remake,null,null);
			SysFormProcess sfp = new SysFormProcess();
			sfp.setFormId(Long.valueOf(String.valueOf(taskVo.getFormId())));
			List<SysFormProcess> list = formProcessService.selectSysFormProcessList(sfp);
			List<Long> ids = new ArrayList<>();
			for (SysFormProcess s : list) {
				QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
				todowrapper.eq("document_sid", s.getFormId());
				todoTaskMapper.delete(todowrapper);
				QueryWrapper<SysFormProcess> fpqw = new QueryWrapper<>();
				fpqw.eq("form_id", s.getFormId());
				sysFormProcessMapper.delete(fpqw);
			}
			//生成代办任务
			SysTodoTask todoTask = new SysTodoTask();
			todoTask.setUserId(Long.valueOf(list.get(0).getCreateById()));
			todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB);
			todoTask.setTableName(WorkFlowUtil.backNameByFormType(taskVo.getFormType()));
			if(parameter.getParentId()!=null){
				todoTask.setDocumentSid(Long.valueOf(parameter.getParentId()));
			}else{
				todoTask.setDocumentSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
			}
			todoTask.setDocumentCode(taskVo.getFormCode());
			todoTask.setNoticeDate(new Date());
			String titel = WorkFlowUtil.backNameByFormType(taskVo.getFormType()) + ":" + taskVo.getFormCode() + "被驳回!";
			todoTask.setTitle(titel);
			//设置菜单ID(工作台跳转详情)
			Long menuId = WorkbenchUtil.setMenuId(taskVo.getFormType(), String.valueOf(taskVo.getFormId()));
			if (menuId != null) {
				todoTask.setMenuId(menuId);
			}
			if (FormType.PurchaseOrder.getCode().equals(taskVo.getFormType())) {
				PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_sp);
				} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_wl);
				}
			} else if (FormType.SalesOrder.getCode().equals(taskVo.getFormType())) {
				SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_sp);
				} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_wl);
				}
			} else if (FormType.PurchasePrice.getCode().equals(taskVo.getFormType())) {
				todoTask.setDocumentSid(Long.valueOf(parameter.getParentId()));
				todoTask.setDocumentItemSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
			}
			todoTaskMapper.insert(todoTask);
		}
		//插入日志
		if (!isExistType(taskVo.getFormType())) {
			parentIds.forEach(p -> {
				MongodbUtil.insertApprovalLog(Long.valueOf(p), BusinessType.APPROVAL.getValue(), taskVo.getComment());
			});
		}
		return AjaxResult.success("驳回到提交人成功！");
	}

	/**
	 * 驳回到提交人
	 *
	 * @param taskVo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public AjaxResult taskRejectToSubmit (FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			throw new CustomException("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			throw new CustomException("未获取到单据类型，请联系管理员！");
		}
		List<com.platform.flowable.domain.vo.FormParameter> parameterList = taskVo.getParameterList();
		if (parameterList == null || parameterList.size() == 0) {
			throw new CustomException("未获取到单据信息，请联系管理员！");
		}
		parameterList.forEach(li->{
			List<com.platform.flowable.domain.vo.FormParameter> formParameters = new ArrayList<>();
			formParameters.add(li);
			taskVo.setParameterList(formParameters);
			taskReject(taskVo);
		});
		return AjaxResult.success("驳回成功");
	}


	public int  taskReject(FlowTaskVo taskVo){
		List<com.platform.flowable.domain.vo.FormParameter> parameterList = taskVo.getParameterList();
		List<String> parentIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
		});
		taskVo.setFormId(Long.valueOf(taskVo.getParameterList().get(0).getFormId()));
		//查询任务状态，回写实例表
		List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
		AjaxResult taskResult = flowableService.taskRejectToSubmit(taskVo);
		if (taskResult.get("msg").equals("任务不存在!")) {
			throw new CustomException("任务不存在");
		}
		if (taskResult.get("msg").equals("当前用户不是该任务审批人，操作失败！")) {
			throw new CustomException("当前用户不是该任务审批人，操作失败！");
		}
		String taskName = taskList.get(0).getTaskName();
		String remake = null;
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			remake = taskName + "驳回：" + taskVo.getComment();
		} else {
			remake = taskName + "驳回";
		}
		//修改单据状态为已退回
		String taskDefId = taskList.get(0).getProcDefId();
		if (taskDefId.contains(BG)) {
			updateStatus(formTypeChange(taskVo.getFormType()), Long.valueOf(String.valueOf(taskVo.getFormId())), HandleStatus.BG_RETURN.getCode(), remake,null,null);
		} else {
			//获取退回节点名称给业务去判断
			SysProcessTaskConfig config = new SysProcessTaskConfig();
			try {
				config = taskConfigMapper.selectOne(new QueryWrapper<SysProcessTaskConfig>().lambda()
						.eq(SysProcessTaskConfig::getProcessKey,taskVo.getFormType())
						.eq(SysProcessTaskConfig::getTaskId,taskVo.getTargetKey()));
			}catch (Exception e){
				throw new CustomException("流程任务节点配置错误，请联系管理员");
			}
			String approvalNode = null;
			if (config != null){
				approvalNode = config.getTaskName();
			}
			updateStatus(taskVo.getFormType(), Long.valueOf(String.valueOf(taskVo.getFormId())), HandleStatus.RETURNED.getCode(), remake,approvalNode,null);
		}
		SysFormProcess sfp = new SysFormProcess();
		sfp.setFormId(Long.valueOf(String.valueOf(taskVo.getFormId())));
		List<SysFormProcess> list = formProcessService.selectSysFormProcessList(sfp);
		List<Long> ids = new ArrayList<>();
		for (SysFormProcess s : list) {
			QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
			todowrapper.eq("document_sid", s.getFormId());
			todoTaskMapper.delete(todowrapper);
			QueryWrapper<SysFormProcess> fpqw = new QueryWrapper<>();
			fpqw.eq("form_id", s.getFormId());
			sysFormProcessMapper.delete(fpqw);
			//有些单据的formId放的是明细的sid
			//by @chenkw @2022：03：28：14：22：45
			QueryWrapper<SysTodoTask> todowrapper2 = new QueryWrapper<>();
			todowrapper2.eq("document_item_sid", s.getFormId());
			todowrapper2.eq("task_category", ConstantsEms.TODO_TASK_DP);
			todoTaskMapper.delete(todowrapper2);
		}
		//生成代办任务
		for (com.platform.flowable.domain.vo.FormParameter p : parameterList) {
			String parentId = null;
			if (Strings.isEmpty(p.getParentId())) {
				parentId = p.getFormId();
			} else {
				parentId = p.getParentId();
			}
			SysTodoTask todoTask = new SysTodoTask();
			todoTask.setUserId(Long.valueOf(list.get(0).getCreateById()));
			todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB);
			if (taskVo.getFormType().contains(BG)) {
				todoTask.setTableName(formTypeChange(taskVo.getFormType()));
			} else {
				todoTask.setTableName(WorkFlowUtil.backNameByFormType(taskVo.getFormType()));
			}
			todoTask.setDocumentSid(Long.parseLong(parentId));
			todoTask.setDocumentItemSid(Long.valueOf(p.getFormId()));
			todoTask.setDocumentCode(taskVo.getFormCode());
			todoTask.setNoticeDate(new Date());
			String titel = WorkFlowUtil.backNameByFormType(taskVo.getFormType()) + ":" + taskVo.getFormCode() + "被驳回!";
			todoTask.setTitle(titel);
			//设置菜单ID(工作台跳转详情)
			Long menuId = WorkbenchUtil.setMenuId(taskVo.getFormType(), String.valueOf(taskVo.getFormId()));
			if (menuId != null) {
				todoTask.setMenuId(menuId);
			}
			if (FormType.PurchaseOrder.getCode().equals(taskVo.getFormType())) {
				PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_sp);
				} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_wl);
				}
			} else if (FormType.SalesOrder.getCode().equals(taskVo.getFormType())) {
				SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_sp);
				} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_wl);
				}
			}
			todoTaskMapper.insert(todoTask);
		}
		//插入日志
		if (!isExistType(taskVo.getFormType())) {
			for (String parentId : parentIds) {
				MongodbUtil.insertApprovalLog(Long.valueOf(parentId), BusinessType.APPROVAL.getValue(), remake);
			}
		} else {
			if (FormType.Bom.getCode().equals(taskVo.getFormType())) {
				TecBomHead bom = new TecBomHead();
				bom.setMaterialSid(Long.parseLong(String.valueOf(taskVo.getFormId())));
				List<TecBomHead> bomList = getBomService().selectTecBomHeadList(bom);
				for (TecBomHead b : bomList) {
					MongodbUtil.insertApprovalLog(Long.valueOf(b.getBomSid()), BusinessType.LZ.getValue(), remake);
				}
			}
		}
		return 1;
	}

	@Override
	public AjaxResult taskRejectListToSubmit (FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		List<com.platform.flowable.domain.vo.FormParameter> parameterList = taskVo.getParameterList();
		if (parameterList == null || parameterList.size() == 0) {
			return AjaxResult.error("未获取到单据信息，请联系管理员！");
		}
		List<String> parentIds = new ArrayList<>();
		parameterList.forEach(p -> {
			if (!parentIds.contains(p.getParentId())) {
				parentIds.add(p.getParentId());
			}
		});
		for (com.platform.flowable.domain.vo.FormParameter parameter : parameterList) {
			taskVo.setFormId(Long.valueOf(parameter.getFormId()));
			taskVo.setFormCode(parameter.getFormCode());
			if (StringUtils.isEmpty(taskVo.getComment())) {
				taskVo.setComment("批量驳回.");
			}
			//查询任务状态，回写实例表
			List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
			String taskName = taskList.get(0).getTaskName();
			AjaxResult taskResult = flowableService.taskRejectToSubmit(taskVo);
			if (taskResult.get("msg").equals("任务不存在!")) {
				return AjaxResult.error("任务不存在!");
			}
			if (taskResult.get("msg").equals("当前用户不是该任务审批人，操作失败！")) {
				return AjaxResult.error("当前用户不是该任务审批人，操作失败！");
			}
			String remake = null;
			if (StringUtils.isNotEmpty(taskVo.getComment())) {
				remake = taskName + "驳回：" + taskVo.getComment();
			} else {
				remake = taskName + "驳回";
			}
			//修改单据状态为已退回
			updateStatus(taskVo.getFormType(), Long.valueOf(String.valueOf(taskVo.getFormId())), HandleStatus.RETURNED.getCode(), remake,null,null);
			SysFormProcess sfp = new SysFormProcess();
			sfp.setFormId(Long.valueOf(String.valueOf(taskVo.getFormId())));
			List<SysFormProcess> list = formProcessService.selectSysFormProcessList(sfp);
			List<Long> ids = new ArrayList<>();
			for (SysFormProcess s : list) {
				QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
				todowrapper.eq("document_sid", s.getFormId());
				todoTaskMapper.delete(todowrapper);
				QueryWrapper<SysFormProcess> fpqw = new QueryWrapper<>();
				fpqw.eq("form_id", s.getFormId());
				sysFormProcessMapper.delete(fpqw);
			}
			//生成代办任务
			SysTodoTask todoTask = new SysTodoTask();
			todoTask.setUserId(Long.valueOf(list.get(0).getCreateById()));
			todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB);
			todoTask.setTableName(WorkFlowUtil.backNameByFormType(taskVo.getFormType()));
			if(parameter.getParentId()!=null){
				todoTask.setDocumentSid(Long.valueOf(parameter.getParentId()));
			}else{
				todoTask.setDocumentSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
			}
			todoTask.setDocumentCode(taskVo.getFormCode());
			todoTask.setNoticeDate(new Date());
			String titel = WorkFlowUtil.backNameByFormType(taskVo.getFormType()) + ":" + taskVo.getFormCode() + "被驳回!";
			todoTask.setTitle(titel);
			//设置菜单ID(工作台跳转详情)
			Long menuId = WorkbenchUtil.setMenuId(taskVo.getFormType(), String.valueOf(taskVo.getFormId()));
			if (menuId != null) {
				todoTask.setMenuId(menuId);
			}
			if (FormType.ProductProcessStep.getCode().equals((taskVo.getFormType()))) {
				// 获取菜单id
				SysMenu menu = new SysMenu();
				menu.setMenuName(ConstantsEms.TODO_PRO_STEP_INFO_MENU_NAME);
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todoTask.setMenuId(menu.getMenuId());
				}
			} else if (FormType.ProductProcessStepUpdate.getCode().equals(taskVo.getFormType())) {
				// 获取菜单id
				SysMenu menu = new SysMenu();
				menu.setMenuName(ConstantsEms.TODO_UP_PRO_STEP_INFO_MENU_NAME);
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todoTask.setMenuId(menu.getMenuId());
				}
			}
			if (FormType.PurchaseOrder.getCode().equals(taskVo.getFormType())) {
				PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_sp);
				} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.purchase_order_wl);
				}
			} else if (FormType.SalesOrder.getCode().equals(taskVo.getFormType())) {
				SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
				if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_sp);
				} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
					todoTask.setMenuId(ConstantsWorkbench.sale_order_wl);
				}
			} else if (FormType.PurchasePrice.getCode().equals(taskVo.getFormType())) {
				todoTask.setDocumentSid(Long.valueOf(parameter.getParentId()));
				todoTask.setDocumentItemSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
			}
			todoTaskMapper.insert(todoTask);
		}
		//插入日志
		if (!isExistType(taskVo.getFormType())) {
			parentIds.forEach(p -> {
				MongodbUtil.insertApprovalLog(Long.valueOf(p), BusinessType.APPROVAL.getValue(), taskVo.getComment());
			});
		}
		return AjaxResult.success("驳回到提交人成功！");
	}

	@Override
	public AjaxResult getMyProcess (FlowProcessRequest request) {
		return flowableService.getMyProcess(request);
	}

	@Override
	public AjaxResult getMyApprovalTask (FlowProcessRequest request) {
		return flowableService.getMyApprovalTask(request);
	}

	/**
	 * 获取可退回节点
	 *
	 * @param taskVo
	 * @return
	 * @Author qhq
	 */
	@Override
	public AjaxResult getReturnList (FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getTaskName())) {
			return AjaxResult.error("未获取到任务名称，请联系管理员！");
		}
		if (Objects.isNull(String.valueOf(taskVo.getFormId()))) {
			return AjaxResult.error("未获取到单据id，请联系管理员！");
		}

		checkCurrentUserIsAssignee(taskVo);

		SysProcessTaskConfig taskConfig = new SysProcessTaskConfig();
		taskConfig.setTaskName(taskVo.getTaskName());
		taskConfig.setProcessKey(taskVo.getFormType());
		List<SysProcessTaskConfig> configList = taskConfigMapper.selectSysProcessTaskConfigList(taskConfig);
		List<Map<String, String>> mapList = new ArrayList<>();
		Map<String, String> map = new HashMap<>(10);
		if (configList.size() == 0) {
			//驳回到提交人
			map.put("id", "2");
			map.put("name", "驳回到提交人");
			mapList.add(map);
			return AjaxResult.success(mapList);
		}
		for (SysProcessTaskConfig config : configList) {
			if (config.getPropertiesId() == 1) {
				//退回到上一级
				map.put("id", "1");
				map.put("name", "退回到上一级");
				mapList.add(map);
				return AjaxResult.success(mapList);
			} else if (config.getPropertiesId() == 2) {
				//驳回到提交人
				map.put("id", "2");
				map.put("name", "驳回到提交人");
				mapList.add(map);
				return AjaxResult.success(mapList);
			} else if (config.getPropertiesId() == 3) {
				//退回到任意节点
				return flowableService.getReturnList(taskVo);
			}
		}
		return AjaxResult.error("获取退回节点失败！");
	}

	/**
	 * 检查当前用户是不是审批人，不是就抛异常
	 * @throws CheckedException
	 */
	private void checkCurrentUserIsAssignee(FlowTaskVo taskVo) {
		Long userId = ApiThreadLocalUtil.getLoginUserId();
		if (userId == null){
			throw new CheckedException("帐号未登录");
		}

		SysFormProcess sysFormProcess = this.sysFormProcessMapper.selectOne(
				Wrappers.lambdaQuery(SysFormProcess.class)
						.eq(SysFormProcess::getFormId, String.valueOf(taskVo.getFormId()))
		);

		if (sysFormProcess.getProcessInstanceId() == null){
			throw new CheckedException("未查询到单据关联流程实例");
		}
		AjaxResult userAssignee = flowTaskService.isUserAssignee(userId, sysFormProcess.getProcessInstanceId());
		if (500 == Integer.parseInt(userAssignee.get(AjaxResult.CODE_TAG).toString())) {
			throw new CheckedException((String) userAssignee.get(AjaxResult.MSG_TAG));
		}
		if (true != (boolean) userAssignee.get(AjaxResult.DATA_TAG)){
			throw new CheckedException((String) userAssignee.get(AjaxResult.MSG_TAG));
		}
	}


	/**
	 * 退回到指定节点
	 *
	 * @param taskVo
	 * @return
	 * @Author qhq
	 */
	@Override
	public AjaxResult returnNode (FlowTaskVo taskVo) {
		if (Objects.isNull(taskVo.getUserId())) {
			return AjaxResult.error("未获取到当前用户信息，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getTargetKey())) {
			return AjaxResult.error("未获取到任务ID，请联系管理员！");
		}
		if (Objects.isNull(String.valueOf(taskVo.getFormId()))) {
			return AjaxResult.error("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormType())) {
			return AjaxResult.error("未获取到单据类型，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getFormCode())) {
			return AjaxResult.error("未获取到单据编码，请联系管理员！");
		}
		if (Objects.isNull(taskVo.getComment())) {
			return AjaxResult.error("未获取到审批意见，请联系管理员！");
		}
		//查询任务状态，回写实例表
		List<FlowTaskDto> taskList2 = flowableService.findTask(taskVo);
		String taskName = taskList2.get(0).getTaskName();
		String remake = null;
		if (StringUtils.isNotEmpty(taskVo.getComment())) {
			remake = taskName + "驳回：" + taskVo.getComment();
		} else {
			remake = taskName + "驳回";
		}
		AjaxResult ajaxResult = flowableService.returnNode(taskVo);
		SysProcessTaskConfig config = new SysProcessTaskConfig();
		try {
			config = taskConfigMapper.selectOne(new QueryWrapper<SysProcessTaskConfig>().lambda()
					.eq(SysProcessTaskConfig::getProcessKey,taskVo.getFormType())
					.eq(SysProcessTaskConfig::getTaskId,taskVo.getTargetKey()));
		}catch (Exception e){
			throw new CustomException("流程任务节点配置错误，请联系管理员");
		}
		String approvalNode = null;
		if (config != null){
			approvalNode = config.getTaskName();
		}
		updateStatusApproval(taskVo.getFormType(),Long.valueOf(String.valueOf(taskVo.getFormId())),BusinessType.DISAPPROVED.getValue(),remake,approvalNode);
		if (ajaxResult.get("msg").equals("操作成功")) {
			SysFormProcess sfp = new SysFormProcess();
			sfp.setFormId(Long.valueOf(String.valueOf(taskVo.getFormId())));
			List<SysFormProcess> list = formProcessService.selectSysFormProcessList(sfp);
			List<Long> ids = new ArrayList<>();
			for (SysFormProcess s : list) {
				QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
				todowrapper.eq("document_sid", s.getFormId());
				todoTaskMapper.delete(todowrapper);
				//清理formId是作为item_sid存在待办待批中的
				QueryWrapper<SysTodoTask> todowrapper2 = new QueryWrapper<>();
				todowrapper2.eq("document_item_sid", s.getFormId());
				todowrapper2.eq("task_category", ConstantsEms.TODO_TASK_DP);
				todoTaskMapper.delete(todowrapper2);
				QueryWrapper<SysFormProcess> fpqw = new QueryWrapper<>();
				fpqw.eq("form_id", s.getFormId());
				List<FlowTaskDto> taskList = flowableService.findTask(taskVo);
				setApprovalInfo(s, taskList);
				s.setFormStatus("1");
				sysFormProcessMapper.update(s, fpqw);
				String approvalUserId = s.getApprovalUserId();
				String[] arrs = approvalUserId.split(",");
				for (String arr : arrs) {
					//生成代办任务
					SysTodoTask todoTask = new SysTodoTask();
					todoTask.setUserId(Long.valueOf(arr));
					todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DP);
					todoTask.setTableName(WorkFlowUtil.backNameByFormType(taskVo.getFormType()));
					List<com.platform.flowable.domain.vo.FormParameter> parameterList = taskVo.getParameterList();
					if(CollectionUtil.isNotEmpty(parameterList)){
						com.platform.flowable.domain.vo.FormParameter formParameter = parameterList.get(0);
						if(formParameter.getParentId()!=null){
							todoTask.setDocumentSid(Long.valueOf(formParameter.getParentId()));
						}else{
							todoTask.setDocumentSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
						}
					}else{
						todoTask.setDocumentSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
					}
					todoTask.setDocumentCode(taskVo.getFormCode());
					todoTask.setNoticeDate(new Date());
					String titel = WorkFlowUtil.backNameByFormType(taskVo.getFormType()) + ":" + taskVo.getFormCode() + "待审批!";
					todoTask.setTitle(titel);
					//设置菜单ID(工作台跳转详情)
					Long menuId = WorkbenchUtil.setMenuId(taskVo.getFormType(), String.valueOf(taskVo.getFormId()));
					if (menuId != null) {
						todoTask.setMenuId(menuId);
					}
					if (FormType.ProductProcessStep.getCode().equals((taskVo.getFormType()))) {
						// 获取菜单id
						SysMenu menu = new SysMenu();
						menu.setMenuName(ConstantsEms.TODO_PRO_STEP_INFO_MENU_NAME);
						menu = remoteMenuService.getInfoByName(menu).getData();
						if (menu != null && menu.getMenuId() != null) {
							todoTask.setMenuId(menu.getMenuId());
						}
					} else if (FormType.ProductProcessStepUpdate.getCode().equals(taskVo.getFormType())) {
						// 获取菜单id
						SysMenu menu = new SysMenu();
						menu.setMenuName(ConstantsEms.TODO_UP_PRO_STEP_INFO_MENU_NAME);
						menu = remoteMenuService.getInfoByName(menu).getData();
						if (menu != null && menu.getMenuId() != null) {
							todoTask.setMenuId(menu.getMenuId());
						}
					}
					if (FormType.PurchaseOrder.getCode().equals(taskVo.getFormType())) {
						PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
						if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
							todoTask.setMenuId(ConstantsWorkbench.purchase_order_sp);
						} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
							todoTask.setMenuId(ConstantsWorkbench.purchase_order_wl);
						}
					} else if (FormType.SalesOrder.getCode().equals(taskVo.getFormType())) {
						SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(String.valueOf(taskVo.getFormId())));
						if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
							todoTask.setMenuId(ConstantsWorkbench.sale_order_sp);
						} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
							todoTask.setMenuId(ConstantsWorkbench.sale_order_wl);
						}
					} else if (FormType.PurchasePrice.getCode().equals(taskVo.getFormType())) {
						todoTask.setDocumentSid(Long.valueOf(taskVo.getParameterList().get(0).getParentId()));
						todoTask.setDocumentItemSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
					}else if(FormType.SalePrice.getCode().equals(taskVo.getFormType())){
						todoTask.setDocumentSid(Long.valueOf(taskVo.getParameterList().get(0).getParentId()));
						todoTask.setDocumentItemSid(Long.valueOf(String.valueOf(taskVo.getFormId())));
					}
					todoTaskMapper.insert(todoTask);
				}
			}
			if (!isExistType(taskVo.getFormType())) {
				MongodbUtil.insertApprovalLog(Long.valueOf(String.valueOf(taskVo.getFormId())), BusinessType.APPROVAL.getValue(), remake);
			} else {
				setApprovalLog(String.valueOf(taskVo.getFormId()), taskVo.getFormType(), remake);
			}
		}
		return AjaxResult.success("操作成功！");
	}

	/**
	 * 查询流程定义列表
	 *
	 * @param map
	 * @return
	 * @Author qhq
	 */
	@Override
	public AjaxResult getProdefList (Map<String, String> map) {

		return AjaxResult.success(flowableService.getProdefList(map));
	}

	@Override
	public AjaxResult addApproval (FlowTaskVo taskVo) {
		QueryWrapper<SysFormProcess> queryWrapper = new QueryWrapper<SysFormProcess>();
		queryWrapper.eq("form_id", String.valueOf(taskVo.getFormId()));
		queryWrapper.eq("form_type", taskVo.getFormType());
		List<SysFormProcess> fpList = formProcessService.list(queryWrapper);
		SysFormProcess fp = new SysFormProcess();
		if(fpList.size()>0){
			fp = fpList.get(0);
		}else{
			return AjaxResult.error("未查询到单据对应流程信息，委托审批人失败.");
		}
		Map<String, Object> instance = new HashMap<>();
		instance.put("processInstanceId",fp.getProcessInstanceId());
		instance.put("assignee",taskVo.getAssignee());
		flowableService.addTaskAssignee(instance);

		//发起待办推送
		Map<String, Object> variables = new HashMap<>();
		variables.put("businesskey",String.valueOf(taskVo.getFormId()));
		pushTodoTask(taskVo.getFormType(), taskVo.getAssignee(), variables, String.valueOf(taskVo.getFormId()), null);

		//修改关联表审批人字段
		UpdateWrapper<SysFormProcess> fpuw = new UpdateWrapper<>();
		fpuw.eq("form_id",String.valueOf(taskVo.getFormId()))
		.eq("form_type",taskVo.getFormType())
		.set("approval_user_id",fp.getApprovalUserId()+","+taskVo.getAssignee());
		formProcessService.update(fpuw);

		return AjaxResult.success("委托审批人成功！");
	}


	@Override
	public AjaxResult removeProcess(FlowTaskVo taskVo){
		AjaxResult removeInfo = flowableService.removeProcess(taskVo);
		if(!removeInfo.get("msg").equals("操作成功！")){
			return AjaxResult.error(removeInfo.get("msg").toString());
		}
		QueryWrapper<SysTodoTask> todoWrapper = new QueryWrapper<>();
		todoWrapper.eq("document_sid",String.valueOf(taskVo.getFormId()));
		todoTaskMapper.delete(todoWrapper);
		QueryWrapper<SysFormProcess> fpWrapper = new QueryWrapper<>();
		fpWrapper.eq("form_id",String.valueOf(taskVo.getFormId()));
		sysFormProcessMapper.delete(fpWrapper);
		updateStatus(taskVo.getFormType(),Long.parseLong(String.valueOf(taskVo.getFormId())),"1","申请人主动撤销提交",null,null);
		return AjaxResult.success("撤销流程成功！");
	}

	/**
	 * 单据审批中修改单据对应状态,处理业务逻辑
	 * 由于单据众多，所以每种单据都用类型区分开，再添加相应业务逻辑，方便处理
	 *
	 * @param formType 单据类型
	 * @param formSid  单据sids
	 */
	public void updateStatusApproval (String formType, Long formSid, String submitType, String comment, String approvalNode) {
		if (FormType.QuoteBargain.getCode().equals(formType)) {
			PurQuoteBargainItem purQuoteBargainItem = new PurQuoteBargainItem();
			purQuoteBargainItem.setQuoteBargainItemSid(formSid);
			purQuoteBargainItem.setComment(comment);
			//审批中
			if (BusinessType.APPROVAL.getValue().equals(submitType)){
				getPurQuoteBargainService().submit(purQuoteBargainItem);
			}
			//驳回
			else if (BusinessType.DISAPPROVED.getValue().equals(submitType)){
				purQuoteBargainItem.setApprovalNode(approvalNode);
				getPurQuoteBargainService().rejected(purQuoteBargainItem);
			} else {}
		} else if (FormType.OutsourceQuoteBargain.getCode().equals(formType)) {
			PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem = new PurOutsourceQuoteBargainItem();
			purOutsourceQuoteBargainItem.setOutsourceQuoteBargainItemSid(formSid).setComment(comment);
			//审批中
			if (BusinessType.APPROVAL.getValue().equals(submitType)){
				getPurOutsourceQuoteBargainService().submit(purOutsourceQuoteBargainItem);
			}
			//驳回
			else if (BusinessType.DISAPPROVED.getValue().equals(submitType)){
				purOutsourceQuoteBargainItem.setApprovalNode(approvalNode);
				getPurOutsourceQuoteBargainService().rejected(purOutsourceQuoteBargainItem);
			} else {}
		}
	}

	/**
	 * 单据提交/审批后修改单据对应状态,处理业务逻辑
	 * 由于单据众多，所以每种单据都用类型区分开，再添加相应业务逻辑，方便处理
	 *
	 * @chenkw 新增退回节点 approvalNode ，给业务处理逻辑
	 * @param formType 单据类型
	 * @param formSid  单据sids
	 */
	public void updateStatus (String formType, Long formSid, String status, String comment, String approvalNode,HashMap<String,String> map) {
		if (status.equals(HandleStatus.CONFIRMED.getCode())) {
			QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
			todowrapper.eq("document_sid", formSid);
			todoTaskMapper.delete(todowrapper);
		}
		Long[] sids = {formSid};
		if (FormType.PurchaseContract.getCode().equals(formType)) {
			PurPurchaseContract purchase = new PurPurchaseContract();
			purchase.setPurchaseContractSid(formSid);
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				purchase.setHandleStatus(status);
				getPurPurchaseContractService().updateById(purchase);
				//生成财务流水
				getPurPurchaseContractService().advancePayment(getPurPurchaseContractService().selectPurPurchaseContractById(formSid));
			}
			else if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				purchase.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getPurPurchaseContractService().updateById(purchase);
			} else {
				purchase.setHandleStatus(status);
				getPurPurchaseContractService().updateById(purchase);
			}
		} else if (FormType.Bom.getCode().equals(formType)) {
			TecBomHead bom = new TecBomHead();
			bom.setMaterialSid(formSid);
			List<TecBomHead> bomList = getBomService().getListByMaterialSid(formSid);
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				getBomService().insertZipperSku(bomList);
			}
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				status = HandleStatus.CONFIRMED.getCode();
			}
			for (TecBomHead b : bomList) {
				b.setHandleStatus(status);
				getBomService().updateById(b);
			}
		} else if (FormType.OutsourcePurchasePrice.getCode().equals(formType)) {
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				status = HandleStatus.CONFIRMED.getCode();
			}
			PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem = new PurOutsourcePurchasePriceItem();
			purOutsourcePurchasePriceItem.setOutsourcePurchasePriceItemSid(formSid);
			purOutsourcePurchasePriceItem.setHandleStatus(status);
			getPurOutsourcePurchasePriceService().flowHandle(purOutsourcePurchasePriceItem, comment);
		} else if (FormType.PurchasePrice.getCode().equals(formType)) {
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				status = HandleStatus.CONFIRMED.getCode();
			}
			PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
			purPurchasePriceItem.setHandleStatus(status)
					.setPurchasePriceItemSid(formSid);
			if(map==null){
				map=new HashMap<>();
			}
			if(map.get("isApproval")!=null){
				purPurchasePriceItem.setIsApproval(map.get("isApproval"));
			}
			getPurPurchasePriceService().flowHandle(purPurchasePriceItem, comment);
		} else if (FormType.PurchaseOrder.getCode().equals(formType)) {
			PurPurchaseOrder order = new PurPurchaseOrder();
			Long[] sidArray = {formSid};
			order.setPurchaseOrderSids(sidArray);
			order.setHandleStatus(status);
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				getPurPurchaseOrderService().confirm(order);
			} else {
				if(HandleStatus.BG_RETURN.getCode().equals(status)){
					getPurPurchaseOrderService().setValueNull(formSid);
					status=HandleStatus.CONFIRMED.getCode();
				}
				getPurPurchaseOrderService().update(new UpdateWrapper<PurPurchaseOrder>().lambda()
						.in(PurPurchaseOrder::getPurchaseOrderSid, sidArray)
						.set(PurPurchaseOrder::getHandleStatus, status)
				);
			}
		} else if (FormType.PurchaseOrderVendor.getCode().equals(formType)) {//供应商寄售结算单
			PurPurchaseOrder order = new PurPurchaseOrder();
			order.setPurchaseOrderSid(formSid);
			order.setHandleStatus(status);
			getPurPurchaseOrderService().updateById(order);
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				getPurPurchaseOrderService().confirm(order);
			}
		} else if (FormType.GoodReceiptNote.getCode().equals(formType)) {//要货单
			getIInvGoodReceiptNoteService().update(new InvGoodReceiptNote(), new UpdateWrapper<InvGoodReceiptNote>().lambda()
					.eq(InvGoodReceiptNote::getNoteSid, formSid)
					.set(InvGoodReceiptNote::getHandleStatus, status));
		} else if (FormType.GoodIssueNote.getCode().equals(formType)) {//收货单
			if(ConstantsEms.CHECK_STATUS.equals(status)){
                InvGoodIssueNote invGoodIssueNote = new InvGoodIssueNote();
                invGoodIssueNote.setGoodIssueNoteSidList(sids);
				invGoodIssueNote.setHandleStatus(status);
                getIInvGoodIssueNoteService().check(invGoodIssueNote);
            }else{
                getIInvGoodIssueNoteService().update(new InvGoodIssueNote(), new UpdateWrapper<InvGoodIssueNote>().lambda()
                        .eq(InvGoodIssueNote::getNoteSid, formSid)
                        .set(InvGoodIssueNote::getHandleStatus, status));
            }
		} else if (FormType.MaterialRequisition.getCode().equals(formType)) {//领退料
			if(HandleStatus.CONFIRMED.getCode().equals(status)){
				InvMaterialRequisition invMaterialRequisition = new InvMaterialRequisition();
				invMaterialRequisition.setMaterialRequisitionSids(sids);
				getIInvMaterialRequisitionService().confirm(invMaterialRequisition);
			}else{
				getIInvMaterialRequisitionService().update(new InvMaterialRequisition(), new UpdateWrapper<InvMaterialRequisition>().lambda()
						.eq(InvMaterialRequisition::getMaterialRequisitionSid, formSid)
						.set(InvMaterialRequisition::getHandleStatus, status));
			}
		} else if (FormType.InventoryTransfer.getCode().equals(formType)) {//调拨单
			if(ConstantsEms.CHECK_STATUS.equals(status)){
				InvInventoryTransfer invInventoryTransfer = new InvInventoryTransfer();
				invInventoryTransfer.setInventoryTransferSidList(sids);
				invInventoryTransfer.setHandleStatus(status);
				getIInvInventoryTransferService().check(invInventoryTransfer);
			}else{
				getIInvInventoryTransferService().update(new InvInventoryTransfer(), new UpdateWrapper<InvInventoryTransfer>().lambda()
						.eq(InvInventoryTransfer::getInventoryTransferSid, formSid)
						.set(InvInventoryTransfer::getHandleStatus, status));
			}
		} else if (FormType.InventorySheet.getCode().equals(formType)||FormType.YPPD.getCode().equals(formType)) {//盘点单/样品盘点
			getIInvInventorySheetService().update(new InvInventorySheet(), new UpdateWrapper<InvInventorySheet>().lambda()
					.eq(InvInventorySheet::getInventorySheetSid, formSid)
					.set(InvInventorySheet::getHandleStatus, status));
		} else if (FormType.InventoryAdjust.getCode().equals(formType)) {//库存调整单
			getIInvInventoryAdjustService().update(new InvInventoryAdjust(), new UpdateWrapper<InvInventoryAdjust>().lambda()
					.in(InvInventoryAdjust::getInventoryAdjustSid, formSid)
					.set(InvInventoryAdjust::getHandleStatus, status));
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				InvInventoryAdjust invInventoryAdjust = new InvInventoryAdjust();
				invInventoryAdjust.setInventoryAdjustSids(sids);
				invInventoryAdjust.setHandleStatus(status);
				getIInvInventoryAdjustService().confirm(invInventoryAdjust);
			}
		} else if (FormType.CorssColor.getCode().equals(formType)) {//串色串码
			getIInvInventoryAdjustService().update(new InvInventoryAdjust(), new UpdateWrapper<InvInventoryAdjust>().lambda()
					.eq(InvInventoryAdjust::getInventoryAdjustSid, formSid)
					.set(InvInventoryAdjust::getHandleStatus, status));
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				InvInventoryAdjust invInventoryAdjust = new InvInventoryAdjust();
				invInventoryAdjust.setInventoryAdjustSids(sids);
				getIInvInventoryAdjustService().confirm(invInventoryAdjust);
			}
		} else if (FormType.SalePrice.getCode().equals(formType)) {
			SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
			salSalePriceItem.setHandleStatus(status)
					.setSalePriceItemSid(formSid);
			getSalSalePriceService().flowHandle(salSalePriceItem, comment);
		} else if (FormType.SalesOrder.getCode().equals(formType)) {
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				SalSalesOrder salSalesOrder = new SalSalesOrder();
				salSalesOrder.setSalesOrderSids(sids);
				salSalesOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getSalSalesOrderService().confirm(salSalesOrder);
			} else {
				if(HandleStatus.BG_RETURN.getCode().equals(status)){
                    getSalSalesOrderService().setValueNull(formSid);
					status=HandleStatus.CONFIRMED.getCode();
				}
				getSalSalesOrderService().update(new SalSalesOrder(), new UpdateWrapper<SalSalesOrder>()
						.lambda()
						.eq(SalSalesOrder::getSalesOrderSid, formSid)
						.set(SalSalesOrder::getHandleStatus, status));
			}
		} else if (FormType.QuoteBargain.getCode().equals(formType)) {
			PurQuoteBargainItem purQuoteBargainItem = new PurQuoteBargainItem();
			purQuoteBargainItem.setQuoteBargainItemSid(formSid);
			purQuoteBargainItem.setHandleStatus(status);
			purQuoteBargainItem.setComment(comment);
			if (HandleStatus.RETURNED.getCode().equals(status)){
				purQuoteBargainItem.setApprovalNode(approvalNode);
				getPurQuoteBargainService().rejected(purQuoteBargainItem);
			}else {
				getPurQuoteBargainService().submit(purQuoteBargainItem);
			}

		} else if (FormType.OutsourceQuoteBargain.getCode().equals(formType)) {
			PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem = new PurOutsourceQuoteBargainItem();
			purOutsourceQuoteBargainItem.setHandleStatus(status)
					.setOutsourceQuoteBargainItemSid(formSid).setComment(comment);
			if (HandleStatus.RETURNED.getCode().equals(status)){
				purOutsourceQuoteBargainItem.setApprovalNode(approvalNode);
				getPurOutsourceQuoteBargainService().rejected(purOutsourceQuoteBargainItem);
			}else {
				getPurOutsourceQuoteBargainService().submit(purOutsourceQuoteBargainItem);
			}
		} else if (FormType.ProductProcessStep.getCode().equals(formType)) {
			Long[] sidList = new Long[]{formSid};
			PayProductProcessStep payProductProcessStep = new PayProductProcessStep();
			payProductProcessStep.setHandleStatus(status)
					.setProductProcessStepSidList(sidList).setComment(comment);
			if (HandleStatus.RETURNED.getCode().equals(status)){
				payProductProcessStep.setApprovalNode(approvalNode);
				getPayProductProcessStepService().check(payProductProcessStep);
			}else {
				getPayProductProcessStepService().check(payProductProcessStep);
			}
		} else if (FormType.PurchaseRequire.getCode().equals(formType)) {
			ReqPurchaseRequire reqPurchaseRequire = new ReqPurchaseRequire();
			reqPurchaseRequire.setPurchaseRequireSid(formSid);
			reqPurchaseRequire.setHandleStatus(status);
			getReqPurchaseRequireService().updateById(reqPurchaseRequire);
		} else if (FormType.MonthManufacturePlan.getCode().equals(formType)) {
			ManMonthManufacturePlan manMonthManufacturePlan = new ManMonthManufacturePlan();
			manMonthManufacturePlan.setMonthManufacturePlanSid(formSid);
			manMonthManufacturePlan.setHandleStatus(status);
			getManMonthManufacturePlanService().updateById(manMonthManufacturePlan);
		} else if (FormType.WeekManufacturePlan.getCode().equals(formType)) {
			ManWeekManufacturePlan manWeekManufacturePlan = new ManWeekManufacturePlan();
			manWeekManufacturePlan.setWeekManufacturePlanSid(formSid);
			manWeekManufacturePlan.setHandleStatus(status);
			getManWeekManufacturePlanService().updateById(manWeekManufacturePlan);
		} else if (FormType.ManDayManufactureProgress.getCode().equals(formType)) {
			ManDayManufactureProgress manDayManufactureProgress = new ManDayManufactureProgress();
			manDayManufactureProgress.setDayManufactureProgressSid(formSid);
			manDayManufactureProgress.setHandleStatus(status);
			getManDayManufactureProgressService().updateById(manDayManufactureProgress);
		} else if (FormType.ManufactureCompleteNote.getCode().equals(formType)) {
			ManManufactureCompleteNote manManufactureCompleteNote = new ManManufactureCompleteNote();
			if (status.equals(HandleStatus.SUBMIT.getCode())) {
				manManufactureCompleteNote.setManufactureCompleteNoteSidList(sids);
				manManufactureCompleteNote.setHandleStatus(status);
				getManManufactureCompleteNoteService().approval(manManufactureCompleteNote);
			} else if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				manManufactureCompleteNote.setManufactureCompleteNoteSid(formSid);
				manManufactureCompleteNote.setHandleStatus(status);
				getManManufactureCompleteNoteService().updateById(manManufactureCompleteNote);
			}
		} else if (FormType.OutsourceBill.getCode().equals(formType)) {
			ManManufactureOutsourceSettle manManufactureOutsourceSettle = new ManManufactureOutsourceSettle();
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				List<Long> sidList = new ArrayList<>();
				sidList.add(formSid);
				Long[] outsourceSettleSid = sidList.toArray(new Long[sidList.size()]);
				manManufactureOutsourceSettle.setManufactureOutsourceSettleSid(formSid);
				manManufactureOutsourceSettle.setHandleStatus(status);
				manManufactureOutsourceSettle.setManufactureOutsourceSettleSidList(outsourceSettleSid);
				getManManufactureOutsourceSettleService().check(manManufactureOutsourceSettle);
			} else {
				manManufactureOutsourceSettle.setManufactureOutsourceSettleSid(formSid);
				manManufactureOutsourceSettle.setHandleStatus(status);
				getManManufactureOutsourceSettleService().updateById(manManufactureOutsourceSettle);
			}
		} else if (FormType.ProductCostSale.getCode().equals(formType)) {//销售产前成本核算
			getCosProductCostService().update(new CosProductCost(), new UpdateWrapper<CosProductCost>().lambda()
					.eq(CosProductCost::getProductCostSid, formSid)
					.set(CosProductCost::getHandleStatus, status));
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				CosProductCost cosProductCost = new CosProductCost();
				cosProductCost.setProductCostSidList(sids);
				getCosProductCostService().handleStatus(Arrays.asList(sids));//物料sid
			}
		} else if (FormType.ProductCostPurchase.getCode().equals(formType)) {//采购产前成本核算
			getCosProductCostService().update(new CosProductCost(), new UpdateWrapper<CosProductCost>().lambda()
					.eq(CosProductCost::getProductCostSid, formSid)
					.set(CosProductCost::getHandleStatus, status));
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				CosProductCost cosProductCost = new CosProductCost();
				cosProductCost.setProductCostSidList(sids);
				getCosProductCostService().handleStatus(Arrays.asList(sids));//物料sid
			}
		} else if (FormType.ManufactureOrder.getCode().equals(formType)) {
			ManManufactureOrder manManufactureOrder = new ManManufactureOrder();
			manManufactureOrder.setManufactureOrderSid(formSid);
			manManufactureOrder.setHandleStatus(status);
			getManManufactureOrderService().confirm(manManufactureOrder);
		} else if (FormType.OutsourceGrantBill.getCode().equals(formType)) {
			DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote = new DelOutsourceMaterialIssueNote();
			delOutsourceMaterialIssueNote.setIssueNoteSid(formSid);
			delOutsourceMaterialIssueNote.setHandleStatus(status);
			getDelOutsourceMaterialIssueNoteService().updateById(delOutsourceMaterialIssueNote);
		} else if (FormType.OutsourceDeliveryNote.getCode().equals(formType)) {
			DelOutsourceDeliveryNote delOutsourceDeliveryNote = new DelOutsourceDeliveryNote();
			delOutsourceDeliveryNote.setDeliveryNoteSid(formSid);
			delOutsourceDeliveryNote.setHandleStatus(status);
			getDelOutsourceDeliveryNoteService().updateById(delOutsourceDeliveryNote);
		} else if (FormType.InvOwnerMaterialSettle.getCode().equals(formType)) {//甲供料结算单
			getInvOwnerMaterialSettleService().update(new InvOwnerMaterialSettle(), new UpdateWrapper<InvOwnerMaterialSettle>().lambda()
					.eq(InvOwnerMaterialSettle::getSettleSid, formSid)
					.set(InvOwnerMaterialSettle::getHandleStatus, status));
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				InvOwnerMaterialSettle invOwnerMaterialSettle = new InvOwnerMaterialSettle();
				invOwnerMaterialSettle.setSettleSidList(sids);
				invOwnerMaterialSettle.setHandleStatus(status);
				getInvOwnerMaterialSettleService().check(invOwnerMaterialSettle);
			}
		} else if (FormType.SaleContract.getCode().equals(formType)) {
			SalSaleContract saleContract = new SalSaleContract();
			saleContract.setSaleContractSid(formSid);
			if (HandleStatus.CONFIRMED.getCode().equals(status)) {
				saleContract.setHandleStatus(status);
				getSalSaleContractService().updateById(saleContract);
				// 调用e签宝
				getSalSaleContractService().approvalContractToEsign(saleContract);
				//生成预收流水
				getSalSaleContractService().advanceReceipt(getSalSaleContractService().selectSalSaleContractById(formSid));
			}
			else if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				saleContract.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getSalSaleContractService().updateById(saleContract);
			}else {
				saleContract.setHandleStatus(status);
				getSalSaleContractService().updateById(saleContract);
			}
		} else if (FormType.AssetRecord.getCode().equals(formType)) {
			Long[] sidList = new Long[]{formSid};
			AssAssetRecord assAssetRecord = new AssAssetRecord().setAssetSidList(sidList);
			if (HandleStatus.CONFIRMED.getCode().equals(status)) {
				assAssetRecord.setHandleStatus(status);
				getAssAssetRecordService().check(assAssetRecord);
			} else if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				assAssetRecord.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getAssAssetRecordService().check(assAssetRecord);
			} else {
				assAssetRecord.setHandleStatus(status);
				getAssAssetRecordService().check(assAssetRecord);
			}
		} else if (FormType.FundRecord.getCode().equals(formType)) {
			Long[] sidList = new Long[]{formSid};
			FunFundRecord fundRecord = new FunFundRecord().setFundRecordSidList(sidList);
			if (HandleStatus.CONFIRMED.getCode().equals(status)) {
				fundRecord.setHandleStatus(status);
				getFunFundRecordService().check(fundRecord);
			} else if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				fundRecord.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getFunFundRecordService().check(fundRecord);
			} else {
				fundRecord.setHandleStatus(status);
				getFunFundRecordService().check(fundRecord);
			}
		} else if (FormType.FundAccount.getCode().equals(formType)) {
			Long[] sidList = new Long[]{formSid};
			FunFundAccount fundAccount = new FunFundAccount().setFundAccountSidList(sidList);
			if (HandleStatus.CONFIRMED.getCode().equals(status)) {
				fundAccount.setHandleStatus(status);
				getFunFundAccountService().check(fundAccount);
			} else if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				fundAccount.setHandleStatus(HandleStatus.CONFIRMED.getCode());
				getFunFundAccountService().check(fundAccount);
			} else {
				fundAccount.setHandleStatus(status);
				getFunFundAccountService().check(fundAccount);
			}
		} else if (FormType.GYSZC.getCode().equals(formType)) {
			Long[] sidList = new Long[]{formSid};
			BasVendorRegister vendorRegister = new BasVendorRegister().setVendorRegisterSidList(sidList).setHandleStatus(status);
			getBasVendorRegisterService().check(vendorRegister);
		} else if (FormType.MaterialSize.getCode().equals(formType)) {
			TecMaterialSize materialSize = new TecMaterialSize();
			if (status.equals(HandleStatus.CONFIRMED.getCode())) {
				materialSize.setMaterialSizeSidList(sids);
				materialSize.setHandleStatus(status);
				getTecMaterialSizeService().check(materialSize);
			} else {
				materialSize.setMaterialSizeSid(formSid);
				materialSize.setHandleStatus(status);
				getTecMaterialSizeService().updateTecMaterialSize(materialSize);
			}
		}else if(FormType.YPJH.getCode().equals(formType)){//样品借还
			getSamSampleLendreturnService().deleteDOTo(Arrays.asList(sids));
			if(HandleStatus.CONFIRMED.getCode().equals(status)){
				SamSampleLendreturn samSampleLendreturn = new SamSampleLendreturn();
				samSampleLendreturn.setHandleStatus(status)
						.setLendreturnSidList(sids);
				getSamSampleLendreturnService().check(samSampleLendreturn);
			}else{
				SamSampleLendreturn samSampleLendreturn = new SamSampleLendreturn();
				samSampleLendreturn.setHandleStatus(status)
						.setLendreturnSid(formSid);
				getSamSampleLendreturnService().updateById(samSampleLendreturn);
			}
		} else if (FormType.WGLSB.getCode().equals(formType)) {
			PayProcessStepComplete payProcessStepComplete = new PayProcessStepComplete();
			payProcessStepComplete.setHandleStatus(status);
			if (ConstantsEms.CHECK_STATUS.equals(status)) {
				payProcessStepComplete.setStepCompleteSidList(new Long[]{formSid});
				getPayProcessStepCompleteService().check(payProcessStepComplete);
			} else if (ConstantsEms.SUBMIT_STATUS.equals(status)) {
				payProcessStepComplete.setStepCompleteSid(formSid);
				getPayProcessStepCompleteService().verify(payProcessStepComplete);
			} else {
				payProcessStepComplete.setStepCompleteSid(formSid);
				getPayProcessStepCompleteService().updateById(payProcessStepComplete);
			}
		} else if (FormType.KQXX.getCode().equals(formType)) {
			PayWorkattendRecord payWorkattendRecord = new PayWorkattendRecord();
			payWorkattendRecord.setHandleStatus(status);
			if (ConstantsEms.CHECK_STATUS.equals(status)) {
				payWorkattendRecord.setWorkattendRecordSidList(new Long[]{formSid});
				getPayWorkattendRecordService().check(payWorkattendRecord);
			} else if (ConstantsEms.SUBMIT_STATUS.equals(status)) {
				payWorkattendRecord.setWorkattendRecordSid(formSid);
				getPayWorkattendRecordService().verify(payWorkattendRecord);
			} else {
				payWorkattendRecord.setWorkattendRecordSid(formSid);
				getPayWorkattendRecordService().updateById(payWorkattendRecord);
			}
		} else if (FormType.GZD.getCode().equals(formType)) {
			PaySalaryBill paySalaryBill = new PaySalaryBill();
			paySalaryBill.setHandleStatus(status);
			if (ConstantsEms.CHECK_STATUS.equals(status)) {
				paySalaryBill.setSalaryBillSidList(new Long[]{formSid});
				getPaySalaryBillService().check(paySalaryBill);
			} else if (ConstantsEms.SUBMIT_STATUS.equals(status)) {
				paySalaryBill.setSalaryBillSid(formSid);
				getPaySalaryBillService().verify(paySalaryBill);
			} else {
				paySalaryBill.setSalaryBillSid(formSid);
				getPaySalaryBillService().updateById(paySalaryBill);
			}
		} else if (FormType.BZFY.getCode().equals(formType) || FormType.CQFY.getCode().equals(formType)) {
			TecRecordFengyang tecRecordFengyang = new TecRecordFengyang();
			tecRecordFengyang.setHandleStatus(status);
			if (ConstantsEms.CHECK_STATUS.equals(status)) {
				tecRecordFengyang.setRecordFengyangSidList(new Long[]{formSid});
				getTecRecordFengyangService().check(tecRecordFengyang);
			} else if (ConstantsEms.SUBMIT_STATUS.equals(status)) {
				tecRecordFengyang.setRecordFengyangSid(formSid);
				getTecRecordFengyangService().verify(tecRecordFengyang);
			} else {
				tecRecordFengyang.setRecordFengyangSid(formSid);
				getTecRecordFengyangService().updateById(tecRecordFengyang);
			}
		} else if (FormType.JSZY.getCode().equals(formType)) {
			TecRecordTechtransfer tecRecordTechtransfer = new TecRecordTechtransfer();
			tecRecordTechtransfer.setHandleStatus(status);
			if (ConstantsEms.CHECK_STATUS.equals(status)) {
				tecRecordTechtransfer.setRecordTechtransferSidList(new Long[]{formSid});
				getTecRecordTechtransferService().check(tecRecordTechtransfer);
			} else if (ConstantsEms.SUBMIT_STATUS.equals(status)) {
				tecRecordTechtransfer.setRecordTechtransferSid(formSid);
				getTecRecordTechtransferService().verify(tecRecordTechtransfer);
			} else {
				tecRecordTechtransfer.setRecordTechtransferSid(formSid);
				getTecRecordTechtransferService().updateById(tecRecordTechtransfer);
			}
		} else if (FormType.DeliveryNote.getCode().equals(formType)) {
		    if(ConstantsEms.CHECK_STATUS.equals(status)){
                DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
                delDeliveryNote.setHandleStatus(status);
                delDeliveryNote.setDeliveryNoteSids(sids);
                getDelDeliveryNoteService().confirm(delDeliveryNote);
            }else {
                DelDeliveryNote delDeliveryNote = new DelDeliveryNote();
                delDeliveryNote.setHandleStatus(status);
                delDeliveryNote.setDeliveryNoteSid(formSid);
		        getDelDeliveryNoteMapper().updateById(delDeliveryNote);
            }
		} else if (FormType.TGPF.getCode().equals(formType)){
			DevDesignDrawForm devDesignDrawForm = new DevDesignDrawForm();
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				devDesignDrawForm.setHandleStatus(ConstantsEms.CHECK_STATUS);
			} else {
				devDesignDrawForm.setHandleStatus(status);
			}
			devDesignDrawForm.setDesignDrawFormSid(formSid);
			getDevDesignDrawFormService().updateStatus(devDesignDrawForm);
		} else if(FormType.WCYBXD.getCode().equals(formType)){
			if(HandleStatus.SUBMIT.getCode().equals(status)){
				getSamOsbSampleReimburseService().submitItem(formSid);
			} else if (HandleStatus.CONFIRMED.getCode().equals(status)){
				getSamOsbSampleReimburseService().over(formSid);
			} else if(HandleStatus.RETURNED.getCode().equals(status)){
				getSamOsbSampleReimburseService().returned(formSid);
			}
		} else if(FormType.DYZX.getCode().equals(formType)){
			DevMakeSampleForm devMakeSampleForm = new DevMakeSampleForm();
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				devMakeSampleForm.setHandleStatus(ConstantsEms.CHECK_STATUS);
			} else {
				devMakeSampleForm.setHandleStatus(status);
			}
			devMakeSampleForm.setMakeSampleFormSid(formSid);
			if (HandleStatus.CONFIRMED.getCode().equals(status)){
				devMakeSampleForm.setConfirmDate(new Date())
						.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
			} else if(HandleStatus.RETURNED.getCode().equals(status)){
				devMakeSampleForm.setUpdateDate(new Date())
						.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
			}
			getDevMakeSampleFormService().updateHandleStatus(devMakeSampleForm);
		} else if(FormType.YPPS.getCode().equals(formType)){
			DevSampleReviewForm devSampleReviewForm = new DevSampleReviewForm();
			if (HandleStatus.BG_RETURN.getCode().equals(status)) {
				devSampleReviewForm.setHandleStatus(ConstantsEms.CHECK_STATUS);
			} else {
				devSampleReviewForm.setHandleStatus(status);
			}
			devSampleReviewForm.setSampleReviewFormSid(formSid);
			if (HandleStatus.CONFIRMED.getCode().equals(status)){
				devSampleReviewForm.setConfirmDate(new Date())
						.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
			} else if(HandleStatus.RETURNED.getCode().equals(status)){
				devSampleReviewForm.setUpdateDate(new Date())
						.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
			}
			getDevSampleReviewFormService().updateHandleStatus(devSampleReviewForm);
		}
	}

	/**
	 * 设置审批变量
	 *
	 * @param formProcess
	 * @param taskList
	 * @return
	 */
	public SysFormProcess setApprovalInfo (SysFormProcess formProcess, List<FlowTaskDto> taskList) {
		List<String> userIds = new ArrayList<String>();
		List<String> userNames = new ArrayList<String>();
		List<String> nodeList = new ArrayList<String>();
		for (FlowTaskDto task : taskList) {
			String node = task.getTaskName();
			nodeList.add(node);
			List<String> ids = Arrays.asList(task.getAssigneeId().split(","));
			for (String id : ids) {
				userNames.add(userService.selectSysUserById(Long.valueOf(id)).getNickName());
				userIds.add(id);
			}
		}
		formProcess.setApprovalUserName(String.join(",", userNames));
		formProcess.setApprovalNode(String.join(",", nodeList));
		formProcess.setApprovalUserId(String.join(",", userIds));
		return formProcess;
	}

	public void setTodoInfo (SysTodoTask todotask, List<FlowTaskDto> taskList, String formId, String formCode, String formType, String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			parentId = formId;
		}

		formType = formTypeChange(formType);

		for (FlowTaskDto task : taskList) {
			List<String> ids = Arrays.asList(task.getAssigneeId().split(","));
			List<SysTodoTask> todoTaskList = new ArrayList<>();
			SysTodoTask todo = new SysTodoTask();
			String titel = WorkFlowUtil.backNameByFormType(formType) + ":" + formCode + "待审批!";
			todo.setTitle(titel);
			todo.setTaskCategory(ConstantsEms.TODO_TASK_DP);
			todo.setDocumentCode(formCode);
			todo.setDocumentSid(Long.valueOf(parentId));
			todo.setDocumentItemSid(Long.valueOf(formId));
			todo.setNoticeDate(new Date());
			todo.setTableName(WorkFlowUtil.backNameByFormType(formType));
			//设置菜单ID(工作台跳转详情)
			Long menuId = WorkbenchUtil.setMenuId(formType, formId);
			if (menuId != null) {
				todo.setMenuId(menuId);
			}
			if (FormType.ProductProcessStep.getCode().equals(formType)) {
				// 获取菜单id
				SysMenu menu = new SysMenu();
				menu.setMenuName(ConstantsEms.TODO_PRO_STEP_INFO_MENU_NAME);
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todo.setMenuId(menu.getMenuId());
				}
			} else if (FormType.ProductProcessStepUpdate.getCode().equals(formType)) {
				// 获取菜单id
				SysMenu menu = new SysMenu();
				menu.setMenuName(ConstantsEms.TODO_UP_PRO_STEP_INFO_MENU_NAME);
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todo.setMenuId(menu.getMenuId());
				}
			}
			else if (FormType.OutsourcePurchasePrice.getCode().equals(formType)) {
				// 获取菜单id
				SysMenu menu = new SysMenu();
				menu.setMenuName(ConstantsEms.TODO_OUT_PUR_PRICE_MENU_NAME);
				menu = remoteMenuService.getInfoByName(menu).getData();
				if (menu != null && menu.getMenuId() != null) {
					todo.setMenuId(menu.getMenuId());
				}
			}
			if (FormType.PurchaseOrder.getCode().equals(formType)) {
				PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(formId));
				if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
					todo.setMenuId(ConstantsWorkbench.purchase_order_sp);
				} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
					todo.setMenuId(ConstantsWorkbench.purchase_order_wl);
				}
			} else if (FormType.SalesOrder.getCode().equals(formType)) {
				SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(formId));
				if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
					todo.setMenuId(ConstantsWorkbench.sale_order_sp);
				} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
					todo.setMenuId(ConstantsWorkbench.sale_order_wl);
				}
			}
			for (String s : ids) {
				SysTodoTask one = new SysTodoTask();
				BeanCopyUtils.copyProperties(todo, one);
				one.setUserId(Long.valueOf(s));
				todoTaskList.add(one);
			}
			todoTaskMapper.inserts(todoTaskList);
		}
	}

	/**
	 * 推送待批任务
	 *
	 * @param formType  单据类型
	 * @param userIds   待批用户
	 * @param instance  流程实例
	 * @param variables 流程参数
	 * @Author qhq
	 */
	public void pushTodoTask (String formType, List<String> userIds, Map<String, Object> variables, String formId, String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			parentId = formId;
		}
		SysTodoTask todo = new SysTodoTask();
		todo.setDocumentSid(Long.valueOf(variables.get("businesskey").toString()));
		List<SysTodoTask> todoTaskList = todoTaskMapper.selectSysTodoTaskList(todo);
		if (null != todoTaskList && todoTaskList.size() > 0) {
			for (SysTodoTask todoTask : todoTaskList) {
				QueryWrapper<SysTodoTask> todowrapper = new QueryWrapper<>();
				todowrapper.eq("todo_task_sid", todoTask.getTodoTaskSid());
				todoTaskMapper.delete(todowrapper);
			}
		}

		formType = formTypeChange(formType);
		String titel = WorkFlowUtil.backNameByFormType(formType) + ":" + variables.get("formCode").toString() + "待审批!";
		todo.setTitle(titel);
		todo.setDocumentCode(variables.get("formCode").toString());
		todo.setDocumentSid(Long.parseLong(parentId));
		todo.setDocumentItemSid(Long.parseLong(formId));
		todo.setNoticeDate(new Date());
		todo.setTaskCategory(ConstantsEms.TODO_TASK_DP);
		todo.setTableName(WorkFlowUtil.backNameByFormType(formType));
		//设置菜单ID(工作台跳转详情)
		Long menuId = WorkbenchUtil.setMenuId(formType, formId);
		if (menuId != null) {
			todo.setMenuId(menuId);
		}
		if (FormType.ProductProcessStep.getCode().equals(formType)) {
			// 获取菜单id
			SysMenu menu = new SysMenu();
			menu.setMenuName(ConstantsEms.TODO_PRO_STEP_INFO_MENU_NAME);
			menu = remoteMenuService.getInfoByName(menu).getData();
			if (menu != null && menu.getMenuId() != null) {
				todo.setMenuId(menu.getMenuId());
			}
		} else if (FormType.ProductProcessStepUpdate.getCode().equals(formType)) {
			// 获取菜单id
			SysMenu menu = new SysMenu();
			menu.setMenuName(ConstantsEms.TODO_UP_PRO_STEP_INFO_MENU_NAME);
			menu = remoteMenuService.getInfoByName(menu).getData();
			if (menu != null && menu.getMenuId() != null) {
				todo.setMenuId(menu.getMenuId());
			}
		}
		else if (FormType.OutsourcePurchasePrice.getCode().equals(formType)) {
			// 获取菜单id
			SysMenu menu = new SysMenu();
			menu.setMenuName(ConstantsEms.TODO_OUT_PUR_PRICE_MENU_NAME);
			menu = remoteMenuService.getInfoByName(menu).getData();
			if (menu != null && menu.getMenuId() != null) {
				todo.setMenuId(menu.getMenuId());
			}
		}
		if (FormType.PurchaseOrder.getCode().equals(formType)) {
			PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(Long.valueOf(formId));
			if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(purPurchaseOrder.getMaterialCategory())) {
				todo.setMenuId(ConstantsWorkbench.purchase_order_sp);
			} else if (purPurchaseOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(purPurchaseOrder.getMaterialCategory())) {
				todo.setMenuId(ConstantsWorkbench.purchase_order_wl);
			}
		} else if (FormType.SalesOrder.getCode().equals(formType)) {
			SalSalesOrder salSalesOrder = salSalesOrderMapper.selectSalSalesOrderById(Long.valueOf(formId));
			if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_SP.equals(salSalesOrder.getMaterialCategory())) {
				todo.setMenuId(ConstantsWorkbench.sale_order_sp);
			} else if (salSalesOrder != null && ConstantsEms.MATERIAL_CATEGORY_WL.equals(salSalesOrder.getMaterialCategory())) {
				todo.setMenuId(ConstantsWorkbench.sale_order_wl);
			}
		}
		List<String> ids = new ArrayList<>();
		for (String id : userIds) {
			if (id.startsWith("[")) {
				id = id.substring(1, id.length());
			}
			if (id.endsWith("]")) {
				id = id.substring(0, id.length() - 1);
			}
			String[] idArr = id.split(",");
			for (String s : idArr) {
				ids.add(s);
			}
		}
		for (String userId : ids) {
			String[] idArr = userId.split(",");
			for (String s : idArr) {
				todo.setUserId(Long.valueOf(s));
				todo.setTodoTaskSid(null);
				todoTaskMapper.insert(todo);
			}
		}
	}

	/**
	 * 记录到关联单据表
	 *
	 * @param formId
	 * @param formType
	 * @param startUserId
	 * @param instance
	 * @return
	 * @Author qhq
	 */
	public List<String> recordFormProcess (String formId, String formType, String startUserId, ProcessInstanceResponse instance) {
		//记录到关联单据表
		SysFormProcess formProcess = new SysFormProcess();
		formProcess.setProcessInstanceId(instance.getProcessInstanceId());
		formProcess.setFormId(Long.valueOf(formId));
		formProcess.setFormType(formType);
		formProcess.setFormStatus("1");
		formProcess.setCreateById(startUserId);
		List<String> node = new ArrayList<>();
		for (String approvalNode : instance.getApprovalNodes()) {
			node.addAll(Arrays.asList(approvalNode.split(",")));
		}
		formProcess.setApprovalNode(String.join(",", node));

		List<String> userIds = new ArrayList<>();
		for (String approvalId : instance.getApprovalIds()) {
			userIds.addAll(Arrays.asList(approvalId.split(",")));
		}
		List<String> ids = new ArrayList<String>();
		List<String> userNames = new ArrayList<String>();
		for (String id : userIds) {
			ids.add(id);
			userNames.add(userService.selectSysUserById(Long.valueOf(id)).getNickName());
		}
		formProcess.setApprovalUserId(String.join(",", ids));
		formProcess.setApprovalUserName(String.join(",", userNames));
		formProcessService.insertSysFormProcess(formProcess);
		return userIds;
	}

	/**
	 * 判断单据类型是否存在，主要用于一些屏蔽或者特殊处理
	 *
	 * @param formType
	 * @return
	 * @Author qhq
	 */
	private boolean isExistType (String formType) {
		List<String> shieldType = new ArrayList<>();
		shieldType.add(FormType.PurchasePrice.getCode());
		shieldType.add(FormType.OutsourcePurchasePrice.getCode());
		shieldType.add(FormType.SalePrice.getCode());
		shieldType.add(FormType.QuoteBargain.getCode());
		shieldType.add(FormType.OutsourceQuoteBargain.getCode());
		shieldType.add(FormType.Bom.getCode());
		if (shieldType.contains(formType)) {
			return true;
		}
		return false;
	}

	/**
	 * 需要特殊处理的单据类型操作日志
	 *
	 * @param formId
	 * @param formType
	 * @param comment
	 * @Author qhq
	 */
	private void setApprovalLog (String formId, String formType, String comment) {
		Long id = Long.decode(formId);
		if (FormType.PurchasePrice.getCode().equals(formType)) {
			PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
			purPurchasePriceItem.setPurchasePriceItemSid(id);
			getPurPurchasePriceService().setApprovalLog(purPurchasePriceItem, comment);
		} else if (FormType.OutsourcePurchasePrice.getCode().equals(formType)) {
			PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem = new PurOutsourcePurchasePriceItem();
			purOutsourcePurchasePriceItem.setOutsourcePurchasePriceItemSid(id);
			getPurOutsourcePurchasePriceService().setApprovalLog(purOutsourcePurchasePriceItem, comment);
		} else if (FormType.SalePrice.getCode().equals(formType)) {
			SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
			salSalePriceItem.setSalePriceItemSid(id);
			getSalSalePriceService().setApprovalLog(salSalePriceItem, comment);
		} else if (FormType.Bom.getCode().equals(formType)) {
			TecBomHead bom = new TecBomHead();
			bom.setMaterialSid(id);
			List<TecBomHead> bomList = getBomService().selectTecBomHeadList(bom);
			for (TecBomHead b : bomList) {
				MongodbUtil.insertApprovalLog(Long.valueOf(b.getBomSid()), BusinessType.LZ.getValue(), comment);
			}
		}
	}

	private String formTypeChange (String formType) {
		if (formType.contains(BG)) {
			if (formType.equals(FormType.CGHT_BG.getCode())) {
				return FormType.PurchaseContract.getCode();
			} else if (formType.equals(FormType.CGDD_BG.getCode())) {
				return FormType.PurchaseOrder.getCode();
			} else if (formType.equals(FormType.CGJ_BG.getCode())) {
				return FormType.PurchasePrice.getCode();
			} else if (formType.equals(FormType.XSDD_BG.getCode())) {
				return FormType.SalesOrder.getCode();
			} else if (formType.equals(FormType.XSHT_BG.getCode())) {
				return FormType.SaleContract.getCode();
			} else if (formType.equals(FormType.XSJ_BG.getCode())) {
				return FormType.SalePrice.getCode();
			}else if(formType.equals(FormType.JGCGJ_BG.getCode())){
				return FormType.OutsourcePurchasePrice.getCode();
			}else if(formType.equals(FormType.CQFY_BG.getCode())){
				return FormType.CQFY.getCode();
			}else if(formType.equals(FormType.JSZY_BG.getCode())){
				return FormType.JSZY.getCode();
			}else if(formType.equals(FormType.TGPF_BG.getCode())){
				return FormType.TGPF.getCode();
			}else if(formType.equals(FormType.DYZX_BG.getCode())){
				return FormType.DYZX.getCode();
			}else if(formType.equals(FormType.YPPS_BG.getCode())){
				return FormType.YPPS.getCode();
			} else if(formType.equals(FormType.AssetRecord_BG.getCode())){
				return FormType.AssetRecord.getCode();
			} else if(formType.equals(FormType.FundAccount_BG.getCode())){
				return FormType.FundAccount.getCode();
			} else if(formType.equals(FormType.FundRecord_BG.getCode())){
				return FormType.FundRecord.getCode();
			}
		}
		return formType;
	}

	/**
	 * 提交流程验证方法，用于在提交时需要进行前置校验内容的情况
	 */
	public String submitValidation(String formType, Long formSid){
//		if(FormType.TGPF.getCode().equals(formType)){
//			if(!getDevDesignDrawFormService().attachmentIsExist(formSid)){
//				return "附件不能为空!";
//			}
//		}
		if(FormType.WCYBXD.getCode().equals(formType)){
			if(!getSamOsbSampleReimburseService().itemValidation(formSid)){
				return  "明细不能为空！";
			}
//			String tips = getSamOsbSampleReimburseService().wbx(formSid);
//			if(tips!=null&&tips!=""){
//				return tips;
//			}
		}
		return null;
	}
}
