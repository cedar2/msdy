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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.RepInventoryTopStock;
import com.platform.ems.service.IRepInventoryTopStockService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * T100库存Controller
 *
 * @author linhongwei
 * @date 2022-02-25
 */
@RestController
@RequestMapping("/rep/inventory/top/stock")
@Api(tags = "T100库存")
public class RepInventoryTopStockController extends BaseController {

    @Autowired
    private IRepInventoryTopStockService repInventoryTopStockService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询T100库存列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询T100库存列表", notes = "查询T100库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepInventoryTopStock.class))
    public TableDataInfo list(@RequestBody RepInventoryTopStock repInventoryTopStock) {
        startPage(repInventoryTopStock);
        List<RepInventoryTopStock> list = repInventoryTopStockService.selectRepInventoryTopStockList(repInventoryTopStock);
        return getDataTable(list);
    }

    /**
     * 导出T100库存列表
     */
    @Log(title = "T100库存", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出T100库存列表", notes = "导出T100库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, RepInventoryTopStock repInventoryTopStock) throws IOException {
        List<RepInventoryTopStock> list = repInventoryTopStockService.selectRepInventoryTopStockList(repInventoryTopStock);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<RepInventoryTopStock> util = new ExcelUtil<>(RepInventoryTopStock.class, dataMap);
        util.exportExcel(response, list, "T100库存" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取T100库存详细信息
     */
    @ApiOperation(value = "获取T100库存详细信息", notes = "获取T100库存详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = RepInventoryTopStock.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dataRecordSid) {
        if (dataRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(repInventoryTopStockService.selectRepInventoryTopStockById(dataRecordSid));
    }

    /**
     * 新增T100库存
     */
    @ApiOperation(value = "新增T100库存", notes = "新增T100库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "T100库存", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid RepInventoryTopStock repInventoryTopStock) {
        return toAjax(repInventoryTopStockService.insertRepInventoryTopStock(repInventoryTopStock));
    }

    /**
     * 删除T100库存
     */
    @ApiOperation(value = "删除T100库存", notes = "删除T100库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:stock:remove")
    @Log(title = "T100库存", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dataRecordSids) {
        if (CollectionUtils.isEmpty(dataRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(repInventoryTopStockService.deleteRepInventoryTopStockByIds(dataRecordSids));
    }

}
