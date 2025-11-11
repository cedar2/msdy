package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.domain.dto.request.form.FinCustomerDeductionBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinCustomerDeductionBillItemFormResponse;
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
import com.platform.ems.domain.FinCustomerDeductionBillItem;
import com.platform.ems.service.IFinCustomerDeductionBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户扣款单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-21
 */
@RestController
@RequestMapping("/fin/customer/deduction/item")
@Api(tags = "客户扣款单明细报表")
public class FinCustomerDeductionBillItemController extends BaseController {

    @Autowired
    private IFinCustomerDeductionBillItemService finCustomerDeductionBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户扣款单明细报表列表
     */
    @PostMapping("/getReportForm")
    @ApiOperation(value = "查询客户扣款单明细报表列表", notes = "查询客户扣款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBillItem.class))
    public TableDataInfo list(@RequestBody FinCustomerDeductionBillItemFormRequest request) {
        FinCustomerDeductionBillItem finCustomerDeductionBillItem = new FinCustomerDeductionBillItem();
        BeanUtil.copyProperties(request, finCustomerDeductionBillItem);
        startPage(finCustomerDeductionBillItem);
        List<FinCustomerDeductionBillItem> list = finCustomerDeductionBillItemService.selectFinCustomerDeductionBillItemList(finCustomerDeductionBillItem);
        return getDataTable(list, FinCustomerDeductionBillItemFormResponse::new);
    }

    /**
     * 导出客户扣款单明细报表列表
     */
    @ApiOperation(value = "导出客户扣款单明细报表列表", notes = "导出客户扣款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerDeductionBillItemFormRequest request) throws IOException {
        FinCustomerDeductionBillItem finCustomerDeductionBillItem = new FinCustomerDeductionBillItem();
        BeanUtil.copyProperties(request, finCustomerDeductionBillItem);
        List<FinCustomerDeductionBillItem> responsesList = finCustomerDeductionBillItemService.selectFinCustomerDeductionBillItemList(finCustomerDeductionBillItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerDeductionBillItem> util = new ExcelUtil<>(FinCustomerDeductionBillItem.class, dataMap);
        util.exportExcel(response, responsesList, "客户扣款单明细报表");
    }

    /**
     * 获取客户扣款单明细报表详细信息
     */
    @ApiOperation(value = "获取客户扣款单明细报表详细信息", notes = "获取客户扣款单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deductionBillItemSid) {
        if (deductionBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerDeductionBillItemService.selectFinCustomerDeductionBillItemById(deductionBillItemSid));
    }

}
