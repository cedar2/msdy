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

import com.platform.ems.plug.domain.ConBuTypeCustomerAccountAdjust;
import com.platform.ems.plug.service.IConBuTypeCustomerAccountAdjustService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;

import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_客户调账单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/adjust/customer")
@Api(tags = "业务类型_客户调账单")
public class ConBuTypeCustomerAccountAdjustController extends BaseController {

    @Autowired
    private IConBuTypeCustomerAccountAdjustService conBuTypeCustomerAccountAdjustService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_客户调账单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_客户调账单列表", notes = "查询业务类型_客户调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerAccountAdjust.class))
    public TableDataInfo list(@RequestBody ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        startPage(conBuTypeCustomerAccountAdjust);
        List<ConBuTypeCustomerAccountAdjust> list = conBuTypeCustomerAccountAdjustService.selectConBuTypeCustomerAccountAdjustList(conBuTypeCustomerAccountAdjust);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_客户调账单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:export")
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_客户调账单列表", notes = "导出业务类型_客户调账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) throws IOException {
        List<ConBuTypeCustomerAccountAdjust> list = conBuTypeCustomerAccountAdjustService.selectConBuTypeCustomerAccountAdjustList(conBuTypeCustomerAccountAdjust);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeCustomerAccountAdjust> util = new ExcelUtil<>(ConBuTypeCustomerAccountAdjust.class, dataMap);
        util.exportExcel(response, list, "业务类型_客户调账单");
    }

    /**
     * 获取业务类型_客户调账单详细信息
     */
    @ApiOperation(value = "获取业务类型_客户调账单详细信息", notes = "获取业务类型_客户调账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerAccountAdjust.class))
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeCustomerAccountAdjustService.selectConBuTypeCustomerAccountAdjustById(sid));
    }

    /**
     * 新增业务类型_客户调账单
     */
    @ApiOperation(value = "新增业务类型_客户调账单", notes = "新增业务类型_客户调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:add")
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        return toAjax(conBuTypeCustomerAccountAdjustService.insertConBuTypeCustomerAccountAdjust(conBuTypeCustomerAccountAdjust));
    }

    /**
     * 修改业务类型_客户调账单
     */
    @ApiOperation(value = "修改业务类型_客户调账单", notes = "修改业务类型_客户调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:edit")
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        return toAjax(conBuTypeCustomerAccountAdjustService.updateConBuTypeCustomerAccountAdjust(conBuTypeCustomerAccountAdjust));
    }

    /**
     * 变更业务类型_客户调账单
     */
    @ApiOperation(value = "变更业务类型_客户调账单", notes = "变更业务类型_客户调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:change")
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        return toAjax(conBuTypeCustomerAccountAdjustService.changeConBuTypeCustomerAccountAdjust(conBuTypeCustomerAccountAdjust));
    }

    /**
     * 删除业务类型_客户调账单
     */
    @ApiOperation(value = "删除业务类型_客户调账单", notes = "删除业务类型_客户调账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:remove")
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeCustomerAccountAdjustService.deleteConBuTypeCustomerAccountAdjustByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        return AjaxResult.success(conBuTypeCustomerAccountAdjustService.changeStatus(conBuTypeCustomerAccountAdjust));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bu:type:customer:account:adjust:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_客户调账单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        conBuTypeCustomerAccountAdjust.setConfirmDate(new Date());
        conBuTypeCustomerAccountAdjust.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeCustomerAccountAdjust.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeCustomerAccountAdjustService.check(conBuTypeCustomerAccountAdjust));
    }

}
