package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.dto.request.ManProduceConcernTaskGroupRequest;
import com.platform.ems.domain.dto.response.ManProduceConcernTaskGroupResponse;
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
import com.platform.ems.domain.ManProduceConcernTaskGroup;
import com.platform.ems.service.IManProduceConcernTaskGroupService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产关注事项组Controller
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
@RestController
@RequestMapping("/manProduceConcernTaskGroup")
@Api(tags = "生产关注事项组")
public class ManProduceConcernTaskGroupController extends BaseController {

    @Autowired
    private IManProduceConcernTaskGroupService manProduceConcernTaskGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产关注事项组列表
     */
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产关注事项组列表", notes = "查询生产关注事项组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTaskGroup.class))
    public TableDataInfo list(@RequestBody ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        startPage(manProduceConcernTaskGroup);
        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupService.selectManProduceConcernTaskGroupList(manProduceConcernTaskGroup);
        return getDataTable(list);
    }

    @PostMapping("/report")
    @ApiOperation(value = "查询生产关注事项组-明细报表", notes = "查询生产关注事项组-明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTaskGroup.class))
    public TableDataInfo report(@RequestBody ManProduceConcernTaskGroupRequest manProduceConcernTaskGroup) {
        startPage(manProduceConcernTaskGroup);
        List<ManProduceConcernTaskGroupResponse> list = manProduceConcernTaskGroupService.getReport(manProduceConcernTaskGroup);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出生产关注事项组明细报表", notes = "导出生产关注事项组明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportReport(HttpServletResponse response, ManProduceConcernTaskGroupRequest manProduceConcernTaskGroup) throws IOException {
        List<ManProduceConcernTaskGroupResponse> list = manProduceConcernTaskGroupService.getReport(manProduceConcernTaskGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProduceConcernTaskGroupResponse> util = new ExcelUtil<>(ManProduceConcernTaskGroupResponse.class, dataMap);
        util.exportExcel(response, list, "关注事项组明细报表");
    }
    /**
     * 获取生产关注事项组下拉列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "获取生产关注事项组下拉列表", notes = "获取生产关注事项组下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTaskGroup.class))
    public AjaxResult getList(@RequestBody ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupService.selectManProduceConcernTaskGroupList(manProduceConcernTaskGroup);
        return AjaxResult.success(list);
    }

    /**
     * 导出生产关注事项组列表
     */
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:export")
    @Log(title = "生产关注事项组", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产关注事项组列表", notes = "导出生产关注事项组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProduceConcernTaskGroup manProduceConcernTaskGroup) throws IOException {
        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupService.selectManProduceConcernTaskGroupList(manProduceConcernTaskGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProduceConcernTaskGroup> util = new ExcelUtil<>(ManProduceConcernTaskGroup.class, dataMap);
        util.exportExcel(response, list, "生产关注事项组");
    }


    /**
     * 获取生产关注事项组详细信息
     */
    @ApiOperation(value = "获取生产关注事项组详细信息", notes = "获取生产关注事项组详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTaskGroup.class))
    @GetMapping("/getInfo/{concernTaskGroupSid}")
    public AjaxResult getInfo(@PathVariable Long concernTaskGroupSid) {
        if (concernTaskGroupSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProduceConcernTaskGroupService.selectManProduceConcernTaskGroupById(concernTaskGroupSid));
    }


    @ApiOperation(value = "生产月计划-获取生产关注事项组", notes = "生产月计划-获取生产关注事项组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceConcernTaskGroup.class))
    @GetMapping("/month/getInfo/{concernTaskGroupSid}")
    public AjaxResult monthGetInfo(@PathVariable Long concernTaskGroupSid) {
        if (concernTaskGroupSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProduceConcernTaskGroupService.monthConcernTaskGroupById(concernTaskGroupSid));
    }
    /**
     * 新增生产关注事项组
     */
    @ApiOperation(value = "新增生产关注事项组", notes = "新增生产关注事项组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:add")
    @Log(title = "生产关注事项组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        int row = manProduceConcernTaskGroupService.insertManProduceConcernTaskGroup(manProduceConcernTaskGroup);
        return AjaxResult.success(manProduceConcernTaskGroup);
    }

    @ApiOperation(value = "修改生产关注事项组", notes = "修改生产关注事项组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:edit")
    @Log(title = "生产关注事项组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        return toAjax(manProduceConcernTaskGroupService.updateManProduceConcernTaskGroup(manProduceConcernTaskGroup));
    }

    /**
     * 变更生产关注事项组
     */
    @ApiOperation(value = "变更生产关注事项组", notes = "变更生产关注事项组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:change")
    @Log(title = "生产关注事项组", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        return toAjax(manProduceConcernTaskGroupService.changeManProduceConcernTaskGroup(manProduceConcernTaskGroup));
    }

    /**
     * 删除生产关注事项组
     */
    @ApiOperation(value = "删除生产关注事项组", notes = "删除生产关注事项组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:remove")
    @Log(title = "生产关注事项组", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> concernTaskGroupSids) {
        if (CollectionUtils.isEmpty(concernTaskGroupSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProduceConcernTaskGroupService.deleteManProduceConcernTaskGroupByIds(concernTaskGroupSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产关注事项组", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        return AjaxResult.success(manProduceConcernTaskGroupService.changeStatus(manProduceConcernTaskGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manProduceConcernTaskGroup:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产关注事项组", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        return toAjax(manProduceConcernTaskGroupService.check(manProduceConcernTaskGroup));
    }

}
