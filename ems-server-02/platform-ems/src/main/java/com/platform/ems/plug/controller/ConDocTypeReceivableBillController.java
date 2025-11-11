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

import com.platform.ems.plug.domain.ConDocTypeReceivableBill;
import com.platform.ems.plug.service.IConDocTypeReceivableBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.system.service.ISysDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_收款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/bill/receivable")
@Api(tags = "单据类型_收款单")
public class ConDocTypeReceivableBillController extends BaseController {

    @Autowired
    private IConDocTypeReceivableBillService conDocTypeReceivableBillService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    /**
     * 查询单据类型_收款单列表
     */
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_收款单列表", notes = "查询单据类型_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeReceivableBill.class))
    public TableDataInfo list(@RequestBody ConDocTypeReceivableBill conDocTypeReceivableBill) {
        startPage(conDocTypeReceivableBill);
        List<ConDocTypeReceivableBill> list = conDocTypeReceivableBillService.selectConDocTypeReceivableBillList(conDocTypeReceivableBill);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_收款单列表
     */
    @ApiOperation(value = "导出单据类型_收款单列表", notes = "导出单据类型_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.EXPORT)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeReceivableBill conDocTypeReceivableBill) throws IOException {
        List<ConDocTypeReceivableBill> list = conDocTypeReceivableBillService.selectConDocTypeReceivableBillList(conDocTypeReceivableBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeReceivableBill> util = new ExcelUtil<>(ConDocTypeReceivableBill.class, dataMap);
        util.exportExcel(response, list, "单据类型_收款单");
    }

    /**
     * 获取单据类型_收款单详细信息
     */
    @ApiOperation(value = "获取单据类型_收款单详细信息", notes = "获取单据类型_收款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeReceivableBill.class))
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeReceivableBillService.selectConDocTypeReceivableBillById(sid));
    }

    /**
     * 新增单据类型_收款单
     */
    @ApiOperation(value = "新增单据类型_收款单", notes = "新增单据类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.INSERT)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:add")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeReceivableBill conDocTypeReceivableBill) {
        return toAjax(conDocTypeReceivableBillService.insertConDocTypeReceivableBill(conDocTypeReceivableBill));
    }

    /**
     * 修改单据类型_收款单
     */
    @ApiOperation(value = "修改单据类型_收款单", notes = "修改单据类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:edit")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeReceivableBill conDocTypeReceivableBill) {
        return toAjax(conDocTypeReceivableBillService.updateConDocTypeReceivableBill(conDocTypeReceivableBill));
    }

    /**
     * 变更单据类型_收款单
     */
    @ApiOperation(value = "变更单据类型_收款单", notes = "变更单据类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.CHANGE)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:change")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeReceivableBill conDocTypeReceivableBill) {
        return toAjax(conDocTypeReceivableBillService.changeConDocTypeReceivableBill(conDocTypeReceivableBill));
    }

    /**
     * 删除单据类型_收款单
     */
    @ApiOperation(value = "删除单据类型_收款单", notes = "删除单据类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.DELETE)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:remove")
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeReceivableBillService.deleteConDocTypeReceivableBillByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeReceivableBill conDocTypeReceivableBill) {
        return AjaxResult.success(conDocTypeReceivableBillService.changeStatus(conDocTypeReceivableBill));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_收款单", businessType = BusinessType.CHECK)
    @PreAuthorize(hasPermi = "ems:docType:bill:receivable:check")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeReceivableBill conDocTypeReceivableBill) {
        conDocTypeReceivableBill.setConfirmDate(new Date());
        conDocTypeReceivableBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeReceivableBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeReceivableBillService.check(conDocTypeReceivableBill));
    }

    @PostMapping("/getConDocTypeReceivableBillList")
    @ApiOperation(value = "单据类型-收款单下拉列表", notes = "单据类型-收款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeReceivableBill.class))
    public AjaxResult getConDocTypeReceivableBillList() {
        ConDocTypeReceivableBill conDocType = new ConDocTypeReceivableBill();
        conDocType.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conDocTypeReceivableBillService.getConDocTypeReceivableBillList(conDocType));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "单据类型-收款单下拉列表", notes = "单据类型-收款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeReceivableBill.class))
    public AjaxResult getList(@RequestBody ConDocTypeReceivableBill conDocType) {
        return AjaxResult.success(conDocTypeReceivableBillService.getConDocTypeReceivableBillList(conDocType));
    }
}
