package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManSchedulingInfo;
import com.platform.ems.service.IManSchedulingInfoService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产排程信息Controller
 *
 * @author chenkw
 * @date 2023-05-24
 */
@RestController
@RequestMapping("/info")
@Api(tags = "生产排程信息")
public class ManSchedulingInfoController extends BaseController {

    @Autowired
    private IManSchedulingInfoService manSchedulingInfoService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产排程信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产排程信息列表", notes = "查询生产排程信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManSchedulingInfo.class))
    public TableDataInfo list(@RequestBody ManSchedulingInfo manSchedulingInfo) {
        startPage(manSchedulingInfo);
        List<ManSchedulingInfo> list = manSchedulingInfoService.selectManSchedulingInfoList(manSchedulingInfo);
        return getDataTable(list);
    }

    /**
     * 导出生产排程信息列表
     */
    @ApiOperation(value = "导出生产排程信息列表", notes = "导出生产排程信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManSchedulingInfo manSchedulingInfo) throws IOException {
        List<ManSchedulingInfo> list = manSchedulingInfoService.selectManSchedulingInfoList(manSchedulingInfo);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManSchedulingInfo> util = new ExcelUtil<>(ManSchedulingInfo.class, dataMap);
        util.exportExcel(response, list, "生产排程信息");
    }

    /**
     * 获取生产排程信息详细信息
     */
    @ApiOperation(value = "获取生产排程信息详细信息", notes = "获取生产排程信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManSchedulingInfo.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long schedulingInfoSid) {
        if (schedulingInfoSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manSchedulingInfoService.selectManSchedulingInfoById(schedulingInfoSid));
    }

    /**
     * 新增生产排程信息
     */
    @ApiOperation(value = "新增生产排程信息", notes = "新增生产排程信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManSchedulingInfo manSchedulingInfo) {
        return toAjax(manSchedulingInfoService.insertManSchedulingInfoByProcessStep(manSchedulingInfo));
    }

    @ApiOperation(value = "修改生产排程信息", notes = "修改生产排程信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManSchedulingInfo manSchedulingInfo) {
        return toAjax(manSchedulingInfoService.updateManSchedulingInfo(manSchedulingInfo));
    }

    /**
     * 变更生产排程信息
     */
    @ApiOperation(value = "变更生产排程信息", notes = "变更生产排程信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManSchedulingInfo manSchedulingInfo) {
        return toAjax(manSchedulingInfoService.changeManSchedulingInfo(manSchedulingInfo));
    }

    /**
     * 删除生产排程信息
     */
    @ApiOperation(value = "删除生产排程信息", notes = "删除生产排程信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产排程信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> schedulingInfoSids) {
        if (CollectionUtils.isEmpty(schedulingInfoSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manSchedulingInfoService.deleteManSchedulingInfoByIds(schedulingInfoSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManSchedulingInfo manSchedulingInfo) {
        return toAjax(manSchedulingInfoService.check(manSchedulingInfo));
    }

}
