package com.platform.ems.controller;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.domain.SalCustomerMonthAccountBillAttach;
import com.platform.ems.service.ISalCustomerMonthAccountBillAttachService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 客户对账单-附件Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/sal/customer/month/account/bill/attach")
@Api(tags = "客户对账单-附件")
public class SalCustomerMonthAccountBillAttachController extends BaseController {

    @Autowired
    private ISalCustomerMonthAccountBillAttachService salCustomerMonthAccountBillAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户对账单-附件列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户对账单-附件列表", notes = "查询客户对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalCustomerMonthAccountBillAttach.class))
    public TableDataInfo list(@RequestBody SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        startPage(salCustomerMonthAccountBillAttach);
        List<SalCustomerMonthAccountBillAttach> list = salCustomerMonthAccountBillAttachService.selectSalCustomerMonthAccountBillAttachList(salCustomerMonthAccountBillAttach);
        return getDataTable(list);
    }

    /**
     * 导出客户对账单-附件列表
     */
    @ApiOperation(value = "导出客户对账单-附件列表", notes = "导出客户对账单-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) throws IOException {
        List<SalCustomerMonthAccountBillAttach> list = salCustomerMonthAccountBillAttachService.selectSalCustomerMonthAccountBillAttachList(salCustomerMonthAccountBillAttach);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalCustomerMonthAccountBillAttach> util = new ExcelUtil<SalCustomerMonthAccountBillAttach>(SalCustomerMonthAccountBillAttach.class, dataMap);
        util.exportExcel(response, list, "客户对账单-附件");
    }


    /**
     * 获取客户对账单-附件详细信息
     */
    @ApiOperation(value = "获取客户对账单-附件详细信息", notes = "获取客户对账单-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalCustomerMonthAccountBillAttach.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long monthAccountBillAttachmentSid) {
        if (monthAccountBillAttachmentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salCustomerMonthAccountBillAttachService.selectSalCustomerMonthAccountBillAttachById(monthAccountBillAttachmentSid));
    }

    /**
     * 新增客户对账单-附件
     */
    @ApiOperation(value = "新增客户对账单-附件", notes = "新增客户对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        return toAjax(salCustomerMonthAccountBillAttachService.insertSalCustomerMonthAccountBillAttach(salCustomerMonthAccountBillAttach));
    }

    /**
     * 修改客户对账单-附件
     */
    @ApiOperation(value = "修改客户对账单-附件", notes = "修改客户对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        return toAjax(salCustomerMonthAccountBillAttachService.updateSalCustomerMonthAccountBillAttach(salCustomerMonthAccountBillAttach));
    }

    /**
     * 变更客户对账单-附件
     */
    @ApiOperation(value = "变更客户对账单-附件", notes = "变更客户对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SalCustomerMonthAccountBillAttach salCustomerMonthAccountBillAttach) {
        return toAjax(salCustomerMonthAccountBillAttachService.changeSalCustomerMonthAccountBillAttach(salCustomerMonthAccountBillAttach));
    }

    /**
     * 删除客户对账单-附件
     */
    @ApiOperation(value = "删除客户对账单-附件", notes = "删除客户对账单-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> monthAccountBillAttachmentSids) {
        if (CollectionUtils.isEmpty(monthAccountBillAttachmentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salCustomerMonthAccountBillAttachService.deleteSalCustomerMonthAccountBillAttachByIds(monthAccountBillAttachmentSids));
    }

}
