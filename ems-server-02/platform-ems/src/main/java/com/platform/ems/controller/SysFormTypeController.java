package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
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
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.SysFormType;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISysFormTypeService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 系统单据定义Controller
 *
 * @author qhq
 * @date 2021-09-06
 */
@RestController
@RequestMapping("/form/type")
@Api(tags = "系统单据定义")
public class SysFormTypeController extends BaseController {

    @Autowired
    private ISysFormTypeService sysFormTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询系统单据定义列表
     */
    @PreAuthorize(hasPermi = "ems:type:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询系统单据定义列表", notes = "查询系统单据定义列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysFormType.class))
    public TableDataInfo list(@RequestBody SysFormType sysFormType) {
        startPage(sysFormType);
        List<SysFormType> list = sysFormTypeService.selectSysFormTypeList(sysFormType);
        return getDataTable(list);
    }

    /**
     * 导出系统单据定义列表
     */
    @PreAuthorize(hasPermi = "ems:type:export")
    @Log(title = "系统单据定义", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出系统单据定义列表", notes = "导出系统单据定义列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFormType sysFormType) throws IOException {
        List<SysFormType> list = sysFormTypeService.selectSysFormTypeList(sysFormType);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SysFormType> util = new ExcelUtil<SysFormType>(SysFormType.class,dataMap);
        util.exportExcel(response, list, "系统单据定义"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取系统单据定义详细信息
     */
    @ApiOperation(value = "获取系统单据定义详细信息", notes = "获取系统单据定义详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysFormType.class))
    @PreAuthorize(hasPermi = "ems:type:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long id) {
                    if(id==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(sysFormTypeService.selectSysFormTypeById(id));
    }

    /**
     * 新增系统单据定义
     */
    @ApiOperation(value = "新增系统单据定义", notes = "新增系统单据定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:add")
    @Log(title = "系统单据定义", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysFormType sysFormType) {
        return toAjax(sysFormTypeService.insertSysFormType(sysFormType));
    }

    /**
     * 修改系统单据定义
     */
    @ApiOperation(value = "修改系统单据定义", notes = "修改系统单据定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:edit")
    @Log(title = "系统单据定义", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysFormType sysFormType) {
        return toAjax(sysFormTypeService.updateSysFormType(sysFormType));
    }

    /**
     * 变更系统单据定义
     */
    @ApiOperation(value = "变更系统单据定义", notes = "变更系统单据定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:change")
    @Log(title = "系统单据定义", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysFormType sysFormType) {
        return toAjax(sysFormTypeService.changeSysFormType(sysFormType));
    }

    /**
     * 删除系统单据定义
     */
    @ApiOperation(value = "删除系统单据定义", notes = "删除系统单据定义")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:type:remove")
    @Log(title = "系统单据定义", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  ids) {
        if(CollectionUtils.isEmpty( ids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysFormTypeService.deleteSysFormTypeByIds(ids));
    }

}
