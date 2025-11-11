package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.form.FinBookVendorAccountAdjustFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookVendorAccountAdjustFormResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookVendorAccountAdjust;
import com.platform.ems.service.IFinBookVendorAccountAdjustService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 财务流水账-供应商调账Controller
 *
 * @author qhq
 * @date 2021-06-02
 */
@RestController
@RequestMapping("/book/vendor/adjust")
@Api(tags = "财务流水账-供应商调账")
public class FinBookVendorAccountAdjustController extends BaseController {

    @Autowired
    private IFinBookVendorAccountAdjustService finBookVendorAccountAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 导出财务流水账-供应商调账列表
     */
    @ApiOperation(value = "导出财务流水账-供应商调账列表", notes = "导出财务流水账-供应商调账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookVendorAccountAdjust request) throws IOException {
        //转换
        FinBookVendorAccountAdjust finBookVendorAccountAdjust = new FinBookVendorAccountAdjust();
        BeanCopyUtils.copyProperties(request, finBookVendorAccountAdjust);
        List<FinBookVendorAccountAdjust> list = finBookVendorAccountAdjustService.getReportForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookVendorAccountAdjust> util = new ExcelUtil<>(FinBookVendorAccountAdjust.class, dataMap);
        util.exportExcel(response, list, "供应商调账流水报表");
    }

    @ApiOperation(value = "供应商调账流水报表查询", notes = "供应商调账流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookVendorAccountAdjustFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookVendorAccountAdjustFormRequest request) {
        //转换
        FinBookVendorAccountAdjust finBookVendorAccountAdjust = new FinBookVendorAccountAdjust();
        BeanCopyUtils.copyProperties(request, finBookVendorAccountAdjust);
        startPage(finBookVendorAccountAdjust);
        List<FinBookVendorAccountAdjust> requestList = finBookVendorAccountAdjustService.getReportForm(finBookVendorAccountAdjust);
        return getDataTable(requestList, FinBookVendorAccountAdjustFormResponse::new);
    }
}
