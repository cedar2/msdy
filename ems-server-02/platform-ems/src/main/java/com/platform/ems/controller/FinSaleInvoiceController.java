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
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.financial.FinSaleInvoiceDiscountListRequest;
import com.platform.ems.domain.dto.request.financial.FinSaleInvoiceInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinSaleInvoiceListRequest;
import com.platform.ems.domain.dto.response.financial.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.*;
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
 * 销售发票Controller
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@RestController
@RequestMapping("/sale/invoice")
@Api(tags = "销售发票")
public class FinSaleInvoiceController extends BaseController {

    @Autowired
    private IFinSaleInvoiceService finSaleInvoiceService;
    @Autowired
    private IFinBookCustomerAccountAdjustService finBookCustomerAccountAdjustService;
    @Autowired
    private IFinBookCustomerDeductionService finBookCustomerDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售发票列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售发票列表", notes = "查询销售发票列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinSaleInvoice.class))
    public TableDataInfo list(@RequestBody FinSaleInvoiceListRequest request) {
        FinSaleInvoice finSaleInvoice = new FinSaleInvoice();
        BeanUtil.copyProperties(request, finSaleInvoice);
        startPage(finSaleInvoice);
        List<FinSaleInvoice> list = finSaleInvoiceService.selectFinSaleInvoiceList(finSaleInvoice);
        return getDataTable(list, FinSaleInvoiceListResponse::new);
    }

    /**
     * 导出销售发票列表
     */
    @ApiOperation(value = "导出销售发票列表", notes = "导出销售发票列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinSaleInvoiceListRequest request) throws IOException {
        FinSaleInvoice finSaleInvoice = new FinSaleInvoice();
        BeanUtil.copyProperties(request, finSaleInvoice);
        List<FinSaleInvoice> list = finSaleInvoiceService.selectFinSaleInvoiceList(finSaleInvoice);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinSaleInvoice> util = new ExcelUtil<>(FinSaleInvoice.class, dataMap);
        util.exportExcel(response, list, "销售开票");
    }

    /**
     * 获取销售发票详细信息
     */
    @ApiOperation(value = "获取销售发票详细信息", notes = "获取销售发票详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinSaleInvoice.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long saleInvoiceSid) {
        if (saleInvoiceSid == null) {
            throw new CheckedException("参数缺失");
        }
        //得到详情
        FinSaleInvoice finSaleInvoice = finSaleInvoiceService.selectFinSaleInvoiceById(saleInvoiceSid);
        //转换主表
        FinSaleInvoiceInfoResponse response = new FinSaleInvoiceInfoResponse();
        BeanUtil.copyProperties(finSaleInvoice, response);
        //转换明细表
        if (finSaleInvoice.getFinSaleInvoiceItemList() != null) {
            List<FinSaleInvoiceItemListResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(
                    finSaleInvoice.getFinSaleInvoiceItemList(), FinSaleInvoiceItemListResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //转换折扣表
        if (finSaleInvoice.getFinSaleInvoiceDiscountList() != null) {
            List<FinSaleInvoiceDiscountListResponse> discountInfoResponses = BeanCopyUtils.copyListProperties(
                    finSaleInvoice.getFinSaleInvoiceDiscountList(), FinSaleInvoiceDiscountListResponse::new);
            response.setDiscountList(discountInfoResponses);
        }
        //附件
        response.setAttachmentList(finSaleInvoice.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增销售发票
     */
    @ApiOperation(value = "新增销售发票", notes = "新增销售发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinSaleInvoiceInfoRequest request) {
        FinSaleInvoice finSaleInvoice = this.transform(request);
        return toAjax(finSaleInvoiceService.insertFinSaleInvoice(finSaleInvoice));
    }

    /**
     * 修改销售发票
     */
    @ApiOperation(value = "修改销售发票", notes = "修改销售发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinSaleInvoiceInfoRequest request) {
        FinSaleInvoice finSaleInvoice = this.transform(request);
        return toAjax(finSaleInvoiceService.updateFinSaleInvoice(finSaleInvoice));
    }

    /**
     * 删除销售发票
     */
    @ApiOperation(value = "删除销售发票", notes = "删除销售发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> saleInvoiceSids) {
        if (ArrayUtil.isEmpty(saleInvoiceSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finSaleInvoiceService.deleteFinSaleInvoiceByIds(saleInvoiceSids));
    }

    /**
     * 销售发票提交前校验
     */
    @PostMapping("/verification")
    @ApiOperation(value = "销售发票提交前校验", notes = "销售发票提交前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult verification(Long saleInvoiceSid) {
        FinSaleInvoice finSaleInvoice = finSaleInvoiceService.selectFinSaleInvoiceById(saleInvoiceSid);
        finSaleInvoice.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        finSaleInvoiceService.setConfirmInfo(finSaleInvoice);
        return AjaxResult.success();
    }

    /**
     * 销售发票确认
     */
    @PostMapping("/check")
    @ApiOperation(value = "销售发票确认", notes = "销售发票确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody FinSaleInvoiceListRequest request) {
        FinSaleInvoice finSaleInvoice = new FinSaleInvoice();
        BeanUtil.copyProperties(request, finSaleInvoice);
        return AjaxResult.success(finSaleInvoiceService.confirm(finSaleInvoice));
    }

    /**
     * 销售发票变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "销售发票变更", notes = "销售发票变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid FinSaleInvoiceInfoRequest request) {
        FinSaleInvoice finSaleInvoice = this.transform(request);
        return AjaxResult.success(finSaleInvoiceService.change(finSaleInvoice));
    }

    /**
     * dto转换
     *
     * @param request 请求实体
     * @return FinSaleInvoice 返回实体
     */
    private FinSaleInvoice transform(FinSaleInvoiceInfoRequest request) {

        //转为DO
        FinSaleInvoice finSaleInvoice = new FinSaleInvoice();
        BeanUtil.copyProperties(request, finSaleInvoice);
        //明细表
        if (request.getItemList() != null) {
            List<FinSaleInvoiceItem> finSaleInvoiceItems = BeanCopyUtils.copyListProperties(
                    request.getItemList(), FinSaleInvoiceItem::new);
            finSaleInvoice.setFinSaleInvoiceItemList(finSaleInvoiceItems);
        }
        //折扣表
        if (request.getDiscountList() != null) {
            List<FinSaleInvoiceDiscount> finSaleInvoiceDiscounts = BeanCopyUtils.copyListProperties(
                    request.getDiscountList(), FinSaleInvoiceDiscount::new);
            finSaleInvoice.setFinSaleInvoiceDiscountList(finSaleInvoiceDiscounts);
        }
        //附件
        finSaleInvoice.setAttachmentList(request.getAttachmentList());
        return finSaleInvoice;
    }

    @ApiOperation(value = "新增折扣明细的查询接口", notes = "新增折扣明细的查询接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinSaleInvoiceDiscountListResponse.class))
    @PostMapping("/bookList")
    public TableDataInfo bookList(@RequestBody FinSaleInvoiceDiscountListRequest request) {
        FinSaleInvoiceDiscount finSaleInvoiceDiscount = new FinSaleInvoiceDiscount();
        BeanUtil.copyProperties(request, finSaleInvoiceDiscount);

        String bookType = request.getBookType();
        //转为折扣返回实体列表
        List<FinSaleInvoiceDiscount> responses = new ArrayList<>();
        TableDataInfo rspData = new TableDataInfo();
        //供应商扣款流水
        if (ConstantsFinance.BOOK_TYPE_CKK.equals(bookType)) {
            FinBookCustomerDeduction book = new FinBookCustomerDeduction();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            book.setHandleStatusNot(HandleStatus.INVALID.getCode());
            //查找供应商扣款报表
            startPage(book);
            List<FinBookCustomerDeduction> bookList = finBookCustomerDeductionService.getReportForm(book);
            rspData = getDataTable(bookList);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinSaleInvoiceDiscount response = new FinSaleInvoiceDiscount();
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
        if (ConstantsFinance.BOOK_TYPE_CTZ.equals(bookType)) {
            FinBookCustomerAccountAdjust book = new FinBookCustomerAccountAdjust();
            BeanUtil.copyProperties(request, book);
            book.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
            book.setHandleStatusNot(HandleStatus.INVALID.getCode());
            //查找供应商调账报表
            startPage(book);
            List<FinBookCustomerAccountAdjust> bookList = finBookCustomerAccountAdjustService.getReportForm(book);
            rspData = getDataTable(bookList);
            //转为折扣返回实体列表
            bookList.forEach(bookItem -> {
                FinSaleInvoiceDiscount response = new FinSaleInvoiceDiscount();
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
        rspData.setRows(BeanCopyUtils.copyListProperties(responses, FinSaleInvoiceDiscountListResponse::new));
        return rspData;
    }

    @ApiOperation(value = "作废发票接口", notes = "作废发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/invalidInvoice")
    public AjaxResult invalidInvoice(Long saleInvoiceSid) {
        return AjaxResult.success(finSaleInvoiceService.invalidInvoice(saleInvoiceSid));
    }

    @ApiOperation(value = "红冲发票接口", notes = "红冲发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/redDashed")
    public AjaxResult redDashed(Long saleInvoiceSid) {
        return AjaxResult.success(finSaleInvoiceService.redDashed(saleInvoiceSid));
    }

    @ApiOperation(value = "签收发票接口", notes = "签收发票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscountListResponse.class))
    @PostMapping("/changeSignFlag")
    public AjaxResult changeSignFlag(@RequestBody List<Long> saleInvoiceSids) {
        return AjaxResult.success(finSaleInvoiceService.changeSignFlag(saleInvoiceSids));
    }

}
