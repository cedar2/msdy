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
import com.platform.ems.domain.SysRoleAuthorityObject;
import com.platform.ems.service.ISysRoleAuthorityObjectService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 角色信息-权限对象Controller
 *
 * @author chenkw
 * @date 2021-12-28
 */
@RestController
@RequestMapping("/SysRoleAuthorityObject")
@Api(tags = "角色信息-权限对象")
public class SysRoleAuthorityObjectController extends BaseController {

    @Autowired
    private ISysRoleAuthorityObjectService sysRoleAuthorityObjectService;

    /**
     * 查询角色信息-权限对象列表
     */
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询角色信息-权限对象列表", notes = "查询角色信息-权限对象列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleAuthorityObject.class))
    public TableDataInfo list(@RequestBody SysRoleAuthorityObject sysRoleAuthorityObject) {
        startPage(sysRoleAuthorityObject);
        List<SysRoleAuthorityObject> list = sysRoleAuthorityObjectService.selectSysRoleAuthorityObjectList(sysRoleAuthorityObject);
        return getDataTable(list);
    }

    /**
     * 导出角色信息-权限对象列表
     */
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:export")
    @Log(title = "角色信息-权限对象", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出角色信息-权限对象列表", notes = "导出角色信息-权限对象列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleAuthorityObject sysRoleAuthorityObject) throws IOException {
        List<SysRoleAuthorityObject> list = sysRoleAuthorityObjectService.selectSysRoleAuthorityObjectList(sysRoleAuthorityObject);
        ExcelUtil<SysRoleAuthorityObject> util = new ExcelUtil<>(SysRoleAuthorityObject.class);
        util.exportExcel(response, list, "角色信息-权限对象" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取角色信息-权限对象详细信息
     */
    @ApiOperation(value = "获取角色信息-权限对象详细信息", notes = "获取角色信息-权限对象详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleAuthorityObject.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long roleAuthorityObjectSid) {
        if (roleAuthorityObjectSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysRoleAuthorityObjectService.selectSysRoleAuthorityObjectById(roleAuthorityObjectSid));
    }

    /**
     * 新增角色信息-权限对象
     */
    @ApiOperation(value = "新增角色信息-权限对象", notes = "新增角色信息-权限对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:add")
    @Log(title = "角色信息-权限对象", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysRoleAuthorityObject sysRoleAuthorityObject) {
        return toAjax(sysRoleAuthorityObjectService.insertSysRoleAuthorityObject(sysRoleAuthorityObject));
    }

    /**
     * 修改角色信息-权限对象
     */
    @ApiOperation(value = "修改角色信息-权限对象", notes = "修改角色信息-权限对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:edit")
    @Log(title = "角色信息-权限对象", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysRoleAuthorityObject sysRoleAuthorityObject) {
        return toAjax(sysRoleAuthorityObjectService.updateSysRoleAuthorityObject(sysRoleAuthorityObject));
    }

    /**
     * 变更角色信息-权限对象
     */
    @ApiOperation(value = "变更角色信息-权限对象", notes = "变更角色信息-权限对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:change")
    @Log(title = "角色信息-权限对象", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysRoleAuthorityObject sysRoleAuthorityObject) {
        return toAjax(sysRoleAuthorityObjectService.changeSysRoleAuthorityObject(sysRoleAuthorityObject));
    }

    /**
     * 删除角色信息-权限对象
     */
    @ApiOperation(value = "删除角色信息-权限对象", notes = "删除角色信息-权限对象")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysRoleAuthorityObject:remove")
    @Log(title = "角色信息-权限对象", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> roleAuthorityObjectSids) {
        if (CollectionUtils.isEmpty(roleAuthorityObjectSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysRoleAuthorityObjectService.deleteSysRoleAuthorityObjectByIds(roleAuthorityObjectSids));
    }

}
