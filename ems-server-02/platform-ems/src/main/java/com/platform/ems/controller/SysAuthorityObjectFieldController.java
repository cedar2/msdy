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
import com.platform.ems.domain.SysAuthorityObjectField;
import com.platform.ems.service.ISysAuthorityObjectFieldService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 权限对象-字段明细Controller
 *
 * @author chenkw
 * @date 2021-12-28
 */
@RestController
@RequestMapping("/SysAuthorityObjectField")
@Api(tags = "权限对象-字段明细")
public class SysAuthorityObjectFieldController extends BaseController {

    @Autowired
    private ISysAuthorityObjectFieldService sysAuthorityObjectFieldService;

    /**
     * 查询权限对象-字段明细列表
     */
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询权限对象-字段明细列表", notes = "查询权限对象-字段明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysAuthorityObjectField.class))
    public TableDataInfo list(@RequestBody SysAuthorityObjectField sysAuthorityObjectField) {
        startPage(sysAuthorityObjectField);
        List<SysAuthorityObjectField> list = sysAuthorityObjectFieldService.selectSysAuthorityObjectFieldList(sysAuthorityObjectField);
        return getDataTable(list);
    }

    /**
     * 导出权限对象-字段明细列表
     */
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:export")
    @Log(title = "权限对象-字段明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出权限对象-字段明细列表", notes = "导出权限对象-字段明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysAuthorityObjectField sysAuthorityObjectField) throws IOException {
        List<SysAuthorityObjectField> list = sysAuthorityObjectFieldService.selectSysAuthorityObjectFieldList(sysAuthorityObjectField);
        ExcelUtil<SysAuthorityObjectField> util = new ExcelUtil<>(SysAuthorityObjectField.class);
        util.exportExcel(response, list, "权限对象-字段明细" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取权限对象-字段明细详细信息
     */
    @ApiOperation(value = "获取权限对象-字段明细详细信息", notes = "获取权限对象-字段明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysAuthorityObjectField.class))
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long authorityObjectFieldSid) {
        if (authorityObjectFieldSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysAuthorityObjectFieldService.selectSysAuthorityObjectFieldById(authorityObjectFieldSid));
    }

    /**
     * 新增权限对象-字段明细
     */
    @ApiOperation(value = "新增权限对象-字段明细", notes = "新增权限对象-字段明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:add")
    @Log(title = "权限对象-字段明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysAuthorityObjectField sysAuthorityObjectField) {
        return toAjax(sysAuthorityObjectFieldService.insertSysAuthorityObjectField(sysAuthorityObjectField));
    }

    /**
     * 修改权限对象-字段明细
     */
    @ApiOperation(value = "修改权限对象-字段明细", notes = "修改权限对象-字段明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:edit")
    @Log(title = "权限对象-字段明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysAuthorityObjectField sysAuthorityObjectField) {
        return toAjax(sysAuthorityObjectFieldService.updateSysAuthorityObjectField(sysAuthorityObjectField));
    }

    /**
     * 变更权限对象-字段明细
     */
    @ApiOperation(value = "变更权限对象-字段明细", notes = "变更权限对象-字段明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:change")
    @Log(title = "权限对象-字段明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysAuthorityObjectField sysAuthorityObjectField) {
        return toAjax(sysAuthorityObjectFieldService.changeSysAuthorityObjectField(sysAuthorityObjectField));
    }

    /**
     * 删除权限对象-字段明细
     */
    @ApiOperation(value = "删除权限对象-字段明细", notes = "删除权限对象-字段明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "system:SysAuthorityObjectField:remove")
    @Log(title = "权限对象-字段明细", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> authorityObjectFieldSids) {
        if (CollectionUtils.isEmpty(authorityObjectFieldSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysAuthorityObjectFieldService.deleteSysAuthorityObjectFieldByIds(authorityObjectFieldSids));
    }

}
