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
import com.platform.ems.plug.domain.ConBuTypeVendorDeduction;
import com.platform.ems.plug.service.IConBuTypeVendorDeductionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_供应商扣款单Controller
 *
 * @author chenkw
 * @date 2021-08-03
 */
@RestController
@RequestMapping("/con/buType/vendor/deduction")
@Api(tags = "业务类型_供应商扣款单")
public class ConBuTypeVendorDeductionController extends BaseController {

    @Autowired
    private IConBuTypeVendorDeductionService conBuTypeVendorDeductionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_供应商扣款单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_供应商扣款单列表", notes = "查询业务类型_供应商扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorDeduction.class))
    public TableDataInfo list(@RequestBody ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        startPage(conBuTypeVendorDeduction);
        List<ConBuTypeVendorDeduction> list = conBuTypeVendorDeductionService.selectConBuTypeVendorDeductionList(conBuTypeVendorDeduction);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_供应商扣款单列表
     */
    @ApiOperation(value = "导出业务类型_供应商扣款单列表", notes = "导出业务类型_供应商扣款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeVendorDeduction conBuTypeVendorDeduction) throws IOException {
        List<ConBuTypeVendorDeduction> list = conBuTypeVendorDeductionService.selectConBuTypeVendorDeductionList(conBuTypeVendorDeduction);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeVendorDeduction> util = new ExcelUtil<>(ConBuTypeVendorDeduction.class, dataMap);
        util.exportExcel(response, list, "业务类型_供应商扣款单");
    }


    /**
     * 获取业务类型_供应商扣款单详细信息
     */
    @ApiOperation(value = "获取业务类型_供应商扣款单详细信息", notes = "获取业务类型_供应商扣款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorDeduction.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeVendorDeductionService.selectConBuTypeVendorDeductionById(sid));
    }

    /**
     * 新增业务类型_供应商扣款单
     */
    @ApiOperation(value = "新增业务类型_供应商扣款单", notes = "新增业务类型_供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        return toAjax(conBuTypeVendorDeductionService.insertConBuTypeVendorDeduction(conBuTypeVendorDeduction));
    }

    /**
     * 修改业务类型_供应商扣款单
     */
    @ApiOperation(value = "修改业务类型_供应商扣款单", notes = "修改业务类型_供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        return toAjax(conBuTypeVendorDeductionService.updateConBuTypeVendorDeduction(conBuTypeVendorDeduction));
    }

    /**
     * 变更业务类型_供应商扣款单
     */
    @ApiOperation(value = "变更业务类型_供应商扣款单", notes = "变更业务类型_供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        return toAjax(conBuTypeVendorDeductionService.changeConBuTypeVendorDeduction(conBuTypeVendorDeduction));
    }

    /**
     * 删除业务类型_供应商扣款单
     */
    @ApiOperation(value = "删除业务类型_供应商扣款单", notes = "删除业务类型_供应商扣款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (CollectionUtils.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeVendorDeductionService.deleteConBuTypeVendorDeductionByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        return AjaxResult.success(conBuTypeVendorDeductionService.changeStatus(conBuTypeVendorDeduction));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeVendorDeduction conBuTypeVendorDeduction) {
        conBuTypeVendorDeduction.setConfirmDate(new Date());
        conBuTypeVendorDeduction.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeVendorDeduction.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeVendorDeductionService.check(conBuTypeVendorDeduction));
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getConBuTypeVendorDeductionList")
    @ApiOperation(value = "业务类型_供应商扣款单下拉框列表", notes = "业务类型_供应商扣款单下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeVendorDeduction.class))
    public AjaxResult getConBuTypeVendorDeductionList() {
        return AjaxResult.success(conBuTypeVendorDeductionService.getConBuTypeVendorDeductionList());
    }
}
