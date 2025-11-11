package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinBookVendorAccountBalanceFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookVendorAccountBalanceFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookVendorAccountBalance;
import com.platform.ems.service.IFinBookVendorAccountBalanceService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-供应商账互抵Controller
 *
 * @author linhongwei
 * @date 2021-06-18
 */
@RestController
@RequestMapping("/book/vendor/balance")
@Api(tags = "财务流水账-供应商账互抵")
public class FinBookVendorAccountBalanceController extends BaseController {

    @Autowired
    private IFinBookVendorAccountBalanceService finBookVendorAccountBalanceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-供应商账互抵列表
     */
    @ApiOperation(value = "导出财务流水账-供应商账互抵列表", notes = "导出财务流水账-供应商账互抵列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookVendorAccountBalanceFormRequest request) throws IOException {
        //转换
        FinBookVendorAccountBalance finBookVendorAccountBalance = new FinBookVendorAccountBalance();
        BeanCopyUtils.copyProperties(request, finBookVendorAccountBalance);
        List<FinBookVendorAccountBalance> list = finBookVendorAccountBalanceService.getReportForm(finBookVendorAccountBalance);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookVendorAccountBalance> util = new ExcelUtil<>(FinBookVendorAccountBalance.class, dataMap);
        util.exportExcel(response, list, "供应商账互抵流水报表");
    }

    @ApiOperation(value = "供应商账互抵流水报表查询", notes = "供应商账互抵流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookVendorAccountBalanceFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookVendorAccountBalanceFormRequest request) {
        //转换
        FinBookVendorAccountBalance finBookVendorAccountBalance = new FinBookVendorAccountBalance();
        BeanCopyUtils.copyProperties(request, finBookVendorAccountBalance);
        startPage(finBookVendorAccountBalance);
        List<FinBookVendorAccountBalance> requestList = finBookVendorAccountBalanceService.getReportForm(finBookVendorAccountBalance);
        return getDataTable(requestList, FinBookVendorAccountBalanceFormResponse::new);
    }
}
