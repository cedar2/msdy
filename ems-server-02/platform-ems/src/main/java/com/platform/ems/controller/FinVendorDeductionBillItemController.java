package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.domain.dto.request.form.FinVendorDeductionBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinVendorDeductionBillItemFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.platform.ems.domain.FinVendorDeductionBillItem;
import com.platform.ems.service.IFinVendorDeductionBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商扣款单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-17
 */
@RestController
@RequestMapping("/vendor/deduction/item")
@Api(tags = "供应商扣款单明细报表")
public class FinVendorDeductionBillItemController extends BaseController {

    @Autowired
    private IFinVendorDeductionBillItemService finVendorDeductionBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商扣款单明细报表列表
     */
    @PostMapping("/getReportForm")
    @ApiOperation(value = "查询供应商扣款单明细报表列表", notes = "查询供应商扣款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBillItem.class))
    public TableDataInfo list(@RequestBody FinVendorDeductionBillItemFormRequest request) {
        FinVendorDeductionBillItem finVendorDeductionBillItem = new FinVendorDeductionBillItem();
        BeanUtil.copyProperties(request,finVendorDeductionBillItem);
        startPage(finVendorDeductionBillItem);
        List<FinVendorDeductionBillItem> list = finVendorDeductionBillItemService.selectFinVendorDeductionBillItemList(finVendorDeductionBillItem);
        return getDataTable(list, FinVendorDeductionBillItemFormResponse::new);
    }

    /**
     * 导出供应商扣款单明细报表列表
     */
    @ApiOperation(value = "导出供应商扣款单明细报表列表", notes = "导出供应商扣款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorDeductionBillItemFormRequest request) throws IOException {
        FinVendorDeductionBillItem finVendorDeductionBillItem = new FinVendorDeductionBillItem();
        BeanUtil.copyProperties(request,finVendorDeductionBillItem);
        List<FinVendorDeductionBillItem> responsesList = finVendorDeductionBillItemService.selectFinVendorDeductionBillItemList(finVendorDeductionBillItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorDeductionBillItem> util = new ExcelUtil<>(FinVendorDeductionBillItem.class, dataMap);
        util.exportExcel(response, responsesList, "供应商扣款单明细报表");
    }

    /**
     * 获取供应商扣款单明细报表详细信息
     */
    @ApiOperation(value = "获取供应商扣款单明细报表详细信息", notes = "获取供应商扣款单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deductionBillItemSid) {
        if (deductionBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorDeductionBillItemService.selectFinVendorDeductionBillItemById(deductionBillItemSid));
    }
}
