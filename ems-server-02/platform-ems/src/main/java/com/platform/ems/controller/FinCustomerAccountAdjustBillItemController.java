package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.domain.dto.request.form.FinCustomerAccountAdjustBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinCustomerAccountAdjustBillItemFormResponse;

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
import com.platform.ems.domain.FinCustomerAccountAdjustBillItem;
import com.platform.ems.service.IFinCustomerAccountAdjustBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户调账单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/fin/customer/account/bill/item")
@Api(tags = "客户调账单明细报表")
public class FinCustomerAccountAdjustBillItemController extends BaseController {

    @Autowired
    private IFinCustomerAccountAdjustBillItemService finCustomerAccountAdjustBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户调账单明细报表列表
     */
    @PostMapping("/getReportForm")
    @ApiOperation(value = "查询客户调账单明细报表列表", notes = "查询客户调账单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountAdjustBillItem.class))
    public TableDataInfo list(@RequestBody FinCustomerAccountAdjustBillItemFormRequest request) {
        FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem = new FinCustomerAccountAdjustBillItem();
        BeanUtil.copyProperties(request,finCustomerAccountAdjustBillItem);
        startPage(finCustomerAccountAdjustBillItem);
        List<FinCustomerAccountAdjustBillItem> list = finCustomerAccountAdjustBillItemService.selectFinCustomerAccountAdjustBillItemList(finCustomerAccountAdjustBillItem);
        return getDataTable(list, FinCustomerAccountAdjustBillItemFormResponse::new);
    }

    /**
     * 导出客户调账单明细报表列表
     */
    @ApiOperation(value = "导出客户调账单明细报表列表", notes = "导出客户调账单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerAccountAdjustBillItemFormRequest request) throws IOException {

        FinCustomerAccountAdjustBillItem finCustomerAccountAdjustBillItem = new FinCustomerAccountAdjustBillItem();
        BeanUtil.copyProperties(request,finCustomerAccountAdjustBillItem);

        List<FinCustomerAccountAdjustBillItem> list = finCustomerAccountAdjustBillItemService.selectFinCustomerAccountAdjustBillItemList(finCustomerAccountAdjustBillItem);
        List<FinCustomerAccountAdjustBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinCustomerAccountAdjustBillItemFormResponse::new);

        Map<String,Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerAccountAdjustBillItemFormResponse> util = new ExcelUtil<>(FinCustomerAccountAdjustBillItemFormResponse.class,dataMap);
        util.exportExcel(response, responsesList, "客户调账单明细报表");
    }


    /**
     * 获取客户调账单明细报表详细信息
     */
    @ApiOperation(value = "获取客户调账单明细报表详细信息", notes = "获取客户调账单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountAdjustBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long adjustBillItemSid) {
        if(adjustBillItemSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerAccountAdjustBillItemService.selectFinCustomerAccountAdjustBillItemById(adjustBillItemSid));
    }


}
