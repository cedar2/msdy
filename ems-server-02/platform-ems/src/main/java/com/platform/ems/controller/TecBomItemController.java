package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.request.InvFundRequest;
import com.platform.ems.domain.dto.response.InvFundResponse;
import com.platform.ems.service.ITecBomItemService;
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
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * 物料清单（BOM）组件清单Controller
 *
 * @author qhq
 * @date 2021-03-15
 */
@RestController
@RequestMapping("/bom/item")
@Api(tags = "物料清单（BOM）组件清单")
public class TecBomItemController extends BaseController {

    @Autowired
    private ITecBomItemService tecBomItemService;

    /**
     * 查询物料清单（BOM）组件清单列表
     */
    @PreAuthorize(hasPermi = "ems:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询物料清单（BOM）组件清单列表", notes = "查询物料清单（BOM）组件清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomItem.class))
    public TableDataInfo list(@RequestBody TecBomItem tecBomItem) {
        startPage();
        List<TecBomItem> list = tecBomItemService.selectTecBomItemList(tecBomItem);
        return getDataTable(list);
    }

    @PostMapping("/materialCode/sku1/add")
    @ApiOperation(value = "查询按款选料(按款色)", notes = "查询按款选料(按款色)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvFundResponse.class))
    public TableDataInfo getFund(@RequestBody InvFundRequest invFundRequest) {
        startPage(invFundRequest);
        List<InvFundResponse> list = tecBomItemService.getFund(invFundRequest);
        return getDataTable(list);
    }

    @PostMapping("/materialCode/sku2/add")
    @ApiOperation(value = "查询按款选料(按款色码)", notes = "查询按款选料(按款色码)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvFundResponse.class))
    public TableDataInfo getFundSku2(@RequestBody InvFundRequest invFundRequest) {
        startPage(invFundRequest);
        List<InvFundResponse> list = tecBomItemService.getFundSku2(invFundRequest);
        return getDataTable(list);
    }

    /**
     * 导出物料清单（BOM）组件清单列表
     */
    @PreAuthorize(hasPermi = "ems:item:export")
    @Log(title = "物料清单（BOM）组件清单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料清单（BOM）组件清单列表", notes = "导出物料清单（BOM）组件清单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecBomItem tecBomItem) throws IOException {
        List<TecBomItem> list = tecBomItemService.selectTecBomItemList(tecBomItem);
        ExcelUtil<TecBomItem> util = new ExcelUtil<TecBomItem>(TecBomItem.class);
        util.exportExcel(response, list, "item");
    }

    /**
     * 获取物料清单（BOM）组件清单详细信息
     */
    @ApiOperation(value = "获取物料清单（BOM）组件清单详细信息", notes = "获取物料清单（BOM）组件清单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomItem.class))
    @PreAuthorize(hasPermi = "ems:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long clientId) {
        return AjaxResult.success(tecBomItemService.selectTecBomItemById(clientId));
    }

    /**
     * 新增物料清单（BOM）组件清单
     */
    @ApiOperation(value = "新增物料清单（BOM）组件清单", notes = "新增物料清单（BOM）组件清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "物料清单（BOM）组件清单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecBomItem tecBomItem) {
        return toAjax(tecBomItemService.insertTecBomItem(tecBomItem));
    }

    /**
     * 修改物料清单（BOM）组件清单
     */
    @ApiOperation(value = "修改物料清单（BOM）组件清单", notes = "修改物料清单（BOM）组件清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:edit")
    @Log(title = "物料清单（BOM）组件清单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecBomItem tecBomItem) {
        return toAjax(tecBomItemService.updateTecBomItem(tecBomItem));
    }

    /**
     * 删除物料清单（BOM）组件清单
     */
    @ApiOperation(value = "删除物料清单（BOM）组件清单", notes = "删除物料清单（BOM）组件清单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "物料清单（BOM）组件清单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  clientIds) {
        return toAjax(tecBomItemService.deleteTecBomItemByIds(clientIds));
    }
}
