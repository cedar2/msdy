package com.platform.ems.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;


import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.ISysOverdueBusinessService;
import com.platform.ems.service.ISysToexpireBusinessService;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysToexpireBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.system.domain.SysTodoTask;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 待办事项列Controller
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@RestController
@RequestMapping("/todo/task")
@Api(tags = "待办事项")
public class SysTodoTaskController extends BaseController {

    @Autowired
    private ISysTodoTaskService sysTodoTaskListService;

    @Autowired
    private ISysToexpireBusinessService sysToexpireBusinessService;
    @Autowired
    private ISysOverdueBusinessService sysOverdueBusinessService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询待办事项列表 (用户工作台)
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询待办事项列表", notes = "查询待办事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysTodoTask.class))
    public TableDataInfo list(@RequestBody SysTodoTask sysTodoTask) {
        startPage(sysTodoTask);
        List<SysTodoTask> list = sysTodoTaskListService.selectSysTodoTaskListTable(sysTodoTask);
        return getDataTable(list);
    }

    /**
     * 查询待办事项报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询待办事项列表", notes = "查询待办事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysTodoTask.class))
    public TableDataInfo report(@RequestBody SysTodoTask sysTodoTask) {
        startPage(sysTodoTask);
        List<SysTodoTask> list = sysTodoTaskListService.selectSysTodoTaskReport(sysTodoTask);
        return getDataTable(list);
    }

    /**
     * 导出待办事项列表
     */
    @ApiOperation(value = "导出待办事项列表", notes = "导出待办事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysTodoTask sysTodoTask) throws IOException {
        List<SysTodoTask> list = sysTodoTaskListService.selectSysTodoTaskLists(sysTodoTask);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysTodoTask> util = new ExcelUtil<>(SysTodoTask.class, dataMap);
        util.exportExcel(response, list, "待办事项列" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取待办事项详细信息
     */
    @ApiOperation(value = "获取待办事项详细信息", notes = "获取待办事项详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysTodoTask.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String id) {
        if (id == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysTodoTaskListService.selectSysTodoTaskListById(id));
    }

    /**
     * 新增待办事项列
     */
    @ApiOperation(value = "新增待办事项", notes = "新增待办事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysTodoTask sysTodoTask) {
        return toAjax(sysTodoTaskListService.insertSysTodoTaskList(sysTodoTask));
    }

    /**
     * 修改待办事项列
     */
    @ApiOperation(value = "修改待办事项列", notes = "修改待办事项列")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysTodoTask sysTodoTask) {
        return toAjax(sysTodoTaskListService.updateSysTodoTaskList(sysTodoTask));
    }


    /**
     * 删除待办事项
     */
    @ApiOperation(value = "删除待办事项", notes = "删除待办事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> todoTaskSids) {
        if (ArrayUtil.isEmpty(todoTaskSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysTodoTaskListService.deleteSysTodoTaskListByIds(todoTaskSids));
    }

    /**
     * 用户工作台总数计算
     */
    @ApiOperation(value = "用户工作台总数计算", notes = "用户工作台总数计算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/totalCount")
    public AjaxResult totalCount(@RequestBody SysTodoTask request) {
        Long[] userId = request.getUserIdList();

        // 待办
        SysTodoTask todoTask = new SysTodoTask();
        todoTask.setPageSize(2).setPageNum(1);
        todoTask.setUserIdList(userId);
        todoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB);
        todoTask.setTitle(request.getTitle()).setBeginTime(request.getBeginTime()).setEndTime(request.getEndTime());

        startPage(todoTask);
        List<SysTodoTask> todoList = sysTodoTaskListService.selectSysTodoTaskReport(todoTask);
        TableDataInfo pageTodo = getDataTable(todoList);

        // 待批
        SysTodoTask approveTask = new SysTodoTask();
        approveTask.setPageSize(2).setPageNum(1);
        approveTask.setUserIdList(userId);
        approveTask.setTaskCategory(ConstantsEms.TODO_TASK_DP);
        approveTask.setTitle(request.getTitle()).setBeginTime(request.getBeginTime()).setEndTime(request.getEndTime());

        startPage(approveTask);
        List<SysTodoTask> approveList = sysTodoTaskListService.selectSysTodoTaskReport(approveTask);
        TableDataInfo pageApprove = getDataTable(approveList);

        // 即将到期
        SysToexpireBusiness toexpireBusiness = new SysToexpireBusiness();
        toexpireBusiness.setPageSize(2).setPageNum(1);
        toexpireBusiness.setUserIdList(userId);
        toexpireBusiness.setTitle(request.getTitle()).setBeginTime(request.getBeginTime()).setEndTime(request.getEndTime());

        startPage(toexpireBusiness);
        List<SysToexpireBusiness> toexpirelist = sysToexpireBusinessService.selectSysToexpireBusinessReport(toexpireBusiness);
        TableDataInfo pageToexpire = getDataTable(toexpirelist);

        // 已逾期
        SysOverdueBusiness overdueBusiness = new SysOverdueBusiness();
        overdueBusiness.setPageSize(2).setPageNum(1);
        overdueBusiness.setUserIdList(userId);
        overdueBusiness.setTitle(request.getTitle()).setBeginTime(request.getBeginTime()).setEndTime(request.getEndTime());

        startPage(overdueBusiness);
        List<SysOverdueBusiness> overduelist = sysOverdueBusinessService.selectSysOverdueBusinessReport(overdueBusiness);
        TableDataInfo pageOverdue = getDataTable(overduelist);

        // 汇总
        Map<String, Long> map = new HashMap<>();
        map.put("todo", pageTodo.getTotal());
        map.put("approve", pageApprove.getTotal());
        map.put("toexpire", pageToexpire.getTotal());
        map.put("overdue", pageOverdue.getTotal());

        return AjaxResult.success(map);

    }

}
