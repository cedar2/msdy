package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinCustomerFundsFreezeBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinCustomerFundsFreezeBillItemFormResponse;
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
import com.platform.ems.domain.FinCustomerFundsFreezeBillItem;
import com.platform.ems.service.IFinCustomerFundsFreezeBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户暂押款-明细Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/customer/funds/freeze/bill/item")
@Api(tags = "客户暂押款-明细")
public class FinCustomerFundsFreezeBillItemController extends BaseController {

    @Autowired
    private IFinCustomerFundsFreezeBillItemService finCustomerFundsFreezeBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户暂押款-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户暂押款-明细列表", notes = "查询客户暂押款-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerFundsFreezeBillItem.class))
    public TableDataInfo list(@RequestBody FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        startPage(finCustomerFundsFreezeBillItem);
        List<FinCustomerFundsFreezeBillItem> list = finCustomerFundsFreezeBillItemService.selectFinCustomerFundsFreezeBillItemList(finCustomerFundsFreezeBillItem);
        return getDataTable(list);
    }

    /**
     * 获取客户暂押款-明细详细信息
     */
    @ApiOperation(value = "获取客户暂押款-明细详细信息", notes = "获取客户暂押款-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerFundsFreezeBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundsFreezeBillItemSid) {
                    if(fundsFreezeBillItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(finCustomerFundsFreezeBillItemService.selectFinCustomerFundsFreezeBillItemById(fundsFreezeBillItemSid));
    }

    /**
     * 新增客户暂押款-明细
     */
    @ApiOperation(value = "新增客户暂押款-明细", notes = "新增客户暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        return toAjax(finCustomerFundsFreezeBillItemService.insertFinCustomerFundsFreezeBillItem(finCustomerFundsFreezeBillItem));
    }

    /**
     * 修改客户暂押款-明细
     */
    @ApiOperation(value = "修改客户暂押款-明细", notes = "修改客户暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        return toAjax(finCustomerFundsFreezeBillItemService.updateFinCustomerFundsFreezeBillItem(finCustomerFundsFreezeBillItem));
    }

    /**
     * 变更客户暂押款-明细
     */
    @ApiOperation(value = "变更客户暂押款-明细", notes = "变更客户暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) {
        return toAjax(finCustomerFundsFreezeBillItemService.changeFinCustomerFundsFreezeBillItem(finCustomerFundsFreezeBillItem));
    }

    /**
     * 删除客户暂押款-明细
     */
    @ApiOperation(value = "删除客户暂押款-明细", notes = "删除客户暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  fundsFreezeBillItemSids) {
        if(CollectionUtils.isEmpty( fundsFreezeBillItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerFundsFreezeBillItemService.deleteFinCustomerFundsFreezeBillItemByIds(fundsFreezeBillItemSids));
    }

    @ApiOperation(value = "客户暂押款明细报表查询", notes = "客户暂押款明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinCustomerFundsFreezeBillItemFormRequest request) {
        FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem = new FinCustomerFundsFreezeBillItem();
        BeanUtil.copyProperties(request, finCustomerFundsFreezeBillItem);
        startPage(finCustomerFundsFreezeBillItem);
        List<FinCustomerFundsFreezeBillItem> requestList = finCustomerFundsFreezeBillItemService.selectFinCustomerFundsFreezeBillItemList(finCustomerFundsFreezeBillItem);
        return getDataTable(requestList, FinCustomerFundsFreezeBillItemFormResponse::new);
    }

    /**
     * 导出客户暂押款-明细列表
     */
    @ApiOperation(value = "导出客户暂押款-明细列表", notes = "导出客户暂押款-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerFundsFreezeBillItem finCustomerFundsFreezeBillItem) throws IOException {
        List<FinCustomerFundsFreezeBillItem> list = finCustomerFundsFreezeBillItemService.selectFinCustomerFundsFreezeBillItemList(finCustomerFundsFreezeBillItem);
        List<FinCustomerFundsFreezeBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinCustomerFundsFreezeBillItemFormResponse::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerFundsFreezeBillItemFormResponse> util = new ExcelUtil<>(FinCustomerFundsFreezeBillItemFormResponse.class,dataMap);
        util.exportExcel(response, responsesList, "客户暂押款明细报表");
    }

}
