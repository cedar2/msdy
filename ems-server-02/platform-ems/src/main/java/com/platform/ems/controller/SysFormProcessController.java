package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.SysFormProcess;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysDictDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.service.ISysFormProcessService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * 单据关联流程实例Controller
 *
 * @author qhq
 * @date 2021-09-06
 */
@RestController
@RequestMapping("/form/process")
@Api(tags = "单据关联流程实例")
public class SysFormProcessController extends BaseController {
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据关联流程实例列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据关联流程实例列表", notes = "查询单据关联流程实例列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysFormProcess.class))
    public TableDataInfo list(@RequestBody SysFormProcess sysFormProcess) {
        startPage(sysFormProcess);
        List<SysFormProcess> list = sysFormProcessService.selectSysFormProcessList(sysFormProcess);
        return getDataTable(list);
    }

    /**
     * 导出单据关联流程实例列表
     */
    @ApiOperation(value = "导出单据关联流程实例列表", notes = "导出单据关联流程实例列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFormProcess sysFormProcess) throws IOException {
        List<SysFormProcess> list = sysFormProcessService.selectSysFormProcessList(sysFormProcess);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysFormProcess> util = new ExcelUtil<SysFormProcess>(SysFormProcess.class, dataMap);
        util.exportExcel(response, list, "单据关联流程实例" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取单据关联流程实例详细信息
     */
    @ApiOperation(value = "获取单据关联流程实例详细信息", notes = "获取单据关联流程实例详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysFormProcess.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long id) {
        if (id == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysFormProcessService.selectSysFormProcessById(id));
    }

    /**
     * 新增单据关联流程实例
     */
    @ApiOperation(value = "新增单据关联流程实例", notes = "新增单据关联流程实例")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysFormProcess sysFormProcess) {
        return toAjax(sysFormProcessService.insertSysFormProcess(sysFormProcess));
    }

    /**
     * 修改单据关联流程实例
     */
    @ApiOperation(value = "修改单据关联流程实例", notes = "修改单据关联流程实例")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysFormProcess sysFormProcess) {
        return toAjax(sysFormProcessService.updateSysFormProcess(sysFormProcess));
    }

    /**
     * 变更单据关联流程实例
     */
    @ApiOperation(value = "变更单据关联流程实例", notes = "变更单据关联流程实例")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysFormProcess sysFormProcess) {
        return toAjax(sysFormProcessService.changeSysFormProcess(sysFormProcess));
    }

    /**
     * 删除单据关联流程实例
     */
    @ApiOperation(value = "删除单据关联流程实例", notes = "删除单据关联流程实例")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysFormProcessService.deleteSysFormProcessByIds(ids));
    }
}
