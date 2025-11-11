package com.platform.ems.controller;

import java.util.List;

import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinCustomerCashPledgeBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinCustomerCashPledgeBillItemFormResponse;
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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinCustomerCashPledgeBillItem;
import com.platform.ems.service.IFinCustomerCashPledgeBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户押金-明细Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/customer/cash/pledge/bill/item")
@Api(tags = "客户押金-明细")
public class FinCustomerCashPledgeBillItemController extends BaseController {

    @Autowired
    private IFinCustomerCashPledgeBillItemService finCustomerCashPledgeBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户押金-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户押金-明细列表", notes = "查询客户押金-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerCashPledgeBillItem.class))
    public TableDataInfo list(@RequestBody FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        startPage(finCustomerCashPledgeBillItem);
        List<FinCustomerCashPledgeBillItem> list = finCustomerCashPledgeBillItemService.selectFinCustomerCashPledgeBillItemList(finCustomerCashPledgeBillItem);
        return getDataTable(list);
    }

    /**
     * 获取客户押金-明细详细信息
     */
    @ApiOperation(value = "获取客户押金-明细详细信息", notes = "获取客户押金-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerCashPledgeBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long cashPledgeBillItemSid) {
        if (cashPledgeBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerCashPledgeBillItemService.selectFinCustomerCashPledgeBillItemById(cashPledgeBillItemSid));
    }

    /**
     * 新增客户押金-明细
     */
    @ApiOperation(value = "新增客户押金-明细", notes = "新增客户押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        return toAjax(finCustomerCashPledgeBillItemService.insertFinCustomerCashPledgeBillItem(finCustomerCashPledgeBillItem));
    }

    /**
     * 修改客户押金-明细
     */
    @ApiOperation(value = "修改客户押金-明细", notes = "修改客户押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        return toAjax(finCustomerCashPledgeBillItemService.updateFinCustomerCashPledgeBillItem(finCustomerCashPledgeBillItem));
    }

    /**
     * 变更客户押金-明细
     */
    @ApiOperation(value = "变更客户押金-明细", notes = "变更客户押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) {
        return toAjax(finCustomerCashPledgeBillItemService.changeFinCustomerCashPledgeBillItem(finCustomerCashPledgeBillItem));
    }

    /**
     * 删除客户押金-明细
     */
    @ApiOperation(value = "删除客户押金-明细", notes = "删除客户押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> cashPledgeBillItemSids) {
        if (CollectionUtils.isEmpty(cashPledgeBillItemSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerCashPledgeBillItemService.deleteFinCustomerCashPledgeBillItemByIds(cashPledgeBillItemSids));
    }

    @ApiOperation(value = "客户押金明细报表查询", notes = "客户押金明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinCustomerCashPledgeBillItemFormRequest request) {
        FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem = new FinCustomerCashPledgeBillItem();
        BeanUtil.copyProperties(request, finCustomerCashPledgeBillItem);
        startPage(finCustomerCashPledgeBillItem);
        List<FinCustomerCashPledgeBillItem> requestList = finCustomerCashPledgeBillItemService.selectFinCustomerCashPledgeBillItemList(finCustomerCashPledgeBillItem);
        return getDataTable(requestList, FinCustomerCashPledgeBillItemFormResponse::new);
    }

    /**
     * 导出客户押金-明细列表
     */
    @ApiOperation(value = "导出客户押金-明细列表", notes = "导出客户押金-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerCashPledgeBillItem finCustomerCashPledgeBillItem) throws IOException {
        List<FinCustomerCashPledgeBillItem> list = finCustomerCashPledgeBillItemService.selectFinCustomerCashPledgeBillItemList(finCustomerCashPledgeBillItem);
        List<FinCustomerCashPledgeBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinCustomerCashPledgeBillItemFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerCashPledgeBillItemFormResponse> util = new ExcelUtil<>(FinCustomerCashPledgeBillItemFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "客户押金明细报表");
    }

}
