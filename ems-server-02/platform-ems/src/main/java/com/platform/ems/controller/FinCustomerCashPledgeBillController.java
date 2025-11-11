package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinCustomerCashPledgeBill;
import com.platform.ems.service.IFinCustomerCashPledgeBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 客户押金Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/customer/cash/pledge/bill")
@Api(tags = "客户押金")
public class FinCustomerCashPledgeBillController extends BaseController {

    @Autowired
    private IFinCustomerCashPledgeBillService finCustomerCashPledgeBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户押金列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户押金列表", notes = "查询客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerCashPledgeBill.class))
    public TableDataInfo list(@RequestBody FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        startPage(finCustomerCashPledgeBill);
        List<FinCustomerCashPledgeBill> list = finCustomerCashPledgeBillService.selectFinCustomerCashPledgeBillList(finCustomerCashPledgeBill);
        return getDataTable(list);
    }

    /**
     * 导出客户押金列表
     */
    @ApiOperation(value = "导出客户押金列表", notes = "导出客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerCashPledgeBill finCustomerCashPledgeBill) throws IOException {
        List<FinCustomerCashPledgeBill> list = finCustomerCashPledgeBillService.selectFinCustomerCashPledgeBillList(finCustomerCashPledgeBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerCashPledgeBill> util = new ExcelUtil<>(FinCustomerCashPledgeBill.class, dataMap);
        util.exportExcel(response, list, "客户押金");
    }


    /**
     * 获取客户押金详细信息
     */
    @ApiOperation(value = "获取客户押金详细信息", notes = "获取客户押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerCashPledgeBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long cashPledgeBillSid) {
        if (cashPledgeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerCashPledgeBillService.selectFinCustomerCashPledgeBillById(cashPledgeBillSid));
    }

    /**
     * 新增客户押金
     */
    @ApiOperation(value = "新增客户押金", notes = "新增客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        int row = finCustomerCashPledgeBillService.insertFinCustomerCashPledgeBill(finCustomerCashPledgeBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinCustomerCashPledgeBill().setCashPledgeBillSid(finCustomerCashPledgeBill.getCashPledgeBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改客户押金
     */
    @ApiOperation(value = "修改客户押金", notes = "修改客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        return toAjax(finCustomerCashPledgeBillService.updateFinCustomerCashPledgeBill(finCustomerCashPledgeBill));
    }

    /**
     * 变更客户押金
     */
    @ApiOperation(value = "变更客户押金", notes = "变更客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        return toAjax(finCustomerCashPledgeBillService.changeFinCustomerCashPledgeBill(finCustomerCashPledgeBill));
    }

    /**
     * 删除客户押金
     */
    @ApiOperation(value = "删除客户押金", notes = "删除客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> cashPledgeBillSids) {
        if (CollectionUtils.isEmpty(cashPledgeBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerCashPledgeBillService.deleteFinCustomerCashPledgeBillByIds(cashPledgeBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerCashPledgeBill finCustomerCashPledgeBill) {
        return toAjax(finCustomerCashPledgeBillService.check(finCustomerCashPledgeBill));
    }

    /**
     * 复制客户押金详细信息
     */
    @ApiOperation(value = "复制客户押金详细信息", notes = "复制客户押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerCashPledgeBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long cashPledgeBillSid) {
        if (cashPledgeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinCustomerCashPledgeBill finCustomerCashPledgeBill = finCustomerCashPledgeBillService.selectFinCustomerCashPledgeBillById(cashPledgeBillSid);
        finCustomerCashPledgeBill.setCashPledgeBillSid(null).setCashPledgeBillCode(null).setHandleStatus(null).setCreatorAccount(null)
                .setCreatorAccountName(null).setCreateDate(null).setRemark(null);
        finCustomerCashPledgeBill.setDocumentDate(null);
        finCustomerCashPledgeBill.getItemList().forEach(item -> item.setCashPledgeBillItemSid(null).setCashPledgeBillSid(null).setRemark(null));
        return AjaxResult.success(finCustomerCashPledgeBill);
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long cashPledgeBillSid) {
        return AjaxResult.success(finCustomerCashPledgeBillService.invalid(cashPledgeBillSid));
    }
}
