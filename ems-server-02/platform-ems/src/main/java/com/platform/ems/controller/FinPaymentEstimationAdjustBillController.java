package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinPaymentEstimationAdjustBill;
import com.platform.ems.service.IFinPaymentEstimationAdjustBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 应付暂估调价量单Controller
 *
 * @author chenkw
 * @date 2022-01-10
 */
@RestController
@RequestMapping("/fin/payment/estimation/adjustBill")
@Api(tags = "应付暂估调价量单")
public class FinPaymentEstimationAdjustBillController extends BaseController {

    @Autowired
    private IFinPaymentEstimationAdjustBillService finPaymentEstimationAdjustBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询应付暂估调价量单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询应付暂估调价量单列表", notes = "查询应付暂估调价量单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPaymentEstimationAdjustBill.class))
    public TableDataInfo list(@RequestBody FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        startPage(finPaymentEstimationAdjustBill);
        List<FinPaymentEstimationAdjustBill> list = finPaymentEstimationAdjustBillService.selectFinPaymentEstimationAdjustBillList(finPaymentEstimationAdjustBill);
        return getDataTable(list);
    }

    /**
     * 导出应付暂估调价量单列表
     */
    @ApiOperation(value = "导出应付暂估调价量单列表", notes = "导出应付暂估调价量单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) throws IOException {
        List<FinPaymentEstimationAdjustBill> list = finPaymentEstimationAdjustBillService.selectFinPaymentEstimationAdjustBillList(finPaymentEstimationAdjustBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPaymentEstimationAdjustBill> util = new ExcelUtil<>(FinPaymentEstimationAdjustBill.class, dataMap);
        util.exportExcel(response, list, "应付暂估调价量单");
    }

    /**
     * 获取应付暂估调价量单详细信息
     */
    @ApiOperation(value = "获取应付暂估调价量单详细信息", notes = "获取应付暂估调价量单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPaymentEstimationAdjustBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long paymentEstimationAdjustBillSid) {
        if (paymentEstimationAdjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finPaymentEstimationAdjustBillService.selectFinPaymentEstimationAdjustBillById(paymentEstimationAdjustBillSid));
    }

    /**
     * 新增应付暂估调价量单
     */
    @ApiOperation(value = "新增应付暂估调价量单", notes = "新增应付暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        int row = finPaymentEstimationAdjustBillService.insertFinPaymentEstimationAdjustBill(finPaymentEstimationAdjustBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinPaymentEstimationAdjustBill()
                    .setPaymentEstimationAdjustBillSid(finPaymentEstimationAdjustBill.getPaymentEstimationAdjustBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改应付暂估调价量单
     */
    @ApiOperation(value = "修改应付暂估调价量单", notes = "修改应付暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        return toAjax(finPaymentEstimationAdjustBillService.updateFinPaymentEstimationAdjustBill(finPaymentEstimationAdjustBill));
    }

    /**
     * 变更应付暂估调价量单
     */
    @ApiOperation(value = "变更应付暂估调价量单", notes = "变更应付暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        return toAjax(finPaymentEstimationAdjustBillService.changeFinPaymentEstimationAdjustBill(finPaymentEstimationAdjustBill));
    }

    /**
     * 删除应付暂估调价量单
     */
    @ApiOperation(value = "删除应付暂估调价量单", notes = "删除应付暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> paymentEstimationAdjustBillSids) {
        if (CollectionUtils.isEmpty(paymentEstimationAdjustBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finPaymentEstimationAdjustBillService.deleteFinPaymentEstimationAdjustBillByIds(paymentEstimationAdjustBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinPaymentEstimationAdjustBill finPaymentEstimationAdjustBill) {
        return toAjax(finPaymentEstimationAdjustBillService.check(finPaymentEstimationAdjustBill));
    }

}
