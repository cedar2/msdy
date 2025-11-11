package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.QuaRawmatCheck;
import com.platform.ems.service.IQuaRawmatCheckService;
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
 * 面辅料检测单-主Controller
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@RestController
@RequestMapping("/rawmat/check")
@Api(tags = "面辅料检测单-主")
public class QuaRawmatCheckController extends BaseController {

    @Autowired
    private IQuaRawmatCheckService quaRawmatCheckService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询面辅料检测单-主列表
     */
//    @PreAuthorize(hasPermi = "ems:rawmat:check:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询面辅料检测单-主列表", notes = "查询面辅料检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaRawmatCheck.class))
    public TableDataInfo list(@RequestBody QuaRawmatCheck quaRawmatCheck) {
        startPage(quaRawmatCheck);
        List<QuaRawmatCheck> list = quaRawmatCheckService.selectQuaRawmatCheckList(quaRawmatCheck);
        return getDataTable(list);
    }

    /**
     * 导出面辅料检测单-主列表
     */
//    @PreAuthorize(hasPermi = "ems:rawmat:check:export")
    @Log(title = "面辅料检测单-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出面辅料检测单-主列表", notes = "导出面辅料检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuaRawmatCheck quaRawmatCheck) throws IOException {
        List<QuaRawmatCheck> list = quaRawmatCheckService.selectQuaRawmatCheckList(quaRawmatCheck);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaRawmatCheck> util = new ExcelUtil<>(QuaRawmatCheck.class, dataMap);
        util.exportExcel(response, list, "面辅料检测单_" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取面辅料检测单-主详细信息
     */
    @ApiOperation(value = "获取面辅料检测单-主详细信息", notes = "获取面辅料检测单-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaRawmatCheck.class))
//    @PreAuthorize(hasPermi = "ems:rawmat:check:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long rawmatCheckSid) {
        if (rawmatCheckSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(quaRawmatCheckService.selectQuaRawmatCheckById(rawmatCheckSid));
    }

    /**
     * 新增面辅料检测单-主
     */
    @ApiOperation(value = "新增面辅料检测单-主", notes = "新增面辅料检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:rawmat:check:add")
    @Log(title = "面辅料检测单-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid QuaRawmatCheck quaRawmatCheck) {
        return toAjax(quaRawmatCheckService.insertQuaRawmatCheck(quaRawmatCheck));
    }

    /**
     * 修改面辅料检测单-主
     */
    @ApiOperation(value = "修改面辅料检测单-主", notes = "修改面辅料检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:rawmat:check:edit")
    @Log(title = "面辅料检测单-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid QuaRawmatCheck quaRawmatCheck) {
        return toAjax(quaRawmatCheckService.updateQuaRawmatCheck(quaRawmatCheck));
    }

    /**
     * 变更面辅料检测单-主
     */
    @ApiOperation(value = "变更面辅料检测单-主", notes = "变更面辅料检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:rawmat:check:change")111
    @Log(title = "面辅料检测单-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid QuaRawmatCheck quaRawmatCheck) {
        return toAjax(quaRawmatCheckService.changeQuaRawmatCheck(quaRawmatCheck));
    }

    /**
     * 删除面辅料检测单-主
     */
    @ApiOperation(value = "删除面辅料检测单-主", notes = "删除面辅料检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:rawmat:check:remove")
    @Log(title = "面辅料检测单-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> rawmatCheckSids) {
        if (CollectionUtils.isEmpty(rawmatCheckSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(quaRawmatCheckService.deleteQuaRawmatCheckByIds(rawmatCheckSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
//    @PreAuthorize(hasPermi = "ems:rawmat:check:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "面辅料检测单-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody QuaRawmatCheck quaRawmatCheck) {
        return toAjax(quaRawmatCheckService.check(quaRawmatCheck));
    }

}
