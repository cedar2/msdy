package com.platform.system.controller;

import java.util.List;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.system.service.ISysDefaultSettingSystemService;

/**
 * 系统默认设置_系统级Controller
 *
 * @author chenkw
 * @date 2022-04-22
 */
@RestController
@RequestMapping("/default/setting/system")
@Api(tags = "系统默认设置_系统级")
public class SysDefaultSettingSystemController extends BaseController {

    @Autowired
    private ISysDefaultSettingSystemService sysDefaultSettingSystemService;

    /**
     * 查询系统默认设置_系统级列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询系统默认设置_系统级列表", notes = "查询系统默认设置_系统级列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDefaultSettingSystem.class))
    public TableDataInfo list(@RequestBody SysDefaultSettingSystem sysDefaultSettingSystem) {
        startPage(sysDefaultSettingSystem);
        List<SysDefaultSettingSystem> list = sysDefaultSettingSystemService.selectSysDefaultSettingSystemList(sysDefaultSettingSystem);
        return getDataTable(list);
    }
    @PostMapping("/get")
    @ApiOperation(value = "查询系统默认设置_系统级列表", notes = "查询系统默认设置_系统级列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDefaultSettingSystem.class))
    public AjaxResult get(@RequestBody SysDefaultSettingSystem sysDefaultSettingSystem) {
        List<SysDefaultSettingSystem> list = sysDefaultSettingSystemService.selectSysDefaultSettingSystemList(sysDefaultSettingSystem);
        SysDefaultSettingSystem settingSystem = list.get(0);
        return AjaxResult.success(settingSystem);
    }

    /**
     * 获取系统默认设置_系统级详细信息
     */
    @ApiOperation(value = "获取系统默认设置_系统级详细信息", notes = "获取系统默认设置_系统级详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDefaultSettingSystem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        if (StrUtil.isEmpty(clientId)) {
            throw new CheckedException("参数缺失");
        }
        SysDefaultSettingSystem sysDefaultSettingSystem = sysDefaultSettingSystemService.selectSysDefaultSettingSystemById(clientId);
        return AjaxResult.success(sysDefaultSettingSystem);
    }

    /**
     * 新增系统默认设置_系统级
     */
    @ApiOperation(value = "新增系统默认设置_系统级", notes = "新增系统默认设置_系统级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysDefaultSettingSystem sysDefaultSettingSystem) {
        return toAjax(sysDefaultSettingSystemService.insertSysDefaultSettingSystem(sysDefaultSettingSystem));
    }

    /**
     * 修改系统默认设置_系统级
     */
    @ApiOperation(value = "修改系统默认设置_系统级", notes = "修改系统默认设置_系统级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysDefaultSettingSystem sysDefaultSettingSystem) {
        return toAjax(sysDefaultSettingSystemService.updateSysDefaultSettingSystem(sysDefaultSettingSystem));
    }

    /**
     * 变更系统默认设置_系统级
     */
    @ApiOperation(value = "变更系统默认设置_系统级", notes = "变更系统默认设置_系统级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysDefaultSettingSystem sysDefaultSettingSystem) {

        return toAjax(sysDefaultSettingSystemService.changeSysDefaultSettingSystem(sysDefaultSettingSystem));
    }

    /**
     * 删除系统默认设置_系统级
     */
    @ApiOperation(value = "删除系统默认设置_系统级", notes = "删除系统默认设置_系统级")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysDefaultSettingSystemService.deleteSysDefaultSettingSystemByIds(clientIds));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysDefaultSettingSystem sysDefaultSettingSystem) {
        return toAjax(sysDefaultSettingSystemService.check(sysDefaultSettingSystem));
    }

}
