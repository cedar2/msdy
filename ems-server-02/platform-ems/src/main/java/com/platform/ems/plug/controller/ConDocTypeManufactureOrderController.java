package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

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
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDocTypeManufactureOrder;
import com.platform.ems.plug.service.IConDocTypeManufactureOrderService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_生产订单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/manufacture/order")
@Api(tags = "单据类型_生产订单")
public class ConDocTypeManufactureOrderController extends BaseController {

    @Autowired
    private IConDocTypeManufactureOrderService conDocTypeManufactureOrderService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询单据类型_生产订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_生产订单列表", notes = "查询单据类型_生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOrder.class))
    public TableDataInfo list(@RequestBody ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        startPage(conDocTypeManufactureOrder);
        List<ConDocTypeManufactureOrder> list = conDocTypeManufactureOrderService.selectConDocTypeManufactureOrderList(conDocTypeManufactureOrder);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_生产订单列表
     */
    @Log(title = "单据类型_生产订单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_生产订单列表", notes = "导出单据类型_生产订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeManufactureOrder conDocTypeManufactureOrder) throws IOException {
        List<ConDocTypeManufactureOrder> list = conDocTypeManufactureOrderService.selectConDocTypeManufactureOrderList(conDocTypeManufactureOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeManufactureOrder> util = new ExcelUtil<>(ConDocTypeManufactureOrder.class, dataMap);
        util.exportExcel(response, list, "单据类型_生产订单");
    }

    @ApiOperation(value = "下载单据类型_生产订单导入模板", notes = "下载单据类型_生产订单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeManufactureOrder> util = new ExcelUtil<>(ConDocTypeManufactureOrder.class);
        util.importTemplateExcel(response, "单据类型_生产订单导入模板");
    }

    /**
     * 获取单据类型_生产订单详细信息
     */
    @ApiOperation(value = "获取单据类型_生产订单详细信息", notes = "获取单据类型_生产订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeManufactureOrderService.selectConDocTypeManufactureOrderById(sid));
    }

    /**
     * 新增单据类型_生产订单
     */
    @ApiOperation(value = "新增单据类型_生产订单", notes = "新增单据类型_生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        return toAjax(conDocTypeManufactureOrderService.insertConDocTypeManufactureOrder(conDocTypeManufactureOrder));
    }

    /**
     * 修改单据类型_生产订单
     */
    @ApiOperation(value = "修改单据类型_生产订单", notes = "修改单据类型_生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        return toAjax(conDocTypeManufactureOrderService.updateConDocTypeManufactureOrder(conDocTypeManufactureOrder));
    }

    /**
     * 变更单据类型_生产订单
     */
    @ApiOperation(value = "变更单据类型_生产订单", notes = "变更单据类型_生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        return toAjax(conDocTypeManufactureOrderService.changeConDocTypeManufactureOrder(conDocTypeManufactureOrder));
    }

    /**
     * 删除单据类型_生产订单
     */
    @ApiOperation(value = "删除单据类型_生产订单", notes = "删除单据类型_生产订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeManufactureOrderService.deleteConDocTypeManufactureOrderByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        return AjaxResult.success(conDocTypeManufactureOrderService.changeStatus(conDocTypeManufactureOrder));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_生产订单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        conDocTypeManufactureOrder.setConfirmDate(new Date());
        conDocTypeManufactureOrder.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeManufactureOrder.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeManufactureOrderService.check(conDocTypeManufactureOrder));
    }

    /**
     * 单据类型_生产订单下拉框接口
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_生产订单下拉框接口", notes = "单据类型_生产订单下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeManufactureOrder.class))
    public AjaxResult getList() {
        return AjaxResult.success(conDocTypeManufactureOrderService.getList());
    }
}
