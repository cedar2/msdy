package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
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
import cn.hutool.core.util.ArrayUtil;

import com.platform.system.domain.SysBusinessBcst;
import com.platform.ems.service.ISysBusinessBcstService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务动态Controller
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@RestController
@RequestMapping("/business/bcst")
@Api(tags = "业务动态")
public class SysBusinessBcstController extends BaseController {

    @Autowired
    private ISysBusinessBcstService sysBusinessBcstService;
    @Autowired
    private ISystemDictDataService sysDictDataService;



    @PostMapping("/insert")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult insert(@RequestBody SysBusinessBcst sysBusinessBcst) {
        sysBusinessBcstService.insertSysBusinessBcst(sysBusinessBcst);
        return AjaxResult.success();
    }

    /**
     * 查询业务动态列表（用户工作台）
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务动态列表", notes = "查询业务动态列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysBusinessBcst.class))
    public TableDataInfo list(@RequestBody SysBusinessBcst sysBusinessBcst) {
        startPage(sysBusinessBcst);
        List<SysBusinessBcst> list = sysBusinessBcstService.selectSysBusinessBcstList(sysBusinessBcst);
        return getDataTable(list);
    }


    /**
     * 查询业务动态报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询业务动态报表", notes = "查询业务动态报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysBusinessBcst.class))
    public TableDataInfo report(@RequestBody SysBusinessBcst sysBusinessBcst) {
        startPage(sysBusinessBcst);
        List<SysBusinessBcst> list = sysBusinessBcstService.selectSysBusinessBcstReport(sysBusinessBcst);
        return getDataTable(list);
    }

    /**
     * 导出业务动态列表
     */
    @PreAuthorize(hasPermi = "ems:business:bcst:export")
    @Log(title = "业务动态", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务动态列表", notes = "导出业务动态列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBusinessBcst sysBusinessBcst) throws IOException {
        List<SysBusinessBcst> list = sysBusinessBcstService.selectSysBusinessBcstList(sysBusinessBcst);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysBusinessBcst> util = new ExcelUtil<>(SysBusinessBcst.class, dataMap);
        util.exportExcel(response, list, "业务动态" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取业务动态详细信息
     */
    @ApiOperation(value = "获取业务动态详细信息", notes = "获取业务动态详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysBusinessBcst.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long businessBcstSid) {
        if (businessBcstSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysBusinessBcstService.selectSysBusinessBcstById(businessBcstSid));
    }


    /**
     * 删除业务动态
     */
    @ApiOperation(value = "删除业务动态", notes = "删除业务动态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:business:bcst:remove")
    @Log(title = "业务动态", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> ids) {
        if (ArrayUtil.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysBusinessBcstService.deleteSysBusinessBcstByIds(ids));
    }


}
