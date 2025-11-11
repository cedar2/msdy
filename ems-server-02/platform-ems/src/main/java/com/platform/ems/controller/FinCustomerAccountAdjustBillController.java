package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinCustomerAccountAdjustBill;
import com.platform.ems.domain.FinCustomerAccountAdjustBillItem;
import com.platform.ems.domain.FinVendorDeductionBill;
import com.platform.ems.domain.dto.request.financial.FinCustomerAccountAdjustBillListRequest;
import com.platform.ems.domain.dto.request.financial.FinCustomerAccountAdjustBillInfoRequest;
import com.platform.ems.domain.dto.response.financial.*;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.service.IFinCustomerAccountAdjustBillService;
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
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 客户调账单Controller
 *
 * @author qhq
 * @date 2021-05-26
 */
@RestController
@RequestMapping("/customer/adjust/bill")
@Api(tags = "客户调账单")
public class FinCustomerAccountAdjustBillController extends BaseController {
    @Autowired
    private IFinCustomerAccountAdjustBillService finCustomerAccountAdjustBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商调账单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商调账单列表", notes = "查询供应商调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountAdjustBill.class))
    public TableDataInfo list(@RequestBody FinCustomerAccountAdjustBillListRequest request) {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        //分页查询
        startPage(finCustomerAccountAdjustBill);
        List<FinCustomerAccountAdjustBill> list = finCustomerAccountAdjustBillService.selectFinCustomerAccountAdjustBillList(finCustomerAccountAdjustBill);
        return getDataTable(list,FinCustomerAccountAdjustBillListResponse::new);
    }

    /**
     * 导出供应商调账单列表
     */
    @ApiOperation(value = "导出供应商调账单列表", notes = "导出供应商调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerAccountAdjustBillListRequest request) throws IOException {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        //查询
        List<FinCustomerAccountAdjustBill> list = finCustomerAccountAdjustBillService.selectFinCustomerAccountAdjustBillList(finCustomerAccountAdjustBill);
        List<FinCustomerAccountAdjustBillListResponse> newDiscountList = BeanCopyUtils.copyListProperties(list, FinCustomerAccountAdjustBillListResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerAccountAdjustBillListResponse> util = new ExcelUtil<>(FinCustomerAccountAdjustBillListResponse.class, dataMap);
        util.exportExcel(response, newDiscountList, "供应商调账单");
    }

    /**
     * 获取供应商调账单详细信息
     */
    @ApiOperation(value = "获取供应商调账单详细信息", notes = "获取供应商调账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerAccountAdjustBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long adjustBillSid) {
        if (adjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = finCustomerAccountAdjustBillService.selectFinCustomerAccountAdjustBillById(adjustBillSid);
        FinCustomerAccountAdjustBillInfoResponse response = new FinCustomerAccountAdjustBillInfoResponse();
        BeanCopyUtils.copyProperties(finCustomerAccountAdjustBill, response);
        //子表
        if (finCustomerAccountAdjustBill.getItemList() != null) {
            List<FinCustomerAccountAdjustBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finCustomerAccountAdjustBill.getItemList(), FinCustomerAccountAdjustBillItemInfoResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //附件
        response.setAttachmentList(finCustomerAccountAdjustBill.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增供应商调账单
     */
    @ApiOperation(value = "新增供应商调账单", notes = "新增供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerAccountAdjustBillInfoRequest request) {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerAccountAdjustBillItem::new);
            finCustomerAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finCustomerAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountAdjustBillService.insertFinCustomerAccountAdjustBill(finCustomerAccountAdjustBill));
    }

    /**
     * 修改供应商调账单
     */
    @ApiOperation(value = "修改供应商调账单", notes = "修改供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerAccountAdjustBillInfoRequest request) {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerAccountAdjustBillItem::new);
            finCustomerAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finCustomerAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountAdjustBillService.updateFinCustomerAccountAdjustBill(finCustomerAccountAdjustBill));
    }

    /**
     * 变更供应商调账单
     */
    @ApiOperation(value = "变更供应商调账单", notes = "变更供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerAccountAdjustBillInfoRequest request) {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinCustomerAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinCustomerAccountAdjustBillItem::new);
            finCustomerAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finCustomerAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finCustomerAccountAdjustBillService.changeFinCustomerAccountAdjustBill(finCustomerAccountAdjustBill));
    }

    /**
     * 删除供应商调账单
     */
    @ApiOperation(value = "删除供应商调账单", notes = "删除供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> adjustBillSids) {
        if (ArrayUtil.isEmpty(adjustBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerAccountAdjustBillService.deleteFinCustomerAccountAdjustBillByIds(adjustBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerAccountAdjustBillInfoRequest request) {
        //转换
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = new FinCustomerAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finCustomerAccountAdjustBill);
        finCustomerAccountAdjustBill.setConfirmDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(finCustomerAccountAdjustBillService.check(finCustomerAccountAdjustBill));
    }

    /**
     * 复制客户调账单详细信息
     */
    @ApiOperation(value = "复制客户调账单详细信息", notes = "复制客户调账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long adjustBillSid) {
        if (adjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinCustomerAccountAdjustBill finCustomerAccountAdjustBill = finCustomerAccountAdjustBillService.selectFinCustomerAccountAdjustBillById(adjustBillSid);
        FinCustomerAccountAdjustBillInfoResponse response = new FinCustomerAccountAdjustBillInfoResponse();
        BeanCopyUtils.copyProperties(finCustomerAccountAdjustBill, response);
        response.setAdjustBillSid(null).setAdjustBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        //子表
        if (finCustomerAccountAdjustBill.getItemList() != null) {
            List<FinCustomerAccountAdjustBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finCustomerAccountAdjustBill.getItemList(), FinCustomerAccountAdjustBillItemInfoResponse::new);
            itemInfoResponse.forEach(item->{
                item.setAdjustBillItemSid(null).setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null);
            });
            response.setItemList(itemInfoResponse);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long adjustBillSid) {
        return AjaxResult.success(finCustomerAccountAdjustBillService.invalid(adjustBillSid));
    }
}
