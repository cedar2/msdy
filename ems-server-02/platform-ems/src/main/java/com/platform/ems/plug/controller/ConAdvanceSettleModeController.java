package com.platform.ems.plug.controller;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConAdvanceSettleMode;
import com.platform.ems.plug.service.IConAdvanceSettleModeService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advance/settle/mode")
@Api(tags = "预付款/预收款结算方式")
public class ConAdvanceSettleModeController extends BaseController {

    @Autowired
    private IConAdvanceSettleModeService conAdvanceSettleModeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询预付款/预收款结算方式列表
     */
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询预付款/预收款结算方式列表", notes = "查询预付款/预收款结算方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdvanceSettleMode.class))
    public TableDataInfo list(@RequestBody ConAdvanceSettleMode conAdvanceSettleMode) {
        startPage(conAdvanceSettleMode);
        List<ConAdvanceSettleMode> list = conAdvanceSettleModeService.selectConAdvanceSettleModeList(conAdvanceSettleMode);
        return getDataTable(list);
    }

    /**
     * 导出预付款/预收款结算方式列表
     */
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:export")
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出预付款/预收款结算方式列表", notes = "导出预付款/预收款结算方式列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAdvanceSettleMode conAdvanceSettleMode) throws IOException {
        List<ConAdvanceSettleMode> list = conAdvanceSettleModeService.selectConAdvanceSettleModeList(conAdvanceSettleMode);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAdvanceSettleMode> util = new ExcelUtil<>(ConAdvanceSettleMode.class, dataMap);
        util.exportExcel(response, list, "预收付款结算方式");
    }

    /**
     * 获取预付款/预收款结算方式详细信息
     */
    @ApiOperation(value = "获取预付款/预收款结算方式详细信息", notes = "获取预付款/预收款结算方式详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdvanceSettleMode.class))
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conAdvanceSettleModeService.selectConAdvanceSettleModeById(sid));
    }

    /**
     * 新增预付款/预收款结算方式
     */
    @ApiOperation(value = "新增预付款/预收款结算方式", notes = "新增预付款/预收款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:add")
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAdvanceSettleMode conAdvanceSettleMode) {
        return toAjax(conAdvanceSettleModeService.insertConAdvanceSettleMode(conAdvanceSettleMode));
    }

    /**
     * 修改预付款/预收款结算方式
     */
    @ApiOperation(value = "修改预付款/预收款结算方式", notes = "修改预付款/预收款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:edit")
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAdvanceSettleMode conAdvanceSettleMode) {
        return toAjax(conAdvanceSettleModeService.updateConAdvanceSettleMode(conAdvanceSettleMode));
    }

    /**
     * 变更预付款/预收款结算方式
     */
    @ApiOperation(value = "变更预付款/预收款结算方式", notes = "变更预付款/预收款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:change")
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAdvanceSettleMode conAdvanceSettleMode) {
        return toAjax(conAdvanceSettleModeService.changeConAdvanceSettleMode(conAdvanceSettleMode));
    }

    /**
     * 删除预付款/预收款结算方式
     */
    @ApiOperation(value = "删除预付款/预收款结算方式", notes = "删除预付款/预收款结算方式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:remove")
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAdvanceSettleModeService.deleteConAdvanceSettleModeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAdvanceSettleMode conAdvanceSettleMode) {
        return AjaxResult.success(conAdvanceSettleModeService.changeStatus(conAdvanceSettleMode));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:advance:settle:mode:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "预付款/预收款结算方式", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAdvanceSettleMode conAdvanceSettleMode) {
        conAdvanceSettleMode.setConfirmDate(new Date());
        conAdvanceSettleMode.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAdvanceSettleMode.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAdvanceSettleModeService.check(conAdvanceSettleMode));
    }

    @PostMapping("/getConAdvanceSettleModeList")
    @ApiOperation(value = "预付款/预收款结算方式下拉列表", notes = "预付款/预收款结算方式下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdvanceSettleMode.class))
    public AjaxResult getConAdvanceSettleModeList() {
        return AjaxResult.success(conAdvanceSettleModeService.getConAdvanceSettleModeList());
    }
}
