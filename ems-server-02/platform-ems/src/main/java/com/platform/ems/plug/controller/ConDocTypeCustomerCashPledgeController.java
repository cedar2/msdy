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
import com.platform.ems.plug.domain.ConDocTypeCustomerCashPledge;
import com.platform.ems.plug.service.IConDocTypeCustomerCashPledgeService;
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
 * 单据类型_客户押金Controller
 *
 * @author linhongwei
 * @date 2021-09-25
 */
@RestController
@RequestMapping("/customer/cash/pledge")
@Api(tags = "单据类型_客户押金")
public class ConDocTypeCustomerCashPledgeController extends BaseController {

    @Autowired
    private IConDocTypeCustomerCashPledgeService conDocTypeCustomerCashPledgeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询单据类型_客户押金列表
     */
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_客户押金列表", notes = "查询单据类型_客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerCashPledge.class))
    public TableDataInfo list(@RequestBody ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        startPage(conDocTypeCustomerCashPledge);
        List<ConDocTypeCustomerCashPledge> list = conDocTypeCustomerCashPledgeService.selectConDocTypeCustomerCashPledgeList(conDocTypeCustomerCashPledge);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_客户押金列表
     */
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:export")
    @Log(title = "单据类型_客户押金", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_客户押金列表", notes = "导出单据类型_客户押金列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) throws IOException {
        List<ConDocTypeCustomerCashPledge> list = conDocTypeCustomerCashPledgeService.selectConDocTypeCustomerCashPledgeList(conDocTypeCustomerCashPledge);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeCustomerCashPledge> util = new ExcelUtil<>(ConDocTypeCustomerCashPledge.class, dataMap);
        util.exportExcel(response, list, "单据类型_客户押金");
    }


    /**
     * 获取单据类型_客户押金详细信息
     */
    @ApiOperation(value = "获取单据类型_客户押金详细信息", notes = "获取单据类型_客户押金详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerCashPledge.class))
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeCustomerCashPledgeService.selectConDocTypeCustomerCashPledgeById(sid));
    }

    /**
     * 新增单据类型_客户押金
     */
    @ApiOperation(value = "新增单据类型_客户押金", notes = "新增单据类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:add")
    @Log(title = "单据类型_客户押金", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return toAjax(conDocTypeCustomerCashPledgeService.insertConDocTypeCustomerCashPledge(conDocTypeCustomerCashPledge));
    }

    /**
     * 修改单据类型_客户押金
     */
    @ApiOperation(value = "修改单据类型_客户押金", notes = "修改单据类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:edit")
    @Log(title = "单据类型_客户押金", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return toAjax(conDocTypeCustomerCashPledgeService.updateConDocTypeCustomerCashPledge(conDocTypeCustomerCashPledge));
    }

    /**
     * 变更单据类型_客户押金
     */
    @ApiOperation(value = "变更单据类型_客户押金", notes = "变更单据类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:change")
    @Log(title = "单据类型_客户押金", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return toAjax(conDocTypeCustomerCashPledgeService.changeConDocTypeCustomerCashPledge(conDocTypeCustomerCashPledge));
    }

    /**
     * 删除单据类型_客户押金
     */
    @ApiOperation(value = "删除单据类型_客户押金", notes = "删除单据类型_客户押金")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:remove")
    @Log(title = "单据类型_客户押金", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeCustomerCashPledgeService.deleteConDocTypeCustomerCashPledgeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_客户押金", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return AjaxResult.success(conDocTypeCustomerCashPledgeService.changeStatus(conDocTypeCustomerCashPledge));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:customer:cash:pledge:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_客户押金", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        conDocTypeCustomerCashPledge.setConfirmDate(new Date());
        conDocTypeCustomerCashPledge.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeCustomerCashPledge.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeCustomerCashPledgeService.check(conDocTypeCustomerCashPledge));
    }

    /**
     * 单据类型_客户押金下拉框列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_客户押金下拉框列表", notes = "单据类型_客户押金下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeCustomerCashPledge.class))
    public AjaxResult getList(@RequestBody ConDocTypeCustomerCashPledge conDocTypeCustomerCashPledge) {
        return AjaxResult.success(conDocTypeCustomerCashPledgeService.getList(conDocTypeCustomerCashPledge));
    }
}
