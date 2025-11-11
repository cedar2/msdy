package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.BasVendorRegister;
import com.platform.ems.service.IBasVendorRegisterService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商注册-基础Controller
 *
 * @author chenkw
 * @date 2022-02-21
 */
@RestController
@RequestMapping("/vendor/register")
@Api(tags = "供应商注册-基础")
public class BasVendorRegisterController extends BaseController {

    @Autowired
    private IBasVendorRegisterService basVendorRegisterService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商注册-基础列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商注册-基础列表", notes = "查询供应商注册-基础列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorRegister.class))
    public TableDataInfo list(@RequestBody BasVendorRegister basVendorRegister) {
        startPage(basVendorRegister);
        List<BasVendorRegister> list = basVendorRegisterService.selectBasVendorRegisterList(basVendorRegister);
        return getDataTable(list);
    }

    /**
     * 导出供应商注册-基础列表
     */
    @Log(title = "供应商注册-基础", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商注册-基础列表", notes = "导出供应商注册-基础列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorRegister basVendorRegister) throws IOException {
        List<BasVendorRegister> list = basVendorRegisterService.selectBasVendorRegisterList(basVendorRegister);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasVendorRegister> util = new ExcelUtil<>(BasVendorRegister.class, dataMap);
        util.exportExcel(response, list, "供应商注册基础");
    }

    /**
     * 获取供应商注册-基础详细信息
     */
    @ApiOperation(value = "获取供应商注册-基础详细信息(流水号和注册码)", notes = "获取供应商注册-基础详细信息(流水号和注册码)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorRegister.class))
    @PostMapping("/getInfo/byCode")
    public AjaxResult getInfo(@RequestBody BasVendorRegister basVendorRegister) {
        if (basVendorRegister == null || basVendorRegister.getVendorRegisterNum() == null || StrUtil.isBlank(basVendorRegister.getVendorRegisterCode())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basVendorRegisterService.selectBasVendorRegisterByCode(basVendorRegister));
    }

    /**
     * 获取供应商注册-基础详细信息
     */
    @ApiOperation(value = "获取供应商注册-基础详细信息", notes = "获取供应商注册-基础详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorRegister.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorRegisterSid) {
        if (vendorRegisterSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basVendorRegisterService.selectBasVendorRegisterById(vendorRegisterSid));
    }

    /**
     * 新增供应商注册-基础
     */
    @ApiOperation(value = "新增供应商注册-基础", notes = "新增供应商注册-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商注册-基础", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendorRegister basVendorRegister) {
        return AjaxResult.success(basVendorRegisterService.insertBasVendorRegister(basVendorRegister));
    }

    /**
     * 修改供应商注册-基础
     */
    @ApiOperation(value = "修改供应商注册-基础", notes = "修改供应商注册-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商注册-基础", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasVendorRegister basVendorRegister) {
        return toAjax(basVendorRegisterService.updateBasVendorRegister(basVendorRegister));
    }

    /**
     * 变更供应商注册-基础
     */
    @ApiOperation(value = "变更供应商注册-基础", notes = "变更供应商注册-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商注册-基础", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasVendorRegister basVendorRegister) {
        return toAjax(basVendorRegisterService.changeBasVendorRegister(basVendorRegister));
    }

    /**
     * 删除供应商注册-基础
     */
    @ApiOperation(value = "删除供应商注册-基础", notes = "删除供应商注册-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商注册-基础", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorRegisterSids) {
        if (CollectionUtils.isEmpty(vendorRegisterSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basVendorRegisterService.deleteBasVendorRegisterByIds(vendorRegisterSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商注册-基础", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasVendorRegister basVendorRegister) {
        return toAjax(basVendorRegisterService.check(basVendorRegister));
    }

}
