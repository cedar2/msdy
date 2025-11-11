package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.service.IConTaxRateService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 税率配置Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/tax/rate")
@Api(tags = "税率配置")
public class ConTaxRateController extends BaseController {

    @Autowired
    private IConTaxRateService conTaxRateService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询税率配置列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询税率配置列表", notes = "查询税率配置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaxRate.class))
    public TableDataInfo list(@RequestBody ConTaxRate conTaxRate) {
        startPage(conTaxRate);
        List<ConTaxRate> list = conTaxRateService.selectConTaxRateList(conTaxRate);
        return getDataTable(list);
    }

    /**
     * 导出税率配置列表
     */
    @ApiOperation(value = "导出税率配置列表", notes = "导出税率配置列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConTaxRate conTaxRate) throws IOException {
        List<ConTaxRate> list = conTaxRateService.selectConTaxRateList(conTaxRate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConTaxRate> util = new ExcelUtil<>(ConTaxRate.class, dataMap);
        util.exportExcel(response, list, "税率");
    }


    /**
     * 获取税率配置详细信息
     */
    @ApiOperation(value = "获取税率配置详细信息", notes = "获取税率配置详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaxRate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long taxRateSid) {
        if (taxRateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conTaxRateService.selectConTaxRateById(taxRateSid));
    }

    /**
     * 新增税率配置
     */
    @ApiOperation(value = "新增税率配置", notes = "新增税率配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConTaxRate conTaxRate) {
        return toAjax(conTaxRateService.insertConTaxRate(conTaxRate));
    }

    /**
     * 修改税率配置
     */
    @ApiOperation(value = "修改税率配置", notes = "修改税率配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConTaxRate conTaxRate) {
        return toAjax(conTaxRateService.updateConTaxRate(conTaxRate));
    }

    /**
     * 变更税率配置
     */
    @ApiOperation(value = "变更税率配置", notes = "变更税率配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConTaxRate conTaxRate) {
        return toAjax(conTaxRateService.changeConTaxRate(conTaxRate));
    }

    /**
     * 删除税率配置
     */
    @ApiOperation(value = "删除税率配置", notes = "删除税率配置")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> taxRateSids) {
        if (ArrayUtil.isEmpty(taxRateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conTaxRateService.deleteConTaxRateByIds(taxRateSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConTaxRate conTaxRate) {
        return AjaxResult.success(conTaxRateService.changeStatus(conTaxRate));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConTaxRate conTaxRate) {
        conTaxRate.setConfirmDate(new Date());
        conTaxRate.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conTaxRate.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conTaxRateService.check(conTaxRate));
    }

    @PostMapping("/getConTaxRateList")
    @ApiOperation(value = "税率下拉列表", notes = "税率下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaxRate.class))
    public AjaxResult getConTaxRateList() {
        return AjaxResult.success(conTaxRateService.getConTaxRateList());
    }

    @PostMapping("/getList")
    @ApiOperation(value = "税率下拉列表", notes = "税率下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConTaxRate.class))
    public AjaxResult getList(@RequestBody ConTaxRate conTaxRate) {
        return AjaxResult.success(conTaxRateService.getList(conTaxRate));
    }
}
