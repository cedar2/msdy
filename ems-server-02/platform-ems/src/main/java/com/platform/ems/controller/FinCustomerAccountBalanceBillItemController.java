package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinCustomerAccountBalanceBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinCustomerAccountBalanceBillItemFormResponse;
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
import com.platform.ems.domain.FinCustomerAccountBalanceBillItem;
import com.platform.ems.service.IFinCustomerAccountBalanceBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户账互抵单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/fin/customer/account/balance/bill/item")
@Api(tags = "客户账互抵单明细报表")
public class FinCustomerAccountBalanceBillItemController extends BaseController {

    @Autowired
    private IFinCustomerAccountBalanceBillItemService finCustomerAccountBalanceBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户账互抵单明细报表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户账互抵单明细报表列表", notes = "查询客户账互抵单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBillItem.class))
    public TableDataInfo list(@RequestBody FinCustomerAccountBalanceBillItemFormRequest request) {
        FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem = new FinCustomerAccountBalanceBillItem();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBillItem);
        startPage(finCustomerAccountBalanceBillItem);
        List<FinCustomerAccountBalanceBillItem> list = finCustomerAccountBalanceBillItemService.selectFinCustomerAccountBalanceBillItemList(finCustomerAccountBalanceBillItem);
        return getDataTable(list, FinCustomerAccountBalanceBillItemFormResponse::new);
    }

    /**
     * 导出客户账互抵单明细报表列表
     */
    @ApiOperation(value = "导出客户账互抵单明细报表列表", notes = "导出客户账互抵单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem) throws IOException {
        List<FinCustomerAccountBalanceBillItem> list = finCustomerAccountBalanceBillItemService.selectFinCustomerAccountBalanceBillItemList(finCustomerAccountBalanceBillItem);
        List<FinCustomerAccountBalanceBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinCustomerAccountBalanceBillItemFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerAccountBalanceBillItemFormResponse> util = new ExcelUtil<>(FinCustomerAccountBalanceBillItemFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "客户账互抵单明细报表");
    }


    /**
     * 获取客户账互抵单明细报表详细信息
     */
    @ApiOperation(value = "获取客户账互抵单明细报表详细信息", notes = "获取客户账互抵单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBillItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long accountBalanceBillItemSid) {
        if (accountBalanceBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerAccountBalanceBillItemService.selectFinCustomerAccountBalanceBillItemById(accountBalanceBillItemSid));
    }

}
