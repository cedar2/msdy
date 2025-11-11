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
import com.platform.ems.plug.domain.ConDocTypeVendorCashPledge;
import com.platform.ems.plug.service.IConDocTypeVendorCashPledgeService;
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
 * 单据类型_供应商押金Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/vendor/cash/pledge")
@Api(tags = "单据类型_供应商押金")
public class ConDocTypeVendorCashPledgeController extends BaseController {

    @Autowired
    private IConDocTypeVendorCashPledgeService conDocTypeVendorCashPledgeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_供应商押金列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_供应商押金列表", notes = "查询单据类型_供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeVendorCashPledge.class))
    public TableDataInfo list(@RequestBody ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        startPage(conDocTypeVendorCashPledge);
        List<ConDocTypeVendorCashPledge> list = conDocTypeVendorCashPledgeService.selectConDocTypeVendorCashPledgeList(conDocTypeVendorCashPledge);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_供应商押金列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:export")
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_供应商押金列表", notes = "导出单据类型_供应商押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) throws IOException {
        List<ConDocTypeVendorCashPledge> list = conDocTypeVendorCashPledgeService.selectConDocTypeVendorCashPledgeList(conDocTypeVendorCashPledge);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeVendorCashPledge> util = new ExcelUtil<>(ConDocTypeVendorCashPledge.class, dataMap);
        util.exportExcel(response, list, "单据类型_供应商押金");
    }


    /**
     * 获取单据类型_供应商押金详细信息
     */
    @ApiOperation(value = "获取单据类型_供应商押金详细信息", notes = "获取单据类型_供应商押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeVendorCashPledge.class))
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeVendorCashPledgeService.selectConDocTypeVendorCashPledgeById(sid));
    }

    /**
     * 新增单据类型_供应商押金
     */
    @ApiOperation(value = "新增单据类型_供应商押金", notes = "新增单据类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:add")
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return toAjax(conDocTypeVendorCashPledgeService.insertConDocTypeVendorCashPledge(conDocTypeVendorCashPledge));
    }

    /**
     * 修改单据类型_供应商押金
     */
    @ApiOperation(value = "修改单据类型_供应商押金", notes = "修改单据类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:edit")
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return toAjax(conDocTypeVendorCashPledgeService.updateConDocTypeVendorCashPledge(conDocTypeVendorCashPledge));
    }

    /**
     * 变更单据类型_供应商押金
     */
    @ApiOperation(value = "变更单据类型_供应商押金", notes = "变更单据类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:change")
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return toAjax(conDocTypeVendorCashPledgeService.changeConDocTypeVendorCashPledge(conDocTypeVendorCashPledge));
    }

    /**
     * 删除单据类型_供应商押金
     */
    @ApiOperation(value = "删除单据类型_供应商押金", notes = "删除单据类型_供应商押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:remove")
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeVendorCashPledgeService.deleteConDocTypeVendorCashPledgeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return AjaxResult.success(conDocTypeVendorCashPledgeService.changeStatus(conDocTypeVendorCashPledge));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:vendor:cash:pledge:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_供应商押金", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        conDocTypeVendorCashPledge.setConfirmDate(new Date());
        conDocTypeVendorCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeVendorCashPledge.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeVendorCashPledgeService.check(conDocTypeVendorCashPledge));
    }

    /**
     * 单据类型_供应商押金下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_供应商押金下拉框列表", notes = "单据类型_供应商押金下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeVendorCashPledge.class))
    public AjaxResult getList(@RequestBody ConDocTypeVendorCashPledge conDocTypeVendorCashPledge) {
        return AjaxResult.success(conDocTypeVendorCashPledgeService.getList(conDocTypeVendorCashPledge));
    }
}
