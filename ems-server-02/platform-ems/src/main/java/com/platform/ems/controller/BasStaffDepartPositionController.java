package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasStaffDepartPosition;
import com.platform.ems.service.IBasStaffDepartPositionService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 员工所属部门岗位信息Controller
 *
 * @author qhq
 * @date 2021-03-18
 */
@RestController
@RequestMapping("/staffDeptPosition")
@Api(tags = "员工所属部门岗位信息")
public class BasStaffDepartPositionController extends BaseController {

    @Autowired
    private IBasStaffDepartPositionService basStaffDepartPositionService;

    /**
     * 查询员工所属部门岗位信息列表
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:list")
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询员工所属部门岗位信息列表", notes = "查询员工所属部门岗位信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaffDepartPosition.class))
    public TableDataInfo list(@RequestBody BasStaffDepartPosition basStaffDepartPosition) {
        startPage();
        List<BasStaffDepartPosition> list = basStaffDepartPositionService.selectBasStaffDepartPositionList(basStaffDepartPosition);
        return getDataTable(list);
    }

    /**
     * 导出员工所属部门岗位信息列表
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:export")
     */
    @Log(title = "员工所属部门岗位信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出员工所属部门岗位信息列表", notes = "导出员工所属部门岗位信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasStaffDepartPosition basStaffDepartPosition) throws IOException {
        List<BasStaffDepartPosition> list = basStaffDepartPositionService.selectBasStaffDepartPositionList(basStaffDepartPosition);
        ExcelUtil<BasStaffDepartPosition> util = new ExcelUtil<BasStaffDepartPosition>(BasStaffDepartPosition.class);
        util.exportExcel(response, list, "员工所属部门岗位信息");
    }

    /**
     * 获取员工所属部门岗位信息详细信息
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:query")
     */
    @ApiOperation(value = "获取员工所属部门岗位信息详细信息", notes = "获取员工所属部门岗位信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStaffDepartPosition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String sid) {
        return AjaxResult.success(basStaffDepartPositionService.selectBasStaffDepartPositionById(sid));
    }

    /**
     * 新增员工所属部门岗位信息
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:add")
     */
    @ApiOperation(value = "新增员工所属部门岗位信息", notes = "新增员工所属部门岗位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "员工所属部门岗位信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasStaffDepartPosition basStaffDepartPosition) {
        return toAjax(basStaffDepartPositionService.insertBasStaffDepartPosition(basStaffDepartPosition));
    }

    /**
     * 修改员工所属部门岗位信息
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:edit")
     */
    @ApiOperation(value = "修改员工所属部门岗位信息", notes = "修改员工所属部门岗位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "员工所属部门岗位信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasStaffDepartPosition basStaffDepartPosition) {
        return toAjax(basStaffDepartPositionService.updateBasStaffDepartPosition(basStaffDepartPosition));
    }

    /**
     * 删除员工所属部门岗位信息
     * @PreAuthorize(hasPermi = "ems:staffDeptPosition:remove")
     */
    @ApiOperation(value = "删除员工所属部门岗位信息", notes = "删除员工所属部门岗位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "员工所属部门岗位信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  sids) {
        return toAjax(basStaffDepartPositionService.deleteBasStaffDepartPositionByIds(sids));
    }
}
