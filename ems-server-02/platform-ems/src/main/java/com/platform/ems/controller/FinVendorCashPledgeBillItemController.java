package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinVendorCashPledgeBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinVendorCashPledgeBillItemFormResponse;
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
import com.platform.ems.domain.FinVendorCashPledgeBillItem;
import com.platform.ems.service.IFinVendorCashPledgeBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商押金-明细Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/vendor/cash/pledge/bill/item")
@Api(tags = "供应商押金-明细")
public class FinVendorCashPledgeBillItemController extends BaseController {

    @Autowired
    private IFinVendorCashPledgeBillItemService finVendorCashPledgeBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商押金-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商押金-明细列表", notes = "查询供应商押金-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorCashPledgeBillItem.class))
    public TableDataInfo list(@RequestBody FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        startPage(finVendorCashPledgeBillItem);
        List<FinVendorCashPledgeBillItem> list = finVendorCashPledgeBillItemService.selectFinVendorCashPledgeBillItemList(finVendorCashPledgeBillItem);
        return getDataTable(list);
    }

    /**
     * 获取供应商押金-明细详细信息
     */
    @ApiOperation(value = "获取供应商押金-明细详细信息", notes = "获取供应商押金-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorCashPledgeBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long cashPledgeBillItemSid) {
        if(cashPledgeBillItemSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorCashPledgeBillItemService.selectFinVendorCashPledgeBillItemById(cashPledgeBillItemSid));
    }

    /**
     * 新增供应商押金-明细
     */
    @ApiOperation(value = "新增供应商押金-明细", notes = "新增供应商押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        return toAjax(finVendorCashPledgeBillItemService.insertFinVendorCashPledgeBillItem(finVendorCashPledgeBillItem));
    }

    /**
     * 修改供应商押金-明细
     */
    @ApiOperation(value = "修改供应商押金-明细", notes = "修改供应商押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        return toAjax(finVendorCashPledgeBillItemService.updateFinVendorCashPledgeBillItem(finVendorCashPledgeBillItem));
    }

    /**
     * 变更供应商押金-明细
     */
    @ApiOperation(value = "变更供应商押金-明细", notes = "变更供应商押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) {
        return toAjax(finVendorCashPledgeBillItemService.changeFinVendorCashPledgeBillItem(finVendorCashPledgeBillItem));
    }

    /**
     * 删除供应商押金-明细
     */
    @ApiOperation(value = "删除供应商押金-明细", notes = "删除供应商押金-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  cashPledgeBillItemSids) {
        if(CollectionUtils.isEmpty( cashPledgeBillItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorCashPledgeBillItemService.deleteFinVendorCashPledgeBillItemByIds(cashPledgeBillItemSids));
    }

    @ApiOperation(value = "供应商押金明细报表查询", notes = "供应商押金明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinVendorCashPledgeBillItemFormRequest request) {
        FinVendorCashPledgeBillItem finVendorCashPledgeBillItem = new FinVendorCashPledgeBillItem();
        BeanUtil.copyProperties(request, finVendorCashPledgeBillItem);
        startPage(finVendorCashPledgeBillItem);
        List<FinVendorCashPledgeBillItem> requestList = finVendorCashPledgeBillItemService.selectFinVendorCashPledgeBillItemList(finVendorCashPledgeBillItem);
        return getDataTable(requestList, FinVendorCashPledgeBillItemFormResponse::new);
    }

    /**
     * 导出供应商押金-明细列表
     */
    @ApiOperation(value = "导出供应商押金-明细列表", notes = "导出供应商押金-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorCashPledgeBillItem finVendorCashPledgeBillItem) throws IOException {
        List<FinVendorCashPledgeBillItem> list = finVendorCashPledgeBillItemService.selectFinVendorCashPledgeBillItemList(finVendorCashPledgeBillItem);
        List<FinVendorCashPledgeBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinVendorCashPledgeBillItemFormResponse::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorCashPledgeBillItemFormResponse> util = new ExcelUtil<>(FinVendorCashPledgeBillItemFormResponse.class,dataMap);
        util.exportExcel(response, responsesList, "供应商押金明细报表");
    }
}
