package com.platform.ems.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.SysUserAgency;
import com.platform.ems.service.ISysUserAgencyService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;

/**
 * 账号代办设置Controller
 *
 * @author qhq
 * @date 2021-10-18
 */
@RestController
@RequestMapping("/user/agency")
@Api(tags = "账号代办设置")
public class SysUserAgencyController extends BaseController {

    @Autowired
    private ISysUserAgencyService sysUserAgencyService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询账号代办设置列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询账号代办设置列表", notes = "查询账号代办设置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUserAgency.class))
    public TableDataInfo list(@RequestBody SysUserAgency sysUserAgency) {
        startPage(sysUserAgency);
        List<SysUserAgency> list = sysUserAgencyService.selectSysUserAgencyList(sysUserAgency);
        return getDataTable(list);
    }

    @PostMapping("/getListByUserId/{userId}")
    public AjaxResult getListByUserId(@PathVariable(value = "userId") String userId) {
        List<String> result = new ArrayList<>();
        SysUserAgency sysUserAgency = new SysUserAgency();
        sysUserAgency.setUserId(userId);
        List<SysUserAgency> list = sysUserAgencyService.selectSysUserAgencyList(sysUserAgency);
        for (SysUserAgency agency : list) {
            if (agency.getEndDate().getTime() > System.currentTimeMillis()) {
                result.add(agency.getAgencyUserId());
            }
        }
        return AjaxResult.success(result);
    }

    /**
     * 导出账号代办设置列表
     */
    @ApiOperation(value = "导出账号代办设置列表", notes = "导出账号代办设置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserAgency sysUserAgency) throws IOException {
        List<SysUserAgency> list = sysUserAgencyService.selectSysUserAgencyList(sysUserAgency);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysUserAgency> util = new ExcelUtil<>(SysUserAgency.class, dataMap);
        util.exportExcel(response, list, "账号代办设置");
    }


    /**
     * 获取账号代办设置详细信息
     */
    @ApiOperation(value = "获取账号代办设置详细信息", notes = "获取账号代办设置详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysUserAgency.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long userAgencySid) {
        if (userAgencySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysUserAgencyService.selectSysUserAgencyById(userAgencySid));
    }

    /**
     * 新增账号代办设置
     */
    @ApiOperation(value = "新增账号代办设置", notes = "新增账号代办设置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysUserAgency sysUserAgency) {
        return toAjax(sysUserAgencyService.insertSysUserAgency(sysUserAgency));
    }

    /**
     * 修改账号代办设置
     */
    @ApiOperation(value = "修改账号代办设置", notes = "修改账号代办设置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SysUserAgency sysUserAgency) {
        return toAjax(sysUserAgencyService.updateSysUserAgency(sysUserAgency));
    }

    /**
     * 变更账号代办设置
     */
    @ApiOperation(value = "变更账号代办设置", notes = "变更账号代办设置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SysUserAgency sysUserAgency) {
        return toAjax(sysUserAgencyService.changeSysUserAgency(sysUserAgency));
    }

    /**
     * 删除账号代办设置
     */
    @ApiOperation(value = "删除账号代办设置", notes = "删除账号代办设置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> userAgencySids) {
        if (CollectionUtils.isEmpty(userAgencySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysUserAgencyService.deleteSysUserAgencyByIds(userAgencySids));
    }

}
