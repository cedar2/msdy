package com.platform.ems.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.financial.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IFinBookVendorAccountAdjustService;
import com.platform.ems.service.IFinBookVendorDeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.dto.request.financial.FinPurchaseInvoiceDiscountListRequest;
import com.platform.ems.domain.dto.request.financial.FinPurchaseInvoiceInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinPurchaseInvoiceListRequest;
import com.platform.ems.service.IFinPurchaseInvoiceService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 采购发票Controller
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@RestController
@RequestMapping("/purchase/invoice")
@Api(tags = "采购发票")
public class FinPurchaseInvoiceController extends BaseController {

    @Autowired
    private IFinPurchaseInvoiceService finPurchaseInvoiceService;
    @Autowired
    private IFinBookVendorAccountAdjustService finBookVendorAccountAdjustService;
    @Autowired
    private IFinBookVendorDeductionService finBookVendorDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;


    /**
     * 查询采购发票列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购发票列表", notes = "查询采购发票列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoice.class))
    public TableDataInfo list(@RequestBody FinPurchaseInvoiceListRequest request) {
        FinPurchaseInvoice finPurchaseInvoice = new FinPurchaseInvoice();
        BeanUtil.copyProperties(request, finPurchaseInvoice);
        startPage(finPurchaseInvoice);
        List<FinPurchaseInvoice> list = finPurchaseInvoiceService.selectFinPurchaseInvoiceList(finPurchaseInvoice);
        return getDataTable(list,FinPurchaseInvoiceListResponse::new);
    }

    /**
     * 导出采购发票列表
     */
    @ApiOperation(value = "导出采购发票列表", notes = "导出采购发票列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinPurchaseInvoiceListRequest request) throws IOException {
        FinPurchaseInvoice finPurchaseInvoice = new FinPurchaseInvoice();
        BeanUtil.copyProperties(request, finPurchaseInvoice);
        List<FinPurchaseInvoice> list = finPurchaseInvoiceService.selectFinPurchaseInvoiceList(finPurchaseInvoice);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPurchaseInvoice> util = new ExcelUtil<>(FinPurchaseInvoice.class, dataMap);
        util.exportExcel(response, list, "采购发票");
    }

    /**
     * 获取采购发票详细信息
     */
    @ApiOperation(value = "获取采购发票详细信息", notes = "获取采购发票详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoice.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseInvoiceSid) {
        if (purchaseInvoiceSid == null) {
            throw new CheckedException("参数缺失");
        }
        //得到详情
        FinPurchaseInvoice finPurchaseInvoice = finPurchaseInvoiceService.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        //转换主表
        FinPurchaseInvoiceInfoResponse response = new FinPurchaseInvoiceInfoResponse();
        BeanUtil.copyProperties(finPurchaseInvoice, response);
        //转换明细表
        if (finPurchaseInvoice.getFinPurchaseInvoiceItemList() != null) {
            List<FinPurchaseInvoiceItemListResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(
                    finPurchaseInvoice.getFinPurchaseInvoiceItemList(), FinPurchaseInvoiceItemListResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //转换折扣表
        if (finPurchaseInvoice.getFinPurchaseInvoiceDiscountList() != null) {
            List<FinPurchaseInvoiceDiscountListResponse> discountInfoResponses = BeanCopyUtils.copyListProperties(
                    finPurchaseInvoice.getFinPurchaseInvoiceDiscountList(), FinPurchaseInvoiceDiscountListResponse::new);
            response.setDiscountList(discountInfoResponses);
        }
        //附件
        response.setAttachmentList(finPurchaseInvoice.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增采购发票
     */
    @ApiOperation(value = "新增采购发票", notes = "新增采购发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinPurchaseInvoiceInfoRequest request) {
        FinPurchaseInvoice finPurchaseInvoice = this.transform(request);
        return toAjax(finPurchaseInvoiceService.insertFinPurchaseInvoice(finPurchaseInvoice));
    }

    /**
     * 修改采购发票
     */
    @ApiOperation(value = "修改采购发票", notes = "修改采购发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinPurchaseInvoiceInfoRequest request) {
        FinPurchaseInvoice finPurchaseInvoice = this.transform(request);
        return toAjax(finPurchaseInvoiceService.updateFinPurchaseInvoice(finPurchaseInvoice));
    }

    /**
     * 删除采购发票
     */
    @ApiOperation(value = "删除采购发票", notes = "删除采购发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> purchaseInvoiceSids) {
        if (ArrayUtil.isEmpty(purchaseInvoiceSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finPurchaseInvoiceService.deleteFinPurchaseInvoiceByIds(purchaseInvoiceSids));
    }

    /**
     * 采购发票提交前校验
     */
    @PostMapping("/verification")
    @ApiOperation(value = "采购发票提交前校验", notes = "采购发票提交前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult verification(Long purchaseInvoiceSid) {
        FinPurchaseInvoice finPurchaseInvoice = finPurchaseInvoiceService.selectFinPurchaseInvoiceById(purchaseInvoiceSid);
        finPurchaseInvoice.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        finPurchaseInvoiceService.setConfirmInfo(finPurchaseInvoice);
        return AjaxResult.success();
    }

    /**
     * 采购发票确认
     */
    @PostMapping("/check")
    @ApiOperation(value = "采购发票确认", notes = "采购发票确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody FinPurchaseInvoiceListRequest request) {
        FinPurchaseInvoice finPurchaseInvoice = new FinPurchaseInvoice();
        BeanUtil.copyProperties(request, finPurchaseInvoice);
        finPurchaseInvoice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                .setConfirmDate(new Date())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        return AjaxResult.success(finPurchaseInvoiceService.confirm(finPurchaseInvoice));
    }

    /**
     * 采购发票变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "采购发票变更", notes = "采购发票变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid FinPurchaseInvoiceInfoRequest request) {
        FinPurchaseInvoice finPurchaseInvoice = this.transform(request);
        return AjaxResult.success(finPurchaseInvoiceService.change(finPurchaseInvoice));
    }

    /**
     * dto转换
     *
     * @param request 请求实体
     * @return FinPurchaseInvoice 返回实体
     */
    private FinPurchaseInvoice transform(FinPurchaseInvoiceInfoRequest request) {
        //转为DO
        FinPurchaseInvoice finPurchaseInvoice = new FinPurchaseInvoice();
        BeanUtil.copyProperties(request, finPurchaseInvoice);
        //明细表
        if (request.getItemList() != null) {
            List<FinPurchaseInvoiceItem> finPurchaseInvoiceItems = BeanCopyUtils.copyListProperties(
                    request.getItemList(), FinPurchaseInvoiceItem::new);
            finPurchaseInvoice.setFinPurchaseInvoiceItemList(finPurchaseInvoiceItems);
        }
        //折扣表
        if (request.getDiscountList() != null) {
            List<FinPurchaseInvoiceDiscount> finPurchaseInvoiceDiscounts = BeanCopyUtils.copyListProperties(
                    request.getDiscountList(), FinPurchaseInvoiceDiscount::new);
            finPurchaseInvoice.setFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscounts);
        }
        //附件
        finPurchaseInvoice.setAttachmentList(request.getAttachmentList());
        return finPurchaseInvoice;
    }

    @ApiOperation(value = "新增折扣明细的查询接口", notes = "新增折扣明细的查询接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/bookList")
    public TableDataInfo bookList(@RequestBody FinPurchaseInvoiceDiscountListRequest request) {
        FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount = new FinPurchaseInvoiceDiscount();
        BeanUtil.copyProperties(request,finPurchaseInvoiceDiscount);
        String bookType = request.getBookType();
        //转为折扣返回实体列表
        List<FinPurchaseInvoiceDiscount> responses = new ArrayList<>();
        TableDataInfo rspData = new TableDataInfo();
        //供应商扣款流水
        if (ConstantsFinance.BOOK_TYPE_VKK.equals(bookType)) {
            FinBookVendorDeduction book = new FinBookVendorDeduction();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            book.setHandleStatusNot(HandleStatus.INVALID.getCode());
            //查找供应商扣款报表
            startPage(book);
            List<FinBookVendorDeduction> bookList = finBookVendorDeductionService.getReportForm(book);
            rspData = getDataTable(bookList);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinPurchaseInvoiceDiscount response = new FinPurchaseInvoiceDiscount();
                BeanUtil.copyProperties(bookItem, response);
                //处理字段不一致的参数
                response.setAccountDocumentSid(bookItem.getBookDeductionSid()).setAccountDocumentCode(bookItem.getBookDeductionCode())
                        .setAccountItemSid(bookItem.getBookDeductionItemSid());
                //避免空指针
                if (bookItem.getCurrencyAmountTaxKk() == null) {
                    bookItem.setCurrencyAmountTaxKk(BigDecimal.ZERO);
                }
                response.setCurrencyAmountTaxYingD(bookItem.getCurrencyAmountTaxKk())
                        .setCurrencyAmountTaxDaiD(bookItem.getCurrencyAmountTaxDhx());
                responses.add(response);
            });
        }
        //供应商调账流水
        if (ConstantsFinance.BOOK_TYPE_VTZ.equals(bookType)) {
            FinBookVendorAccountAdjust book = new FinBookVendorAccountAdjust();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            book.setHandleStatusNot(HandleStatus.INVALID.getCode());
            //查找供应商调账报表
            startPage(book);
            List<FinBookVendorAccountAdjust> bookList = finBookVendorAccountAdjustService.getReportForm(book);
            rspData = getDataTable(bookList);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinPurchaseInvoiceDiscount response = new FinPurchaseInvoiceDiscount();
                BeanUtil.copyProperties(bookItem, response);
                //处理字段不一致的参数
                response.setAccountDocumentSid(bookItem.getBookAccountAdjustSid()).setAccountDocumentCode(bookItem.getBookAccountAdjustCode())
                        .setAccountItemSid(bookItem.getBookAccountAdjustItemSid());
                //避免空指针
                if (bookItem.getCurrencyAmountTaxTz() == null) {
                    bookItem.setCurrencyAmountTaxTz(BigDecimal.ZERO);
                }
                response.setCurrencyAmountTaxYingD(bookItem.getCurrencyAmountTaxTz())
                        .setCurrencyAmountTaxDaiD(bookItem.getCurrencyAmountTaxDhx());
                responses.add(response);
            });
        }
        rspData.setRows(BeanCopyUtils.copyListProperties(responses, FinPurchaseInvoiceDiscountListResponse::new));
        return rspData;
    }

    @ApiOperation(value = "作废发票接口", notes = "作废发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/invalidInvoice")
    public AjaxResult invalidInvoice(Long purchaseInvoiceSid) {
        return AjaxResult.success(finPurchaseInvoiceService.invalidInvoice(purchaseInvoiceSid));
    }

    @ApiOperation(value = "红冲发票接口", notes = "红冲发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/redDashed")
    public AjaxResult redDashed(Long purchaseInvoiceSid) {
        return AjaxResult.success(finPurchaseInvoiceService.redDashed(purchaseInvoiceSid));
    }

    @ApiOperation(value = "签收发票接口", notes = "签收发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/changeSignFlag")
    public AjaxResult changeSignFlag(@RequestBody List<Long> purchaseInvoiceSids) {
        return AjaxResult.success(finPurchaseInvoiceService.changeSignFlag(purchaseInvoiceSids));
    }


}
