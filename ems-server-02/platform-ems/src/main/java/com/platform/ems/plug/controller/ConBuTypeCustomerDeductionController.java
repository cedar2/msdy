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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.plug.domain.ConBuTypeCustomerDeduction;
import com.platform.ems.plug.service.IConBuTypeCustomerDeductionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_客户扣款单Controller
 *
 * @author chenkw
 * @date 2021-08-03
 */
@RestController
@RequestMapping("/con/buType/customer/deduction")
@Api(tags = "业务类型_客户扣款单")
public class ConBuTypeCustomerDeductionController extends BaseController {

    @Autowired
    private IConBuTypeCustomerDeductionService conBuTypeCustomerDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_客户扣款单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_客户扣款单列表", notes = "查询业务类型_客户扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerDeduction.class))
    public TableDataInfo list(@RequestBody ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        startPage(conBuTypeCustomerDeduction);
        List<ConBuTypeCustomerDeduction> list = conBuTypeCustomerDeductionService.selectConBuTypeCustomerDeductionList(conBuTypeCustomerDeduction);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_客户扣款单列表
     */
    @ApiOperation(value = "导出业务类型_客户扣款单列表", notes = "导出业务类型_客户扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) throws IOException {
        List<ConBuTypeCustomerDeduction> list = conBuTypeCustomerDeductionService.selectConBuTypeCustomerDeductionList(conBuTypeCustomerDeduction);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeCustomerDeduction> util = new ExcelUtil<>(ConBuTypeCustomerDeduction.class, dataMap);
        util.exportExcel(response, list, "业务类型_客户扣款单");
    }


    /**
     * 获取业务类型_客户扣款单详细信息
     */
    @ApiOperation(value = "获取业务类型_客户扣款单详细信息", notes = "获取业务类型_客户扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerDeduction.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeCustomerDeductionService.selectConBuTypeCustomerDeductionById(sid));
    }

    /**
     * 新增业务类型_客户扣款单
     */
    @ApiOperation(value = "新增业务类型_客户扣款单", notes = "新增业务类型_客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        return toAjax(conBuTypeCustomerDeductionService.insertConBuTypeCustomerDeduction(conBuTypeCustomerDeduction));
    }

    /**
     * 修改业务类型_客户扣款单
     */
    @ApiOperation(value = "修改业务类型_客户扣款单", notes = "修改业务类型_客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        return toAjax(conBuTypeCustomerDeductionService.updateConBuTypeCustomerDeduction(conBuTypeCustomerDeduction));
    }

    /**
     * 变更业务类型_客户扣款单
     */
    @ApiOperation(value = "变更业务类型_客户扣款单", notes = "变更业务类型_客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        return toAjax(conBuTypeCustomerDeductionService.changeConBuTypeCustomerDeduction(conBuTypeCustomerDeduction));
    }

    /**
     * 删除业务类型_客户扣款单
     */
    @ApiOperation(value = "删除业务类型_客户扣款单", notes = "删除业务类型_客户扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeCustomerDeductionService.deleteConBuTypeCustomerDeductionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        return AjaxResult.success(conBuTypeCustomerDeductionService.changeStatus(conBuTypeCustomerDeduction));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeCustomerDeduction conBuTypeCustomerDeduction) {
        conBuTypeCustomerDeduction.setConfirmDate(new Date());
        conBuTypeCustomerDeduction.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeCustomerDeduction.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeCustomerDeductionService.check(conBuTypeCustomerDeduction));
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getConBuTypeCustomerDeductionList")
    @ApiOperation(value = "业务类型_客户扣款单下拉框列表", notes = "业务类型_客户扣款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeCustomerDeduction.class))
    public AjaxResult getConBuTypeCustomerDeductionList() {
        return AjaxResult.success(conBuTypeCustomerDeductionService.getConBuTypeCustomerDeductionList());
    }
}
