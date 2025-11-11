package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.domain.dto.request.form.FinVendorAccountAdjustBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinVendorAccountAdjustBillItemFormResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
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

import com.platform.ems.domain.FinVendorAccountAdjustBillItem;
import com.platform.ems.service.IFinVendorAccountAdjustBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商调账单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-21
 */
@RestController
@RequestMapping("/fin/vendor/account/bill/item")
@Api(tags = "供应商调账单明细报表")
public class FinVendorAccountAdjustBillItemController extends BaseController {

    @Autowired
    private IFinVendorAccountAdjustBillItemService finVendorAccountAdjustBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商调账单明细报表列表
     */
    @PostMapping("/getReportForm")
    @ApiOperation(value = "查询供应商调账单明细报表列表", notes = "查询供应商调账单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountAdjustBillItem.class))
    public TableDataInfo list(@RequestBody FinVendorAccountAdjustBillItemFormRequest request) {
        FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem = new FinVendorAccountAdjustBillItem();
        BeanUtil.copyProperties(request, finVendorAccountAdjustBillItem);
        startPage(finVendorAccountAdjustBillItem);
        List<FinVendorAccountAdjustBillItem> list = finVendorAccountAdjustBillItemService.selectFinVendorAccountAdjustBillItemList(finVendorAccountAdjustBillItem);
        return getDataTable(list, FinVendorAccountAdjustBillItemFormResponse::new);
    }

    /**
     * 导出供应商调账单明细报表列表
     */
    @ApiOperation(value = "导出供应商调账单明细报表列表", notes = "导出供应商调账单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorAccountAdjustBillItemFormRequest request) throws IOException {

        FinVendorAccountAdjustBillItem finVendorAccountAdjustBillItem = new FinVendorAccountAdjustBillItem();
        BeanUtil.copyProperties(request, finVendorAccountAdjustBillItem);

        List<FinVendorAccountAdjustBillItem> list = finVendorAccountAdjustBillItemService.selectFinVendorAccountAdjustBillItemList(finVendorAccountAdjustBillItem);
        List<FinVendorAccountAdjustBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinVendorAccountAdjustBillItemFormResponse::new);

        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorAccountAdjustBillItemFormResponse> util = new ExcelUtil<>(FinVendorAccountAdjustBillItemFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "客户调账单明细报表");

    }

    /**
     * 获取供应商调账单明细报表详细信息
     */
    @ApiOperation(value = "获取供应商调账单明细报表详细信息", notes = "获取供应商调账单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountAdjustBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long adjustBillItemSid) {
        if (adjustBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorAccountAdjustBillItemService.selectFinVendorAccountAdjustBillItemById(adjustBillItemSid));
    }


}
