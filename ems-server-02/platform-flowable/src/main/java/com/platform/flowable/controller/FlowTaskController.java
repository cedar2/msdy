package com.platform.flowable.controller;

import com.platform.common.core.domain.AjaxResult;
import com.platform.flowable.domain.AssigneeQuery;
import com.platform.flowable.domain.dto.FlowTaskDto;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.request.FlowProcessRequest;
import com.platform.flowable.response.ProcessInstanceResponse;
import com.platform.flowable.service.IFlowTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * <p>工作流任务管理<p>
 *
 * @author c
 */
@Slf4j
@Api(tags = "工作流流程任务管理")
@RestController
@RequestMapping("/flowable/task")
public class FlowTaskController {

	static String PARENT_NODE = "1";
	static String SUBMIT_USER = "2";

	@Autowired
	private IFlowTaskService flowTaskService;

	@ApiOperation(value = "我发起的流程", response = ProcessInstanceResponse.class)
	@PostMapping(value = "/getMyProcess")
	public AjaxResult myProcess (@RequestBody FlowProcessRequest request) {
		return flowTaskService.myProcess(request);
	}

	@ApiOperation(value = "审批任务")
	@PostMapping(value = "/complete")
	public AjaxResult complete (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.complete(flowTaskVo);
	}

	@ApiOperation(value = "驳回任务到提交人")
	@PostMapping(value = "/rejectToSubmit")
	public AjaxResult taskRejectToSubmit (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.taskRejectToSubmit(flowTaskVo);
	}

	@ApiOperation(value = "我审批的任务", response = FlowTaskDto.class)
	@PostMapping(value = "/getMyApprovalTask")
	public AjaxResult myApprovalTask (@RequestBody FlowProcessRequest request) {
		return flowTaskService.myApprovalTask(request);
	}

	@ApiOperation(value = "根据businessKey获取任务", response = FlowTaskDto.class)
	@PostMapping(value = "/getTaskByBusinessKey")
	public AjaxResult getTaskByBusinessKey (@RequestBody FlowTaskVo flowTaskVo) {
		return AjaxResult.success(flowTaskService.getTaskByBusinessKey(flowTaskVo));
	}

	@ApiOperation(value = "退回任务")
	@PostMapping(value = "/return")
	public AjaxResult taskReturn (@RequestBody FlowTaskVo flowTaskVo) {
		if (PARENT_NODE.equals(flowTaskVo.getTargetKey())) {
			return AjaxResult.success(flowTaskService.returnToParent(flowTaskVo));
		}
		if (SUBMIT_USER.equals(flowTaskVo.getTargetKey())) {
			return AjaxResult.success(flowTaskService.taskRejectToSubmit(flowTaskVo));
		}
		return AjaxResult.success(flowTaskService.taskReturn(flowTaskVo));
	}

	@ApiOperation(value = "获取所有可回退的节点")
	@PostMapping(value = "/returnList")
	public AjaxResult findReturnTaskList (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.findReturnTaskList(flowTaskVo);
	}

	@ApiOperation(value = "测试用")
	@PostMapping(value = "/getParentTaskId")
	public String getParentTaskId (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.getParentTaskId(flowTaskVo.getInstanceId());
	}

	@ApiOperation(value = "获取用户待办任务")
	@PostMapping(value = "/getUserTaskList")
	public AjaxResult getUserTaskList (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.getUserTaskList(flowTaskVo);
	}

	@ApiOperation(value = "添加任务审批人")
	@PostMapping(value = "/addTaskAssignee")
	public void addTaskAssignee (@RequestBody Map<String, Object> instance) {
		flowTaskService.addTaskAssignee(instance);
	}


	@ApiOperation(value = "删除流程信息（撤回相关操作使用）")
	@PostMapping(value = "/removeProcess")
	public AjaxResult removeProcess(@RequestBody FlowTaskVo flowTaskVo){
		return flowTaskService.removeProcess(flowTaskVo);
	}




	@ApiOperation(value = "取消申请", response = FlowTaskDto.class)
	@PostMapping(value = "/stopProcess")
	public AjaxResult stopProcess (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.stopProcess(flowTaskVo);
	}

	@ApiOperation(value = "撤回流程", response = FlowTaskDto.class)
	@PostMapping(value = "/revokeProcess")
	public AjaxResult revokeProcess (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.revokeProcess(flowTaskVo);
	}

	@ApiOperation(value = "获取待办列表", response = FlowTaskDto.class)
	@GetMapping(value = "/todoList")
	public AjaxResult todoList (@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
								@ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
		return flowTaskService.todoList(pageNum, pageSize);
	}

	@ApiOperation(value = "获取已办任务", response = FlowTaskDto.class)
	@GetMapping(value = "/finishedList")
	public AjaxResult finishedList (@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
									@ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize,
									@ApiParam(value = "用户ID", required = true) @RequestParam String userId) {
		return flowTaskService.finishedList(pageNum, pageSize,userId);
	}


	@ApiOperation(value = "流程历史流转记录", response = FlowTaskDto.class)
	@GetMapping(value = "/flowRecord")
	public AjaxResult flowRecord (String procInsId, String deployId) {
		return flowTaskService.flowRecord(procInsId, deployId);
	}

	@ApiOperation(value = "获取流程变量", response = FlowTaskDto.class)
	@GetMapping(value = "/processVariables/{taskId}")
	public AjaxResult processVariables (@ApiParam(value = "流程任务Id") @PathVariable(value = "taskId") String taskId) {
		return flowTaskService.processVariables(taskId);
	}

	@ApiOperation(value = "删除任务")
	@DeleteMapping(value = "/delete")
	public AjaxResult delete (@RequestBody FlowTaskVo flowTaskVo) {
		flowTaskService.deleteTask(flowTaskVo);
		return AjaxResult.success();
	}

	@ApiOperation(value = "认领/签收任务")
	@PostMapping(value = "/claim")
	public AjaxResult claim (@RequestBody FlowTaskVo flowTaskVo) {
		flowTaskService.claim(flowTaskVo);
		return AjaxResult.success();
	}

	@ApiOperation(value = "取消认领/签收任务")
	@PostMapping(value = "/unClaim")
	public AjaxResult unClaim (@RequestBody FlowTaskVo flowTaskVo) {
		flowTaskService.unClaim(flowTaskVo);
		return AjaxResult.success();
	}

	@ApiOperation(value = "委派任务")
	@PostMapping(value = "/delegate")
	public AjaxResult delegate (@RequestBody FlowTaskVo flowTaskVo) {
		flowTaskService.delegateTask(flowTaskVo);
		return AjaxResult.success();
	}

	@ApiOperation(value = "转办任务")
	@PostMapping(value = "/assign")
	public AjaxResult assign (@RequestBody FlowTaskVo flowTaskVo) {
		flowTaskService.assignTask(flowTaskVo);
		return AjaxResult.success();
	}

	@ApiOperation(value = "获取下一节点")
	@PostMapping(value = "/nextFlowNode")
	public AjaxResult getNextFlowNode (@RequestBody FlowTaskVo flowTaskVo) {
		return flowTaskService.getNextFlowNode(flowTaskVo);
	}

	/**
	 * 生成流程图
	 *
	 * @param processId 任务ID
	 */
	@RequestMapping("/diagram/{processId}")
	public void genProcessDiagram (HttpServletResponse response,
								   @PathVariable("processId") String processId) {
		InputStream inputStream = flowTaskService.diagram(processId);
		OutputStream os = null;
		BufferedImage image = null;
		try {
			image = ImageIO.read(inputStream);
			response.setContentType("image/png");
			os = response.getOutputStream();
			if (image != null) {
				ImageIO.write(image, "png", os);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成流程图
	 *
	 * @param procInsId 任务ID
	 */
	@RequestMapping("/flowViewer/{procInsId}")
	public AjaxResult getFlowViewer (@PathVariable("procInsId") String procInsId) {
		return flowTaskService.getFlowViewer(procInsId);
	}


	@PostMapping("/userIsAssignee")
	public AjaxResult isUserAssignee(@RequestBody AssigneeQuery query){
		return this.flowTaskService.isUserAssignee(query.getUserId(), query.getProcessInstanceId());
	}
}
