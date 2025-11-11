package com.platform.ems.controller;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.domain.dto.response.form.DevDevelopPlanForm;
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
import com.platform.ems.domain.DevDevelopPlan;
import com.platform.ems.service.IDevDevelopPlanService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

import static java.util.stream.Collectors.toList;

/**
 * 开发计划Controller
 *
 * @author chenkw
 * @date 2022-12-08
 */
@RestController
@RequestMapping("/dev/develop/plan")
@Api(tags = "开发计划")
public class DevDevelopPlanController extends BaseController {

    @Autowired
    private IDevDevelopPlanService devDevelopPlanService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private String SORT_RULE_CATEGORY = "category";

    /**
     * 查询开发计划列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_DEVELOP_PLAN_ALL)
    @ApiOperation(value = "查询开发计划列表", notes = "查询开发计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDevelopPlan.class))
    public TableDataInfo list(@RequestBody DevDevelopPlan devDevelopPlan) {
        startPage(devDevelopPlan);
        List<DevDevelopPlan> list = devDevelopPlanService.selectDevDevelopPlanList(devDevelopPlan);
        // 品类规划详情查看开发计划排序
        if (CollUtil.isNotEmpty(list) && SORT_RULE_CATEGORY.equals(devDevelopPlan.getSortRule())) {
            list = list.stream().sorted(Comparator.comparing(DevDevelopPlan::getYear,
                    Comparator.nullsFirst(String::compareTo).thenComparingLong(Long::parseLong).reversed())
                    .thenComparing(DevDevelopPlan::getBigClassName, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(DevDevelopPlan::getMiddleClassName, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(DevDevelopPlan::getSmallClassName, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                    .thenComparing(DevDevelopPlan::getGroupType, Comparator.nullsFirst(String::compareTo))).collect(toList());
        }
        return getDataTable(list);
    }

    /**
     * 导出开发计划列表
     */
    @Log(title = "开发计划", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_DEVELOP_PLAN_ALL, loc = 1)
    @ApiOperation(value = "导出开发计划列表", notes = "导出开发计划列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevDevelopPlan devDevelopPlan) throws IOException {
        List<DevDevelopPlan> list = devDevelopPlanService.selectDevDevelopPlanList(devDevelopPlan);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevDevelopPlan> util = new ExcelUtil<>(DevDevelopPlan.class, dataMap);
        util.exportExcel(response, list, "开发计划");
    }

    /**
     * 获取开发计划详细信息
     */
    @ApiOperation(value = "获取开发计划详细信息", notes = "获取开发计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDevelopPlan.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long developPlanSid) {
        if (developPlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devDevelopPlanService.selectDevDevelopPlanById(developPlanSid));
    }

    /**
     * 复制开发计划详细信息
     */
    @ApiOperation(value = "复制开发计划详细信息", notes = "复制开发计划详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDevelopPlan.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long developPlanSid) {
        if (developPlanSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devDevelopPlanService.copyDevDevelopPlanById(developPlanSid));
    }

    /**
     * 新增开发计划
     */
    @ApiOperation(value = "新增开发计划", notes = "新增开发计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid DevDevelopPlan devDevelopPlan) {
        int row = devDevelopPlanService.insertDevDevelopPlan(devDevelopPlan);
        if (row > 0) {
            return AjaxResult.success(devDevelopPlanService.selectDevDevelopPlanById(devDevelopPlan.getDevelopPlanSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改开发计划", notes = "修改开发计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid DevDevelopPlan devDevelopPlan) {
        return toAjax(devDevelopPlanService.updateDevDevelopPlan(devDevelopPlan));
    }

    /**
     * 变更开发计划
     */
    @ApiOperation(value = "变更开发计划", notes = "变更开发计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DevDevelopPlan devDevelopPlan) {
        return toAjax(devDevelopPlanService.changeDevDevelopPlan(devDevelopPlan));
    }

    /**
     * 删除开发计划
     */
    @ApiOperation(value = "删除开发计划", notes = "删除开发计划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> developPlanSids) {
        if (CollectionUtils.isEmpty(developPlanSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devDevelopPlanService.deleteDevDevelopPlanByIds(developPlanSids));
    }

    /**
     * 修改开发计划处理状态（确认）
     */
    @ApiOperation(value = "修改开发计划处理状态（确认）", notes = "修改开发计划处理状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody DevDevelopPlan devDevelopPlan) {
        if (ArrayUtil.isEmpty(devDevelopPlan.getDevelopPlanSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(devDevelopPlan.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devDevelopPlanService.check(devDevelopPlan));
    }

    /**
     * 设置开发计划负责人
     */
    @ApiOperation(value = "设置开发计划负责人", notes = "设置开发计划负责人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setLeader")
    public AjaxResult setLeader(@RequestBody DevDevelopPlan devDevelopPlan) {
        return toAjax(devDevelopPlanService.setLeader(devDevelopPlan));
    }

    /**
     * 查询开发项目报表
     */
    @PostMapping("/form")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL)
    @ApiOperation(value = "查询开发项目报表", notes = "查询开发项目报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevDevelopPlanForm.class))
    public TableDataInfo form(@RequestBody DevDevelopPlanForm request) {
        startPage(request);
        List<DevDevelopPlanForm> list = devDevelopPlanService.selectDevDevelopPlanForm(request);
        return getDataTable(list);
    }

    /**
     * 导出开发项目报表
     */
    @ApiOperation(value = "导出开发项目报表", notes = "导出开发项目报表")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PROJECT_ALL, loc = 1)
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/form/export")
    public void toExpireExport(HttpServletResponse response, DevDevelopPlanForm devDevelopPlanForm) throws IOException {
        List<DevDevelopPlanForm> list = devDevelopPlanService.selectDevDevelopPlanForm(devDevelopPlanForm);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevDevelopPlanForm> util = new ExcelUtil<>(DevDevelopPlanForm.class, dataMap);
        util.exportExcel(response, list, "开发项目报表");
    }

    @ApiOperation(value = "修改品类规划", notes = "修改品类规划")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "开发计划", businessType = BusinessType.UPDATE)
    @PostMapping("/update/categoryPlan")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult updateCategoryPlan(@RequestBody DevDevelopPlan devDevelopPlan) {
        return toAjax(devDevelopPlanService.updateCategoryPlan(devDevelopPlan));
    }
}
