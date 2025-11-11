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
import com.platform.ems.domain.FinVendorFundsFreezeBill;
import com.platform.ems.service.IFinVendorFundsFreezeBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商暂押款Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/vendor/funds/freeze/bill")
@Api(tags = "供应商暂押款")
public class FinVendorFundsFreezeBillController extends BaseController {

    @Autowired
    private IFinVendorFundsFreezeBillService finVendorFundsFreezeBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商暂押款列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商暂押款列表", notes = "查询供应商暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorFundsFreezeBill.class))
    public TableDataInfo list(@RequestBody FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        startPage(finVendorFundsFreezeBill);
        List<FinVendorFundsFreezeBill> list = finVendorFundsFreezeBillService.selectFinVendorFundsFreezeBillList(finVendorFundsFreezeBill);
        return getDataTable(list);
    }

    /**
     * 导出供应商暂押款列表
     */
    @ApiOperation(value = "导出供应商暂押款列表", notes = "导出供应商暂押款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorFundsFreezeBill finVendorFundsFreezeBill) throws IOException {
        List<FinVendorFundsFreezeBill> list = finVendorFundsFreezeBillService.selectFinVendorFundsFreezeBillList(finVendorFundsFreezeBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorFundsFreezeBill> util = new ExcelUtil<>(FinVendorFundsFreezeBill.class, dataMap);
        util.exportExcel(response, list, "供应商暂押款");
    }

    /**
     * 获取供应商暂押款详细信息
     */
    @ApiOperation(value = "获取供应商暂押款详细信息", notes = "获取供应商暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorFundsFreezeBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long fundsFreezeBillSid) {
        if (fundsFreezeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorFundsFreezeBillService.selectFinVendorFundsFreezeBillById(fundsFreezeBillSid));
    }

    /**
     * 新增供应商暂押款
     */
    @ApiOperation(value = "新增供应商暂押款", notes = "新增供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        int row = finVendorFundsFreezeBillService.insertFinVendorFundsFreezeBill(finVendorFundsFreezeBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinVendorFundsFreezeBill().setFundsFreezeBillSid(finVendorFundsFreezeBill.getFundsFreezeBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改供应商暂押款
     */
    @ApiOperation(value = "修改供应商暂押款", notes = "修改供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        return toAjax(finVendorFundsFreezeBillService.updateFinVendorFundsFreezeBill(finVendorFundsFreezeBill));
    }

    /**
     * 变更供应商暂押款
     */
    @ApiOperation(value = "变更供应商暂押款", notes = "变更供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        return toAjax(finVendorFundsFreezeBillService.changeFinVendorFundsFreezeBill(finVendorFundsFreezeBill));
    }

    /**
     * 删除供应商暂押款
     */
    @ApiOperation(value = "删除供应商暂押款", notes = "删除供应商暂押款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> fundsFreezeBillSids) {
        if (CollectionUtils.isEmpty(fundsFreezeBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorFundsFreezeBillService.deleteFinVendorFundsFreezeBillByIds(fundsFreezeBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorFundsFreezeBill finVendorFundsFreezeBill) {
        return toAjax(finVendorFundsFreezeBillService.check(finVendorFundsFreezeBill));
    }

    /**
     * 复制供应商暂押款详细信息
     */
    @ApiOperation(value = "复制供应商暂押款详细信息", notes = "复制供应商暂押款详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorFundsFreezeBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long cashPledgeBillSid) {
        if (cashPledgeBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinVendorFundsFreezeBill finVendorFundsFreezeBill = finVendorFundsFreezeBillService.selectFinVendorFundsFreezeBillById(cashPledgeBillSid);
        finVendorFundsFreezeBill.setFundsFreezeBillSid(null).setFundsFreezeBillCode(null).setHandleStatus(null).setCreatorAccount(null)
                .setCreatorAccountName(null).setCreateDate(null).setRemark(null);
        finVendorFundsFreezeBill.setDocumentDate(null);
        finVendorFundsFreezeBill.getItemList().forEach(item -> item.setFundsFreezeBillSid(null).setFundsFreezeBillItemSid(null).setRemark(null));
        return AjaxResult.success(finVendorFundsFreezeBill);
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long fundsFreezeBillSid) {
        return AjaxResult.success(finVendorFundsFreezeBillService.invalid(fundsFreezeBillSid));
    }
}
