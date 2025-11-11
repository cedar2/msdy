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
import com.platform.ems.domain.BasCustomerTag;
import com.platform.ems.service.IBasCustomerTagService;
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
import java.util.List;
import java.util.Map;

/**
 * 客户标签(分组)Controller
 *
 * @author c
 * @date 2022-03-30
 */
@RestController
@RequestMapping("/customer/tag")
@Api(tags = "客户标签(分组)")
public class BasCustomerTagController extends BaseController {

    @Autowired
    private IBasCustomerTagService basCustomerTagService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户标签(分组)列表
     */
    @PreAuthorize(hasPermi = "ems:customer:tag:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询客户标签(分组)列表", notes = "查询客户标签(分组)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerTag.class))
    public TableDataInfo list(@RequestBody BasCustomerTag basCustomerTag) {
        startPage(basCustomerTag);
        List<BasCustomerTag> list = basCustomerTagService.selectBasCustomerTagList(basCustomerTag);
        return getDataTable(list);
    }

    /**
     * 导出客户标签(分组)列表
     */
    @PreAuthorize(hasPermi = "ems:customer:tag:export")
    @Log(title = "客户标签(分组)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出客户标签(分组)列表", notes = "导出客户标签(分组)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasCustomerTag basCustomerTag) throws IOException {
        List<BasCustomerTag> list = basCustomerTagService.selectBasCustomerTagList(basCustomerTag);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasCustomerTag> util = new ExcelUtil<>(BasCustomerTag.class, dataMap);
        util.exportExcel(response, list, "客户分组");
    }


    /**
     * 获取客户标签(分组)详细信息
     */
    @ApiOperation(value = "获取客户标签(分组)详细信息", notes = "获取客户标签(分组)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasCustomerTag.class))
    @PreAuthorize(hasPermi = "ems:customer:tag:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerTagSid) {
        if (customerTagSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basCustomerTagService.selectBasCustomerTagById(customerTagSid));
    }

    /**
     * 新增客户标签(分组)
     */
    @ApiOperation(value = "新增客户标签(分组)", notes = "新增客户标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:tag:add")
    @Log(title = "客户标签(分组)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasCustomerTag basCustomerTag) {
        return toAjax(basCustomerTagService.insertBasCustomerTag(basCustomerTag));
    }

    /**
     * 修改客户标签(分组)
     */
    @ApiOperation(value = "修改客户标签(分组)", notes = "修改客户标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:tag:edit")
    @Log(title = "客户标签(分组)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody BasCustomerTag basCustomerTag) {
        return toAjax(basCustomerTagService.updateBasCustomerTag(basCustomerTag));
    }

    /**
     * 变更客户标签(分组)
     */
    @ApiOperation(value = "变更客户标签(分组)", notes = "变更客户标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:tag:change")
    @Log(title = "客户标签(分组)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody BasCustomerTag basCustomerTag) {
        return toAjax(basCustomerTagService.changeBasCustomerTag(basCustomerTag));
    }

    /**
     * 删除客户标签(分组)
     */
    @ApiOperation(value = "删除客户标签(分组)", notes = "删除客户标签(分组)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:customer:tag:remove")
    @Log(title = "客户标签(分组)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerTagSids) {
        if (CollUtil.isEmpty(customerTagSids)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(basCustomerTagService.deleteBasCustomerTagByIds(customerTagSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户标签(分组)", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:customer:tag:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BasCustomerTag basCustomerTag) {
        if (ArrayUtil.isEmpty(basCustomerTag.getCustomerTagSidList())) {
            throw new BaseException("参数缺失");
        }
        return AjaxResult.success(basCustomerTagService.changeStatus(basCustomerTag));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:customer:tag:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "客户标签(分组)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasCustomerTag basCustomerTag) {
        if (ArrayUtil.isEmpty(basCustomerTag.getCustomerTagSidList())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(basCustomerTagService.check(basCustomerTag));
    }

}
