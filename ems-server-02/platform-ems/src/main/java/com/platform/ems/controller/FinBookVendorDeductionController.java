package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinBookVendorDeductionFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookVendorDeductionFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookVendorDeduction;
import com.platform.ems.service.IFinBookVendorDeductionService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-供应商扣款Controller
 *
 * @author qhq
 * @date 2021-06-02
 */
@RestController
@RequestMapping("/book/vendor/deduction")
@Api(tags = "财务流水账-供应商扣款")
public class FinBookVendorDeductionController extends BaseController {

    @Autowired
    private IFinBookVendorDeductionService finBookVendorDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-供应商扣款列表
     */
    @ApiOperation(value = "导出财务流水账-供应商扣款列表", notes = "导出财务流水账-供应商扣款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookVendorDeductionFormRequest request) throws IOException {
        //转换
        FinBookVendorDeduction finBookVendorDeduction = new FinBookVendorDeduction();
        BeanCopyUtils.copyProperties(request, finBookVendorDeduction);
        List<FinBookVendorDeduction> list = finBookVendorDeductionService.getReportForm(finBookVendorDeduction);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookVendorDeduction> util = new ExcelUtil<>(FinBookVendorDeduction.class, dataMap);
        util.exportExcel(response, list, "供应商扣款流水报表");
    }

    @ApiOperation(value = "供应商扣款流水报表查询", notes = "供应商扣款流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookVendorDeductionFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookVendorDeductionFormRequest request) {
        //转换
        FinBookVendorDeduction finBookVendorDeduction = new FinBookVendorDeduction();
        BeanCopyUtils.copyProperties(request, finBookVendorDeduction);
        startPage(finBookVendorDeduction);
        List<FinBookVendorDeduction> list = finBookVendorDeductionService.getReportForm(finBookVendorDeduction);
        return getDataTable(list, FinBookVendorDeductionFormResponse::new);
    }
}
