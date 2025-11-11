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
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepPurchaseStatusMaterial;
import com.platform.ems.service.IRepPurchaseStatusMaterialService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购状况-面辅料/商品Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/Purchase/status/material")
@Api(tags = "采购状况-面辅料/商品")
public class RepPurchaseStatusMaterialController extends BaseController {

    @Autowired
    private IRepPurchaseStatusMaterialService repPurchaseStatusMaterialService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购状况-面辅料/商品列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购状况-面辅料/商品列表", notes = "查询采购状况-面辅料/商品列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepPurchaseStatusMaterial.class))
    public TableDataInfo list(@RequestBody RepPurchaseStatusMaterial repPurchaseStatusMaterial) {
        startPage(repPurchaseStatusMaterial);
        List<RepPurchaseStatusMaterial> list = repPurchaseStatusMaterialService.selectRepPurchaseStatusMaterialList(repPurchaseStatusMaterial);
        return getDataTable(list);
    }

    /**
     * 导出采购状况-面辅料/商品列表
     */
    @Log(title = "采购状况-面辅料/商品", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购状况-面辅料/商品列表", notes = "导出采购状况-面辅料/商品列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepPurchaseStatusMaterial repPurchaseStatusMaterial) throws IOException {
        List<RepPurchaseStatusMaterial> list = repPurchaseStatusMaterialService.selectRepPurchaseStatusMaterialList(repPurchaseStatusMaterial);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepPurchaseStatusMaterial> util = new ExcelUtil<>(RepPurchaseStatusMaterial.class, dataMap);
        util.exportExcel(response, list, "采购状况-面辅料/商品" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取采购状况-面辅料/商品详细信息
     */
    @ApiOperation(value = "获取采购状况-面辅料/商品详细信息", notes = "获取采购状况-面辅料/商品详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepPurchaseStatusMaterial.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repPurchaseStatusMaterialService.selectRepPurchaseStatusMaterialById(dataRecordSid));
    }

    /**
     * 新增采购状况-面辅料/商品
     */
    @ApiOperation(value = "新增采购状况-面辅料/商品", notes = "新增采购状况-面辅料/商品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购状况-面辅料/商品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepPurchaseStatusMaterial repPurchaseStatusMaterial) {
        return toAjax(repPurchaseStatusMaterialService.insertRepPurchaseStatusMaterial(repPurchaseStatusMaterial));
    }

    /**
     * 删除采购状况-面辅料/商品
     */
    @ApiOperation(value = "删除采购状况-面辅料/商品", notes = "删除采购状况-面辅料/商品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购状况-面辅料/商品", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repPurchaseStatusMaterialService.deleteRepPurchaseStatusMaterialByIds(dataRecordSids));
    }

}
