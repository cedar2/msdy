package com.platform.ems.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.BasVendorTag;
import com.platform.ems.service.IBasVendorTagService;
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
import java.util.List;
import java.util.Map;

/**
 * 供应商标签(分组)Controller
 *
 * @author c
 * @date 2022-03-30
 */
@RestController
@RequestMapping("/bas/vendortag")
@Api(tags = "供应商标签(分组)")
public class BasVendorTagController extends BaseController {

    @Autowired
    private IBasVendorTagService basVendorTagService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商标签(分组)列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:tag:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商标签(分组)列表", notes = "查询供应商标签(分组)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorTag.class))
    public TableDataInfo list(@RequestBody BasVendorTag basVendorTag) {
        startPage(basVendorTag);
        List<BasVendorTag> list = basVendorTagService.selectBasVendorTagList(basVendorTag);
        return getDataTable(list);
    }

    /**
     * 导出供应商标签(分组)列表
     */
    @PreAuthorize(hasPermi = "ems:vendor:tag:export")
    @Log(title = "供应商标签(分组)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商标签(分组)列表", notes = "导出供应商标签(分组)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorTag basVendorTag) throws IOException {
        List<BasVendorTag> list = basVendorTagService.selectBasVendorTagList(basVendorTag);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasVendorTag> util = new ExcelUtil<>(BasVendorTag.class, dataMap);
        util.exportExcel(response, list, "供应商分组");
    }


    /**
     * 获取供应商标签(分组)详细信息
     */
    @ApiOperation(value = "获取供应商标签(分组)详细信息", notes = "获取供应商标签(分组)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorTag.class))
    @PreAuthorize(hasPermi = "ems:vendor:tag:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorTagSid) {
        if (vendorTagSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basVendorTagService.selectBasVendorTagById(vendorTagSid));
    }

    /**
     * 新增供应商标签(分组)
     */
    @ApiOperation(value = "新增供应商标签(分组)", notes = "新增供应商标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:tag:add")
    @Log(title = "供应商标签(分组)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendorTag basVendorTag) {
        return toAjax(basVendorTagService.insertBasVendorTag(basVendorTag));
    }

    /**
     * 修改供应商标签(分组)
     */
    @ApiOperation(value = "修改供应商标签(分组)", notes = "修改供应商标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:tag:edit")
    @Log(title = "供应商标签(分组)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasVendorTag basVendorTag) {
        return toAjax(basVendorTagService.updateBasVendorTag(basVendorTag));
    }

    /**
     * 变更供应商标签(分组)
     */
    @ApiOperation(value = "变更供应商标签(分组)", notes = "变更供应商标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:tag:change")
    @Log(title = "供应商标签(分组)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasVendorTag basVendorTag) {
        return toAjax(basVendorTagService.changeBasVendorTag(basVendorTag));
    }

    /**
     * 删除供应商标签(分组)
     */
    @ApiOperation(value = "删除供应商标签(分组)", notes = "删除供应商标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vendor:tag:remove")
    @Log(title = "供应商标签(分组)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorTagSids) {
        if (CollUtil.isEmpty(vendorTagSids)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(basVendorTagService.deleteBasVendorTagByIds(vendorTagSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商标签(分组)", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:vendor:tag:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasVendorTag basVendorTag) {
        if (ArrayUtil.isEmpty(basVendorTag.getVendorTagSidList())) {
            throw new BaseException("参数缺失");
        }
        return AjaxResult.success(basVendorTagService.changeStatus(basVendorTag));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:vendor:tag:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商标签(分组)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasVendorTag basVendorTag) {
        if (ArrayUtil.isEmpty(basVendorTag.getVendorTagSidList())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(basVendorTagService.check(basVendorTag));
    }

}
