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
import com.platform.ems.domain.dto.request.financial.FinVendorAccountBalanceBillInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinVendorAccountBalanceBillItemListRequest;
import com.platform.ems.domain.dto.request.financial.FinVendorAccountBalanceBillListRequest;
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
 * 供应商账互抵单Controller
 *
 * @author qhq
 * @date 2021-05-27
 */
@RestController
@RequestMapping("/vendor/balance/bill")
@Api(tags = "供应商账互抵单")
public class FinVendorAccountBalanceBillController extends BaseController {

    @Autowired
    private IFinVendorAccountBalanceBillService finVendorAccountBalanceBillService;
    @Autowired
    private IFinBookPaymentEstimationService finBookPaymentEstimationService;
    @Autowired
    private IFinBookAccountPayableService finBookAccountPayableService;
    @Autowired
    private IFinBookPaymentService finBookPaymentService;
    @Autowired
    private IFinBookVendorDeductionService finBookVendorDeductionService;
    @Autowired
    private IFinBookVendorAccountAdjustService finBookVendorAccountAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商账互抵单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商账互抵单列表", notes = "查询供应商账互抵单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountBalanceBill.class))
    public TableDataInfo list(@RequestBody FinVendorAccountBalanceBillListRequest request) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        startPage(finVendorAccountBalanceBill);
        List<FinVendorAccountBalanceBill> list = finVendorAccountBalanceBillService.selectFinVendorAccountBalanceBillList(finVendorAccountBalanceBill);
        return getDataTable(list, FinVendorAccountBalanceBillListResponse::new);
    }

    /**
     * 导出供应商账互抵单列表
     */
    @ApiOperation(value = "导出供应商账互抵单列表", notes = "导出供应商账互抵单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorAccountBalanceBillListRequest request) throws IOException {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        List<FinVendorAccountBalanceBill> list = finVendorAccountBalanceBillService.selectFinVendorAccountBalanceBillList(finVendorAccountBalanceBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorAccountBalanceBill> util = new ExcelUtil<>(FinVendorAccountBalanceBill.class, dataMap);
        util.exportExcel(response, list, "供应商账互抵单");
    }

    /**
     * 获取供应商账互抵单详细信息
     */
    @ApiOperation(value = "获取供应商账互抵单详细信息", notes = "获取供应商账互抵单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountBalanceBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long accountBalanceBillSid) {
        if (accountBalanceBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = finVendorAccountBalanceBillService.selectFinVendorAccountBalanceBillById(accountBalanceBillSid);
        FinVendorAccountBalanceBillInfoResponse response = new FinVendorAccountBalanceBillInfoResponse();
        BeanUtil.copyProperties(finVendorAccountBalanceBill, response);
        if (CollectionUtils.isNotEmpty(finVendorAccountBalanceBill.getItemList())) {
            List<FinVendorAccountBalanceBillItemInfoResponse> itemInfoResponses = BeanCopyUtils.copyListProperties(
                    finVendorAccountBalanceBill.getItemList(), FinVendorAccountBalanceBillItemInfoResponse::new
            );
            response.setItemList(itemInfoResponses);
        }
        //附件
        response.setAttachmentList(finVendorAccountBalanceBill.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增供应商账互抵单
     */
    @ApiOperation(value = "新增供应商账互抵单", notes = "新增供应商账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorAccountBalanceBillInfoRequest request) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        //附件
        finVendorAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountBalanceBillService.insertFinVendorAccountBalanceBill(finVendorAccountBalanceBill));
    }

    /**
     * 修改供应商账互抵单
     */
    @ApiOperation(value = "修改供应商账互抵单", notes = "修改供应商账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorAccountBalanceBillInfoRequest request) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        //附件
        finVendorAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountBalanceBillService.updateFinVendorAccountBalanceBill(finVendorAccountBalanceBill));
    }

    /**
     * 变更供应商账互抵单
     */
    @ApiOperation(value = "变更供应商账互抵单", notes = "变更供应商账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorAccountBalanceBillInfoRequest request) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        //附件
        finVendorAccountBalanceBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountBalanceBillService.changeFinVendorAccountBalanceBill(finVendorAccountBalanceBill));
    }

    /**
     * 删除供应商账互抵单
     */
    @ApiOperation(value = "删除供应商账互抵单", notes = "删除供应商账互抵单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> accountBalanceBillSids) {
        if (ArrayUtil.isEmpty(accountBalanceBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorAccountBalanceBillService.deleteFinVendorAccountBalanceBillByIds(accountBalanceBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorAccountBalanceBillListRequest request) {
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = new FinVendorAccountBalanceBill();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBill);
        return toAjax(finVendorAccountBalanceBillService.check(finVendorAccountBalanceBill));
    }

    @ApiOperation(value = "新增供应商互抵明细的查询接口", notes = "新增供应商互抵明细的查询接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountBalanceBillItemListReponse.class))
    @PostMapping("/bookList")
    public TableDataInfo bookList(@RequestBody FinVendorAccountBalanceBillItemListRequest request) {
        FinVendorAccountBalanceBillItem finVendorAccountBalanceBillItem = new FinVendorAccountBalanceBillItem();
        BeanUtil.copyProperties(request, finVendorAccountBalanceBillItem);
        finVendorAccountBalanceBillItem.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
        //处理分页问题
        List<FinVendorAccountBalanceBillItem> responseList = new ArrayList<>();
        TableDataInfo rspData = new TableDataInfo();
        //应付暂估
        if (ConstantsFinance.BOOK_TYPE_YFZG.equals(request.getBookType())) {
            FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
            BeanUtil.copyProperties(request, finBookPaymentEstimation);
            finBookPaymentEstimation.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookPaymentEstimation.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookPaymentEstimation);
            List<FinBookPaymentEstimation> bookList = finBookPaymentEstimationService.getReportForm(finBookPaymentEstimation);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookPaymentEstimationSid())
                        .setAccountDocumentCode(item.getBookPaymentEstimationCode())
                        .setAccountItemSid(item.getBookPaymentEstimationItemSid())
                        .setCurrencyAmountTax(null)
                        .setItemNum(item.getItemNum())
                        .setPriceTax(item.getPriceTax() == null ? BigDecimal.ZERO : item.getPriceTax())
                        .setCurrencyAmountTaxDhx(item.getCurrencyAmountTaxDhx() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxDhx())
                        .setCurrencyAmountTaxHxz(item.getCurrencyAmountTaxHxz() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxHxz())
                        .setCurrencyAmountTaxYhx(item.getCurrencyAmountTaxYhx() == null ? BigDecimal.ZERO : item.getCurrencyAmountTaxYhx())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTax() == null ? BigDecimal.ZERO : item.getCurrencyAmountTax());
                responseList.add(bill);
            });
        }
        //应付
        if (ConstantsFinance.BOOK_TYPE_YINGF.equals(request.getBookType())) {
            FinBookAccountPayable finBookAccountPayable = new FinBookAccountPayable();
            BeanUtil.copyProperties(request, finBookAccountPayable);
            finBookAccountPayable.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookAccountPayable.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookAccountPayable);
            List<FinBookAccountPayable> bookList = finBookAccountPayableService.getReportForm(finBookAccountPayable);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountPayableSid())
                        .setAccountDocumentCode(item.getBookAccountPayableCode())
                        .setAccountItemSid(item.getBookAccountPayableItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxYingf())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //付款
        if (ConstantsFinance.BOOK_TYPE_FK.equals(request.getBookType())) {
            FinBookPayment finBookPayment = new FinBookPayment();
            BeanUtil.copyProperties(request, finBookPayment);
            finBookPayment.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookPayment.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookPayment);
            List<FinBookPayment> bookList = finBookPaymentService.getReportForm(finBookPayment);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookPaymentSid())
                        .setAccountDocumentCode(item.getBookPaymentCode())
                        .setAccountItemSid(item.getBookPaymentItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxFk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //供应商扣款
        if (ConstantsFinance.BOOK_TYPE_VKK.equals(request.getBookType())) {
            FinBookVendorDeduction finBookVendorDeduction = new FinBookVendorDeduction();
            BeanUtil.copyProperties(request, finBookVendorDeduction);
            finBookVendorDeduction.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookVendorDeduction.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookVendorDeduction);
            List<FinBookVendorDeduction> bookList = finBookVendorDeductionService.getReportForm(finBookVendorDeduction);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookDeductionSid())
                        .setAccountDocumentCode(item.getBookDeductionCode())
                        .setAccountItemSid(item.getBookDeductionItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxKk())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        //供应商调账
        if (ConstantsFinance.BOOK_TYPE_VTZ.equals(request.getBookType())) {
            FinBookVendorAccountAdjust finBookVendorAccountAdjust = new FinBookVendorAccountAdjust();
            BeanUtil.copyProperties(request, finBookVendorAccountAdjust);
            finBookVendorAccountAdjust.setHandleStatusNot(HandleStatus.INVALID.getCode());
            finBookVendorAccountAdjust.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            startPage(finBookVendorAccountAdjust);
            List<FinBookVendorAccountAdjust> bookList = finBookVendorAccountAdjustService.getReportForm(finBookVendorAccountAdjust);
            rspData = getDataTable(bookList);
            bookList.forEach(item -> {
                FinVendorAccountBalanceBillItem bill = new FinVendorAccountBalanceBillItem();
                BeanUtil.copyProperties(item, bill);
                bill.setAccountDocumentSid(item.getBookAccountAdjustSid())
                        .setAccountDocumentCode(item.getBookAccountAdjustCode())
                        .setAccountItemSid(item.getBookAccountAdjustItemSid())
                        .setCurrencyAmountTaxYhd(item.getCurrencyAmountTaxTz())
                        .setItemNum(item.getItemNum());
                responseList.add(bill);
            });
        }
        rspData.setRows(BeanCopyUtils.copyListProperties(responseList, FinVendorAccountBalanceBillItemListReponse::new));
        return rspData;
    }

    /**
     * 复制供应商账互抵单详细信息
     */
    @ApiOperation(value = "复制供应商账互抵单详细信息", notes = "复制供应商账互抵单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountBalanceBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long accountBalanceBillSid) {
        if (accountBalanceBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        FinVendorAccountBalanceBill finVendorAccountBalanceBill = finVendorAccountBalanceBillService.selectFinVendorAccountBalanceBillById(accountBalanceBillSid);
        FinVendorAccountBalanceBillInfoResponse response = new FinVendorAccountBalanceBillInfoResponse();
        BeanUtil.copyProperties(finVendorAccountBalanceBill, response);
        response.setAccountBalanceBillSid(null).setAccountBalanceBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        if (CollectionUtils.isNotEmpty(finVendorAccountBalanceBill.getItemList())){
            List<FinVendorAccountBalanceBillItemInfoResponse> itemInfoResponses = BeanCopyUtils.copyListProperties(
                    finVendorAccountBalanceBill.getItemList(), FinVendorAccountBalanceBillItemInfoResponse::new
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
        return AjaxResult.success(finVendorAccountBalanceBillService.invalid(accountBalanceBillSid));
    }
}
