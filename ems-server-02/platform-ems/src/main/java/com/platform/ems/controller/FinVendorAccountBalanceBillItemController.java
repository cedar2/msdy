package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinVendorAccountBalanceBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinVendorAccountBalanceBillItemFormResponse;
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
import com.platform.ems.domain.FinVendorAccountBalanceBillItem;
import com.platform.ems.service.IFinVendorAccountBalanceBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商账互抵单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/fin/vendor/account/balance/bill/item")
@Api(tags = "供应商账互抵单明细报表")
public class FinVendorAccountBalanceBillItemController extends BaseController {

    @Autowired
    private IFinVendorAccountBalanceBillItemService finVendorAccountBalanceBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商账互抵单明细报表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商账互抵单明细报表列表", notes = "查询供应商账互抵单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountBalanceBillItem.class))
    public TableDataInfo list(@RequestBody FinVendorAccountBalanceBillItemFormRequest request) {
        FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem = new FinVendorAccountBalanceBillItem();
        BeanUtil.copyProperties(request,finVendorAccountBalanceBillItem);
        startPage(finVendorAccountBalanceBillItem);
        List<FinVendorAccountBalanceBillItem> list = finVendorAccountBalanceBillItemService.selectFinVendorAccountBalanceBillItemList(finVendorAccountBalanceBillItem);
        return getDataTable(list, FinVendorAccountBalanceBillItemFormResponse::new);
    }

    /**
     * 导出供应商账互抵单明细报表列表
     */
    @ApiOperation(value = "导出供应商账互抵单明细报表列表", notes = "导出供应商账互抵单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem) throws IOException {
        List<FinVendorAccountBalanceBillItem> list = finVendorAccountBalanceBillItemService.selectFinVendorAccountBalanceBillItemList(finVendorAccountBalanceBillItem);
        List<FinVendorAccountBalanceBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinVendorAccountBalanceBillItemFormResponse::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorAccountBalanceBillItemFormResponse> util = new ExcelUtil<>(FinVendorAccountBalanceBillItemFormResponse.class,dataMap);
        util.exportExcel(response, responsesList, "供应商账互抵单明细报表");
    }


    /**
     * 获取供应商账互抵单明细报表详细信息
     */
    @ApiOperation(value = "获取供应商账互抵单明细报表详细信息", notes = "获取供应商账互抵单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountBalanceBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long accountBalanceBillItemSid) {
        if(accountBalanceBillItemSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorAccountBalanceBillItemService.selectFinVendorAccountBalanceBillItemById(accountBalanceBillItemSid));
    }


}
