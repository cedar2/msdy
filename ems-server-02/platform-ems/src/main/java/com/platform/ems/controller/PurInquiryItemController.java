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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PurInquiryItem;
import com.platform.ems.service.IPurInquiryItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 物料询价单明细Controller
 *
 * @author chenkw
 * @date 2022-01-11
 */
@RestController
@RequestMapping("/pur/inquiry/item")
@Api(tags = "物料询价单明细")
public class PurInquiryItemController extends BaseController {

    @Autowired
    private IPurInquiryItemService purInquiryItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询物料询价单明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料询价单明细列表", notes = "查询物料询价单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryItem.class))
    public TableDataInfo list(@RequestBody PurInquiryItem purInquiryItem) {
        startPage(purInquiryItem);
        List<PurInquiryItem> list = purInquiryItemService.selectPurInquiryItemList(purInquiryItem);
        return getDataTable(list);
    }

    /**
     * 导出物料询价单明细列表
     */
    @Log(title = "物料询价单明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料询价单明细列表", notes = "导出物料询价单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurInquiryItem purInquiryItem) throws IOException {
        List<PurInquiryItem> list = purInquiryItemService.selectPurInquiryItemList(purInquiryItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurInquiryItem> util = new ExcelUtil<>(PurInquiryItem.class, dataMap);
        util.exportExcel(response, list, "物料询价单明细");
    }


    /**
     * 获取物料询价单明细详细信息
     */
    @ApiOperation(value = "获取物料询价单明细详细信息", notes = "获取物料询价单明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inquiryItemSid) {
        if (inquiryItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purInquiryItemService.selectPurInquiryItemById(inquiryItemSid));
    }

    /**
     * 新增物料询价单明细
     */
    @ApiOperation(value = "新增物料询价单明细", notes = "新增物料询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurInquiryItem purInquiryItem) {
        return toAjax(purInquiryItemService.insertPurInquiryItem(purInquiryItem));
    }

    /**
     * 修改物料询价单明细
     */
    @ApiOperation(value = "修改物料询价单明细", notes = "修改物料询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurInquiryItem purInquiryItem) {
        return toAjax(purInquiryItemService.updatePurInquiryItem(purInquiryItem));
    }

    /**
     * 变更物料询价单明细
     */
    @ApiOperation(value = "变更物料询价单明细", notes = "变更物料询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurInquiryItem purInquiryItem) {
        return toAjax(purInquiryItemService.changePurInquiryItem(purInquiryItem));
    }

    /**
     * 删除物料询价单明细
     */
    @ApiOperation(value = "删除物料询价单明细", notes = "删除物料询价单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料询价单明细", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> inquiryItemSids) {
        if (CollectionUtils.isEmpty(inquiryItemSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purInquiryItemService.deletePurInquiryItemByIds(inquiryItemSids));
    }

    /**
     * 查询物料询价单明细列表
     */
    @PostMapping("/getReportForm")
    @ApiOperation(value = "查询物料询价单明细列表", notes = "查询物料询价单明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurInquiryItem.class))
    public TableDataInfo getReportForm(@RequestBody PurInquiryItem request) {
        startPage(request);
        List<PurInquiryItem> list = purInquiryItemService.getReportForm(request);
        return getDataTable(list);
    }

}
