package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
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

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.ManProduceWeekProgressTotal;
import com.platform.ems.service.IManProduceWeekProgressTotalService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产周进度汇总Controller
 *
 * @author linhongwei
 * @date 2022-08-26
 */
@RestController
@RequestMapping("/man/produce/week/progress/total")
@Api(tags = "生产周进度汇总")
public class ManProduceWeekProgressTotalController extends BaseController {

    @Autowired
    private IManProduceWeekProgressTotalService manProduceWeekProgressTotalService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产周进度汇总列表
     */
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产周进度汇总列表", notes = "查询生产周进度汇总列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceWeekProgressTotal.class))
    public TableDataInfo list(@RequestBody ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        startPage(manProduceWeekProgressTotal);
        List<ManProduceWeekProgressTotal> list = manProduceWeekProgressTotalService.selectManProduceWeekProgressTotalList(manProduceWeekProgressTotal);
        return getDataTable(list);
    }

    /**
     * 导出生产周进度汇总列表
     */
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:export")
    @Log(title = "生产周进度汇总", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产周进度汇总列表", notes = "导出生产周进度汇总列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProduceWeekProgressTotal manProduceWeekProgressTotal) throws IOException {
        List<ManProduceWeekProgressTotal> list = manProduceWeekProgressTotalService.selectManProduceWeekProgressTotalList(manProduceWeekProgressTotal);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProduceWeekProgressTotal> util = new ExcelUtil<>(ManProduceWeekProgressTotal.class, dataMap);
        util.exportExcel(response, list, "生产周进度汇总");
    }


    /**
     * 获取生产周进度汇总详细信息
     */
    @ApiOperation(value = "获取生产周进度汇总详细信息", notes = "获取生产周进度汇总详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProduceWeekProgressTotal.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long weekProgressTotalSid) {
        if (weekProgressTotalSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProduceWeekProgressTotalService.selectManProduceWeekProgressTotalById(weekProgressTotalSid));
    }

    /**
     * 新增生产周进度汇总
     */
    @ApiOperation(value = "新增生产周进度汇总", notes = "新增生产周进度汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:add")
    @Log(title = "生产周进度汇总", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        return toAjax(manProduceWeekProgressTotalService.insertManProduceWeekProgressTotal(manProduceWeekProgressTotal));
    }

    @ApiOperation(value = "修改生产周进度汇总", notes = "修改生产周进度汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:edit")
    @Log(title = "生产周进度汇总", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        return toAjax(manProduceWeekProgressTotalService.updateManProduceWeekProgressTotal(manProduceWeekProgressTotal));
    }

    /**
     * 变更生产周进度汇总
     */
    @ApiOperation(value = "变更生产周进度汇总", notes = "变更生产周进度汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:change")
    @Log(title = "生产周进度汇总", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProduceWeekProgressTotal manProduceWeekProgressTotal) {
        return toAjax(manProduceWeekProgressTotalService.changeManProduceWeekProgressTotal(manProduceWeekProgressTotal));
    }

    /**
     * 删除生产周进度汇总
     */
    @ApiOperation(value = "删除生产周进度汇总", notes = "删除生产周进度汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:remove")
    @Log(title = "生产周进度汇总", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> weekProgressTotalSids) {
        if (CollectionUtils.isEmpty(weekProgressTotalSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProduceWeekProgressTotalService.deleteManProduceWeekProgressTotalByIds(weekProgressTotalSids));
    }

    @ApiOperation(value = "重新汇总", notes = "重新汇总")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:man:produce:week:progress:total:refresh")
    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestBody List<Long> weekProgressTotalSids) {
        if (CollectionUtils.isEmpty(weekProgressTotalSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProduceWeekProgressTotalService.refreshManProduceWeekProgressTotalByIds(weekProgressTotalSids));
    }
}
