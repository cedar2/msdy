package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import javax.validation.Valid;
import com.platform.ems.domain.BasPlantCapacity;
import com.platform.ems.service.IBasPlantCapacityService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂-富余产能明细Controller
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@RestController
@RequestMapping("/capacity")
@Api(tags = "工厂-富余产能明细")
public class BasPlantCapacityController extends BaseController {

    @Autowired
    private IBasPlantCapacityService basPlantCapacityService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询工厂-富余产能明细列表
     */
    @PreAuthorize(hasPermi = "ems:capacity:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂-富余产能明细列表", notes = "查询工厂-富余产能明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantCapacity.class))
    public TableDataInfo list(@RequestBody BasPlantCapacity basPlantCapacity) {
        startPage();
        List<BasPlantCapacity> list = basPlantCapacityService.selectBasPlantCapacityList(basPlantCapacity);
        return getDataTable(list);
    }

    /**
     * 导出工厂-富余产能明细列表
     */
    @PreAuthorize(hasPermi = "ems:capacity:export")
    @Log(title = "工厂-富余产能明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂-富余产能明细列表", notes = "导出工厂-富余产能明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlantCapacity basPlantCapacity) throws IOException {
        List<BasPlantCapacity> list = basPlantCapacityService.selectBasPlantCapacityList(basPlantCapacity);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasPlantCapacity> util = new ExcelUtil<BasPlantCapacity>(BasPlantCapacity.class,dataMap);
        util.exportExcel(response, list, "工厂-富余产能明细");
    }

    /**
     * 获取工厂-富余产能明细详细信息
     */
    @ApiOperation(value = "获取工厂-富余产能明细详细信息", notes = "获取工厂-富余产能明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantCapacity.class))
//    @PreAuthorize(hasPermi = "ems:capacity:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long plantOvercapacitySid) {
        return AjaxResult.success(basPlantCapacityService.selectBasPlantCapacityById(plantOvercapacitySid));
    }

    /**
     * 新增工厂-富余产能明细
     */
    @ApiOperation(value = "新增工厂-富余产能明细", notes = "新增工厂-富余产能明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:capacity:add")
    @Log(title = "工厂-富余产能明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlantCapacity basPlantCapacity) {
        return toAjax(basPlantCapacityService.insertBasPlantCapacity(basPlantCapacity));
    }

    /**
     * 修改工厂-富余产能明细
     */
    @ApiOperation(value = "修改工厂-富余产能明细", notes = "修改工厂-富余产能明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:capacity:edit")
    @Log(title = "工厂-富余产能明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPlantCapacity basPlantCapacity) {
        return toAjax(basPlantCapacityService.updateBasPlantCapacity(basPlantCapacity));
    }

    /**
     * 删除工厂-富余产能明细
     */
    @ApiOperation(value = "删除工厂-富余产能明细", notes = "删除工厂-富余产能明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:capacity:remove")
    @Log(title = "工厂-富余产能明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  plantOvercapacitySids) {
        return toAjax(basPlantCapacityService.deleteBasPlantCapacityByIds(plantOvercapacitySids));
    }
}
