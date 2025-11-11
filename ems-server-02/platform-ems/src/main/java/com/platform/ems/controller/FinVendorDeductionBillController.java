package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinVendorDeductionBillItem;
import com.platform.ems.domain.dto.request.financial.FinVendorDeductionBillInfoRequest;
import com.platform.ems.domain.dto.request.financial.FinVendorDeductionBillListRequest;
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
import com.platform.ems.domain.FinVendorDeductionBill;
import com.platform.ems.service.IFinVendorDeductionBillService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 供应商扣款单Controller
 *
 * @author qhq
 * @date 2021-05-31
 */
@RestController
@RequestMapping("/vendor/deduction/bill")
@Api(tags = "供应商扣款单")
public class FinVendorDeductionBillController extends BaseController {

    @Autowired
    private IFinVendorDeductionBillService finVendorDeductionBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商扣款单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商扣款单列表", notes = "查询供应商扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    public TableDataInfo list(@RequestBody FinVendorDeductionBillListRequest request) {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        //分页查询
        startPage(finVendorDeductionBill);
        List<FinVendorDeductionBill> list = finVendorDeductionBillService.selectFinVendorDeductionBillList(finVendorDeductionBill);
        return getDataTable(list,FinVendorDeductionBillListResponse::new);
    }

    /**
     * 导出供应商扣款单列表
     */
    @ApiOperation(value = "导出供应商扣款单列表", notes = "导出供应商扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorDeductionBillListRequest request) throws IOException {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        //查询
        List<FinVendorDeductionBill> list = finVendorDeductionBillService.selectFinVendorDeductionBillList(finVendorDeductionBill);
        List<FinVendorDeductionBillListResponse> newDiscountList = BeanCopyUtils.copyListProperties(list, FinVendorDeductionBillListResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorDeductionBillListResponse> util = new ExcelUtil<>(FinVendorDeductionBillListResponse.class, dataMap);
        util.exportExcel(response, newDiscountList, "供应商扣款单");
    }

    /**
     * 获取供应商扣款单详细信息
     */
    @ApiOperation(value = "获取供应商扣款单详细信息", notes = "获取供应商扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deductionBillSid) {
        if (deductionBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinVendorDeductionBill finVendorDeductionBill = finVendorDeductionBillService.selectFinVendorDeductionBillById(deductionBillSid);
        FinVendorDeductionBillInfoResponse response = new FinVendorDeductionBillInfoResponse();
        BeanCopyUtils.copyProperties(finVendorDeductionBill, response);
        //子表
        if (finVendorDeductionBill.getItemList() != null) {
            List<FinVendorDeductionBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finVendorDeductionBill.getItemList(), FinVendorDeductionBillItemInfoResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //附件
        response.setAttachmentList(finVendorDeductionBill.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增供应商扣款单
     */
    @ApiOperation(value = "新增供应商扣款单", notes = "新增供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorDeductionBillInfoRequest request) {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorDeductionBillItem::new);
            finVendorDeductionBill.setItemList(itemList);
        }
        //附件
        finVendorDeductionBill.setAttachmentList(request.getAttachmentList());
        int row = finVendorDeductionBillService.insertFinVendorDeductionBill(finVendorDeductionBill);
        if (row > 0) {
            return AjaxResult.success(new FinVendorDeductionBill().setDeductionBillSid(finVendorDeductionBill.getDeductionBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改供应商扣款单
     */
    @ApiOperation(value = "修改供应商扣款单", notes = "修改供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorDeductionBillInfoRequest request) {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorDeductionBillItem::new);
            finVendorDeductionBill.setItemList(itemList);
        }
        //附件
        finVendorDeductionBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorDeductionBillService.updateFinVendorDeductionBill(finVendorDeductionBill));
    }

    /**
     * 变更供应商扣款单
     */
    @ApiOperation(value = "变更供应商扣款单", notes = "变更供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorDeductionBillInfoRequest request) {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorDeductionBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorDeductionBillItem::new);
            finVendorDeductionBill.setItemList(itemList);
        }
        //附件
        finVendorDeductionBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorDeductionBillService.changeFinVendorDeductionBill(finVendorDeductionBill));
    }

    /**
     * 删除供应商扣款单
     */
    @ApiOperation(value = "删除供应商扣款单", notes = "删除供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> deductionBillSids) {
        if (ArrayUtil.isEmpty(deductionBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorDeductionBillService.deleteFinVendorDeductionBillByIds(deductionBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorDeductionBillInfoRequest request) {
        //转换
        FinVendorDeductionBill finVendorDeductionBill = new FinVendorDeductionBill();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        finVendorDeductionBill.setConfirmDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(finVendorDeductionBillService.check(finVendorDeductionBill));
    }

    @ApiOperation(value = "提交", notes = "提交")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody FinVendorDeductionBill request) {
        return toAjax(finVendorDeductionBillService.check(request));
    }

    /**
     * 复制供应商扣款单详细信息
     */
    @ApiOperation(value = "复制供应商扣款单详细信息", notes = "复制供应商扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long deductionBillSid) {
        if (deductionBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinVendorDeductionBill finVendorDeductionBill = finVendorDeductionBillService.selectFinVendorDeductionBillById(deductionBillSid);
        FinVendorDeductionBillInfoResponse response = new FinVendorDeductionBillInfoResponse();
        BeanCopyUtils.copyProperties(finVendorDeductionBill, response);
        response.setDeductionBillSid(null).setDeductionBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        //子表
        if (finVendorDeductionBill.getItemList() != null) {
            List<FinVendorDeductionBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finVendorDeductionBill.getItemList(), FinVendorDeductionBillItemInfoResponse::new);
            itemInfoResponse.forEach(item->{
                item.setDeductionBillItemSid(null).setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null);
            });
            response.setItemList(itemInfoResponse);
        }
        return AjaxResult.success(response);
    }

    @ApiOperation(value = "作废单据接口", notes = "作废单据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    @PostMapping("/invalid")
    public AjaxResult invalid(Long deductionBillSid) {
        return AjaxResult.success(finVendorDeductionBillService.invalid(deductionBillSid));
    }
}
