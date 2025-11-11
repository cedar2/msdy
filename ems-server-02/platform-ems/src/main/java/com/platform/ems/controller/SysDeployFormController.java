package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.flowable.service.ISysDeployFormService;
import com.platform.system.domain.SysDeployForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 单据类型与流程定义Controller
 *
 * @author qhq
 * @date 2021-09-03
 */
@RestController
@RequestMapping("/sysDeployForm")
@Api(tags = "单据类型与流程定义")
public class SysDeployFormController extends BaseController {

    @Autowired
    private ISysDeployFormService sysDeployFormService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型与流程定义列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型与流程定义列表", notes = "查询单据类型与流程定义列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDeployForm.class))
    public TableDataInfo list(@RequestBody SysDeployForm sysDeployForm) {
        startPage(sysDeployForm);
        List<SysDeployForm> list = sysDeployFormService.selectSysDeployFormList(sysDeployForm);
        return getDataTable(list);
    }

    /**
     * 导出单据类型与流程定义列表
     */
    @ApiOperation(value = "导出单据类型与流程定义列表", notes = "导出单据类型与流程定义列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDeployForm sysDeployForm) throws IOException {
        List<SysDeployForm> list = sysDeployFormService.selectSysDeployFormList(sysDeployForm);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SysDeployForm> util = new ExcelUtil<SysDeployForm>(SysDeployForm.class,dataMap);
        util.exportExcel(response, list, "单据类型与流程定义"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取单据类型与流程定义详细信息
     */
    @ApiOperation(value = "获取单据类型与流程定义详细信息", notes = "获取单据类型与流程定义详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDeployForm.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long formProcessRelatSid) {
        if(formProcessRelatSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysDeployFormService.selectSysDeployFormById(formProcessRelatSid));
    }

    /**
     * 新增单据类型与流程定义
     */
    @ApiOperation(value = "新增单据类型与流程定义", notes = "新增单据类型与流程定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysDeployForm sysDeployForm) {
        return toAjax(sysDeployFormService.insertSysDeployForm(sysDeployForm));
    }

    /**
     * 修改单据类型与流程定义
     */
    @ApiOperation(value = "修改单据类型与流程定义", notes = "修改单据类型与流程定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysDeployForm sysDeployForm) {
        return toAjax(sysDeployFormService.updateSysDeployForm(sysDeployForm));
    }

    /**
     * 变更单据类型与流程定义
     */
    @ApiOperation(value = "变更单据类型与流程定义", notes = "变更单据类型与流程定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysDeployForm sysDeployForm) {
        return toAjax(sysDeployFormService.changeSysDeployForm(sysDeployForm));
    }

}

