package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

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
import com.platform.ems.domain.BasVendorRecommend;
import com.platform.ems.service.IBasVendorRecommendService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 供应商推荐-基础Controller
 *
 * @author chenkw
 * @date 2022-02-21
 */
@RestController
@RequestMapping("/vendor/recommend")
@Api(tags = "供应商推荐-基础")
public class BasVendorRecommendController extends BaseController {

    @Autowired
    private IBasVendorRecommendService basVendorRecommendService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商推荐-基础列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商推荐-基础列表", notes = "查询供应商推荐-基础列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorRecommend.class))
    public TableDataInfo list(@RequestBody BasVendorRecommend basVendorRecommend) {
        startPage(basVendorRecommend);
        List<BasVendorRecommend> list = basVendorRecommendService.selectBasVendorRecommendList(basVendorRecommend);
        return getDataTable(list);
    }

    /**
     * 导出供应商推荐-基础列表
     */
    @Log(title = "供应商推荐-基础", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出供应商推荐-基础列表", notes = "导出供应商推荐-基础列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasVendorRecommend basVendorRecommend) throws IOException {
        List<BasVendorRecommend> list = basVendorRecommendService.selectBasVendorRecommendList(basVendorRecommend);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasVendorRecommend> util = new ExcelUtil<>(BasVendorRecommend.class, dataMap);
        util.exportExcel(response, list, "供应商推荐基础");
    }


    /**
     * 获取供应商推荐-基础详细信息
     */
    @ApiOperation(value = "获取供应商推荐-基础详细信息", notes = "获取供应商推荐-基础详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasVendorRecommend.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorRecommendSid) {
        if (vendorRecommendSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basVendorRecommendService.selectBasVendorRecommendById(vendorRecommendSid));
    }

    /**
     * 新增供应商推荐-基础
     */
    @ApiOperation(value = "新增供应商推荐-基础", notes = "新增供应商推荐-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商推荐-基础", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasVendorRecommend basVendorRecommend) {
        return toAjax(basVendorRecommendService.insertBasVendorRecommend(basVendorRecommend));
    }

    /**
     * 修改供应商推荐-基础
     */
    @ApiOperation(value = "修改供应商推荐-基础", notes = "修改供应商推荐-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商推荐-基础", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasVendorRecommend basVendorRecommend) {
        return toAjax(basVendorRecommendService.updateBasVendorRecommend(basVendorRecommend));
    }

    /**
     * 变更供应商推荐-基础
     */
    @ApiOperation(value = "变更供应商推荐-基础", notes = "变更供应商推荐-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商推荐-基础", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasVendorRecommend basVendorRecommend) {
        return toAjax(basVendorRecommendService.changeBasVendorRecommend(basVendorRecommend));
    }

    /**
     * 删除供应商推荐-基础
     */
    @ApiOperation(value = "删除供应商推荐-基础", notes = "删除供应商推荐-基础")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商推荐-基础", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorRecommendSids) {
        if (CollectionUtils.isEmpty(vendorRecommendSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basVendorRecommendService.deleteBasVendorRecommendByIds(vendorRecommendSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "供应商推荐-基础", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody BasVendorRecommend basVendorRecommend) {
        return toAjax(basVendorRecommendService.check(basVendorRecommend));
    }

}
