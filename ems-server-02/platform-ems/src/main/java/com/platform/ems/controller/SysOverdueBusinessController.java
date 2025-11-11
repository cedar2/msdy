package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.system.domain.SysOverdueBusiness;
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
import cn.hutool.core.util.ArrayUtil;

import com.platform.ems.service.ISysOverdueBusinessService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 已逾期警示列Controller
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@RestController
@RequestMapping("/overdue/business")
@Api(tags = "已逾期警示")
public class SysOverdueBusinessController extends BaseController {

    @Autowired
    private ISysOverdueBusinessService sysOverdueBusinessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    @PostMapping("/insert")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult insert(@RequestBody SysOverdueBusiness sysOverdueBusiness) {
        sysOverdueBusinessService.insertSysOverdueBusiness(sysOverdueBusiness);
        return AjaxResult.success();
    }

    /**
     * 查询已逾期警示列列表（用户工作台）
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询已逾期警示列列表", notes = "查询已逾期警示列列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysOverdueBusiness.class))
    public TableDataInfo list(@RequestBody SysOverdueBusiness sysOverdueBusiness) {
        startPage(sysOverdueBusiness);
        List<SysOverdueBusiness> list = sysOverdueBusinessService.selectSysOverdueBusinessList(sysOverdueBusiness);
        return getDataTable(list);
    }

    /**
     * 查询已逾期警示列报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询已逾期警示列报表", notes = "查询已逾期警示列报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysOverdueBusiness.class))
    public TableDataInfo report(@RequestBody SysOverdueBusiness sysOverdueBusiness) {
        startPage(sysOverdueBusiness);
        List<SysOverdueBusiness> list = sysOverdueBusinessService.selectSysOverdueBusinessReport(sysOverdueBusiness);
        return getDataTable(list);
    }

    /**
     * 导出已逾期警示列列表
     */
    @PreAuthorize(hasPermi = "ems:overdue:business:export")
    @Log(title = "已逾期警示", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出已逾期警示列表", notes = "导出已逾期警示列列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOverdueBusiness sysOverdueBusiness) throws IOException {
        List<SysOverdueBusiness> list = sysOverdueBusinessService.selectSysOverdueBusinessList(sysOverdueBusiness);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysOverdueBusiness> util = new ExcelUtil<>(SysOverdueBusiness.class, dataMap);
        util.exportExcel(response, list, "已逾期警示" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取已逾期警示列详细信息
     */
    @ApiOperation(value = "获取已逾期警示列详细信息", notes = "获取已逾期警示列详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysOverdueBusiness.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String id) {
        if (id == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysOverdueBusinessService.selectSysOverdueBusinessById(id));
    }



    /**
     * 删除已逾期警示列
     */
    @ApiOperation(value = "删除已逾期警示列", notes = "删除已逾期警示列")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:overdue:business:remove")
    @Log(title = "已逾期警示列", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> ids) {
        if (ArrayUtil.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysOverdueBusinessService.deleteSysOverdueBusinessByIds(ids));
    }


}
