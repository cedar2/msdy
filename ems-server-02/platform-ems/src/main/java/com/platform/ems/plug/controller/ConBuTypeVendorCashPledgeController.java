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
import com.platform.ems.plug.domain.ConBuTypeVendorCashPledge;
import com.platform.ems.plug.service.IConBuTypeVendorCashPledgeService;
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

/**
 * 业务类型_供应商押金Controller
 *
 * @author c
 * @date 2021-09-27
 */
@RestController
@RequestMapping("/buType/vendor/cash/pledge")
@Api(tags = "业务类型_供应商押金")
public class ConBuTypeVendorCashPledgeController extends BaseController {

    @Autowired
    private IConBuTypeVendorCashPledgeService conBuTypeVendorCashPledgeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_供应商押金列表
     */
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_供应商押金列表", notes = "查询业务类型_供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorCashPledge.class))
    public TableDataInfo list(@RequestBody ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        startPage(conBuTypeVendorCashPledge);
        List<ConBuTypeVendorCashPledge> list = conBuTypeVendorCashPledgeService.selectConBuTypeVendorCashPledgeList(conBuTypeVendorCashPledge);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_供应商押金列表
     */
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:export")
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_供应商押金列表", notes = "导出业务类型_供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) throws IOException {
        List<ConBuTypeVendorCashPledge> list = conBuTypeVendorCashPledgeService.selectConBuTypeVendorCashPledgeList(conBuTypeVendorCashPledge);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeVendorCashPledge> util = new ExcelUtil<>(ConBuTypeVendorCashPledge.class, dataMap);
        util.exportExcel(response, list, "业务类型_供应商押金");
    }


    /**
     * 获取业务类型_供应商押金详细信息
     */
    @ApiOperation(value = "获取业务类型_供应商押金详细信息", notes = "获取业务类型_供应商押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorCashPledge.class))
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeVendorCashPledgeService.selectConBuTypeVendorCashPledgeById(sid));
    }

    /**
     * 新增业务类型_供应商押金
     */
    @ApiOperation(value = "新增业务类型_供应商押金", notes = "新增业务类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:add")
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return toAjax(conBuTypeVendorCashPledgeService.insertConBuTypeVendorCashPledge(conBuTypeVendorCashPledge));
    }

    /**
     * 修改业务类型_供应商押金
     */
    @ApiOperation(value = "修改业务类型_供应商押金", notes = "修改业务类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:edit")
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return toAjax(conBuTypeVendorCashPledgeService.updateConBuTypeVendorCashPledge(conBuTypeVendorCashPledge));
    }

    /**
     * 变更业务类型_供应商押金
     */
    @ApiOperation(value = "变更业务类型_供应商押金", notes = "变更业务类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:change")
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return toAjax(conBuTypeVendorCashPledgeService.changeConBuTypeVendorCashPledge(conBuTypeVendorCashPledge));
    }

    /**
     * 删除业务类型_供应商押金
     */
    @ApiOperation(value = "删除业务类型_供应商押金", notes = "删除业务类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:remove")
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeVendorCashPledgeService.deleteConBuTypeVendorCashPledgeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return AjaxResult.success(conBuTypeVendorCashPledgeService.changeStatus(conBuTypeVendorCashPledge));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:buType:vendor:cash:pledge:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_供应商押金", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        conBuTypeVendorCashPledge.setConfirmDate(new Date());
        conBuTypeVendorCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeVendorCashPledge.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeVendorCashPledgeService.check(conBuTypeVendorCashPledge));
    }

    /**
     * 业务类型_供应商押金下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_供应商押金下拉框列表", notes = "业务类型_供应商押金下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorCashPledge.class))
    public AjaxResult getList(@RequestBody ConBuTypeVendorCashPledge conBuTypeVendorCashPledge) {
        return AjaxResult.success(conBuTypeVendorCashPledgeService.getList(conBuTypeVendorCashPledge));
    }
}
