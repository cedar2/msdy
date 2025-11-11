package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.ems.service.ISysToexpireBusinessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 即将到期预警Controller
 *
 * @author linhongwei
 * @date 2021-06-29
 */
@RestController
@RequestMapping("/business")
@Api(tags = "即将到期预警")
public class SysToexpireBusinessController extends BaseController {

    @Autowired
    private ISysToexpireBusinessService sysToexpireBusinessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @PostMapping("/insert")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult insert(@RequestBody SysToexpireBusiness sysToexpireBusiness) {
        sysToexpireBusinessService.insertSysToexpireBusiness(sysToexpireBusiness);
        return AjaxResult.success();
    }

    /**
     * 查询即将到期预警列表 (用户工作台)
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询即将到期预警列表", notes = "查询即将到期预警列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysToexpireBusiness.class))
    public TableDataInfo list(@RequestBody SysToexpireBusiness sysToexpireBusiness) {
        startPage(sysToexpireBusiness);
        List<SysToexpireBusiness> list = sysToexpireBusinessService.selectSysToexpireBusinessList(sysToexpireBusiness);
        return getDataTable(list);
    }

    /**
     * 查询即将到期预警报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询即将到期预警报表", notes = "查询即将到期预警报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysToexpireBusiness.class))
    public TableDataInfo report(@RequestBody SysToexpireBusiness sysToexpireBusiness) {
        startPage(sysToexpireBusiness);
        List<SysToexpireBusiness> list = sysToexpireBusinessService.selectSysToexpireBusinessReport(sysToexpireBusiness);
        return getDataTable(list);
    }

    /**
     * 导出即将到期预警列表
     */
    @ApiOperation(value = "导出即将到期预警列表", notes = "导出即将到期预警列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysToexpireBusiness sysToexpireBusiness) throws IOException {
        List<SysToexpireBusiness> list = sysToexpireBusinessService.selectSysToexpireBusinessList(sysToexpireBusiness);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysToexpireBusiness> util = new ExcelUtil<SysToexpireBusiness>(SysToexpireBusiness.class, dataMap);
        util.exportExcel(response, list, "即将到期预警" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取即将到期预警详细信息
     */
    @ApiOperation(value = "获取即将到期预警详细信息", notes = "获取即将到期预警详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysToexpireBusiness.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String id) {
        if (id == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysToexpireBusinessService.selectSysToexpireBusinessById(id));
    }


    /**
     * 删除即将到期预警
     */
    @ApiOperation(value = "删除即将到期预警", notes = "删除即将到期预警")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> ids) {
        if (ArrayUtil.isEmpty(ids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysToexpireBusinessService.deleteSysToexpireBusinessByIds(ids));
    }


}
