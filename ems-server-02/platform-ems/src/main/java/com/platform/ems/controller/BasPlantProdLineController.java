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
import com.platform.ems.domain.BasPlantProdLine;
import com.platform.ems.service.IBasPlantProdLineService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂-生产线信息Controller
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@RestController
@RequestMapping("/line")
@Api(tags = "工厂-生产线信息")
public class BasPlantProdLineController extends BaseController {

    @Autowired
    private IBasPlantProdLineService basPlantProdLineService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询工厂-生产线信息列表
     */
    @PreAuthorize(hasPermi = "ems:line:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂-生产线信息列表", notes = "查询工厂-生产线信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantProdLine.class))
    public TableDataInfo list(@RequestBody BasPlantProdLine basPlantProdLine) {
        startPage();
        List<BasPlantProdLine> list = basPlantProdLineService.selectBasPlantProdLineList(basPlantProdLine);
        return getDataTable(list);
    }

    /**
     * 导出工厂-生产线信息列表
     */
    @PreAuthorize(hasPermi = "ems:line:export")
    @Log(title = "工厂-生产线信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂-生产线信息列表", notes = "导出工厂-生产线信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlantProdLine basPlantProdLine) throws IOException {
        List<BasPlantProdLine> list = basPlantProdLineService.selectBasPlantProdLineList(basPlantProdLine);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasPlantProdLine> util = new ExcelUtil<BasPlantProdLine>(BasPlantProdLine.class,dataMap);
        util.exportExcel(response, list, "工厂-生产线信息");
    }

    /**
     * 获取工厂-生产线信息详细信息
     */
    @ApiOperation(value = "获取工厂-生产线信息详细信息", notes = "获取工厂-生产线信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantProdLine.class))
//    @PreAuthorize(hasPermi = "ems:line:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productLineSid) {
        return AjaxResult.success(basPlantProdLineService.selectBasPlantProdLineById(productLineSid));
    }

    /**
     * 新增工厂-生产线信息
     */
    @ApiOperation(value = "新增工厂-生产线信息", notes = "新增工厂-生产线信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:line:add")
    @Log(title = "工厂-生产线信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlantProdLine basPlantProdLine) {
        return toAjax(basPlantProdLineService.insertBasPlantProdLine(basPlantProdLine));
    }

    /**
     * 修改工厂-生产线信息
     */
    @ApiOperation(value = "修改工厂-生产线信息", notes = "修改工厂-生产线信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:line:edit")
    @Log(title = "工厂-生产线信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPlantProdLine basPlantProdLine) {
        return toAjax(basPlantProdLineService.updateBasPlantProdLine(basPlantProdLine));
    }

    /**
     * 删除工厂-生产线信息
     */
    @ApiOperation(value = "删除工厂-生产线信息", notes = "删除工厂-生产线信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:line:remove")
    @Log(title = "工厂-生产线信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  productLineSids) {
        return toAjax(basPlantProdLineService.deleteBasPlantProdLineByIds(productLineSids));
    }
}
