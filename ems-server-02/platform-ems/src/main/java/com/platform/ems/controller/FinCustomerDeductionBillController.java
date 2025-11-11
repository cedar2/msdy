package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinCustomerDeductionBillItem;
import com.platform.ems.domain.FinVendorDeductionBill;
import com.platform.ems.domain.dto.request.financial.FinCustomerDeductionBillInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinCustomerDeductionBillListRequest;
import com.platform.ems.domain.dto.response.financial.*;
import com.platform.common.utils.bean.BeanCopyUtils;
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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.FinCustomerDeductionBill;
import com.platform.ems.service.IFinCustomerDeductionBillService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 客户扣款单Controller
 *
 * @author qhq
 * @date 2021-06-08
 */
@RestController
@RequestMapping("/customer/deduction/bill")
@Api(tags = "客户扣款单")
public class FinCustomerDeductionBillController extends BaseController {

    @Autowired
    private IFinCustomerDeductionBillService finCustomerDeductionBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户扣款单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户扣款单列表", notes = "查询客户扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBill.class))
    public TableDataInfo list(@RequestBody FinCustomerDeductionBillListRequest request) {
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        startPage(finCustomerDeductionBill);
        List<FinCustomerDeductionBill> list = finCustomerDeductionBillService.selectFinCustomerDeductionBillList(finCustomerDeductionBill);
        return getDataTable(list, FinCustomerDeductionBillListResponse::new);
    }

    /**
     * 导出客户扣款单列表
     */
    @ApiOperation(value = "导出客户扣款单列表", notes = "导出客户扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerDeductionBillListRequest request) throws IOException {
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        List<FinCustomerDeductionBill> list = finCustomerDeductionBillService.selectFinCustomerDeductionBillList(finCustomerDeductionBill);
        List<FinCustomerDeductionBillListResponse> newDiscountList = BeanCopyUtils.copyListProperties(list, FinCustomerDeductionBillListResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerDeductionBillListResponse> util = new ExcelUtil<>(FinCustomerDeductionBillListResponse.class, dataMap);
        util.exportExcel(response, newDiscountList, "客户扣款单");
    }


    /**
     * 获取客户扣款单详细信息
     */
    @ApiOperation(value = "获取客户扣款单详细信息", notes = "获取客户扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deductionBillSid) {
        if (deductionBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinCustomerDeductionBill finCustomerDeductionBill = finCustomerDeductionBillService.selectFinCustomerDeductionBillById(deductionBillSid);
        FinCustomerDeductionBillInfoResponse response = new FinCustomerDeductionBillInfoResponse();
        BeanCopyUtils.copyProperties(finCustomerDeductionBill, response);
        //子表
        if (finCustomerDeductionBill.getItemList() != null) {
            List<FinCustomerDeductionBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finCustomerDeductionBill.getItemList(), FinCustomerDeductionBillItemInfoResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //附件
        response.setAttachmentList(finCustomerDeductionBill.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增客户扣款单
     */
    @ApiOperation(value = "新增客户扣款单", notes = "新增客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerDeductionBillInfoRequest request) {
        //转换
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerDeductionBillItem::new);
            finCustomerDeductionBill.setItemList(itemList);
        }
        //附件
        finCustomerDeductionBill.setAttachmentList(request.getAttachmentList());
        int row = finCustomerDeductionBillService.insertFinCustomerDeductionBill(finCustomerDeductionBill);
        if (row > 0) {
            return AjaxResult.success(new FinVendorDeductionBill().setDeductionBillSid(finCustomerDeductionBill.getDeductionBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改客户扣款单
     */
    @ApiOperation(value = "修改客户扣款单", notes = "修改客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerDeductionBillInfoRequest request) {
        //转换
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerDeductionBillItem::new);
            finCustomerDeductionBill.setItemList(itemList);
        }
        //附件
        finCustomerDeductionBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerDeductionBillService.updateFinCustomerDeductionBill(finCustomerDeductionBill));
    }

    /**
     * 变更客户扣款单
     */
    @ApiOperation(value = "变更客户扣款单", notes = "变更客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerDeductionBillInfoRequest request) {
        //转换
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerDeductionBillItem::new);
            finCustomerDeductionBill.setItemList(itemList);
        }
        //附件
        finCustomerDeductionBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerDeductionBillService.changeFinCustomerDeductionBill(finCustomerDeductionBill));
    }

    /**
     * 删除客户扣款单
     */
    @ApiOperation(value = "删除客户扣款单", notes = "删除客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> deductionBillSids) {
        if (ArrayUtil.isEmpty(deductionBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerDeductionBillService.deleteFinCustomerDeductionBillByIds(deductionBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerDeductionBillInfoRequest request) {
        //转换
        FinCustomerDeductionBill finCustomerDeductionBill = new FinCustomerDeductionBill();
        BeanCopyUtils.copyProperties(request, finCustomerDeductionBill);
        finCustomerDeductionBill.setConfirmDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(finCustomerDeductionBillService.check(finCustomerDeductionBill));
    }

    @ApiOperation(value = "提交", notes = "提交")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody FinCustomerDeductionBill request) {
        return toAjax(finCustomerDeductionBillService.check(request));
    }

    /**
     * 复制客户扣款单详细信息
     */
    @ApiOperation(value = "复制客户扣款单详细信息", notes = "复制客户扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long deductionBillSid) {
        if (deductionBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinCustomerDeductionBill finCustomerDeductionBill = finCustomerDeductionBillService.selectFinCustomerDeductionBillById(deductionBillSid);
        FinCustomerDeductionBillInfoResponse response = new FinCustomerDeductionBillInfoResponse();
        BeanCopyUtils.copyProperties(finCustomerDeductionBill, response);
        response.setDeductionBillSid(null).setDeductionBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        //子表
        if (finCustomerDeductionBill.getItemList() != null) {
            List<FinCustomerDeductionBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finCustomerDeductionBill.getItemList(), FinCustomerDeductionBillItemInfoResponse::new);
            itemInfoResponse.forEach(item->{
                item.setDeductionBillItemSid(null).setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null);
            });
            response.setItemList(itemInfoResponse);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerDeductionBill.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long deductionBillSid) {
        return AjaxResult.success(finCustomerDeductionBillService.invalid(deductionBillSid));
    }
}
