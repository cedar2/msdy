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

import com.platform.ems.plug.domain.ConBuTypeReceivableBill;
import com.platform.ems.plug.service.IConBuTypeReceivableBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;

import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_收款单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/bill/receivable")
@Api(tags = "业务类型_收款单")
public class ConBuTypeReceivableBillController extends BaseController {

    @Autowired
    private IConBuTypeReceivableBillService conBuTypeReceivableBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_收款单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_收款单列表", notes = "查询业务类型_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeReceivableBill.class))
    public TableDataInfo list(@RequestBody ConBuTypeReceivableBill conBuTypeReceivableBill) {
        startPage(conBuTypeReceivableBill);
        List<ConBuTypeReceivableBill> list = conBuTypeReceivableBillService.selectConBuTypeReceivableBillList(conBuTypeReceivableBill);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_收款单列表
     */
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:export")
    @Log(title = "业务类型_收款单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_收款单列表", notes = "导出业务类型_收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeReceivableBill conBuTypeReceivableBill) throws IOException {
        List<ConBuTypeReceivableBill> list = conBuTypeReceivableBillService.selectConBuTypeReceivableBillList(conBuTypeReceivableBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeReceivableBill> util = new ExcelUtil<>(ConBuTypeReceivableBill.class, dataMap);
        util.exportExcel(response, list, "业务类型_收款单");
    }

    /**
     * 获取业务类型_收款单详细信息
     */
    @ApiOperation(value = "获取业务类型_收款单详细信息", notes = "获取业务类型_收款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeReceivableBill.class))
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeReceivableBillService.selectConBuTypeReceivableBillById(sid));
    }

    /**
     * 新增业务类型_收款单
     */
    @ApiOperation(value = "新增业务类型_收款单", notes = "新增业务类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:add")
    @Log(title = "业务类型_收款单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeReceivableBill conBuTypeReceivableBill) {
        return toAjax(conBuTypeReceivableBillService.insertConBuTypeReceivableBill(conBuTypeReceivableBill));
    }

    /**
     * 修改业务类型_收款单
     */
    @ApiOperation(value = "修改业务类型_收款单", notes = "修改业务类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:edit")
    @Log(title = "业务类型_收款单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeReceivableBill conBuTypeReceivableBill) {
        return toAjax(conBuTypeReceivableBillService.updateConBuTypeReceivableBill(conBuTypeReceivableBill));
    }

    /**
     * 变更业务类型_收款单
     */
    @ApiOperation(value = "变更业务类型_收款单", notes = "变更业务类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:change")
    @Log(title = "业务类型_收款单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeReceivableBill conBuTypeReceivableBill) {
        return toAjax(conBuTypeReceivableBillService.changeConBuTypeReceivableBill(conBuTypeReceivableBill));
    }

    /**
     * 删除业务类型_收款单
     */
    @ApiOperation(value = "删除业务类型_收款单", notes = "删除业务类型_收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:remove")
    @Log(title = "业务类型_收款单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeReceivableBillService.deleteConBuTypeReceivableBillByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_收款单", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeReceivableBill conBuTypeReceivableBill) {
        return AjaxResult.success(conBuTypeReceivableBillService.changeStatus(conBuTypeReceivableBill));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:bu:type:receivable:bill:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_收款单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeReceivableBill conBuTypeReceivableBill) {
        conBuTypeReceivableBill.setConfirmDate(new Date());
        conBuTypeReceivableBill.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeReceivableBill.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeReceivableBillService.check(conBuTypeReceivableBill));
    }

    @PostMapping("/getConBuTypeReceivableBillList")
    @ApiOperation(value = "业务类型-收款单下拉列表", notes = "业务类型-收款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeReceivableBill.class))
    public AjaxResult getConBuTypeReceivableBilllList() {
        return AjaxResult.success(conBuTypeReceivableBillService.getConBuTypeReceivableBillList());
    }

}
