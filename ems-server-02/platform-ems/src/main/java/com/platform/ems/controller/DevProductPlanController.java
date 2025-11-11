package com.platform.ems.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.DevProductPlan;
import com.platform.ems.service.IDevProductPlanService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 品类规划信息Controller
 *
 * @author qhq
 * @date 2021-11-08
 */
@RestController
@RequestMapping("/product/plan")
@Api(tags = "品类规划信息")
public class DevProductPlanController extends BaseController {

    @Autowired
    private IDevProductPlanService devProductPlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询品类规划信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询品类规划信息列表", notes = "查询品类规划信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevProductPlan.class))
    @PreAuthorize(hasPermi = "ems:product:plan:query")
    public TableDataInfo list(@RequestBody DevProductPlan devProductPlan) {
        startPage(devProductPlan);
        List<DevProductPlan> list = devProductPlanService.selectDevProductPlanList(devProductPlan);
        return getDataTable(list);
    }

    /**
     * 导出品类规划信息列表
     */
    @Log(title = "品类规划信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出品类规划信息列表", notes = "导出品类规划信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    @PreAuthorize(hasPermi = "ems:product:plan:export")
    public void export(HttpServletResponse response, DevProductPlan devProductPlan) throws IOException {
        List<DevProductPlan> list = devProductPlanService.selectDevProductPlanList(devProductPlan);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<DevProductPlan> util = new ExcelUtil<DevProductPlan>(DevProductPlan.class,dataMap);
        util.exportExcel(response, list, "品类规划信息");
    }


    /**
     * 获取品类规划信息详细信息
     */
    @ApiOperation(value = "获取品类规划信息详细信息", notes = "获取品类规划信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevProductPlan.class))
    @PostMapping("/getInfo")
    @PreAuthorize(hasPermi = "ems:product:plan:getInfo")
    public AjaxResult getInfo(Long productPlanSid) {
                    if(productPlanSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(devProductPlanService.selectDevProductPlanById(productPlanSid));
    }

    /**
     * 新增品类规划信息
     */
    @ApiOperation(value = "新增品类规划信息", notes = "新增品类规划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @PreAuthorize(hasPermi = "ems:product:plan:add")
    public AjaxResult add(@RequestBody @Valid DevProductPlan devProductPlan) {
        return toAjax(devProductPlanService.insertDevProductPlan(devProductPlan));
    }

    /**
     * 修改品类规划信息
     */
    @ApiOperation(value = "修改品类规划信息", notes = "修改品类规划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @PreAuthorize(hasPermi = "ems:product:plan:edit")
    public AjaxResult edit(@RequestBody DevProductPlan devProductPlan) {
        return toAjax(devProductPlanService.updateDevProductPlan(devProductPlan));
    }

    /**
     * 变更品类规划信息
     */
    @ApiOperation(value = "变更品类规划信息", notes = "变更品类规划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    @PreAuthorize(hasPermi = "ems:product:plan:change")
    public AjaxResult change(@RequestBody DevProductPlan devProductPlan) {
        return toAjax(devProductPlanService.changeDevProductPlan(devProductPlan));
    }

    /**
     * 删除品类规划信息
     */
    @ApiOperation(value = "删除品类规划信息", notes = "删除品类规划信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    @PreAuthorize(hasPermi = "ems:product:plan:delete")
    public AjaxResult remove(@RequestBody List<Long>  productPlanSids) {
        if(CollectionUtils.isEmpty( productPlanSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(devProductPlanService.deleteDevProductPlanByIds(productPlanSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    @PreAuthorize(hasPermi = "ems:product:plan:changeStatus")
    public AjaxResult changeStatus(@RequestBody DevProductPlan devProductPlan) {
        return AjaxResult.success(devProductPlanService.changeStatus(devProductPlan));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "品类规划信息", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @PreAuthorize(hasPermi = "ems:product:plan:check")
    public AjaxResult check(@RequestBody DevProductPlan devProductPlan) {
        return toAjax(devProductPlanService.check(devProductPlan));
    }

}
