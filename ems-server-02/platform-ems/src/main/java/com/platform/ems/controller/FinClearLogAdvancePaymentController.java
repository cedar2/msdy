package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinClearLogAdvancePayment;
import com.platform.ems.service.IFinClearLogAdvancePaymentService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 核销供应商已预付款日志Controller
 *
 * @author platform
 * @date 2024-03-28
 */
@Api(tags = "核销供应商已预付款日志")
@RestController
@RequestMapping("/fin/clear/log/advance/payment")
public class FinClearLogAdvancePaymentController extends BaseController {

    @Autowired
    private IFinClearLogAdvancePaymentService finClearLogAdvancePaymentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询核销供应商已预付款日志列表
     */
    @ApiOperation(value = "查询核销供应商已预付款日志列表", notes = "查询核销供应商已预付款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinClearLogAdvancePayment.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinClearLogAdvancePayment finClearLogAdvancePayment) {
        startPage(finClearLogAdvancePayment);
        List<FinClearLogAdvancePayment> list = finClearLogAdvancePaymentService.selectFinClearLogAdvancePaymentList(finClearLogAdvancePayment);
        return getDataTable(list);
    }

    /**
     * 导出核销供应商已预付款日志列表
     */
    @ApiOperation(value = "导出核销供应商已预付款日志列表", notes = "导出核销供应商已预付款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinClearLogAdvancePayment finClearLogAdvancePayment) throws IOException {
        List<FinClearLogAdvancePayment> list = finClearLogAdvancePaymentService.selectFinClearLogAdvancePaymentList(finClearLogAdvancePayment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinClearLogAdvancePayment> util = new ExcelUtil<>(FinClearLogAdvancePayment.class, dataMap);
        util.exportExcel(response, list, "核销供应商已预付款日志");
    }

}
