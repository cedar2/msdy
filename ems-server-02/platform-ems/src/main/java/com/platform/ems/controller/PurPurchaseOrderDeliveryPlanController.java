package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.PurPurchaseOrderDeliveryPlan;
import com.platform.ems.service.IPurPurchaseOrderDeliveryPlanService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 系统SID-采购订单明细的交货计划明细Controller
 *
 * @author linhongwei
 * @date 2021-11-11
 */
@RestController
@RequestMapping("/plan/PurchaseOrder/delivery")
@Api(tags = "系统SID-采购订单明细的交货计划明细")
public class PurPurchaseOrderDeliveryPlanController extends BaseController {

    @Autowired
    private IPurPurchaseOrderDeliveryPlanService purPurchaseOrderDeliveryPlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询系统SID-采购订单明细的交货计划明细列表
     */
    @PreAuthorize(hasPermi = "ems:plan:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询系统SID-采购订单明细的交货计划明细列表", notes = "查询系统SID-采购订单明细的交货计划明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderDeliveryPlan.class))
    public TableDataInfo list(@RequestBody PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        startPage(purPurchaseOrderDeliveryPlan);
        List<PurPurchaseOrderDeliveryPlan> list = purPurchaseOrderDeliveryPlanService.selectPurPurchaseOrderDeliveryPlanList(purPurchaseOrderDeliveryPlan);
        return getDataTable(list);
    }

    /**
     * 导出系统SID-采购订单明细的交货计划明细列表
     */
    @PreAuthorize(hasPermi = "ems:plan:export")
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出系统SID-采购订单明细的交货计划明细列表", notes = "导出系统SID-采购订单明细的交货计划明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) throws IOException {
        List<PurPurchaseOrderDeliveryPlan> list = purPurchaseOrderDeliveryPlanService.selectPurPurchaseOrderDeliveryPlanList(purPurchaseOrderDeliveryPlan);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseOrderDeliveryPlan> util = new ExcelUtil<PurPurchaseOrderDeliveryPlan>(PurPurchaseOrderDeliveryPlan.class,dataMap);
        util.exportExcel(response, list, "系统SID-采购订单明细的交货计划明细");
    }


    /**
     * 获取系统SID-采购订单明细的交货计划明细详细信息
     */
    @ApiOperation(value = "获取系统SID-采购订单明细的交货计划明细详细信息", notes = "获取系统SID-采购订单明细的交货计划明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderDeliveryPlan.class))
//    @PreAuthorize(hasPermi = "ems:plan:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseOrderItemSid) {
        if (purchaseOrderItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderDeliveryPlanService.selectPurPurchaseOrderDeliveryPlanById(purchaseOrderItemSid));
    }

    /**
     * 新增系统SID-采购订单明细的交货计划明细
     */
    @ApiOperation(value = "新增/编辑 采购订单明细的交货计划明细", notes = "新增/编辑采购订单明细的交货计划明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:add")
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid List<PurPurchaseOrderDeliveryPlan> purPurchaseOrderDeliveryPlans) {
        return toAjax(purPurchaseOrderDeliveryPlanService.insertPurPurchaseOrderDeliveryPlan(purPurchaseOrderDeliveryPlans));
    }

    /**
     * 修改系统SID-采购订单明细的交货计划明细
     */
    @ApiOperation(value = "修改系统SID-采购订单明细的交货计划明细", notes = "修改系统SID-采购订单明细的交货计划明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plan:edit")
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return toAjax(purPurchaseOrderDeliveryPlanService.updatePurPurchaseOrderDeliveryPlan(purPurchaseOrderDeliveryPlan));
    }

    /**
     * 变更系统SID-采购订单明细的交货计划明细
     */
    @ApiOperation(value = "变更系统SID-采购订单明细的交货计划明细", notes = "变更系统SID-采购订单明细的交货计划明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plan:change")
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return toAjax(purPurchaseOrderDeliveryPlanService.changePurPurchaseOrderDeliveryPlan(purPurchaseOrderDeliveryPlan));
    }

    /**
     * 删除系统SID-采购订单明细的交货计划明细
     */
    @ApiOperation(value = "删除系统SID-采购订单明细的交货计划明细", notes = "删除系统SID-采购订单明细的交货计划明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plan:remove")
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  deliveryPlanSids) {
        if(CollectionUtils.isEmpty( deliveryPlanSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderDeliveryPlanService.deletePurPurchaseOrderDeliveryPlanByIds(deliveryPlanSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:plan:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return AjaxResult.success(purPurchaseOrderDeliveryPlanService.changeStatus(purPurchaseOrderDeliveryPlan));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:plan:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "系统SID-采购订单明细的交货计划明细", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurPurchaseOrderDeliveryPlan purPurchaseOrderDeliveryPlan) {
        return toAjax(purPurchaseOrderDeliveryPlanService.check(purPurchaseOrderDeliveryPlan));
    }

}
