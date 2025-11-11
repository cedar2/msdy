package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
import com.platform.ems.domain.RepSalesStatusOrderTotal;
import com.platform.ems.service.IRepSalesStatusOrderTotalService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售状况-销售占比/销售趋势/销售同比Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/sales/status/order/total")
@Api(tags = "销售状况-销售占比/销售趋势/销售同比")
public class RepSalesStatusOrderTotalController extends BaseController {

    @Autowired
    private IRepSalesStatusOrderTotalService repSalesStatusOrderTotalService;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    @PostMapping("/get/report")
    @ApiOperation(value = "查询销售状况-销售占比看板", notes = "查询销售状况-销售占比看板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesStatusOrderTotal.class))
    public AjaxResult get(String productSeasonCode) {
        return AjaxResult.success(repSalesStatusOrderTotalService.getReport(productSeasonCode));
    }

    @PostMapping("/get/report/trend")
    @ApiOperation(value = "查询销售状况销售趋势看板", notes = "查询销售状况销售趋势看板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesStatusOrderTotal.class))
    public AjaxResult getTrend(String code) {
        return AjaxResult.success(repSalesStatusOrderTotalService.geTrendt(code));
    }
    @PostMapping("/get/report/basis")
    @ApiOperation(value = "查询销售状况销售同比看板", notes = "查询销售状况销售同比看板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesStatusOrderTotal.class))
    public AjaxResult basis(String productSeasonCode) {
        return AjaxResult.success(repSalesStatusOrderTotalService.getPro(productSeasonCode));
    }
    /**
     * 获取销售状况-销售占比/销售趋势/销售同比详细信息
     */
    @ApiOperation(value = "获取销售状况-销售占比/销售趋势/销售同比详细信息", notes = "获取销售状况-销售占比/销售趋势/销售同比详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesStatusOrderTotal.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repSalesStatusOrderTotalService.selectRepSalesStatusOrderTotalById(dataRecordSid));
    }

    /**
     * 新增销售状况-销售占比/销售趋势/销售同比
     */
    @ApiOperation(value = "新增销售状况-销售占比/销售趋势/销售同比", notes = "新增销售状况-销售占比/销售趋势/销售同比")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售状况-销售占比/销售趋势/销售同比", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepSalesStatusOrderTotal repSalesStatusOrderTotal) {
        return toAjax(repSalesStatusOrderTotalService.insertRepSalesStatusOrderTotal(repSalesStatusOrderTotal));
    }

    /**
     * 删除销售状况-销售占比/销售趋势/销售同比
     */
    @ApiOperation(value = "删除销售状况-销售占比/销售趋势/销售同比", notes = "删除销售状况-销售占比/销售趋势/销售同比")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售状况-销售占比/销售趋势/销售同比", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repSalesStatusOrderTotalService.deleteRepSalesStatusOrderTotalByIds(dataRecordSids));
    }

}
