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
import com.platform.ems.plug.domain.ConBuTypeCustomerCashPledge;
import com.platform.ems.plug.service.IConBuTypeCustomerCashPledgeService;
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
 * 业务类型_客户押金Controller
 *
 * @author linhongwei
 * @date 2021-09-27
 */
@RestController
@RequestMapping("/buType/customer/cash/pledge")
@Api(tags = "业务类型_客户押金")
public class ConBuTypeCustomerCashPledgeController extends BaseController {

    @Autowired
    private IConBuTypeCustomerCashPledgeService conBuTypeCustomerCashPledgeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_客户押金列表
     */
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_客户押金列表", notes = "查询业务类型_客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerCashPledge.class))
    public TableDataInfo list(@RequestBody ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        startPage(conBuTypeCustomerCashPledge);
        List<ConBuTypeCustomerCashPledge> list = conBuTypeCustomerCashPledgeService.selectConBuTypeCustomerCashPledgeList(conBuTypeCustomerCashPledge);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_客户押金列表
     */
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:export")
    @Log(title = "业务类型_客户押金", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_客户押金列表", notes = "导出业务类型_客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) throws IOException {
        List<ConBuTypeCustomerCashPledge> list = conBuTypeCustomerCashPledgeService.selectConBuTypeCustomerCashPledgeList(conBuTypeCustomerCashPledge);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeCustomerCashPledge> util = new ExcelUtil<>(ConBuTypeCustomerCashPledge.class, dataMap);
        util.exportExcel(response, list, "业务类型_客户押金");
    }


    /**
     * 获取业务类型_客户押金详细信息
     */
    @ApiOperation(value = "获取业务类型_客户押金详细信息", notes = "获取业务类型_客户押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerCashPledge.class))
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeCustomerCashPledgeService.selectConBuTypeCustomerCashPledgeById(sid));
    }

    /**
     * 新增业务类型_客户押金
     */
    @ApiOperation(value = "新增业务类型_客户押金", notes = "新增业务类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:add")
    @Log(title = "业务类型_客户押金", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return toAjax(conBuTypeCustomerCashPledgeService.insertConBuTypeCustomerCashPledge(conBuTypeCustomerCashPledge));
    }

    /**
     * 修改业务类型_客户押金
     */
    @ApiOperation(value = "修改业务类型_客户押金", notes = "修改业务类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:edit")
    @Log(title = "业务类型_客户押金", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return toAjax(conBuTypeCustomerCashPledgeService.updateConBuTypeCustomerCashPledge(conBuTypeCustomerCashPledge));
    }

    /**
     * 变更业务类型_客户押金
     */
    @ApiOperation(value = "变更业务类型_客户押金", notes = "变更业务类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:change")
    @Log(title = "业务类型_客户押金", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return toAjax(conBuTypeCustomerCashPledgeService.changeConBuTypeCustomerCashPledge(conBuTypeCustomerCashPledge));
    }

    /**
     * 删除业务类型_客户押金
     */
    @ApiOperation(value = "删除业务类型_客户押金", notes = "删除业务类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:remove")
    @Log(title = "业务类型_客户押金", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeCustomerCashPledgeService.deleteConBuTypeCustomerCashPledgeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_客户押金", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return AjaxResult.success(conBuTypeCustomerCashPledgeService.changeStatus(conBuTypeCustomerCashPledge));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:buType:customer:cash:pledge:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_客户押金", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        conBuTypeCustomerCashPledge.setConfirmDate(new Date());
        conBuTypeCustomerCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeCustomerCashPledge.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeCustomerCashPledgeService.check(conBuTypeCustomerCashPledge));
    }

    /**
     * 业务类型_客户押金下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_客户押金下拉框列表", notes = "业务类型_客户押金下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerCashPledge.class))
    public AjaxResult getList(@RequestBody ConBuTypeCustomerCashPledge conBuTypeCustomerCashPledge) {
        return AjaxResult.success(conBuTypeCustomerCashPledgeService.getList(conBuTypeCustomerCashPledge));
    }
}
