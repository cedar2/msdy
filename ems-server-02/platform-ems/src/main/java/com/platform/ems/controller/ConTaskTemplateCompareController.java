package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.Log;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.ConTaskTemplateCompare;
import com.platform.ems.service.IConTaskTemplateCompareService;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 任务模板对照关系Controller
 *
 * @author platform
 * @date 2023-11-03
 */
@Api(tags = "任务模板对照关系")
@RestController
@RequestMapping("/con/task/template/compare")
public class ConTaskTemplateCompareController extends BaseController {

    @Autowired
    private IConTaskTemplateCompareService conTaskTemplateCompareService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询任务模板对照关系列表
     */
    @ApiOperation(value = "查询任务模板对照关系列表", notes = "查询任务模板对照关系列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaskTemplateCompare.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody ConTaskTemplateCompare conTaskTemplateCompare) {
        startPage(conTaskTemplateCompare);
        List<ConTaskTemplateCompare> list = conTaskTemplateCompareService.selectConTaskTemplateCompareList(conTaskTemplateCompare);
        return getDataTable(list);
    }

    /**
     * 导出任务模板对照关系Controller列表
     */
    @ApiOperation(value = "导出任务模板对照关系Controller列表", notes = "导出任务模板对照关系Controller列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConTaskTemplateCompare conTaskTemplateCompare) throws IOException {
        List<ConTaskTemplateCompare> list = conTaskTemplateCompareService.selectConTaskTemplateCompareList(conTaskTemplateCompare);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConTaskTemplateCompare> util = new ExcelUtil<>(ConTaskTemplateCompare.class, dataMap);
        util.exportExcel(response, list, "任务模板对照关系");
    }

    /**
     * 获取任务模板对照关系Controller详细信息
     */
    @ApiOperation(value = "获取任务模板对照关系Controller详细信息", notes = "获取任务模板对照关系Controller详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaskTemplateCompare.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long taskTemplateCompareSid) {
        if (taskTemplateCompareSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conTaskTemplateCompareService.selectConTaskTemplateCompareById(taskTemplateCompareSid));
    }

    /**
     * 新增任务模板对照关系Controller
     */
    @ApiOperation(value = "新增任务模板对照关系Controller", notes = "新增任务模板对照关系Controller")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "任务模板对照关系Controller", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConTaskTemplateCompare conTaskTemplateCompare) {
        return toAjax(conTaskTemplateCompareService.insertConTaskTemplateCompare(conTaskTemplateCompare));
    }

    @ApiOperation(value = "修改任务模板对照关系Controller", notes = "修改任务模板对照关系Controller")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "任务模板对照关系Controller", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConTaskTemplateCompare conTaskTemplateCompare) {
        return toAjax(conTaskTemplateCompareService.updateConTaskTemplateCompare(conTaskTemplateCompare));
    }

    /**
     * 变更任务模板对照关系Controller
     */
    @ApiOperation(value = "变更任务模板对照关系Controller", notes = "变更任务模板对照关系Controller")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "任务模板对照关系", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConTaskTemplateCompare conTaskTemplateCompare) {
        return toAjax(conTaskTemplateCompareService.changeConTaskTemplateCompare(conTaskTemplateCompare));
    }

    /**
     * 删除任务模板对照关系Controller
     */
    @ApiOperation(value = "删除任务模板对照关系Controller", notes = "删除任务模板对照关系Controller")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "任务模板对照关系Controller", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> taskTemplateCompareSids) {
        if (CollectionUtils.isEmpty(taskTemplateCompareSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conTaskTemplateCompareService.deleteConTaskTemplateCompareByIds(taskTemplateCompareSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "任务模板对照关系Controller", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConTaskTemplateCompare conTaskTemplateCompare) {
        return AjaxResult.success(conTaskTemplateCompareService.changeStatus(conTaskTemplateCompare));
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "任务模板对照关系Controller", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConTaskTemplateCompare conTaskTemplateCompare) {
        return toAjax(conTaskTemplateCompareService.check(conTaskTemplateCompare));
    }

}

