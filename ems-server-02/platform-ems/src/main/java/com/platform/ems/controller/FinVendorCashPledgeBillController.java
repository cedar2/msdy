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
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinVendorCashPledgeBill;
import com.platform.ems.service.IFinVendorCashPledgeBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商押金Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/vendor/cash/pledge/bill")
@Api(tags = "供应商押金")
public class FinVendorCashPledgeBillController extends BaseController {

    @Autowired
    private IFinVendorCashPledgeBillService finVendorCashPledgeBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商押金列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商押金列表", notes = "查询供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorCashPledgeBill.class))
    public TableDataInfo list(@RequestBody FinVendorCashPledgeBill finVendorCashPledgeBill) {
        startPage(finVendorCashPledgeBill);
        List<FinVendorCashPledgeBill> list = finVendorCashPledgeBillService.selectFinVendorCashPledgeBillList(finVendorCashPledgeBill);
        return getDataTable(list);
    }

    /**
     * 导出供应商押金列表
     */
    @Log(title = "供应商押金", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商押金列表", notes = "导出供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorCashPledgeBill finVendorCashPledgeBill) throws IOException {
        List<FinVendorCashPledgeBill> list = finVendorCashPledgeBillService.selectFinVendorCashPledgeBillList(finVendorCashPledgeBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorCashPledgeBill> util = new ExcelUtil<>(FinVendorCashPledgeBill.class, dataMap);
        util.exportExcel(response, list, "供应商押金");
    }


    /**
     * 获取供应商押金详细信息
     */
    @ApiOperation(value = "获取供应商押金详细信息", notes = "获取供应商押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorCashPledgeBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long cashPledgeBillSid) {
        if (cashPledgeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorCashPledgeBillService.selectFinVendorCashPledgeBillById(cashPledgeBillSid));
    }

    /**
     * 新增供应商押金
     */
    @ApiOperation(value = "新增供应商押金", notes = "新增供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorCashPledgeBill finVendorCashPledgeBill) {
        int row = finVendorCashPledgeBillService.insertFinVendorCashPledgeBill(finVendorCashPledgeBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinVendorCashPledgeBill().setCashPledgeBillSid(finVendorCashPledgeBill.getCashPledgeBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改供应商押金
     */
    @ApiOperation(value = "修改供应商押金", notes = "修改供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorCashPledgeBill finVendorCashPledgeBill) {
        return toAjax(finVendorCashPledgeBillService.updateFinVendorCashPledgeBill(finVendorCashPledgeBill));
    }

    /**
     * 变更供应商押金
     */
    @ApiOperation(value = "变更供应商押金", notes = "变更供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorCashPledgeBill finVendorCashPledgeBill) {
        return toAjax(finVendorCashPledgeBillService.changeFinVendorCashPledgeBill(finVendorCashPledgeBill));
    }

    /**
     * 删除供应商押金
     */
    @ApiOperation(value = "删除供应商押金", notes = "删除供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> cashPledgeBillSids) {
        if (CollectionUtils.isEmpty(cashPledgeBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorCashPledgeBillService.deleteFinVendorCashPledgeBillByIds(cashPledgeBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorCashPledgeBill finVendorCashPledgeBill) {
        finVendorCashPledgeBill.setConfirmDate(new Date());
        finVendorCashPledgeBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        finVendorCashPledgeBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(finVendorCashPledgeBillService.check(finVendorCashPledgeBill));
    }

    /**
     * 复制供应商押金详细信息
     */
    @ApiOperation(value = "复制供应商押金详细信息", notes = "复制供应商押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorCashPledgeBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long cashPledgeBillSid) {
        if (cashPledgeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinVendorCashPledgeBill finVendorCashPledgeBill = finVendorCashPledgeBillService.selectFinVendorCashPledgeBillById(cashPledgeBillSid);
        finVendorCashPledgeBill.setCashPledgeBillSid(null).setCashPledgeBillCode(null).setHandleStatus(null).setCreatorAccount(null)
                .setCreatorAccountName(null).setCreateDate(null).setRemark(null);
        finVendorCashPledgeBill.setDocumentDate(null);
        finVendorCashPledgeBill.getItemList().forEach(item -> {
            item.setCashPledgeBillItemSid(null).setCashPledgeBillSid(null).setRemark(null);
        });
        return AjaxResult.success(finVendorCashPledgeBill);
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long cashPledgeBillSid) {
        return AjaxResult.success(finVendorCashPledgeBillService.invalid(cashPledgeBillSid));
    }

}
