package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.annotation.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.BasSeasonVendor;
import com.platform.ems.service.IBasSeasonVendorService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 季度供应商Controller
 *
 * @author chenkw
 * @date 2023-04-13
 */
@RestController
@RequestMapping("/season/vendor")
@Api(tags = "季度供应商")
public class BasSeasonVendorController extends BaseController {

    @Autowired
    private IBasSeasonVendorService basSeasonVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询季度供应商列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询季度供应商列表", notes = "查询季度供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSeasonVendor.class))
    public TableDataInfo list(@RequestBody BasSeasonVendor basSeasonVendor) {
        startPage(basSeasonVendor);
        List<BasSeasonVendor> list = basSeasonVendorService.selectBasSeasonVendorList(basSeasonVendor);
        return getDataTable(list);
    }

    /**
     * 导出季度供应商列表
     */
    @Log(title = "季度供应商", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出季度供应商列表", notes = "导出季度供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasSeasonVendor basSeasonVendor) throws IOException {
        List<BasSeasonVendor> list = basSeasonVendorService.selectBasSeasonVendorList(basSeasonVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasSeasonVendor> util = new ExcelUtil<>(BasSeasonVendor.class, dataMap);
        util.exportExcel(response, list, "季度供应商");
    }

    /**
     * 获取季度供应商详细信息
     */
    @ApiOperation(value = "获取季度供应商详细信息", notes = "获取季度供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSeasonVendor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long seasonVendorSid) {
        if (seasonVendorSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basSeasonVendorService.selectBasSeasonVendorById(seasonVendorSid));
    }

    /**
     * 新增季度供应商
     */
    @ApiOperation(value = "新增季度供应商", notes = "新增季度供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid BasSeasonVendor basSeasonVendor) {
        return toAjax(basSeasonVendorService.insertBasSeasonVendor(basSeasonVendor));
    }

    @ApiOperation(value = "修改季度供应商", notes = "修改季度供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid BasSeasonVendor basSeasonVendor) {
        return toAjax(basSeasonVendorService.updateBasSeasonVendor(basSeasonVendor));
    }

    /**
     * 变更季度供应商
     */
    @ApiOperation(value = "变更季度供应商", notes = "变更季度供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasSeasonVendor basSeasonVendor) {
        return toAjax(basSeasonVendorService.changeBasSeasonVendor(basSeasonVendor));
    }

    /**
     * 删除季度供应商
     */
    @ApiOperation(value = "删除季度供应商", notes = "删除季度供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> seasonVendorSids) {
        if (CollectionUtils.isEmpty(seasonVendorSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basSeasonVendorService.deleteBasSeasonVendorByIds(seasonVendorSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    public AjaxResult changeStatus(@RequestBody BasSeasonVendor basSeasonVendor) {
        if (ArrayUtil.isEmpty(basSeasonVendor.getSeasonVendorSidList())) {
            throw new CheckedException("请选择行");
        }
        return AjaxResult.success(basSeasonVendorService.changeStatus(basSeasonVendor));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "季度供应商", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody BasSeasonVendor basSeasonVendor) {
        if (ArrayUtil.isEmpty(basSeasonVendor.getSeasonVendorSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(basSeasonVendorService.check(basSeasonVendor));
    }

    @ApiOperation(value = "设置是否快反供应商", notes = "设置是否快反供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setKuaiFan")
    public AjaxResult setKuaiFan(@RequestBody BasSeasonVendor basSeasonVendor) {
        if (ArrayUtil.isEmpty(basSeasonVendor.getSeasonVendorSidList())) {
            throw new CheckedException("请选择行");
        }
        return AjaxResult.success(basSeasonVendorService.setKuaiFan(basSeasonVendor));
    }

}
