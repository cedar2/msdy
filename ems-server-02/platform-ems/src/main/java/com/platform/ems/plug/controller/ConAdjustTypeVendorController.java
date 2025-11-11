package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.plug.domain.ConAdjustTypeVendor;
import com.platform.ems.plug.service.IConAdjustTypeVendorService;
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
 * 调账类型_供应商Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/adjust/type/vendor")
@Api(tags = "调账类型_供应商")
public class ConAdjustTypeVendorController extends BaseController {

    @Autowired
    private IConAdjustTypeVendorService conAdjustTypeVendorService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询调账类型_供应商列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询调账类型_供应商列表", notes = "查询调账类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdjustTypeVendor.class))
    public TableDataInfo list(@RequestBody ConAdjustTypeVendor conAdjustTypeVendor) {
        startPage(conAdjustTypeVendor);
        List<ConAdjustTypeVendor> list = conAdjustTypeVendorService.selectConAdjustTypeVendorList(conAdjustTypeVendor);
        return getDataTable(list);
    }

    /**
     * 导出调账类型_供应商列表
     */
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:export")
    @Log(title = "调账类型_供应商", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出调账类型_供应商列表", notes = "导出调账类型_供应商列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConAdjustTypeVendor conAdjustTypeVendor) throws IOException {
        List<ConAdjustTypeVendor> list = conAdjustTypeVendorService.selectConAdjustTypeVendorList(conAdjustTypeVendor);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConAdjustTypeVendor> util = new ExcelUtil<>(ConAdjustTypeVendor.class, dataMap);
        util.exportExcel(response, list, "调账类型_供应商");
    }

    /**
     * 获取调账类型_供应商详细信息
     */
    @ApiOperation(value = "获取调账类型_供应商详细信息", notes = "获取调账类型_供应商详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdjustTypeVendor.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conAdjustTypeVendorService.selectConAdjustTypeVendorById(sid));
    }

    /**
     * 新增调账类型_供应商
     */
    @ApiOperation(value = "新增调账类型_供应商", notes = "新增调账类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:add")
    @Log(title = "调账类型_供应商", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConAdjustTypeVendor conAdjustTypeVendor) {
        return toAjax(conAdjustTypeVendorService.insertConAdjustTypeVendor(conAdjustTypeVendor));
    }

    /**
     * 修改调账类型_供应商
     */
    @ApiOperation(value = "修改调账类型_供应商", notes = "修改调账类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:edit")
    @Log(title = "调账类型_供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConAdjustTypeVendor conAdjustTypeVendor) {
        return toAjax(conAdjustTypeVendorService.updateConAdjustTypeVendor(conAdjustTypeVendor));
    }

    /**
     * 变更调账类型_供应商
     */
    @ApiOperation(value = "变更调账类型_供应商", notes = "变更调账类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:change")
    @Log(title = "调账类型_供应商", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConAdjustTypeVendor conAdjustTypeVendor) {
        return toAjax(conAdjustTypeVendorService.changeConAdjustTypeVendor(conAdjustTypeVendor));
    }

    /**
     * 删除调账类型_供应商
     */
    @ApiOperation(value = "删除调账类型_供应商", notes = "删除调账类型_供应商")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:remove")
    @Log(title = "调账类型_供应商", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conAdjustTypeVendorService.deleteConAdjustTypeVendorByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调账类型_供应商", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConAdjustTypeVendor conAdjustTypeVendor) {
        return AjaxResult.success(conAdjustTypeVendorService.changeStatus(conAdjustTypeVendor));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:adjust:type:vendor:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "调账类型_供应商", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConAdjustTypeVendor conAdjustTypeVendor) {
        conAdjustTypeVendor.setConfirmDate(new Date());
        conAdjustTypeVendor.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conAdjustTypeVendor.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conAdjustTypeVendorService.check(conAdjustTypeVendor));
    }

    /**
     * 调账类别下拉框列表
     */
    @PostMapping("/getConAdjustTypeVendorList")
    @ApiOperation(value = "调账类别下拉框列表", notes = "调账类别下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConAdjustTypeVendor.class))
    public AjaxResult getConAdjustTypeVendorList() {
        return AjaxResult.success(conAdjustTypeVendorService.getConAdjustTypeVendorList());
    }

}
