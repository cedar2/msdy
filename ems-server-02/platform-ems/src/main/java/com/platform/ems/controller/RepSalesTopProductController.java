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
import com.platform.ems.domain.RepSalesTopProduct;
import com.platform.ems.service.IRepSalesTopProductService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售TOP10款Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/sales/top/product")
@Api(tags = "销售TOP10款")
public class RepSalesTopProductController extends BaseController {

    @Autowired
    private IRepSalesTopProductService repSalesTopProductService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售TOP10款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售TOP10款列表", notes = "查询销售TOP10款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesTopProduct.class))
    public TableDataInfo list(@RequestBody RepSalesTopProduct repSalesTopProduct) {
        startPage(repSalesTopProduct);
        List<RepSalesTopProduct> list = repSalesTopProductService.selectRepSalesTopProductList(repSalesTopProduct);
        return getDataTable(list);
    }

    /**
     * 导出销售TOP10款列表
     */
    @Log(title = "销售TOP10款", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售TOP10款列表", notes = "导出销售TOP10款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepSalesTopProduct repSalesTopProduct) throws IOException {
        List<RepSalesTopProduct> list = repSalesTopProductService.selectRepSalesTopProductList(repSalesTopProduct);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepSalesTopProduct> util = new ExcelUtil<>(RepSalesTopProduct.class, dataMap);
        util.exportExcel(response, list, "销售TOP10款" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取销售TOP10款详细信息
     */
    @ApiOperation(value = "获取销售TOP10款详细信息", notes = "获取销售TOP10款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepSalesTopProduct.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repSalesTopProductService.selectRepSalesTopProductById(dataRecordSid));
    }

    /**
     * 新增销售TOP10款
     */
    @ApiOperation(value = "新增销售TOP10款", notes = "新增销售TOP10款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售TOP10款", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepSalesTopProduct repSalesTopProduct) {
        return toAjax(repSalesTopProductService.insertRepSalesTopProduct(repSalesTopProduct));
    }

    /**
     * 删除销售TOP10款
     */
    @ApiOperation(value = "删除销售TOP10款", notes = "删除销售TOP10款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售TOP10款", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repSalesTopProductService.deleteRepSalesTopProductByIds(dataRecordSids));
    }

}
