package com.platform.ems.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.financial.FinCustomerAccountBalanceBillInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinCustomerAccountBalanceBillItemListRequest;
import com.platform.ems.domain.dto.request.financial.FinCustomerAccountBalanceBillListRequest;
import com.platform.ems.domain.dto.response.financial.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 客户账互抵单Controller
 *
 * @author qhq
 * @date 2021-05-27
 */
@RestController
@RequestMapping("/customer/balance/bill")
@Api(tags = "客户账互抵单")
public class FinCustomerAccountBalanceBillController extends BaseController {

    @Autowired
    private IFinCustomerAccountBalanceBillService finCustomerAccountBalanceBillService;
    @Autowired
    private IFinBookReceiptEstimationService finBookReceiptEstimationService;
    @Autowired
    private IFinBookAccountReceivableService finBookAccountReceivableService;
    @Autowired
    private IFinBookReceiptPaymentService finBookReceiptPaymentService;
    @Autowired
    private IFinBookCustomerDeductionService finBookCustomerDeductionService;
    @Autowired
    private IFinBookCustomerAccountAdjustService finBookCustomerAccountAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户账互抵单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户账互抵单列表", notes = "查询客户账互抵单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBill.class))
    public TableDataInfo list(@RequestBody FinCustomerAccountBalanceBillListRequest request) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        startPage(finCustomerAccountBalanceBill);
        List<FinCustomerAccountBalanceBill> list = finCustomerAccountBalanceBillService.selectFinCustomerAccountBalanceBillList(finCustomerAccountBalanceBill);
        return getDataTable(list, FinCustomerAccountBalanceBillListResponse::new);
    }

    /**
     * 导出客户账互抵单列表
     */
    @ApiOperation(value = "导出客户账互抵单列表", notes = "导出客户账互抵单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerAccountBalanceBillListRequest request) throws IOException {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        List<FinCustomerAccountBalanceBill> list = finCustomerAccountBalanceBillService.selectFinCustomerAccountBalanceBillList(finCustomerAccountBalanceBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerAccountBalanceBill> util = new ExcelUtil<>(FinCustomerAccountBalanceBill.class, dataMap);
        util.exportExcel(response, list, "客户账互抵单");
    }

    /**
     * 获取客户账互抵单详细信息
     */
    @ApiOperation(value = "获取客户账互抵单详细信息", notes = "获取客户账互抵单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long accountBalanceBillSid) {
        if (accountBalanceBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = finCustomerAccountBalanceBillService.selectFinCustomerAccountBalanceBillById(accountBalanceBillSid);
        FinCustomerAccountBalanceBillInfoResponse response = new FinCustomerAccountBalanceBillInfoResponse();
        BeanUtil.copyProperties(finCustomerAccountBalanceBill, response);
        if (CollectionUtils.isNotEmpty(finCustomerAccountBalanceBill.getItemList())){
            List<FinCustomerAccountBalanceBillItemInfoResponse> itemInfoResponses = BeanCopyUtils.copyListProperties(
                    finCustomerAccountBalanceBill.getItemList(), FinCustomerAccountBalanceBillItemInfoResponse::new
            );
            response.setItemList(itemInfoResponses);
        }
        return AjaxResult.success(response);
    }

    /**
     * 新增客户账互抵单
     */
    @ApiOperation(value = "新增客户账互抵单", notes = "新增客户账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerAccountBalanceBillInfoRequest request) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        finCustomerAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountBalanceBillService.insertFinCustomerAccountBalanceBill(finCustomerAccountBalanceBill));
    }

    /**
     * 修改客户账互抵单
     */
    @ApiOperation(value = "修改客户账互抵单", notes = "修改客户账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerAccountBalanceBillInfoRequest request) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        finCustomerAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountBalanceBillService.updateFinCustomerAccountBalanceBill(finCustomerAccountBalanceBill));
    }

    /**
     * 变更客户账互抵单
     */
    @ApiOperation(value = "变更客户账互抵单", notes = "变更客户账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerAccountBalanceBillInfoRequest request) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        finCustomerAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountBalanceBillService.changeFinCustomerAccountBalanceBill(finCustomerAccountBalanceBill));
    }

    /**
     * 删除客户账互抵单
     */
    @ApiOperation(value = "删除客户账互抵单", notes = "删除客户账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerAccountBalanceBillSids) {
        if (ArrayUtil.isEmpty(customerAccountBalanceBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerAccountBalanceBillService.deleteFinCustomerAccountBalanceBillByIds(customerAccountBalanceBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerAccountBalanceBillListRequest request) {
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = new FinCustomerAccountBalanceBill();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBill);
        return toAjax(finCustomerAccountBalanceBillService.check(finCustomerAccountBalanceBill));
    }

    @ApiOperation(value = "新增客户互抵明细的查询接口", notes = "新增客户互抵明细的查询接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBillItemListReponse.class))
    @PostMapping("/bookList")
    public TableDataInfo bookList(@RequestBody FinCustomerAccountBalanceBillItemListRequest request) {
        FinCustomerAccountBalanceBillItem finCustomerAccountBalanceBillItem = new FinCustomerAccountBalanceBillItem();
        BeanUtil.copyProperties(request, finCustomerAccountBalanceBillItem);
        finCustomerAccountBalanceBillItem.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
        //处理分页问题
        List<FinCustomerAccountBalanceBillItem> responseList = new ArrayList<>();
        TableDataInfo rspData = new TableDataInfo();
        //应收暂估
        if (ConstantsFinance.BOOK_TYPE_YSZG.equals(request.getBookType())) {
            FinBookReceiptEstimation finBookReceiptEstimation = new FinBookReceiptEstimation();
            BeanUtil.copyProperties(request, finBookReceiptEstimation);
            finBookReceiptEstimation.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookReceiptEstimation.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookReceiptEstimation);
            List<FinBookReceiptEstimation> bookList = finBookReceiptEstimationService.getReportForm(finBookReceiptEstimation);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookReceiptEstimationSid())
                        .setAccountDocumentCode(item.getBookReceiptEstimationCode())
                        .setAccountItemSid(item.getBookReceiptEstimationItemSid())
                        .setCurrencyAmountTax(null)
                        .setPriceTax(item.getPriceTax() == null ? BigDecimal.ZERO : item.getPriceTax())
                        .setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx())
                        .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz())
                        .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTax())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //应收流水
        if (ConstantsFinance.BOOK_TYPE_YINGS.equals(request.getBookType())) {
            FinBookAccountReceivable finBookAccountReceivable = new FinBookAccountReceivable();
            BeanUtil.copyProperties(request, finBookAccountReceivable);
            finBookAccountReceivable.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookAccountReceivable.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookAccountReceivable);
            List<FinBookAccountReceivable> bookList = finBookAccountReceivableService.getReportForm(finBookAccountReceivable);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountReceivableSid())
                        .setAccountDocumentCode(item.getBookAccountReceivableCode())
                        .setAccountItemSid(item.getBookAccountReceivableItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxYings())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //收款流失
        if (ConstantsFinance.BOOK_TYPE_SK.equals(request.getBookType())) {
            FinBookReceiptPayment finBookReceiptPayment = new FinBookReceiptPayment();
            BeanUtil.copyProperties(request, finBookReceiptPayment);
            finBookReceiptPayment.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookReceiptPayment.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookReceiptPayment);
            List<FinBookReceiptPayment> bookList = finBookReceiptPaymentService.getReportForm(finBookReceiptPayment);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookReceiptPaymentSid())
                        .setAccountDocumentCode(item.getBookReceiptPaymentCode())
                        .setAccountItemSid(item.getBookReceiptPaymentItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxSk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //客户扣款流水
        if (ConstantsFinance.BOOK_TYPE_CKK.equals(request.getBookType())) {
            FinBookCustomerDeduction finBookCustomerDeduction = new FinBookCustomerDeduction();
            BeanUtil.copyProperties(request, finBookCustomerDeduction);
            finBookCustomerDeduction.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookCustomerDeduction.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookCustomerDeduction);
            List<FinBookCustomerDeduction> bookList = finBookCustomerDeductionService.getReportForm(finBookCustomerDeduction);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookDeductionSid())
                        .setAccountDocumentCode(item.getBookDeductionCode())
                        .setAccountItemSid(item.getBookDeductionItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxKk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //客户调账流水
        if (ConstantsFinance.BOOK_TYPE_CTZ.equals(request.getBookType())) {
            FinBookCustomerAccountAdjust finBookCustomerAccountAdjust = new FinBookCustomerAccountAdjust();
            BeanUtil.copyProperties(request, finBookCustomerAccountAdjust);
            finBookCustomerAccountAdjust.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookCustomerAccountAdjust.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookCustomerAccountAdjust);
            List<FinBookCustomerAccountAdjust> bookList = finBookCustomerAccountAdjustService.getReportForm(finBookCustomerAccountAdjust);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinCustomerAccountBalanceBillItem bill = new FinCustomerAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountAdjustSid())
                        .setAccountDocumentCode(item.getBookAccountAdjustCode())
                        .setAccountItemSid(item.getBookAccountAdjustItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxTz())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        rspData.setRows(BeanCopyUtils.copyListProperties(responseList, FinCustomerAccountBalanceBillItemListReponse::new));
        return rspData;
    }

    /**
     * 复制客户账互抵单详细信息
     */
    @ApiOperation(value = "复制客户账互抵单详细信息", notes = "复制客户账互抵单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long accountBalanceBillSid) {
        if (accountBalanceBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinCustomerAccountBalanceBill finCustomerAccountBalanceBill = finCustomerAccountBalanceBillService.selectFinCustomerAccountBalanceBillById(accountBalanceBillSid);
        FinCustomerAccountBalanceBillInfoResponse response = new FinCustomerAccountBalanceBillInfoResponse();
        BeanUtil.copyProperties(finCustomerAccountBalanceBill, response);
        response.setAccountBalanceBillSid(null).setAccountBalanceBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        if (CollectionUtils.isNotEmpty(finCustomerAccountBalanceBill.getItemList())){
            List<FinCustomerAccountBalanceBillItemInfoResponse> itemInfoResponses = BeanCopyUtils.copyListProperties(
                    finCustomerAccountBalanceBill.getItemList(), FinCustomerAccountBalanceBillItemInfoResponse::new
            );
            itemInfoResponses.forEach(item-> item.setAccountBalanceBillItemSid(null));
            response.setItemList(itemInfoResponses);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long accountBalanceBillSid) {
        return AjaxResult.success(finCustomerAccountBalanceBillService.invalid(accountBalanceBillSid));
    }
}
