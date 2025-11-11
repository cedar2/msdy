package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import com.platform.ems.plug.service.IConBuTypeSalesOrderService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;

import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_销售订单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/sales/order")
@Api(tags = "业务类型_销售订单")
public class ConBuTypeSalesOrderController extends BaseController {

    @Autowired
    private IConBuTypeSalesOrderService conBuTypeSalesOrderService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_销售订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_销售订单列表", notes = "查询业务类型_销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeSalesOrder.class))
    public TableDataInfo list(@RequestBody ConBuTypeSalesOrder conBuTypeSalesOrder) {
        startPage(conBuTypeSalesOrder);
        List<ConBuTypeSalesOrder> list = conBuTypeSalesOrderService.selectConBuTypeSalesOrderList(conBuTypeSalesOrder);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_销售订单列表
     */
    @Log(title = "业务类型_销售订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_销售订单列表", notes = "导出业务类型_销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeSalesOrder conBuTypeSalesOrder) throws IOException {
        List<ConBuTypeSalesOrder> list = conBuTypeSalesOrderService.selectConBuTypeSalesOrderList(conBuTypeSalesOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeSalesOrder> util = new ExcelUtil<>(ConBuTypeSalesOrder.class, dataMap);
        util.exportExcel(response, list, "业务类型_销售订单");
    }

    /**
     * 获取业务类型_销售订单详细信息
     */
    @ApiOperation(value = "获取业务类型_销售订单详细信息", notes = "获取业务类型_销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeSalesOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeSalesOrderService.selectConBuTypeSalesOrderById(sid));
    }

    /**
     * 新增业务类型_销售订单
     */
    @ApiOperation(value = "新增业务类型_销售订单", notes = "新增业务类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeSalesOrder conBuTypeSalesOrder) {
        return toAjax(conBuTypeSalesOrderService.insertConBuTypeSalesOrder(conBuTypeSalesOrder));
    }

    /**
     * 修改业务类型_销售订单
     */
    @ApiOperation(value = "修改业务类型_销售订单", notes = "修改业务类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeSalesOrder conBuTypeSalesOrder) {
        return toAjax(conBuTypeSalesOrderService.updateConBuTypeSalesOrder(conBuTypeSalesOrder));
    }

    /**
     * 变更业务类型_销售订单
     */
    @ApiOperation(value = "变更业务类型_销售订单", notes = "变更业务类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeSalesOrder conBuTypeSalesOrder) {
        return toAjax(conBuTypeSalesOrderService.changeConBuTypeSalesOrder(conBuTypeSalesOrder));
    }

    /**
     * 删除业务类型_销售订单
     */
    @ApiOperation(value = "删除业务类型_销售订单", notes = "删除业务类型_销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeSalesOrderService.deleteConBuTypeSalesOrderByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeSalesOrder conBuTypeSalesOrder) {
        return AjaxResult.success(conBuTypeSalesOrderService.changeStatus(conBuTypeSalesOrder));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_销售订单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeSalesOrder conBuTypeSalesOrder) {
        conBuTypeSalesOrder.setConfirmDate(new Date());
        conBuTypeSalesOrder.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeSalesOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeSalesOrderService.check(conBuTypeSalesOrder));
    }

    @PostMapping("/getConBuTypeSalesOrderList")
    @ApiOperation(value = "业务类型-销售订单下拉列表", notes = "业务类型-销售订单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeSalesOrder.class))
    public AjaxResult getConBuTypeSalesOrderList() {
        return AjaxResult.success(conBuTypeSalesOrderService.getConBuTypeSalesOrderList());
    }
}
