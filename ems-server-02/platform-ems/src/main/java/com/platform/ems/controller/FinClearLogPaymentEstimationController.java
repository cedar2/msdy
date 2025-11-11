package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinClearLogPaymentEstimation;
import com.platform.ems.service.IFinClearLogPaymentEstimationService;
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
 * 核销应付暂估日志Controller
 *
 * @author platform
 * @date 2024-03-28
 */
@Api(tags = "核销应付暂估日志")
@RestController
@RequestMapping("/fin/clear/log/payment/estimation")
public class FinClearLogPaymentEstimationController extends BaseController {

    @Autowired
    private IFinClearLogPaymentEstimationService finClearLogPaymentEstimationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询核销应付暂估日志列表
     */
    @ApiOperation(value = "查询核销应付暂估日志列表", notes = "查询核销应付暂估日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinClearLogPaymentEstimation.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinClearLogPaymentEstimation finClearLogPaymentEstimation) {
        startPage(finClearLogPaymentEstimation);
        List<FinClearLogPaymentEstimation> list = finClearLogPaymentEstimationService.selectFinClearLogPaymentEstimationList(finClearLogPaymentEstimation);
        return getDataTable(list);
    }

    /**
     * 导出核销应付暂估日志列表
     */
    @ApiOperation(value = "导出核销应付暂估日志列表", notes = "导出核销应付暂估日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinClearLogPaymentEstimation finClearLogPaymentEstimation) throws IOException {
        List<FinClearLogPaymentEstimation> list = finClearLogPaymentEstimationService.selectFinClearLogPaymentEstimationList(finClearLogPaymentEstimation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinClearLogPaymentEstimation> util = new ExcelUtil<>(FinClearLogPaymentEstimation.class, dataMap);
        util.exportExcel(response, list, "核销应付暂估日志");
    }
}
