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
import com.platform.ems.domain.BasMaterialSkuComponent;
import com.platform.ems.service.IBasMaterialSkuComponentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品SKU实测成分Controller
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@RestController
@RequestMapping("/component")
@Api(tags = "商品SKU实测成分")
public class BasMaterialSkuComponentController extends BaseController {

    @Autowired
    private IBasMaterialSkuComponentService basMaterialSkuComponentService;

    /**
     * 查询商品SKU实测成分列表
     */
    @PreAuthorize(hasPermi = "ems:component:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品SKU实测成分列表", notes = "查询商品SKU实测成分列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSkuComponent.class))
    public TableDataInfo list(@RequestBody BasMaterialSkuComponent basMaterialSkuComponent) {
        startPage();
        List<BasMaterialSkuComponent> list = basMaterialSkuComponentService.selectBasMaterialSkuComponentList(basMaterialSkuComponent);
        return getDataTable(list);
    }

    /**
     * 导出商品SKU实测成分列表
     */
    @PreAuthorize(hasPermi = "ems:component:export")
    @Log(title = "商品SKU实测成分", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品SKU实测成分列表", notes = "导出商品SKU实测成分列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialSkuComponent basMaterialSkuComponent) throws IOException {
        List<BasMaterialSkuComponent> list = basMaterialSkuComponentService.selectBasMaterialSkuComponentList(basMaterialSkuComponent);
        ExcelUtil<BasMaterialSkuComponent> util = new ExcelUtil<BasMaterialSkuComponent>(BasMaterialSkuComponent.class);
        util.exportExcel(response, list, "商品SKU实测成分");
    }

    /**
     * 获取商品SKU实测成分详细信息
     */
    @ApiOperation(value = "获取商品SKU实测成分详细信息", notes = "获取商品SKU实测成分详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSkuComponent.class))
    @PreAuthorize(hasPermi = "ems:component:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        return AjaxResult.success(basMaterialSkuComponentService.selectBasMaterialSkuComponentById(clientId));
    }

    /**
     * 新增商品SKU实测成分
     */
    @ApiOperation(value = "新增商品SKU实测成分", notes = "新增商品SKU实测成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:component:add")
    @Log(title = "商品SKU实测成分", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialSkuComponent basMaterialSkuComponent) {
        return toAjax(basMaterialSkuComponentService.insertBasMaterialSkuComponent(basMaterialSkuComponent));
    }

    /**
     * 修改商品SKU实测成分
     */
    @ApiOperation(value = "修改商品SKU实测成分", notes = "修改商品SKU实测成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:component:edit")
    @Log(title = "商品SKU实测成分", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialSkuComponent basMaterialSkuComponent) {
        return toAjax(basMaterialSkuComponentService.updateBasMaterialSkuComponent(basMaterialSkuComponent));
    }

    /**
     * 删除商品SKU实测成分
     */
    @ApiOperation(value = "删除商品SKU实测成分", notes = "删除商品SKU实测成分")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:component:remove")
    @Log(title = "商品SKU实测成分", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        return toAjax(basMaterialSkuComponentService.deleteBasMaterialSkuComponentByIds(clientIds));
    }
}
