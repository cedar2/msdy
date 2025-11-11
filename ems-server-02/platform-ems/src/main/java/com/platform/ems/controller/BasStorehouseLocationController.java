package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import com.platform.ems.domain.BasStorehouseLocation;
import com.platform.ems.service.IBasStorehouseLocationService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 仓库-库位信息Controller
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@RestController
@RequestMapping("/storehouse/location")
@Api(tags = "仓库-库位信息")
public class BasStorehouseLocationController extends BaseController {

    @Autowired
    private IBasStorehouseLocationService basStorehouseLocationService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询仓库-库位信息列表
     */
    @PreAuthorize(hasPermi = "ems:storehouse:location:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询仓库-库位信息列表", notes = "查询仓库-库位信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseLocation.class))
    public TableDataInfo list(@RequestBody BasStorehouseLocation basStorehouseLocation) {
        startPage(basStorehouseLocation);
        List<BasStorehouseLocation> list = basStorehouseLocationService.selectBasStorehouseLocationList(basStorehouseLocation);
        return getDataTable(list);
    }

    /**
     * 导出仓库-库位信息列表
     */
    @PreAuthorize(hasPermi = "ems:storehouse:location:export")
    @Log(title = "仓库-库位信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出仓库-库位信息列表", notes = "导出仓库-库位信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasStorehouseLocation basStorehouseLocation) throws IOException {
        List<BasStorehouseLocation> list = basStorehouseLocationService.selectBasStorehouseLocationList(basStorehouseLocation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasStorehouseLocation> util = new ExcelUtil<>(BasStorehouseLocation.class, dataMap);
        util.exportExcel(response, list, "仓库库位明细报表");
    }

    /**
     * 获取仓库-库位信息详细信息
     */
    @ApiOperation(value = "获取仓库-库位信息详细信息", notes = "获取仓库-库位信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasStorehouseLocation.class))
    @PreAuthorize(hasPermi = "ems:storehouse:location:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        return AjaxResult.success(basStorehouseLocationService.selectBasStorehouseLocationById(clientId));
    }

    /**
     * 新增仓库-库位信息
     */
    @ApiOperation(value = "新增仓库-库位信息", notes = "新增仓库-库位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:location:add")
    @Log(title = "仓库-库位信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasStorehouseLocation basStorehouseLocation) {
        return toAjax(basStorehouseLocationService.insertBasStorehouseLocation(basStorehouseLocation));
    }

    /**
     * 修改仓库-库位信息
     */
    @ApiOperation(value = "修改仓库-库位信息", notes = "修改仓库-库位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:location:edit")
    @Log(title = "仓库-库位信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasStorehouseLocation basStorehouseLocation) {
        return toAjax(basStorehouseLocationService.updateBasStorehouseLocation(basStorehouseLocation));
    }

    /**
     * 删除仓库-库位信息
     */
    @ApiOperation(value = "删除仓库-库位信息", notes = "删除仓库-库位信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:storehouse:location:remove")
    @Log(title = "仓库-库位信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        return toAjax(basStorehouseLocationService.deleteBasStorehouseLocationByIds(clientIds));
    }
}
