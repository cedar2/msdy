package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinBookCustomerAccountBalanceFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookCustomerAccountBalanceFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookCustomerAccountBalance;
import com.platform.ems.service.IFinBookCustomerAccountBalanceService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-客户账互抵Controller
 *
 * @author qhq
 * @date 2021-06-11
 */
@RestController
@RequestMapping("/book/customer/balance")
@Api(tags = "财务流水账-客户账互抵")
public class FinBookCustomerAccountBalanceController extends BaseController {

    @Autowired
    private IFinBookCustomerAccountBalanceService finBookCustomerAccountBalanceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-客户账互抵列表
     */
    @ApiOperation(value = "导出财务流水账-客户账互抵列表", notes = "导出财务流水账-客户账互抵列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookCustomerAccountBalanceFormRequest request) throws IOException {
        //转换
        FinBookCustomerAccountBalance finBookCustomerAccountBalance = new FinBookCustomerAccountBalance();
        BeanCopyUtils.copyProperties(request, finBookCustomerAccountBalance);
        List<FinBookCustomerAccountBalance> list = finBookCustomerAccountBalanceService.getReportForm(finBookCustomerAccountBalance);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookCustomerAccountBalance> util = new ExcelUtil<>(FinBookCustomerAccountBalance.class, dataMap);
        util.exportExcel(response, list, "客户账互抵流水报表");
    }

    @ApiOperation(value = "客户账互抵流水报表查询", notes = "客户账互抵流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookCustomerAccountBalanceFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookCustomerAccountBalanceFormRequest request) {
        //转换
        FinBookCustomerAccountBalance finBookCustomerAccountBalance = new FinBookCustomerAccountBalance();
        BeanCopyUtils.copyProperties(request, finBookCustomerAccountBalance);
        startPage(finBookCustomerAccountBalance);
        List<FinBookCustomerAccountBalance> requestList = finBookCustomerAccountBalanceService.getReportForm(finBookCustomerAccountBalance);
        return getDataTable(requestList, FinBookCustomerAccountBalanceFormResponse::new);
    }
}
