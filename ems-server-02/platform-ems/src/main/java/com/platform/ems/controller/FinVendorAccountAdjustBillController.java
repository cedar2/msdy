package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FinVendorAccountAdjustBill;
import com.platform.ems.domain.FinVendorAccountAdjustBillItem;
import com.platform.ems.domain.FinVendorDeductionBill;
import com.platform.ems.domain.dto.request.financial.FinVendorAccountAdjustBillListRequest;
import com.platform.ems.domain.dto.request.financial.FinVendorAccountAdjustBillInfoRequest;
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
import com.platform.ems.service.IFinVendorAccountAdjustBillService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 供应商调账单Controller
 *
 * @author qhq
 * @date 2021-05-26
 */
@RestController
@RequestMapping("/vendor/adjust/bill")
@Api(tags = "供应商调账单")
public class FinVendorAccountAdjustBillController extends BaseController {
    @Autowired
    private IFinVendorAccountAdjustBillService finVendorAccountAdjustBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商调账单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商调账单列表", notes = "查询供应商调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountAdjustBill.class))
    public TableDataInfo list(@RequestBody FinVendorAccountAdjustBillListRequest request) {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        //分页查询
        startPage(finVendorAccountAdjustBill);
        List<FinVendorAccountAdjustBill> list = finVendorAccountAdjustBillService.selectFinVendorAccountAdjustBillList(finVendorAccountAdjustBill);
        return getDataTable(list,FinVendorAccountAdjustBillListResponse::new);
    }

    /**
     * 导出供应商调账单列表
     */
    @ApiOperation(value = "导出供应商调账单列表", notes = "导出供应商调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorAccountAdjustBillListRequest request) throws IOException {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        //查询
        List<FinVendorAccountAdjustBill> list = finVendorAccountAdjustBillService.selectFinVendorAccountAdjustBillList(finVendorAccountAdjustBill);
        List<FinVendorAccountAdjustBillListResponse> newDiscountList = BeanCopyUtils.copyListProperties(list, FinVendorAccountAdjustBillListResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorAccountAdjustBillListResponse> util = new ExcelUtil<>(FinVendorAccountAdjustBillListResponse.class, dataMap);
        util.exportExcel(response, newDiscountList, "供应商调账单");
    }

    /**
     * 获取供应商调账单详细信息
     */
    @ApiOperation(value = "获取供应商调账单详细信息", notes = "获取供应商调账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorAccountAdjustBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long adjustBillSid) {
        if (adjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = finVendorAccountAdjustBillService.selectFinVendorAccountAdjustBillById(adjustBillSid);
        FinVendorAccountAdjustBillInfoResponse response = new FinVendorAccountAdjustBillInfoResponse();
        BeanCopyUtils.copyProperties(finVendorAccountAdjustBill, response);
        //子表
        if (finVendorAccountAdjustBill.getItemList() != null) {
            List<FinVendorAccountAdjustBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finVendorAccountAdjustBill.getItemList(), FinVendorAccountAdjustBillItemInfoResponse::new);
            response.setItemList(itemInfoResponse);
        }
        //附件
        response.setAttachmentList(finVendorAccountAdjustBill.getAttachmentList());
        return AjaxResult.success(response);
    }

    /**
     * 新增供应商调账单
     */
    @ApiOperation(value = "新增供应商调账单", notes = "新增供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorAccountAdjustBillInfoRequest request) {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorAccountAdjustBillItem::new);
            finVendorAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finVendorAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountAdjustBillService.insertFinVendorAccountAdjustBill(finVendorAccountAdjustBill));
    }

    /**
     * 修改供应商调账单
     */
    @ApiOperation(value = "修改供应商调账单", notes = "修改供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorAccountAdjustBillInfoRequest request) {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorAccountAdjustBillItem::new);
            finVendorAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finVendorAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountAdjustBillService.updateFinVendorAccountAdjustBill(finVendorAccountAdjustBill));
    }

    /**
     * 变更供应商调账单
     */
    @ApiOperation(value = "变更供应商调账单", notes = "变更供应商调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorAccountAdjustBillInfoRequest request) {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        //子表
        if (request.getItemList() != null) {
            List<FinVendorAccountAdjustBillItem> itemList = BeanCopyUtils.copyListProperties(request.getItemList(), FinVendorAccountAdjustBillItem::new);
            finVendorAccountAdjustBill.setItemList(itemList);
        }
        //附件
        finVendorAccountAdjustBill.setAttachmentList(request.getAttachmentList());
        return toAjax(finVendorAccountAdjustBillService.changeFinVendorAccountAdjustBill(finVendorAccountAdjustBill));
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
        return toAjax(finVendorAccountAdjustBillService.deleteFinVendorAccountAdjustBillByIds(adjustBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorAccountAdjustBillInfoRequest request) {
        //转换
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = new FinVendorAccountAdjustBill();
        BeanCopyUtils.copyProperties(request, finVendorAccountAdjustBill);
        finVendorAccountAdjustBill.setConfirmDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(finVendorAccountAdjustBillService.check(finVendorAccountAdjustBill));
    }

    /**
     * 复制供应商调账单详细信息
     */
    @ApiOperation(value = "复制供应商调账单详细信息", notes = "复制供应商调账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorDeductionBill.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(Long adjustBillSid) {
        if (adjustBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        //主表
        FinVendorAccountAdjustBill finVendorAccountAdjustBill = finVendorAccountAdjustBillService.selectFinVendorAccountAdjustBillById(adjustBillSid);
        FinVendorAccountAdjustBillInfoResponse response = new FinVendorAccountAdjustBillInfoResponse();
        BeanCopyUtils.copyProperties(finVendorAccountAdjustBill, response);
        response.setAdjustBillSid(null).setAdjustBillCode(null).setDocumentDate(null).setCreatorAccount(null).setRemark(null);
        response.setHandleStatus(null);
        //子表
        if (finVendorAccountAdjustBill.getItemList() != null) {
            List<FinVendorAccountAdjustBillItemInfoResponse> itemInfoResponse = BeanCopyUtils.copyListProperties(finVendorAccountAdjustBill.getItemList(), FinVendorAccountAdjustBillItemInfoResponse::new);
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
        return AjaxResult.success(finVendorAccountAdjustBillService.invalid(adjustBillSid));
    }

}
