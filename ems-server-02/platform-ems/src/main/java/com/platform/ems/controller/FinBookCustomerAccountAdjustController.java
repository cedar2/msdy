package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.response.form.FinBookCustomerAccountAdjustFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookCustomerAccountAdjust;
import com.platform.ems.service.IFinBookCustomerAccountAdjustService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-客户调账Controller
 *
 * @author qhq
 * @date 2021-06-08
 */
@RestController
@RequestMapping("/book/customer/adjust")
@Api(tags = "财务流水账-客户调账")
public class FinBookCustomerAccountAdjustController extends BaseController {

    @Autowired
    private IFinBookCustomerAccountAdjustService finBookCustomerAccountAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-客户调账列表
     */
    @ApiOperation(value = "导出财务流水账-客户调账列表", notes = "导出财务流水账-客户调账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookCustomerAccountAdjust request) throws IOException {
        //转换
        FinBookCustomerAccountAdjust finBookCustomerAccountAdjust = new FinBookCustomerAccountAdjust();
        BeanCopyUtils.copyProperties(request, finBookCustomerAccountAdjust);
        List<FinBookCustomerAccountAdjust> list = finBookCustomerAccountAdjustService.getReportForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookCustomerAccountAdjust> util = new ExcelUtil<>(FinBookCustomerAccountAdjust.class, dataMap);
        util.exportExcel(response, list, "客户调账流水报表");
    }

    @ApiOperation(value = "客户调账流水报表查询", notes = "客户调账流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookCustomerAccountAdjustFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookCustomerAccountAdjust request) {
        //转换
        FinBookCustomerAccountAdjust finBookCustomerAccountAdjust = new FinBookCustomerAccountAdjust();
        BeanCopyUtils.copyProperties(request, finBookCustomerAccountAdjust);
        startPage(request);
        List<FinBookCustomerAccountAdjust> requestList = finBookCustomerAccountAdjustService.getReportForm(request);
        return getDataTable(requestList, FinBookCustomerAccountAdjustFormResponse::new);
    }
}
