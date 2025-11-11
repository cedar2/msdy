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
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.TecRecordFengyang;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecRecordFengyangService;
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
 * 封样记录(标准封样、产前封样)Controller
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@RestController
@RequestMapping("/fengyang")
@Api(tags = "封样记录(标准封样、产前封样)")
public class TecRecordFengyangController extends BaseController {

    @Autowired
    private ITecRecordFengyangService tecRecordFengyangService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询封样记录(标准封样、产前封样)列表
     */
    @PreAuthorize(hasPermi = "ems:fengyang:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询封样记录(标准封样、产前封样)列表", notes = "查询封样记录(标准封样、产前封样)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecRecordFengyang.class))
    public TableDataInfo list(@RequestBody TecRecordFengyang tecRecordFengyang) {
        startPage(tecRecordFengyang);
        List<TecRecordFengyang> list = tecRecordFengyangService.selectTecRecordFengyangList(tecRecordFengyang);
        return getDataTable(list);
    }

    /**
     * 导出封样记录(标准封样、产前封样)列表
     */
    @PreAuthorize(hasPermi = "ems:fengyang:export")
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出封样记录(标准封样、产前封样)列表", notes = "导出封样记录(标准封样、产前封样)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecRecordFengyang tecRecordFengyang) throws IOException {
        List<TecRecordFengyang> list = tecRecordFengyangService.selectTecRecordFengyangList(tecRecordFengyang);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecRecordFengyang> util = new ExcelUtil<>(TecRecordFengyang.class, dataMap);
        if (ConstantsEms.FENGYANG_TYPE_BZFY.equals(tecRecordFengyang.getFengyangType())) {
            util.exportExcel(response, list, "标准封样" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        } else if (ConstantsEms.FENGYANG_TYPE_CQFY.equals(tecRecordFengyang.getFengyangType())) {
            util.exportExcel(response, list, "产前封样" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        }

    }


    /**
     * 获取封样记录(标准封样、产前封样)详细信息
     */
    @ApiOperation(value = "获取封样记录(标准封样、产前封样)详细信息", notes = "获取封样记录(标准封样、产前封样)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecRecordFengyang.class))
    @PreAuthorize(hasPermi = "ems:fengyang:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordFengyangSid) {
        if (recordFengyangSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecRecordFengyangService.selectTecRecordFengyangById(recordFengyangSid));
    }

    /**
     * 新增封样记录(标准封样、产前封样)
     */
    @ApiOperation(value = "新增封样记录(标准封样、产前封样)", notes = "新增封样记录(标准封样、产前封样)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:fengyang:add")
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecRecordFengyang tecRecordFengyang) {
        return AjaxResult.success(tecRecordFengyangService.insertTecRecordFengyang(tecRecordFengyang));
    }

    /**
     * 修改封样记录(标准封样、产前封样)
     */
    @ApiOperation(value = "修改封样记录(标准封样、产前封样)", notes = "修改封样记录(标准封样、产前封样)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:fengyang:edit")
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecRecordFengyang tecRecordFengyang) {
        return toAjax(tecRecordFengyangService.updateTecRecordFengyang(tecRecordFengyang));
    }

    /**
     * 变更封样记录(标准封样、产前封样)
     */
    @ApiOperation(value = "变更封样记录(标准封样、产前封样)", notes = "变更封样记录(标准封样、产前封样)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:fengyang:change")
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecRecordFengyang tecRecordFengyang) {
        return toAjax(tecRecordFengyangService.changeTecRecordFengyang(tecRecordFengyang));
    }

    /**
     * 删除封样记录(标准封样、产前封样)
     */
    @ApiOperation(value = "删除封样记录(标准封样、产前封样)", notes = "删除封样记录(标准封样、产前封样)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:fengyang:remove")
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordFengyangSids) {
        if (CollectionUtils.isEmpty(recordFengyangSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecRecordFengyangService.deleteTecRecordFengyangByIds(recordFengyangSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:fengyang:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "封样记录(标准封样、产前封样)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecRecordFengyang tecRecordFengyang) {
        return toAjax(tecRecordFengyangService.check(tecRecordFengyang));
    }

}
