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
import com.platform.ems.domain.SysRoleData;
import com.platform.ems.service.ISysRoleDataService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 数据角色Controller
 *
 * @author chenkw
 * @date 2023-05-16
 */
@RestController
@RequestMapping("/sys/role/data")
@Api(tags = "数据角色")
public class SysRoleDataController extends BaseController {

    @Autowired
    private ISysRoleDataService sysRoleDataService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询数据角色列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询数据角色列表", notes = "查询数据角色列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleData.class))
    public TableDataInfo list(@RequestBody SysRoleData sysRoleData) {
        startPage(sysRoleData);
        List<SysRoleData> list = sysRoleDataService.selectSysRoleDataList(sysRoleData);
        return getDataTable(list);
    }

    /**
     * 导出数据角色列表
     */
    @ApiOperation(value = "导出数据角色列表", notes = "导出数据角色列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleData sysRoleData) throws IOException {
        List<SysRoleData> list = sysRoleDataService.selectSysRoleDataList(sysRoleData);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysRoleData> util = new ExcelUtil<>(SysRoleData.class, dataMap);
        util.exportExcel(response, list, "数据角色");
    }

    /**
     * 获取数据角色详细信息
     */
    @ApiOperation(value = "获取数据角色详细信息", notes = "获取数据角色详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysRoleData.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long roleDataSid) {
        if (roleDataSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysRoleDataService.selectSysRoleDataById(roleDataSid));
    }

    /**
     * 新增数据角色
     */
    @ApiOperation(value = "新增数据角色", notes = "新增数据角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SysRoleData sysRoleData) {
        return toAjax(sysRoleDataService.insertSysRoleData(sysRoleData));
    }

    @ApiOperation(value = "修改数据角色", notes = "修改数据角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody SysRoleData sysRoleData) {
        return toAjax(sysRoleDataService.updateSysRoleData(sysRoleData));
    }

    /**
     * 变更数据角色
     */
    @ApiOperation(value = "变更数据角色", notes = "变更数据角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysRoleData sysRoleData) {
        return toAjax(sysRoleDataService.changeSysRoleData(sysRoleData));
    }

    /**
     * 删除数据角色
     */
    @ApiOperation(value = "删除数据角色", notes = "删除数据角色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "数据角色", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> roleDataSids) {
        if (CollectionUtils.isEmpty(roleDataSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysRoleDataService.deleteSysRoleDataByIds(roleDataSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    public AjaxResult changeStatus(@RequestBody SysRoleData sysRoleData) {
        return AjaxResult.success(sysRoleDataService.changeStatus(sysRoleData));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody SysRoleData sysRoleData) {
        return toAjax(sysRoleDataService.check(sysRoleData));
    }

}
