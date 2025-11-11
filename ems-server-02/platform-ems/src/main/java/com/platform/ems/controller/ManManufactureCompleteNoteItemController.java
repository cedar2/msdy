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

import com.platform.ems.domain.ManManufactureCompleteNoteItem;
import com.platform.ems.service.IManManufactureCompleteNoteItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产完工确认单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/man/complete/note/item")
@Api(tags = "生产完工确认单明细报表")
public class ManManufactureCompleteNoteItemController extends BaseController {

    @Autowired
    private IManManufactureCompleteNoteItemService manManufactureCompleteNoteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产完工确认单明细报表列表
     */
    @PreAuthorize(hasPermi = "ems:man:complete:note:item:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产完工确认单明细报表列表", notes = "查询生产完工确认单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureCompleteNoteItem.class))
    public TableDataInfo list(@RequestBody ManManufactureCompleteNoteItem manManufactureCompleteNoteItem) {
        startPage(manManufactureCompleteNoteItem);
        List<ManManufactureCompleteNoteItem> list = manManufactureCompleteNoteItemService.selectManManufactureCompleteNoteItemList(manManufactureCompleteNoteItem);
        return getDataTable(list);
    }

    /**
     * 导出生产完工确认单明细报表列表
     */
    @PreAuthorize(hasPermi = "ems:man:complete:note:item:export")
    @Log(title = "生产完工确认单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产完工确认单明细报表列表", notes = "导出生产完工确认单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureCompleteNoteItem manManufactureCompleteNoteItem) throws IOException {
        List<ManManufactureCompleteNoteItem> list = manManufactureCompleteNoteItemService.selectManManufactureCompleteNoteItemList(manManufactureCompleteNoteItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureCompleteNoteItem> util = new ExcelUtil<>(ManManufactureCompleteNoteItem.class, dataMap);
        util.exportExcel(response, list, "生产完工确认单明细报表");
    }



    /**
     * 获取生产完工确认单明细报表详细信息
     */
    @ApiOperation(value = "获取生产完工确认单明细报表详细信息", notes = "获取生产完工确认单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureCompleteNoteItem.class))
    @PreAuthorize(hasPermi = "ems:man:complete:note:item:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureCompleteNoteItemSid) {
        if (manufactureCompleteNoteItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureCompleteNoteItemService.selectManManufactureCompleteNoteItemById(manufactureCompleteNoteItemSid));
    }

}
