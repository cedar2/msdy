package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.ems.domain.dto.request.ManManufactureOrderConcernTaskSetRequest;
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

import com.platform.ems.domain.ManManufactureOrderProduct;
import com.platform.ems.service.IManManufactureOrderProductService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产订单产品明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/man/order/product")
@Api(tags = "生产订单产品明细报表")
public class ManManufactureOrderProductController extends BaseController {

    @Autowired
    private IManManufactureOrderProductService manManufactureOrderProductService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询订单产品明细报表列表
     */
    @PreAuthorize(hasPermi = "ems:man:manufacture:order:product:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产订单产品明细报表列表", notes = "查询订单产品明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProduct.class))
    public TableDataInfo list(@RequestBody ManManufactureOrderProduct manManufactureOrderProduct) {
        startPage(manManufactureOrderProduct);
        List<ManManufactureOrderProduct> list = manManufactureOrderProductService.selectManManufactureOrderProductList(manManufactureOrderProduct);
        return getDataTable(list);
    }

    /**
     * 导出订单产品明细报表列表
     */
    @PreAuthorize(hasPermi = "ems:man:manufacture:order:product:export")
    @Log(title = "订单产品明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出订单产品明细报表列表", notes = "导出订单产品明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOrderProduct manManufactureOrderProduct) throws IOException {
        List<ManManufactureOrderProduct> list = manManufactureOrderProductService.selectManManufactureOrderProductList(manManufactureOrderProduct);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderProduct> util = new ExcelUtil<>(ManManufactureOrderProduct.class, dataMap);
        util.exportExcel(response, list, "生产订单商品明细报表");
    }

    /**
     * 获取订单产品明细报表详细信息
     */
    @ApiOperation(value = "获取订单产品明细报表详细信息", notes = "获取订单产品明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderProduct.class))
    @PreAuthorize(hasPermi = "ems:man:manufacture:order:product:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureOrderProductSid) {
        if (manufactureOrderProductSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderProductService.selectManManufactureOrderProductById(manufactureOrderProductSid));
    }

    /**
     *  修改生产订单产品明细
     */
    @ApiOperation(value = "修改生产订单产品明细", notes = "修改生产订单产品明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:manufacture:order:product:edit")
    @Log(title = "生产订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ManManufactureOrderProduct manManufactureOrderProduct) {
        return toAjax(manManufactureOrderProductService.updateManManufactureOrderProduct(manManufactureOrderProduct));
    }

    @ApiOperation(value = "获取SKU颜色尺码双下拉框", notes = "获取SKU颜色尺码双下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getSkuList")
    public AjaxResult getSkuList(@RequestBody ManManufactureOrderProduct manManufactureOrderProduct) {
        return AjaxResult.success(manManufactureOrderProductService.getSkuList(manManufactureOrderProduct));
    }

    @ApiOperation(value = "设置计划投产日期", notes = "设置计划投产日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/planStart")
    public AjaxResult setPlanStart(@RequestBody ManManufactureOrderProduct manManufactureOrderProduct) {
        return AjaxResult.success(manManufactureOrderProductService.setPlanStart(manManufactureOrderProduct));
    }

    @ApiOperation(value = "设置计划完工日期", notes = "设置计划完工日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/planEnd")
    public AjaxResult setPlanEnd(@RequestBody ManManufactureOrderProduct manManufactureOrderProduct) {
        return AjaxResult.success(manManufactureOrderProductService.setPlanEnd(manManufactureOrderProduct));
    }

    @PostMapping("/set/toexpireDays")
    @ApiOperation(value = "生产订单商品明细报表设置到期天数", notes = "生产订单商品明细报表设置到期天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderConcernTaskSetRequest.class))
    public AjaxResult concernSetToexpireDays(@RequestBody @Valid ManManufactureOrderProduct request) {
        return toAjax(manManufactureOrderProductService.setToexpireDays(request));
    }

}
