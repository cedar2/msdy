package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.service.ISalSalesIntentOrderService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售意向单Controller
 *
 * @author chenkw
 * @date 2022-10-17
 */
@RestController
@RequestMapping("/sal/sales/intent/order")
@Api(tags = "销售意向单")
public class SalSalesIntentOrderController extends BaseController {

    @Autowired
    private ISalSalesIntentOrderService salSalesIntentOrderService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售意向单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售意向单列表", notes = "查询销售意向单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesIntentOrder.class))
    public TableDataInfo list(@RequestBody SalSalesIntentOrder salSalesIntentOrder) {
        startPage(salSalesIntentOrder);
        List<SalSalesIntentOrder> list = salSalesIntentOrderService.selectSalSalesIntentOrderList(salSalesIntentOrder);
        return getDataTable(list);
    }

    /**
     * 导出销售意向单列表
     */
    @PostMapping("/export")
    @Log(title = "销售意向单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售意向单列表", notes = "导出销售意向单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, SalSalesIntentOrder salSalesIntentOrder) throws IOException {
        List<SalSalesIntentOrder> list = salSalesIntentOrderService.selectSalSalesIntentOrderList(salSalesIntentOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSalesIntentOrder> util = new ExcelUtil<>(SalSalesIntentOrder.class, dataMap);
        util.exportExcel(response, list, "销售意向单");
    }

    /**
     * 获取销售意向单详细信息
     */
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取销售意向单详细信息", notes = "获取销售意向单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesIntentOrder.class))
    public AjaxResult getInfo(Long salesIntentOrderSid) {
        if (salesIntentOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesIntentOrderService.selectSalSalesIntentOrderById(salesIntentOrderSid));
    }

    /**
     * 复制销售意向单详细信息
     */
    @PostMapping("/copy")
    @ApiOperation(value = "复制销售意向单详细信息", notes = "复制销售意向单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesIntentOrder.class))
    public AjaxResult copy(Long salesIntentOrderSid) {
        if (salesIntentOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesIntentOrderService.copySalSalesIntentOrderById(salesIntentOrderSid));
    }

    /**
     * 新增销售意向单
     */
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "销售意向单", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增销售意向单", notes = "新增销售意向单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid SalSalesIntentOrder salSalesIntentOrder) {
        int row = salSalesIntentOrderService.insertSalSalesIntentOrder(salSalesIntentOrder);
        if (row > 0) {
            return AjaxResult.success("操作成功", new SalSalesIntentOrder().setSalesIntentOrderSid(salSalesIntentOrder.getSalesIntentOrderSid()));
        }
        return toAjax(row);
    }

    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "销售意向单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改销售意向单", notes = "修改销售意向单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult edit(@RequestBody @Valid SalSalesIntentOrder salSalesIntentOrder) {
        return toAjax(salSalesIntentOrderService.updateSalSalesIntentOrder(salSalesIntentOrder));
    }

    /**
     * 变更销售意向单
     */
    @PostMapping("/change")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "销售意向单", businessType = BusinessType.CHANGE)
    @ApiOperation(value = "变更销售意向单", notes = "变更销售意向单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid SalSalesIntentOrder salSalesIntentOrder) {
        return toAjax(salSalesIntentOrderService.changeSalSalesIntentOrder(salSalesIntentOrder));
    }

    /**
     * 删除销售意向单
     */
    @PostMapping("/delete")
    @Log(title = "销售意向单", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除销售意向单", notes = "删除销售意向单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult remove(@RequestBody List<Long> salesIntentOrderSids) {
        if (CollectionUtils.isEmpty(salesIntentOrderSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesIntentOrderService.deleteSalSalesIntentOrderByIds(salesIntentOrderSids));
    }

    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "销售意向单", businessType = BusinessType.CHECK)
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult check(@RequestBody SalSalesIntentOrder salSalesIntentOrder) {
        return toAjax(salSalesIntentOrderService.check(salSalesIntentOrder));
    }

    /**
     * 作废销售意向单
     */
    @PostMapping("/cancel")
    @Log(title = "作废销售意向单", businessType = BusinessType.CANCEL)
    @ApiOperation(value = "作废销售意向单（多选sid数组）", notes = "作废销售意向单（多选sid数组）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult cancel(@RequestBody SalSalesIntentOrder salSalesIntentOrder) {
        if (ArrayUtil.isEmpty(salSalesIntentOrder.getSalesIntentOrderSidList())) {
            throw new BaseException("参数缺失");
        }
        if (StrUtil.isBlank(salSalesIntentOrder.getCancelRemark())) {
            throw new BaseException("请填写作废说明再操作");
        }
        return toAjax(salSalesIntentOrderService.cancel(salSalesIntentOrder));
    }

    @ApiOperation(value = "设置盖章件签收", notes = "设置盖章件签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign/status")
    public AjaxResult signStsu(@RequestBody SalSalesIntentOrder salesOrder) {
        if (ArrayUtil.isEmpty(salesOrder.getSalesIntentOrderSidList())) {
            throw new CheckedException("请勾选行");
        }
        return toAjax(salSalesIntentOrderService.setSignStatus(salesOrder));
    }

    /**
     * 维护纸质合同号
     */
    @ApiOperation(value = "维护纸质合同号", notes = "维护纸质合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/set/paperContract")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult setPaperContract(@RequestBody SalSalesIntentOrder salesOrder) {
        if (salesOrder.getSalesIntentOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesIntentOrderService.setPaperContract(salesOrder));
    }

    @PostMapping("/count/refresh")
    @ApiOperation(value = "订单明细页签-合计字段刷新", notes = "订单明细页签-合计字段刷新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesIntentOrder.class))
    public SalSalesIntentOrder getCount(@RequestBody List<SalSalesIntentOrderItem> items) {
        return salSalesIntentOrderService.getCount(items);
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody SalSalesIntentOrderAttach salesIntentOrderAttach) {
        return salSalesIntentOrderService.checkAttach(salesIntentOrderAttach);
    }

    @ApiOperation(value = "新增采购订单查询页面上传附件", notes = "新增采购订单查询页面上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid SalSalesIntentOrderAttach salesIntentOrderAttach) {
        int row = salSalesIntentOrderService.insertSalSalesIntentOrderAttach(salesIntentOrderAttach);
        if (row > 0) {
            if (ConstantsOrder.PAPER_CONTRACT_XSYXDDHT.equals(salesIntentOrderAttach.getFileType())) {
                salSalesIntentOrderService.update(new UpdateWrapper<SalSalesIntentOrder>().lambda()
                        .eq(SalSalesIntentOrder::getSalesIntentOrderSid, salesIntentOrderAttach.getSalesIntentOrderSid())
                        .set(SalSalesIntentOrder::getUploadStatus, ConstantsEms.CONTRACT_UPLOAD_STATUS_Y));
            }
        }
        return AjaxResult.success(row);
    }
}
