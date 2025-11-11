package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasDepartment;
import com.platform.ems.service.IBasDepartmentService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 部门档案Controller
 *
 * @author qhq
 * @date 2021-04-09
 */
@RestController
@RequestMapping("/department")
@Api(tags = "部门档案")
public class BasDepartmentController extends BaseController {

    @Autowired
    private IBasDepartmentService basDepartmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询部门档案列表
     */
    @PostMapping("/list")
    @PreAuthorize(hasPermi = "ems:department:list")
    @ApiOperation(value = "查询部门档案列表", notes = "查询部门档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasDepartment.class))
    public TableDataInfo list(@RequestBody BasDepartment basDepartment) {
        startPage(basDepartment);
        List<BasDepartment> list = basDepartmentService.selectBasDepartmentList(basDepartment);
        return getDataTable(list);
    }

    /**
     * 导出部门档案列表
     */
    @Log(title = "部门档案", businessType = BusinessType.EXPORT)
    @PreAuthorize(hasPermi = "ems:department:export")
    @ApiOperation(value = "导出部门档案列表", notes = "导出部门档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasDepartment basDepartment) throws IOException {
        List<BasDepartment> list = basDepartmentService.selectBasDepartmentList(basDepartment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasDepartment> util = new ExcelUtil<>(BasDepartment.class, dataMap);
        util.exportExcel(response, list, "部门");
    }

    /**
     * 获取部门档案详细信息
     */
    @ApiOperation(value = "获取部门档案详细信息", notes = "获取部门档案详细信息")
    @PreAuthorize(hasPermi = "ems:department:query")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasDepartment.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long departmentSid) {
        if (departmentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basDepartmentService.selectBasDepartmentById(departmentSid));
    }

    /**
     * 新增部门档案
     */
    @ApiOperation(value = "新增部门档案", notes = "新增部门档案")
    @PreAuthorize(hasPermi = "ems:department:add")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "部门档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasDepartment basDepartment) {
        int row = basDepartmentService.insertBasDepartment(basDepartment);
        return AjaxResult.success(basDepartment);
    }

    /**
     * 修改部门档案
     */
    @ApiOperation(value = "修改部门档案", notes = "修改部门档案")
    @PreAuthorize(hasPermi = "ems:department:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "部门档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasDepartment basDepartment) {
        return toAjax(basDepartmentService.updateBasDepartment(basDepartment));
    }

    /**
     * 删除部门档案
     */
    @ApiOperation(value = "删除部门档案", notes = "删除部门档案")
    @PreAuthorize(hasPermi = "ems:department:remove")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "部门档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> departmentSids) {
        if (ArrayUtil.isEmpty(departmentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basDepartmentService.deleteBasDepartmentByIds(departmentSids));
    }

    /**
     * 启停用 部门
     *
     * @param
     * @return
     */
    @PostMapping("/status")
    @PreAuthorize(hasPermi = "ems:department:enableordisable")
    @Log(title = "部门档案", businessType = BusinessType.ENBLEORDISABLE)
    @ApiOperation(value = "启停用岗位", notes = "启停用岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasDepartment basDepartment) {
        return toAjax(basDepartmentService.status(basDepartment));
    }

    /**
     * 确认 部门
     *
     * @param
     * @return
     */
    @PostMapping("/handleStatus")
    @Log(title = "部门档案", businessType = BusinessType.CHECK)
    @PreAuthorize(hasPermi = "ems:department:check")
    @ApiOperation(value = "处理状态岗位", notes = "处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult handleStatus(@RequestBody BasDepartment basDepartment) {
        return toAjax(basDepartmentService.handleStatus(basDepartment));
    }

    /**
     * 获取公司所属的部门
     */
    @PostMapping("/getCompanyDept")
    @ApiOperation(value = "获取公司所属的岗位", notes = "获取公司所属的岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasDepartment.class))
    public AjaxResult getCompanyDept(Long companySid) {
        return AjaxResult.success(basDepartmentService.getCompanyDept(companySid));
    }

    /**
     * 获取公司所属的部门
     */
    @PostMapping("/getDeptList")
    @ApiOperation(value = "获取公司所属的岗位", notes = "获取公司所属的岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasDepartment.class))
    public AjaxResult getDeptList(@RequestBody BasDepartment basDepartment) {
        basDepartment.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
        return AjaxResult.success(basDepartmentService.getDeptList(basDepartment));
    }
}
