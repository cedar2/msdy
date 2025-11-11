package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.plug.domain.ConProduceStage;
import com.platform.ems.plug.mapper.ConProduceStageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManProduceConcernTask;
import com.platform.ems.service.IManProduceConcernTaskService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产关注事项Controller
 *
 * @author zhuangyz
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/manProduceConcernTask")
@Api(tags = "生产关注事项")
public class ManProduceConcernTaskController extends BaseController {

    @Autowired
    private IManProduceConcernTaskService manProduceConcernTaskService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConProduceStageMapper conProduceStageMapper;

    /**
     * 查询生产关注事项列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产关注事项列表", notes = "查询生产关注事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTask.class))
    public TableDataInfo list(@RequestBody ManProduceConcernTask manProduceConcernTask) {
        startPage(manProduceConcernTask);
        List<ManProduceConcernTask> list = manProduceConcernTaskService.selectManProduceConcernTaskList(manProduceConcernTask);
        return getDataTable(list);
    }


    /**
     * 查询已启动已经确认生产关注事项列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询生产关注事项列表", notes = "查询生产关注事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTask.class))
    public TableDataInfo getList(@RequestBody ManProduceConcernTask manProduceConcernTask) {
        startPage(manProduceConcernTask);
        manProduceConcernTask.setStatus("1").setHandleStatus("5");
        List<ManProduceConcernTask> list = manProduceConcernTaskService.selectManProduceConcernTaskList(manProduceConcernTask);
        return getDataTable(list);
    }

    /**
     * 导出生产关注事项列表
     */
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:export")
    @Log(title = "生产关注事项", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产关注事项列表", notes = "导出生产关注事项列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProduceConcernTask manProduceConcernTask) throws IOException {
        List<ManProduceConcernTask> list = manProduceConcernTaskService.selectManProduceConcernTaskList(manProduceConcernTask);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProduceConcernTask> util = new ExcelUtil<>(ManProduceConcernTask.class, dataMap);
        util.exportExcel(response, list, "生产关注事项");
    }


    /**
     * 获取生产关注事项详细信息
     */
    @ApiOperation(value = "获取生产关注事项详细信息", notes = "获取生产关注事项详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTask.class))
    //@PreAuthorize(hasPermi = "ems:manProduceConcernTask:query")
    @GetMapping("/getInfo/{concernTaskSid}")
    public AjaxResult getInfo(@PathVariable Long concernTaskSid) {
        if (concernTaskSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProduceConcernTaskService.selectManProduceConcernTaskById(concernTaskSid));
    }

    /**
     * 新增生产关注事项
     */
    @ApiOperation(value = "新增生产关注事项", notes = "新增生产关注事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:add")
    @Log(title = "生产关注事项", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProduceConcernTask manProduceConcernTask) {
        int row = manProduceConcernTaskService.insertManProduceConcernTask(manProduceConcernTask);
        return AjaxResult.success(manProduceConcernTask);
    }

    @ApiOperation(value = "修改生产关注事项", notes = "修改生产关注事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:edit")
    @Log(title = "生产关注事项", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProduceConcernTask manProduceConcernTask) {
        return toAjax(manProduceConcernTaskService.updateManProduceConcernTask(manProduceConcernTask));
    }

    /**
     * 变更生产关注事项
     */
    @ApiOperation(value = "变更生产关注事项", notes = "变更生产关注事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:change")
    @Log(title = "生产关注事项", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProduceConcernTask manProduceConcernTask) {
        return toAjax(manProduceConcernTaskService.changeManProduceConcernTask(manProduceConcernTask));
    }

    /**
     * 删除生产关注事项
     */
    @ApiOperation(value = "删除生产关注事项", notes = "删除生产关注事项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:remove")
    @Log(title = "生产关注事项", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> concernTaskSids) {
        if (CollectionUtils.isEmpty(concernTaskSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProduceConcernTaskService.deleteManProduceConcernTaskByIds(concernTaskSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产关注事项", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ManProduceConcernTask manProduceConcernTask) {
        return AjaxResult.success(manProduceConcernTaskService.changeStatus(manProduceConcernTask));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manProduceConcernTask:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产关注事项", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManProduceConcernTask manProduceConcernTask) {
        return toAjax(manProduceConcernTaskService.check(manProduceConcernTask));
    }

}
