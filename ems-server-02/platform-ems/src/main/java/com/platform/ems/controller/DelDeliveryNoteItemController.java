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
import com.platform.ems.domain.DelDeliveryNoteItem;
import com.platform.ems.service.IDelDeliveryNoteItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 交货单-明细Controller
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/note/item")
@Api(tags = "交货单-明细")
public class DelDeliveryNoteItemController extends BaseController {

    @Autowired
    private IDelDeliveryNoteItemService delDeliveryNoteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询交货单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询交货单-明细列表", notes = "查询交货单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteItem.class))
    public TableDataInfo list(@RequestBody DelDeliveryNoteItem delDeliveryNoteItem) {
        startPage();
        List<DelDeliveryNoteItem> list = delDeliveryNoteItemService.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
        return getDataTable(list);
    }

    /**
     * 导出交货单-明细列表
     */
    @PreAuthorize(hasPermi = "ems:item:export")
    @Log(title = "交货单-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出交货单-明细列表", notes = "导出交货单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelDeliveryNoteItem delDeliveryNoteItem) throws IOException {
        List<DelDeliveryNoteItem> list = delDeliveryNoteItemService.selectDelDeliveryNoteItemList(delDeliveryNoteItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<DelDeliveryNoteItem> util = new ExcelUtil<DelDeliveryNoteItem>(DelDeliveryNoteItem.class,dataMap);
        util.exportExcel(response, list, "交货单-明细");
    }

    /**
     * 获取交货单-明细详细信息
     */
    @ApiOperation(value = "获取交货单-明细详细信息", notes = "获取交货单-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteItem.class))
    @PreAuthorize(hasPermi = "ems:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
                    if(StrUtil.isEmpty(clientId)){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(delDeliveryNoteItemService.selectDelDeliveryNoteItemById(clientId));
    }

    /**
     * 新增交货单-明细
     */
    @ApiOperation(value = "新增交货单-明细", notes = "新增交货单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:add")
    @Log(title = "交货单-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DelDeliveryNoteItem delDeliveryNoteItem) {
        return toAjax(delDeliveryNoteItemService.insertDelDeliveryNoteItem(delDeliveryNoteItem));
    }

    /**
     * 修改交货单-明细
     */
    @ApiOperation(value = "修改交货单-明细", notes = "修改交货单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:edit")
    @Log(title = "交货单-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid DelDeliveryNoteItem delDeliveryNoteItem) {
        return toAjax(delDeliveryNoteItemService.updateDelDeliveryNoteItem(delDeliveryNoteItem));
    }

    /**
     * 删除交货单-明细
     */
    @ApiOperation(value = "删除交货单-明细", notes = "删除交货单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:item:remove")
    @Log(title = "交货单-明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        if(ArrayUtil.isEmpty( clientIds)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNoteItemService.deleteDelDeliveryNoteItemByIds(clientIds));
    }
}
