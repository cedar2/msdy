package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinBookCustomerDeductionFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookCustomerDeductionFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookCustomerDeduction;

import com.platform.ems.service.IFinBookCustomerDeductionService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-客户扣款Controller
 *
 * @author qhq
 * @date 2021-06-08
 */
@RestController
@RequestMapping("/book/customer/deduction")
@Api(tags = "财务流水账-客户扣款")
public class FinBookCustomerDeductionController extends BaseController {

    @Autowired
    private IFinBookCustomerDeductionService finBookCustomerDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-客户扣款列表
     */
    @ApiOperation(value = "导出财务流水账-客户扣款列表", notes = "导出财务流水账-客户扣款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookCustomerDeductionFormRequest request) throws IOException {
        //转换
        FinBookCustomerDeduction finBookCustomerDeduction = new FinBookCustomerDeduction();
        BeanCopyUtils.copyProperties(request, finBookCustomerDeduction);
        List<FinBookCustomerDeduction> list = finBookCustomerDeductionService.getReportForm(finBookCustomerDeduction);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookCustomerDeduction> util = new ExcelUtil<>(FinBookCustomerDeduction.class, dataMap);
        util.exportExcel(response, list, "客户扣款流水报表");
    }

    @ApiOperation(value = "客户扣款流水报表查询", notes = "客户扣款流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookCustomerDeductionFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookCustomerDeductionFormRequest request) {
        //转换
        FinBookCustomerDeduction finBookCustomerDeduction = new FinBookCustomerDeduction();
        BeanCopyUtils.copyProperties(request, finBookCustomerDeduction);
        startPage(finBookCustomerDeduction);
        List<FinBookCustomerDeduction> requestList = finBookCustomerDeductionService.getReportForm(finBookCustomerDeduction);
        return getDataTable(requestList, FinBookCustomerDeductionFormResponse::new);
    }
}
