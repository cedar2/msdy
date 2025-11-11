package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
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
import com.platform.ems.domain.BasMaterialSkuDown;
import com.platform.ems.service.IBasMaterialSkuDownService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品SKU羽绒充绒量Controller
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@RestController
@RequestMapping("/down")
@Api(tags = "商品SKU羽绒充绒量")
public class BasMaterialSkuDownController extends BaseController {

    @Autowired
    private IBasMaterialSkuDownService basMaterialSkuDownService;

    /**
     * 查询商品SKU羽绒充绒量列表
     */
    @PreAuthorize(hasPermi = "ems:down:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品SKU羽绒充绒量列表", notes = "查询商品SKU羽绒充绒量列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSkuDown.class))
    public TableDataInfo list(@RequestBody BasMaterialSkuDown basMaterialSkuDown) {
        startPage();
        List<BasMaterialSkuDown> list = basMaterialSkuDownService.selectBasMaterialSkuDownList(basMaterialSkuDown);
        return getDataTable(list);
    }

    /**
     * 导出商品SKU羽绒充绒量列表
     */
    @PreAuthorize(hasPermi = "ems:down:export")
    @Log(title = "商品SKU羽绒充绒量", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品SKU羽绒充绒量列表", notes = "导出商品SKU羽绒充绒量列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialSkuDown basMaterialSkuDown) throws IOException {
        List<BasMaterialSkuDown> list = basMaterialSkuDownService.selectBasMaterialSkuDownList(basMaterialSkuDown);
        ExcelUtil<BasMaterialSkuDown> util = new ExcelUtil<BasMaterialSkuDown>(BasMaterialSkuDown.class);
        util.exportExcel(response, list, "商品SKU羽绒充绒量");
    }

    /**
     * 获取商品SKU羽绒充绒量详细信息
     */
    @ApiOperation(value = "获取商品SKU羽绒充绒量详细信息", notes = "获取商品SKU羽绒充绒量详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSkuDown.class))
    @PreAuthorize(hasPermi = "ems:down:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        return AjaxResult.success(basMaterialSkuDownService.selectBasMaterialSkuDownById(clientId));
    }

    /**
     * 新增商品SKU羽绒充绒量
     */
    @ApiOperation(value = "新增商品SKU羽绒充绒量", notes = "新增商品SKU羽绒充绒量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:down:add")
    @Log(title = "商品SKU羽绒充绒量", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialSkuDown basMaterialSkuDown) {
        return toAjax(basMaterialSkuDownService.insertBasMaterialSkuDown(basMaterialSkuDown));
    }

    /**
     * 修改商品SKU羽绒充绒量
     */
    @ApiOperation(value = "修改商品SKU羽绒充绒量", notes = "修改商品SKU羽绒充绒量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:down:edit")
    @Log(title = "商品SKU羽绒充绒量", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialSkuDown basMaterialSkuDown) {
        return toAjax(basMaterialSkuDownService.updateBasMaterialSkuDown(basMaterialSkuDown));
    }

    /**
     * 删除商品SKU羽绒充绒量
     */
    @ApiOperation(value = "删除商品SKU羽绒充绒量", notes = "删除商品SKU羽绒充绒量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:down:remove")
    @Log(title = "商品SKU羽绒充绒量", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        return toAjax(basMaterialSkuDownService.deleteBasMaterialSkuDownByIds(clientIds));
    }
}
