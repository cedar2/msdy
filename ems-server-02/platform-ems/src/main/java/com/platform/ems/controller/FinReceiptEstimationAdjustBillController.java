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
import com.platform.ems.domain.FinReceiptEstimationAdjustBill;
import com.platform.ems.service.IFinReceiptEstimationAdjustBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 应收暂估调价量单Controller
 *
 * @author chenkw
 * @date 2022-01-10
 */
@RestController
@RequestMapping("/fin/receipt/estimation/adjustBill")
@Api(tags = "应收暂估调价量单")
public class FinReceiptEstimationAdjustBillController extends BaseController {

    @Autowired
    private IFinReceiptEstimationAdjustBillService finReceiptEstimationAdjustBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询应收暂估调价量单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询应收暂估调价量单列表", notes = "查询应收暂估调价量单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceiptEstimationAdjustBill.class))
    public TableDataInfo list(@RequestBody FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        startPage(finReceiptEstimationAdjustBill);
        List<FinReceiptEstimationAdjustBill> list = finReceiptEstimationAdjustBillService.selectFinReceiptEstimationAdjustBillList(finReceiptEstimationAdjustBill);
        return getDataTable(list);
    }

    /**
     * 导出应收暂估调价量单列表
     */
    @ApiOperation(value = "导出应收暂估调价量单列表", notes = "导出应收暂估调价量单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) throws IOException {
        List<FinReceiptEstimationAdjustBill> list = finReceiptEstimationAdjustBillService.selectFinReceiptEstimationAdjustBillList(finReceiptEstimationAdjustBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceiptEstimationAdjustBill> util = new ExcelUtil<>(FinReceiptEstimationAdjustBill.class, dataMap);
        util.exportExcel(response, list, "应收暂估调价量单");
    }

    /**
     * 获取应收暂估调价量单详细信息
     */
    @ApiOperation(value = "获取应收暂估调价量单详细信息", notes = "获取应收暂估调价量单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceiptEstimationAdjustBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long receiptEstimationAdjustBillSid) {
        if (receiptEstimationAdjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finReceiptEstimationAdjustBillService.selectFinReceiptEstimationAdjustBillById(receiptEstimationAdjustBillSid));
    }

    /**
     * 新增应收暂估调价量单
     */
    @ApiOperation(value = "新增应收暂估调价量单", notes = "新增应收暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        int row = finReceiptEstimationAdjustBillService.insertFinReceiptEstimationAdjustBill(finReceiptEstimationAdjustBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinReceiptEstimationAdjustBill()
                    .setReceiptEstimationAdjustBillSid(finReceiptEstimationAdjustBill.getReceiptEstimationAdjustBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改应收暂估调价量单
     */
    @ApiOperation(value = "修改应收暂估调价量单", notes = "修改应收暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        return toAjax(finReceiptEstimationAdjustBillService.updateFinReceiptEstimationAdjustBill(finReceiptEstimationAdjustBill));
    }

    /**
     * 变更应收暂估调价量单
     */
    @ApiOperation(value = "变更应收暂估调价量单", notes = "变更应收暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        return toAjax(finReceiptEstimationAdjustBillService.changeFinReceiptEstimationAdjustBill(finReceiptEstimationAdjustBill));
    }

    /**
     * 删除应收暂估调价量单
     */
    @ApiOperation(value = "删除应收暂估调价量单", notes = "删除应收暂估调价量单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> receiptEstimationAdjustBillSids) {
        if (CollectionUtils.isEmpty(receiptEstimationAdjustBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finReceiptEstimationAdjustBillService.deleteFinReceiptEstimationAdjustBillByIds(receiptEstimationAdjustBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinReceiptEstimationAdjustBill finReceiptEstimationAdjustBill) {
        return toAjax(finReceiptEstimationAdjustBillService.check(finReceiptEstimationAdjustBill));
    }

}
