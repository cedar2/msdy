package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.BasSkuGroup;
import com.platform.ems.domain.dto.response.form.BasMaterialPackageItemFormResponse;
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
import com.platform.ems.domain.BasMaterialPackageItem;
import com.platform.ems.service.IBasMaterialPackageItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 常规辅料包-明细Controller
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@RestController
@RequestMapping("/package/item")
@Api(tags = "常规辅料包-明细")
public class BasMaterialPackageItemController extends BaseController {

    @Autowired
    private IBasMaterialPackageItemService basMaterialPackageItemService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询常规辅料包-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询常规辅料包-明细列表", notes = "查询常规辅料包-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialPackageItem.class))
    public TableDataInfo list(@RequestBody BasMaterialPackageItem basMaterialPackageItem) {
        startPage(basMaterialPackageItem);
        List<BasMaterialPackageItem> list = basMaterialPackageItemService.selectBasMaterialPackageItemList(basMaterialPackageItem);
        return getDataTable(list);
    }

    /**
     * 获取常规辅料包-明细详细信息
     */
    @ApiOperation(value = "获取常规辅料包-明细详细信息", notes = "获取常规辅料包-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialPackageItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String materialPackItemSid) {
        return AjaxResult.success(basMaterialPackageItemService.selectBasMaterialPackageItemById(materialPackItemSid));
    }

    /**
     * 新增常规辅料包-明细
     */
    @ApiOperation(value = "新增常规辅料包-明细", notes = "新增常规辅料包-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:item:add")
    @Log(title = "常规辅料包-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialPackageItem basMaterialPackageItem) {
        return toAjax(basMaterialPackageItemService.insertBasMaterialPackageItem(basMaterialPackageItem));
    }

    /**
     * 修改常规辅料包-明细
     */
    @ApiOperation(value = "修改常规辅料包-明细", notes = "修改常规辅料包-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:item:edit")
    @Log(title = "常规辅料包-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialPackageItem basMaterialPackageItem) {
        return toAjax(basMaterialPackageItemService.updateBasMaterialPackageItem(basMaterialPackageItem));
    }

    /**
     * 删除常规辅料包-明细
     */
    @ApiOperation(value = "删除常规辅料包-明细", notes = "删除常规辅料包-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:package:item:remove")
    @Log(title = "常规辅料包-明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  materialPackItemSid) {
        return toAjax(basMaterialPackageItemService.deleteBasMaterialPackageItemByIds(materialPackItemSid));
    }

    /**
     * 查询辅料包明细报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询辅料包明细报表", notes = "查询辅料包明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    public TableDataInfo report(@RequestBody BasMaterialPackageItem basMaterialPackageItem) {
        startPage(basMaterialPackageItem);
        List<BasMaterialPackageItem> list = basMaterialPackageItemService.getReportForm(basMaterialPackageItem);
        return getDataTable(list,BasMaterialPackageItemFormResponse::new);
    }

    /**
     * 导出常规辅料包-明细列表
     */
    @PreAuthorize(hasPermi = "ems:package:item:export")
    @Log(title = "常规辅料包-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出常规辅料包-明细列表", notes = "导出常规辅料包-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialPackageItem basMaterialPackageItem) throws IOException {
        List<BasMaterialPackageItem> list = basMaterialPackageItemService.selectBasMaterialPackageItemList(basMaterialPackageItem);
        List<BasMaterialPackageItemFormResponse> responseList = BeanCopyUtils.copyListProperties(list, BasMaterialPackageItemFormResponse::new);
        Map<String,Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialPackageItemFormResponse> util = new ExcelUtil<>(BasMaterialPackageItemFormResponse.class,dataMap);
        util.exportExcel(response, responseList, "物料包明细报表");
    }
}
