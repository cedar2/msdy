package com.platform.ems.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.domain.PurOutsourcePurchaseOrderItem;
import com.platform.ems.domain.PurOutsourcePurchasePrice;
import com.platform.ems.service.IPurOutsourcePurchaseOrderItemService;
import com.platform.ems.service.IPurOutsourcePurchasePriceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.domain.PurOutsourcePurchaseOrder;
import com.platform.ems.service.IPurOutsourcePurchaseOrderService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 外发加工单Controller
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@RestController
@RequestMapping("/outsource/purchase/order")
@Api(tags = "外发加工单")
public class PurOutsourcePurchaseOrderController extends BaseController {

    @Autowired
    private IPurOutsourcePurchaseOrderService purOutsourcePurchaseOrderService;
    @Autowired
    private IPurOutsourcePurchaseOrderItemService purOutsourcePurchaseOrderItemService;
    @Autowired
    private IPurOutsourcePurchasePriceService purOutsourcePurchasePriceService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询外发加工单列表
     */
//    @PreAuthorize(hasPermi = "ems:order:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询外发加工单列表", notes = "查询外发加工单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchaseOrder.class))
    public TableDataInfo list(@RequestBody PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        startPage(purOutsourcePurchaseOrder);
        List<PurOutsourcePurchaseOrder> list = purOutsourcePurchaseOrderService.selectPurOutsourcePurchaseOrderList(purOutsourcePurchaseOrder);
        return getDataTable(list);
    }

    /**
     * 导出外发加工单列表
     */
//    @PreAuthorize(hasPermi = "ems:order:export")
    @Log(title = "外发加工单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工单列表", notes = "导出外发加工单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) throws IOException {
        List<PurOutsourcePurchaseOrder> list = purOutsourcePurchaseOrderService.selectPurOutsourcePurchaseOrderList(purOutsourcePurchaseOrder);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourcePurchaseOrder> util = new ExcelUtil<PurOutsourcePurchaseOrder>(PurOutsourcePurchaseOrder.class,dataMap);
        util.exportExcel(response, list, "外发加工单");
    }



    /**
     * 获取外发加工单详细信息
     */
    @ApiOperation(value = "获取外发加工单详细信息", notes = "获取外发加工单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchaseOrder.class))
//    @PreAuthorize(hasPermi = "ems:order:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long outsourcePurchaseOrderSid) {
        if (outsourcePurchaseOrderSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purOutsourcePurchaseOrderService.selectPurOutsourcePurchaseOrderById(outsourcePurchaseOrderSid));
    }

    /**
     * 新增外发加工单
     */
    @ApiOperation(value = "新增外发加工单", notes = "新增外发加工单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:order:add")
    @Log(title = "外发加工单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        return toAjax(purOutsourcePurchaseOrderService.insertPurOutsourcePurchaseOrder(purOutsourcePurchaseOrder));
    }

    /**
     * 修改外发加工单
     */
    @ApiOperation(value = "修改外发加工单", notes = "修改外发加工单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:order:edit")
    @Log(title = "外发加工单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        return toAjax(purOutsourcePurchaseOrderService.updatePurOutsourcePurchaseOrder(purOutsourcePurchaseOrder));
    }

    /**
     * 变更外发加工单
     */
    @ApiOperation(value = "变更外发加工单", notes = "变更外发加工单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:order:change")
    @Log(title = "外发加工单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        return toAjax(purOutsourcePurchaseOrderService.changePurOutsourcePurchaseOrder(purOutsourcePurchaseOrder));
    }

    /**
     * 删除外发加工单
     */
    @ApiOperation(value = "删除外发加工单", notes = "删除外发加工单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:order:remove")
    @Log(title = "外发加工单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> outsourcePurchaseOrderSids) {
        if (CollectionUtils.isEmpty( outsourcePurchaseOrderSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purOutsourcePurchaseOrderService.deletePurOutsourcePurchaseOrderByIds(outsourcePurchaseOrderSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:order:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        purOutsourcePurchaseOrder.setConfirmDate(new Date());
        purOutsourcePurchaseOrder.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        purOutsourcePurchaseOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(purOutsourcePurchaseOrderService.check(purOutsourcePurchaseOrder));
    }

    /**
     * 外发加工单明细报表
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "外发加工单明细报表", notes = "外发加工单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchaseOrderItem.class))
    public TableDataInfo getItemList(@RequestBody PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        startPage(purOutsourcePurchaseOrderItem);
        List<PurOutsourcePurchaseOrderItem> list = purOutsourcePurchaseOrderItemService.getItemList(purOutsourcePurchaseOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出外发加工单明细报表
     */
//    @PreAuthorize(hasPermi = "ems:order:export")
    @Log(title = "外发加工单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工单明细报表", notes = "导出外发加工单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) throws IOException {
        List<PurOutsourcePurchaseOrderItem> list = purOutsourcePurchaseOrderItemService.getItemList(purOutsourcePurchaseOrderItem);
        Map<String,Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOutsourcePurchaseOrderItem> util = new ExcelUtil<>(PurOutsourcePurchaseOrderItem.class,dataMap);
        util.exportExcel(response, list, "外发加工单明细报表"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 获取加工采购价
     */
    @ApiOperation(value = "获取加工采购价", notes = "获取加工采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurOutsourcePurchasePrice.class))
    @PostMapping("/getPurchasePrice")
    public AjaxResult getPurchasePrice(@RequestBody PurOutsourcePurchasePrice purOutsourcePurchasePrice) {

        return AjaxResult.success(purOutsourcePurchasePriceService.getPurchasePrice(purOutsourcePurchasePrice));
    }
}
