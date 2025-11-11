package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinClearLogAdvanceReceiptPayment;
import com.platform.ems.service.IFinClearLogAdvanceReceiptPaymentService;
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
 * 核销客户已预收款日志Controller
 *
 * @author platform
 * @date 2024-03-28
 */
@Api(tags = "核销客户已预收款日志")
@RestController
@RequestMapping("/fin/clear/log/advance/receipt/payment")
public class FinClearLogAdvanceReceiptPaymentController extends BaseController {

    @Autowired
    private IFinClearLogAdvanceReceiptPaymentService finClearLogAdvanceReceiptPaymentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询核销客户已预收款日志列表
     */
    @ApiOperation(value = "查询核销客户已预收款日志列表", notes = "查询核销客户已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinClearLogAdvanceReceiptPayment.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinClearLogAdvanceReceiptPayment finClearLogAdvanceReceiptPayment) {
        startPage(finClearLogAdvanceReceiptPayment);
        List<FinClearLogAdvanceReceiptPayment> list = finClearLogAdvanceReceiptPaymentService.selectFinClearLogAdvanceReceiptPaymentList(finClearLogAdvanceReceiptPayment);
        return getDataTable(list);
    }

    /**
     * 导出核销客户已预收款日志列表
     */
    @ApiOperation(value = "导出核销客户已预收款日志列表", notes = "导出核销客户已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinClearLogAdvanceReceiptPayment finClearLogAdvanceReceiptPayment) throws IOException {
        List<FinClearLogAdvanceReceiptPayment> list = finClearLogAdvanceReceiptPaymentService.selectFinClearLogAdvanceReceiptPaymentList(finClearLogAdvanceReceiptPayment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinClearLogAdvanceReceiptPayment> util = new ExcelUtil<>(FinClearLogAdvanceReceiptPayment.class, dataMap);
        util.exportExcel(response, list, "核销客户已预收款日志");
    }

}
