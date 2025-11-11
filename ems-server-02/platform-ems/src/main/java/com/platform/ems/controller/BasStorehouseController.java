package com.platform.ems.controller;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasStorehouseAddr;
import com.platform.ems.domain.BasStorehouseLocation;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import com.platform.ems.domain.BasStorehouse;
import com.platform.ems.service.IBasStorehouseService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 仓库档案Controller
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/storehouse")
@Api(tags = "仓库档案")
public class BasStorehouseController extends BaseController {

    @Autowired
    private IBasStorehouseService basStorehouseService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询仓库档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询仓库档案列表", notes = "查询仓库档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouse.class))
    public TableDataInfo list(@RequestBody BasStorehouse basStorehouse) {
        startPage(basStorehouse);
        List<BasStorehouse> list = basStorehouseService.selectBasStorehouseList(basStorehouse);
        return getDataTable(list);
    }

    /**
     * 导出仓库档案列表
     */
    @ApiOperation(value = "导出仓库档案列表", notes = "导出仓库档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasStorehouse basStorehouse) throws IOException {
        List<BasStorehouse> list = basStorehouseService.selectBasStorehouseList(basStorehouse);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasStorehouse> util = new ExcelUtil<>(BasStorehouse.class, dataMap);
        util.exportExcel(response, list, "仓库");
    }

    /**
     * 获取仓库档案详细信息
     */
    @ApiOperation(value = "获取仓库档案详细信息", notes = "获取仓库档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouse.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long storehouseSid) {
        return AjaxResult.success(basStorehouseService.selectBasStorehouseById(storehouseSid));
    }

    /**
     * 新增仓库档案
     */
    @ApiOperation(value = "新增仓库档案", notes = "新增仓库档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasStorehouse basStorehouse) {
        int row = basStorehouseService.insertBasStorehouse(basStorehouse);
        return AjaxResult.success(basStorehouse);
    }

    /**
     * 修改仓库档案
     */
    @ApiOperation(value = "修改仓库档案", notes = "修改仓库档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasStorehouse basStorehouse) {
        return AjaxResult.success(basStorehouseService.updateBasStorehouse(basStorehouse));
    }

    /**
     * 删除仓库档案
     */
    @ApiOperation(value = "删除仓库档案", notes = "删除仓库档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody String[] storehouseSids) {
        return AjaxResult.success(basStorehouseService.deleteBasStorehouseByIds(storehouseSids));
    }

    /**
     * 仓库档案确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "仓库档案确认", notes = "仓库档案确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody BasStorehouse basStorehouse) {
        return AjaxResult.success(basStorehouseService.confirm(basStorehouse));
    }

    /**
     * 仓库档案变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "仓库档案变更", notes = "仓库档案变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid BasStorehouse basStorehouse) {
        return AjaxResult.success(basStorehouseService.change(basStorehouse));
    }

    /**
     * 批量启用/停用仓库档案
     */
    @PostMapping("/status")
    @ApiOperation(value = "仓库档案启用/停用", notes = "仓库档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasStorehouse basStorehouse) {
        return AjaxResult.success(basStorehouseService.status(basStorehouse));
    }


    /**
     * 仓库档案下拉框列表
     */
    @PostMapping("/getStorehouseList")
    @ApiOperation(value = "仓库档案下拉框列表", notes = "仓库档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouse.class))
    public AjaxResult getStorehouseList() {
        return AjaxResult.success(basStorehouseService.getStorehouseList());
    }

    @PostMapping("/getList")
    @ApiOperation(value = "仓库档案下拉框列表", notes = "仓库档案下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouse.class))
    public AjaxResult getList(@RequestBody BasStorehouse basStorehouse) {
        return AjaxResult.success(basStorehouseService.getList(basStorehouse));
    }


    /**
     * 获取仓库下库位列表
     */
    @PostMapping("/getStorehouseLocationListById")
    @ApiOperation(value = "获取仓库下库位列表", notes = "获取仓库下库位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseLocation.class))
    public AjaxResult getStorehouseLocationListById(Long storehouseSid) {
        return AjaxResult.success(basStorehouseService.getStorehouseLocationListById(storehouseSid));
    }

    /**
     * 获取多仓库下库位列表
     */
    @PostMapping("/getLocationList")
    @ApiOperation(value = "仓库下库位列表-带参数", notes = "获取多仓库下库位-带参数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseLocation.class))
    public AjaxResult getLocationList(@RequestBody BasStorehouse basStorehouse) {
        basStorehouse.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(basStorehouseService.getLocationList(basStorehouse));
    }

    /**
     * 查询仓库档案联系人列表
     */
    @PostMapping("/addr/list")
    @ApiOperation(value = "查询仓库档案联系人列表", notes = "查询仓库档案联系人列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseAddr.class))
    public TableDataInfo addrList(@RequestBody BasStorehouse basStorehouse) {
        BasStorehouseAddr addr = new BasStorehouseAddr();
        BeanUtil.copyProperties(basStorehouse, addr);
        startPage(addr);
        List<BasStorehouseAddr> list = basStorehouseService.selectBasStorehouseAddrList(addr);
        return getDataTable(list);
    }

}
