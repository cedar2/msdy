package com.platform.ems.plug.controller;

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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.service.IConManufactureDepartmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产操作部门Controller
 *
 * @author zhuangyz
 * @date 2022-07-25
 */
@RestController
@RequestMapping("/conManufactureDepartment")
@Api(tags = "生产操作部门")
public class ConManufactureDepartmentController extends BaseController {

    @Autowired
    private IConManufactureDepartmentService conManufactureDepartmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产操作部门列表
     */
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产操作部门列表", notes = "查询生产操作部门列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConManufactureDepartment.class))
    public TableDataInfo list(@RequestBody ConManufactureDepartment conManufactureDepartment) {
        startPage(conManufactureDepartment);
        List<ConManufactureDepartment> list = conManufactureDepartmentService.selectConManufactureDepartmentList(conManufactureDepartment);
        return getDataTable(list);
    }

    /**
     * 获取生产操作部门下拉列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "获取生产操作部门下拉列表", notes = "获取生产操作部门下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConManufactureDepartment.class))
    public AjaxResult getList(@RequestBody ConManufactureDepartment conManufactureDepartment) {
        List<ConManufactureDepartment> list = conManufactureDepartmentService.selectConManufactureDepartmentList(conManufactureDepartment);
        return  AjaxResult.success(list);
    }

    /**
     * 导出生产操作部门列表
     */
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:export")
    @Log(title = "生产操作部门", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产操作部门列表", notes = "导出生产操作部门列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConManufactureDepartment conManufactureDepartment) throws IOException {
        List<ConManufactureDepartment> list = conManufactureDepartmentService.selectConManufactureDepartmentList(conManufactureDepartment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConManufactureDepartment> util = new ExcelUtil<>(ConManufactureDepartment.class, dataMap);
        util.exportExcel(response, list, "生产操作部门");
    }


    /**
     * 获取生产操作部门详细信息
     */
    @ApiOperation(value = "获取生产操作部门详细信息", notes = "获取生产操作部门详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConManufactureDepartment.class))
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conManufactureDepartmentService.selectConManufactureDepartmentById(sid));
    }

    /**
     * 新增生产操作部门
     */
    @ApiOperation(value = "新增生产操作部门", notes = "新增生产操作部门")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:add")
    @Log(title = "生产操作部门", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ConManufactureDepartment conManufactureDepartment) {
        return toAjax(conManufactureDepartmentService.insertConManufactureDepartment(conManufactureDepartment));
    }

    @ApiOperation(value = "修改生产操作部门", notes = "修改生产操作部门")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:edit")
    @Log(title = "生产操作部门", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ConManufactureDepartment conManufactureDepartment) {
        return toAjax(conManufactureDepartmentService.updateConManufactureDepartment(conManufactureDepartment));
    }

    /**
     * 变更生产操作部门
     */
    @ApiOperation(value = "变更生产操作部门", notes = "变更生产操作部门")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:change")
    @Log(title = "生产操作部门", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConManufactureDepartment conManufactureDepartment) {
        return toAjax(conManufactureDepartmentService.changeConManufactureDepartment(conManufactureDepartment));
    }

    /**
     * 删除生产操作部门
     */
    @ApiOperation(value = "删除生产操作部门", notes = "删除生产操作部门")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:remove")
    @Log(title = "生产操作部门", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conManufactureDepartmentService.deleteConManufactureDepartmentByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产操作部门", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConManufactureDepartment conManufactureDepartment) {
        return AjaxResult.success(conManufactureDepartmentService.changeStatus(conManufactureDepartment));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:conManufactureDepartment:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产操作部门", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ConManufactureDepartment conManufactureDepartment) {
        return toAjax(conManufactureDepartmentService.check(conManufactureDepartment));
    }

}
