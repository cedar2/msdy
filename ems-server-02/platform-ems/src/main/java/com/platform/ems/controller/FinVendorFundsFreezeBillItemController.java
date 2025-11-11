package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinVendorFundsFreezeBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinVendorFundsFreezeBillItemFormResponse;
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
import com.platform.ems.domain.FinVendorFundsFreezeBillItem;
import com.platform.ems.service.IFinVendorFundsFreezeBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商暂押款-明细Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/vendor/funds/freeze//bill/item")
@Api(tags = "供应商暂押款-明细")
public class FinVendorFundsFreezeBillItemController extends BaseController {

    @Autowired
    private IFinVendorFundsFreezeBillItemService finVendorFundsFreezeBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商暂押款-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商暂押款-明细列表", notes = "查询供应商暂押款-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorFundsFreezeBillItem.class))
    public TableDataInfo list(@RequestBody FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        startPage(finVendorFundsFreezeBillItem);
        List<FinVendorFundsFreezeBillItem> list = finVendorFundsFreezeBillItemService.selectFinVendorFundsFreezeBillItemList(finVendorFundsFreezeBillItem);
        return getDataTable(list);
    }

    /**
     * 获取供应商暂押款-明细详细信息
     */
    @ApiOperation(value = "获取供应商暂押款-明细详细信息", notes = "获取供应商暂押款-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorFundsFreezeBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundsFreezeBillItemSid) {
                    if(fundsFreezeBillItemSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(finVendorFundsFreezeBillItemService.selectFinVendorFundsFreezeBillItemById(fundsFreezeBillItemSid));
    }

    /**
     * 新增供应商暂押款-明细
     */
    @ApiOperation(value = "新增供应商暂押款-明细", notes = "新增供应商暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        return toAjax(finVendorFundsFreezeBillItemService.insertFinVendorFundsFreezeBillItem(finVendorFundsFreezeBillItem));
    }

    /**
     * 修改供应商暂押款-明细
     */
    @ApiOperation(value = "修改供应商暂押款-明细", notes = "修改供应商暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        return toAjax(finVendorFundsFreezeBillItemService.updateFinVendorFundsFreezeBillItem(finVendorFundsFreezeBillItem));
    }

    /**
     * 变更供应商暂押款-明细
     */
    @ApiOperation(value = "变更供应商暂押款-明细", notes = "变更供应商暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) {
        return toAjax(finVendorFundsFreezeBillItemService.changeFinVendorFundsFreezeBillItem(finVendorFundsFreezeBillItem));
    }

    /**
     * 删除供应商暂押款-明细
     */
    @ApiOperation(value = "删除供应商暂押款-明细", notes = "删除供应商暂押款-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  fundsFreezeBillItemSids) {
        if(CollectionUtils.isEmpty( fundsFreezeBillItemSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorFundsFreezeBillItemService.deleteFinVendorFundsFreezeBillItemByIds(fundsFreezeBillItemSids));
    }

    @ApiOperation(value = "供应商暂押款明细报表查询", notes = "供应商暂押款明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinVendorFundsFreezeBillItemFormRequest request) {
        FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem = new FinVendorFundsFreezeBillItem();
        BeanUtil.copyProperties(request, finVendorFundsFreezeBillItem);
        startPage(finVendorFundsFreezeBillItem);
        List<FinVendorFundsFreezeBillItem> requestList = finVendorFundsFreezeBillItemService.selectFinVendorFundsFreezeBillItemList(finVendorFundsFreezeBillItem);
        return getDataTable(requestList, FinVendorFundsFreezeBillItemFormResponse::new);
    }

    /**
     * 导出供应商暂押款-明细列表
     */
    @ApiOperation(value = "导出供应商暂押款-明细列表", notes = "导出供应商暂押款-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorFundsFreezeBillItem finVendorFundsFreezeBillItem) throws IOException {
        List<FinVendorFundsFreezeBillItem> list = finVendorFundsFreezeBillItemService.selectFinVendorFundsFreezeBillItemList(finVendorFundsFreezeBillItem);
        List<FinVendorFundsFreezeBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinVendorFundsFreezeBillItemFormResponse::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorFundsFreezeBillItemFormResponse> util = new ExcelUtil<>(FinVendorFundsFreezeBillItemFormResponse.class,dataMap);
        util.exportExcel(response, responsesList, "供应商暂押款明细报表");
    }
}
