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
import com.platform.ems.domain.BasPlantCategory;
import com.platform.ems.service.IBasPlantCategoryService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 工厂-擅长品类信息Controller
 *
 * @author linhongwei
 * @date 2021-03-27
 */
@RestController
@RequestMapping("/category")
@Api(tags = "工厂-擅长品类信息")
public class BasPlantCategoryController extends BaseController {

    @Autowired
    private IBasPlantCategoryService basPlantCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询工厂-擅长品类信息列表
     */
    @PreAuthorize(hasPermi = "ems:category:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询工厂-擅长品类信息列表", notes = "查询工厂-擅长品类信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantCategory.class))
    public TableDataInfo list(@RequestBody BasPlantCategory basPlantCategory) {
        startPage();
        List<BasPlantCategory> list = basPlantCategoryService.selectBasPlantCategoryList(basPlantCategory);
        return getDataTable(list);
    }

    /**
     * 导出工厂-擅长品类信息列表
     */
    @PreAuthorize(hasPermi = "ems:category:export")
    @Log(title = "工厂-擅长品类信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出工厂-擅长品类信息列表", notes = "导出工厂-擅长品类信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPlantCategory basPlantCategory) throws IOException {
        List<BasPlantCategory> list = basPlantCategoryService.selectBasPlantCategoryList(basPlantCategory);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasPlantCategory> util = new ExcelUtil<BasPlantCategory>(BasPlantCategory.class,dataMap);
        util.exportExcel(response, list, "工厂-擅长品类信息");
    }

    /**
     * 获取工厂-擅长品类信息详细信息
     */
    @ApiOperation(value = "获取工厂-擅长品类信息详细信息", notes = "获取工厂-擅长品类信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPlantCategory.class))
    @PreAuthorize(hasPermi = "ems:category:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long plantCategorySid) {
        return AjaxResult.success(basPlantCategoryService.selectBasPlantCategoryById(plantCategorySid));
    }

    /**
     * 新增工厂-擅长品类信息
     */
    @ApiOperation(value = "新增工厂-擅长品类信息", notes = "新增工厂-擅长品类信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:add")
    @Log(title = "工厂-擅长品类信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPlantCategory basPlantCategory) {
        return toAjax(basPlantCategoryService.insertBasPlantCategory(basPlantCategory));
    }

    /**
     * 修改工厂-擅长品类信息
     */
    @ApiOperation(value = "修改工厂-擅长品类信息", notes = "修改工厂-擅长品类信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:edit")
    @Log(title = "工厂-擅长品类信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPlantCategory basPlantCategory) {
        return toAjax(basPlantCategoryService.updateBasPlantCategory(basPlantCategory));
    }

    /**
     * 删除工厂-擅长品类信息
     */
    @ApiOperation(value = "删除工厂-擅长品类信息", notes = "删除工厂-擅长品类信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:category:remove")
    @Log(title = "工厂-擅长品类信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  plantCategorySids) {
        return toAjax(basPlantCategoryService.deleteBasPlantCategoryByIds(plantCategorySids));
    }
}
