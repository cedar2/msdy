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
import com.platform.ems.domain.QuaSpecraftCheck;
import com.platform.ems.service.IQuaSpecraftCheckService;
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
 * 特殊工艺检测单-主Controller
 *
 * @author linhongwei
 * @date 2022-04-12
 */
@RestController
@RequestMapping("/specraft/check")
@Api(tags = "特殊工艺检测单-主")
public class QuaSpecraftCheckController extends BaseController {

    @Autowired
    private IQuaSpecraftCheckService quaSpecraftCheckService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询特殊工艺检测单-主列表
     */
    @PreAuthorize(hasPermi = "ems:specraft:check:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询特殊工艺检测单-主列表", notes = "查询特殊工艺检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaSpecraftCheck.class))
    public TableDataInfo list(@RequestBody QuaSpecraftCheck quaSpecraftCheck) {
        startPage(quaSpecraftCheck);
        List<QuaSpecraftCheck> list = quaSpecraftCheckService.selectQuaSpecraftCheckList(quaSpecraftCheck);
        return getDataTable(list);
    }

    /**
     * 导出特殊工艺检测单-主列表
     */
    @PreAuthorize(hasPermi = "ems:specraft:check:export")
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出特殊工艺检测单-主列表", notes = "导出特殊工艺检测单-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, QuaSpecraftCheck quaSpecraftCheck) throws IOException {
        List<QuaSpecraftCheck> list = quaSpecraftCheckService.selectQuaSpecraftCheckList(quaSpecraftCheck);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<QuaSpecraftCheck> util = new ExcelUtil<>(QuaSpecraftCheck.class, dataMap);
        util.exportExcel(response, list, "特殊工艺检测单_" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取特殊工艺检测单-主详细信息
     */
    @ApiOperation(value = "获取特殊工艺检测单-主详细信息", notes = "获取特殊工艺检测单-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = QuaSpecraftCheck.class))
    @PreAuthorize(hasPermi = "ems:specraft:check:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long specraftCheckSid) {
        if (specraftCheckSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(quaSpecraftCheckService.selectQuaSpecraftCheckById(specraftCheckSid));
    }

    /**
     * 新增特殊工艺检测单-主
     */
    @ApiOperation(value = "新增特殊工艺检测单-主", notes = "新增特殊工艺检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:specraft:check:add")
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid QuaSpecraftCheck quaSpecraftCheck) {
        return toAjax(quaSpecraftCheckService.insertQuaSpecraftCheck(quaSpecraftCheck));
    }

    /**
     * 修改特殊工艺检测单-主
     */
    @ApiOperation(value = "修改特殊工艺检测单-主", notes = "修改特殊工艺检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:specraft:check:edit")
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid QuaSpecraftCheck quaSpecraftCheck) {
        return toAjax(quaSpecraftCheckService.updateQuaSpecraftCheck(quaSpecraftCheck));
    }

    /**
     * 变更特殊工艺检测单-主
     */
    @ApiOperation(value = "变更特殊工艺检测单-主", notes = "变更特殊工艺检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:specraft:check:change")
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid QuaSpecraftCheck quaSpecraftCheck) {
        return toAjax(quaSpecraftCheckService.changeQuaSpecraftCheck(quaSpecraftCheck));
    }

    /**
     * 删除特殊工艺检测单-主
     */
    @ApiOperation(value = "删除特殊工艺检测单-主", notes = "删除特殊工艺检测单-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:specraft:check:remove")
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> specraftCheckSids) {
        if (CollectionUtils.isEmpty(specraftCheckSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(quaSpecraftCheckService.deleteQuaSpecraftCheckByIds(specraftCheckSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:specraft:check:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "特殊工艺检测单-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody QuaSpecraftCheck quaSpecraftCheck) {
        return toAjax(quaSpecraftCheckService.check(quaSpecraftCheck));
    }

}
