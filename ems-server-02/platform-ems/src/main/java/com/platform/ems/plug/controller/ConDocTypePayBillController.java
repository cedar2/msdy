package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Log;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDocTypePayBill;
import com.platform.ems.plug.service.IConDocTypePayBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.service.ISysDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_付款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/bill/pay")
@Api(tags = "单据类型_付款单")
public class ConDocTypePayBillController extends BaseController {

    @Autowired
    private IConDocTypePayBillService conDocTypePayBillService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询单据类型_付款单列表
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_付款单列表", notes = "查询单据类型_付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePayBill.class))
    public TableDataInfo list(@RequestBody ConDocTypePayBill conDocTypePayBill) {
        startPage(conDocTypePayBill);
        List<ConDocTypePayBill> list = conDocTypePayBillService.selectConDocTypePayBillList(conDocTypePayBill);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_付款单列表
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:export")
    @ApiOperation(value = "导出单据类型_付款单列表", notes = "导出单据类型_付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypePayBill conDocTypePayBill) throws IOException {
        List<ConDocTypePayBill> list = conDocTypePayBillService.selectConDocTypePayBillList(conDocTypePayBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypePayBill> util = new ExcelUtil<>(ConDocTypePayBill.class, dataMap);
        util.exportExcel(response, list, "单据类型_付款单");
    }

    /**
     * 获取单据类型_付款单详细信息
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:query")
    @ApiOperation(value = "获取单据类型_付款单详细信息", notes = "获取单据类型_付款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePayBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypePayBillService.selectConDocTypePayBillById(sid));
    }

    /**
     * 新增单据类型_付款单
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:add")
    @ApiOperation(value = "新增单据类型_付款单", notes = "新增单据类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypePayBill conDocTypePayBill) {
        return toAjax(conDocTypePayBillService.insertConDocTypePayBill(conDocTypePayBill));
    }

    /**
     * 修改单据类型_付款单
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:edit")
    @ApiOperation(value = "修改单据类型_付款单", notes = "修改单据类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypePayBill conDocTypePayBill) {
        return toAjax(conDocTypePayBillService.updateConDocTypePayBill(conDocTypePayBill));
    }

    /**
     * 变更单据类型_付款单
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:change")
    @ApiOperation(value = "变更单据类型_付款单", notes = "变更单据类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypePayBill conDocTypePayBill) {
        return toAjax(conDocTypePayBillService.changeConDocTypePayBill(conDocTypePayBill));
    }

    /**
     * 删除单据类型_付款单
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:pay:remove")
    @ApiOperation(value = "删除单据类型_付款单", notes = "删除单据类型_付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypePayBillService.deleteConDocTypePayBillByIds(sids));
    }

    @PreAuthorize(hasPermi = "ems:docType:bill:pay:enbleordisable")
    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypePayBill conDocTypePayBill) {
        return AjaxResult.success(conDocTypePayBillService.changeStatus(conDocTypePayBill));
    }

    @PreAuthorize(hasPermi = "ems:docType:bill:pay:check")
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_付款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypePayBill conDocTypePayBill) {
        conDocTypePayBill.setConfirmDate(new Date());
        conDocTypePayBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypePayBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypePayBillService.check(conDocTypePayBill));
    }

    @PostMapping("/getConDocTypePayBillList")
    @ApiOperation(value = "单据类型-付款单下拉列表", notes = "单据类型-付款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePayBill.class))
    public AjaxResult getConDocTypePayBillList() {
        ConDocTypePayBill conDocType = new ConDocTypePayBill();
        conDocType.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conDocTypePayBillService.getConDocTypePayBillList(conDocType));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "单据类型-付款单下拉列表", notes = "单据类型-付款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypePayBill.class))
    public AjaxResult getList(@RequestBody ConDocTypePayBill conDocType) {
        return AjaxResult.success(conDocTypePayBillService.getConDocTypePayBillList(conDocType));
    }
}
