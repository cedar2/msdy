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
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;
import com.platform.ems.service.ISalSalesOrderDeliveryPlanService;
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
 * 销售订单-发货计划Controller
 *
 * @author linhongwei
 * @date 2021-11-01
 */
@RestController
@RequestMapping("/sale/order/plan")
@Api(tags = "销售订单-发货计划")
public class SalSalesOrderDeliveryPlanController extends BaseController {

    @Autowired
    private ISalSalesOrderDeliveryPlanService salSalesOrderDeliveryPlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售订单-发货计划列表
     */
    @PreAuthorize(hasPermi = "ems:plan:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询销售订单-发货计划列表", notes = "查询销售订单-发货计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderDeliveryPlan.class))
    public TableDataInfo list(@RequestBody SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        startPage(salSalesOrderDeliveryPlan);
        List<SalSalesOrderDeliveryPlan> list = salSalesOrderDeliveryPlanService.selectSalSalesOrderDeliveryPlanList(salSalesOrderDeliveryPlan);
        return getDataTable(list);
    }

    /**
     * 导出销售订单-发货计划列表
     */
    @PreAuthorize(hasPermi = "ems:plan:export")
    @Log(title = "销售订单-发货计划", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售订单-发货计划列表", notes = "导出销售订单-发货计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) throws IOException {
        List<SalSalesOrderDeliveryPlan> list = salSalesOrderDeliveryPlanService.selectSalSalesOrderDeliveryPlanList(salSalesOrderDeliveryPlan);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<SalSalesOrderDeliveryPlan> util = new ExcelUtil<SalSalesOrderDeliveryPlan>(SalSalesOrderDeliveryPlan.class,dataMap);
        util.exportExcel(response, list, "销售订单-发货计划"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取销售订单-发货计划详细信息
     */
    @ApiOperation(value = "获取销售订单-发货计划详细信息", notes = "获取销售订单-发货计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderDeliveryPlan.class))
//    @PreAuthorize(hasPermi = "ems:plan:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long salesOrderItemSid) {
        if (salesOrderItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesOrderDeliveryPlanService.selectSalSalesOrderDeliveryPlanById(salesOrderItemSid));
    }

    /**
     * 新增销售订单-发货计划
     */
    @ApiOperation(value = "新增/编辑 销售订单-发货计划", notes = "新增/编辑 销售订单-发货计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:add")
    @Log(title = "销售订单-发货计划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid List<SalSalesOrderDeliveryPlan> salSalesOrderDeliveryPlans) {
        return toAjax(salSalesOrderDeliveryPlanService.insertSalSalesOrderDeliveryPlan(salSalesOrderDeliveryPlans));
    }

    /**
     * 修改销售订单-发货计划
     */
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:plan:edit")
    @Log(title = "销售订单-发货计划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return toAjax(salSalesOrderDeliveryPlanService.updateSalSalesOrderDeliveryPlan(salSalesOrderDeliveryPlan));
    }

    /**
     * 变更销售订单-发货计划
     */
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plan:change")
    @Log(title = "销售订单-发货计划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return toAjax(salSalesOrderDeliveryPlanService.changeSalSalesOrderDeliveryPlan(salSalesOrderDeliveryPlan));
    }

    /**
     * 删除销售订单-发货计划
     */
    @ApiOperation(value = "删除销售订单-发货计划", notes = "删除销售订单-发货计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:plan:remove")
    @Log(title = "销售订单-发货计划", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  deliveryPlanSids) {
        if(CollectionUtils.isEmpty( deliveryPlanSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderDeliveryPlanService.deleteSalSalesOrderDeliveryPlanByIds(deliveryPlanSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单-发货计划", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:plan:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return AjaxResult.success(salSalesOrderDeliveryPlanService.changeStatus(salSalesOrderDeliveryPlan));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:plan:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售订单-发货计划", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return toAjax(salSalesOrderDeliveryPlanService.check(salSalesOrderDeliveryPlan));
    }

}
