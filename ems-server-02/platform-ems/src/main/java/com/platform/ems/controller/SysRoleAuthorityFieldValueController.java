package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.SysRoleAuthorityFieldValue;
import com.platform.ems.service.ISysRoleAuthorityFieldValueService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 角色信息-权限字段值Controller
 *
 * @author chenkw
 * @date 2021-12-28
 */
@RestController
@RequestMapping("/SysRoleAuthorityFieldValue")
@Api(tags = "角色信息-权限字段值")
public class SysRoleAuthorityFieldValueController extends BaseController {

    @Autowired
    private ISysRoleAuthorityFieldValueService sysRoleAuthorityFieldValueService;

    /**
     * 查询角色信息-权限字段值列表
     */
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询角色信息-权限字段值列表", notes = "查询角色信息-权限字段值列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleAuthorityFieldValue.class))
    public TableDataInfo list(@RequestBody SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        startPage(sysRoleAuthorityFieldValue);
        List<SysRoleAuthorityFieldValue> list = sysRoleAuthorityFieldValueService.selectSysRoleAuthorityFieldValueList(sysRoleAuthorityFieldValue);
        return getDataTable(list);
    }

    /**
     * 导出角色信息-权限字段值列表
     */
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:export")
    @Log(title = "角色信息-权限字段值", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出角色信息-权限字段值列表", notes = "导出角色信息-权限字段值列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) throws IOException {
        List<SysRoleAuthorityFieldValue> list = sysRoleAuthorityFieldValueService.selectSysRoleAuthorityFieldValueList(sysRoleAuthorityFieldValue);
        ExcelUtil<SysRoleAuthorityFieldValue> util = new ExcelUtil<>(SysRoleAuthorityFieldValue.class);
        util.exportExcel(response, list, "角色信息-权限字段值" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取角色信息-权限字段值详细信息
     */
    @ApiOperation(value = "获取角色信息-权限字段值详细信息", notes = "获取角色信息-权限字段值详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleAuthorityFieldValue.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long roleAuthorityFieldValueSid) {
        if (roleAuthorityFieldValueSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysRoleAuthorityFieldValueService.selectSysRoleAuthorityFieldValueById(roleAuthorityFieldValueSid));
    }

    /**
     * 新增角色信息-权限字段值
     */
    @ApiOperation(value = "新增角色信息-权限字段值", notes = "新增角色信息-权限字段值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:add")
    @Log(title = "角色信息-权限字段值", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        return toAjax(sysRoleAuthorityFieldValueService.insertSysRoleAuthorityFieldValue(sysRoleAuthorityFieldValue));
    }

    /**
     * 修改角色信息-权限字段值
     */
    @ApiOperation(value = "修改角色信息-权限字段值", notes = "修改角色信息-权限字段值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:edit")
    @Log(title = "角色信息-权限字段值", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        return toAjax(sysRoleAuthorityFieldValueService.updateSysRoleAuthorityFieldValue(sysRoleAuthorityFieldValue));
    }

    /**
     * 变更角色信息-权限字段值
     */
    @ApiOperation(value = "变更角色信息-权限字段值", notes = "变更角色信息-权限字段值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:change")
    @Log(title = "角色信息-权限字段值", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        return toAjax(sysRoleAuthorityFieldValueService.changeSysRoleAuthorityFieldValue(sysRoleAuthorityFieldValue));
    }

    /**
     * 删除角色信息-权限字段值
     */
    @ApiOperation(value = "删除角色信息-权限字段值", notes = "删除角色信息-权限字段值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityFieldValue:remove")
    @Log(title = "角色信息-权限字段值", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> roleAuthorityFieldValueSids) {
        if (CollectionUtils.isEmpty(roleAuthorityFieldValueSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysRoleAuthorityFieldValueService.deleteSysRoleAuthorityFieldValueByIds(roleAuthorityFieldValueSids));
    }
}
