package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.InvInventorySheetItem;
import com.platform.ems.service.IInvInventorySheetItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 盘点单-明细Controller
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@RestController
@RequestMapping("/Inventory/sheet/item")
@Api(tags = "盘点单-明细")
public class InvInventorySheetItemController extends BaseController {

    @Autowired
    private IInvInventorySheetItemService invInventorySheetItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询盘点单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询盘点单-明细列表", notes = "查询盘点单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheetItem.class))
    public TableDataInfo list(@RequestBody InvInventorySheetItem invInventorySheetItem) {
        startPage();
        List<InvInventorySheetItem> list = invInventorySheetItemService.selectInvInventorySheetItemList(invInventorySheetItem);
        return getDataTable(list);
    }

    /**
     * 导出盘点单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:export")
    @Log(title = "盘点单-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出盘点单-明细列表", notes = "导出盘点单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventorySheetItem invInventorySheetItem) throws IOException {
        List<InvInventorySheetItem> list = invInventorySheetItemService.selectInvInventorySheetItemList(invInventorySheetItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventorySheetItem> util = new ExcelUtil<InvInventorySheetItem>(InvInventorySheetItem.class,dataMap);
        util.exportExcel(response, list, "盘点单-明细");
    }

    /**
     * 获取盘点单-明细详细信息
     */
    @ApiOperation(value = "获取盘点单-明细详细信息", notes = "获取盘点单-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySheetItem.class))
    @PreAuthorize(hasPermi = "ems:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inventorySheetItemSid) {
        if (inventorySheetItemSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventorySheetItemService.selectInvInventorySheetItemById(inventorySheetItemSid));
    }

    /**
     * 新增盘点单-明细
     */
    @ApiOperation(value = "新增盘点单-明细", notes = "新增盘点单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "盘点单-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid InvInventorySheetItem invInventorySheetItem) {
        return toAjax(invInventorySheetItemService.insertInvInventorySheetItem(invInventorySheetItem));
    }

    /**
     * 修改盘点单-明细
     */
    @ApiOperation(value = "修改盘点单-明细", notes = "修改盘点单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:edit")
    @Log(title = "盘点单-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvInventorySheetItem invInventorySheetItem) {
        return toAjax(invInventorySheetItemService.updateInvInventorySheetItem(invInventorySheetItem));
    }

    /**
     * 删除盘点单-明细
     */
    @ApiOperation(value = "删除盘点单-明细", notes = "删除盘点单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "盘点单-明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  inventorySheetItemSids) {
        if(ArrayUtil.isEmpty( inventorySheetItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventorySheetItemService.deleteInvInventorySheetItemByIds(inventorySheetItemSids));
    }
}
