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
import com.platform.ems.domain.TecModelLine;
import com.platform.ems.domain.TecModelLinePos;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ITecModelLineService;
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
 * 版型线Controller
 *
 * @author linhongwei
 * @date 2021-10-19
 */
@RestController
@RequestMapping("/model/line")
@Api(tags = "版型线")
public class TecModelLineController extends BaseController {

    @Autowired
    private ITecModelLineService tecModelLineService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询版型线列表
     */
//    @PreAuthorize(hasPermi = "ems:model:line:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询版型线列表", notes = "查询版型线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelLine.class))
    public TableDataInfo list(@RequestBody TecModelLine tecModelLine) {
        startPage(tecModelLine);
        List<TecModelLine> list = tecModelLineService.selectTecModelLineList(tecModelLine);
        return getDataTable(list);
    }

    /**
     * 导出版型线列表
     */
    @PreAuthorize(hasPermi = "ems:model:line:export")
    @Log(title = "版型线", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出版型线列表", notes = "导出版型线列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecModelLine tecModelLine) throws IOException {
        List<TecModelLine> list = tecModelLineService.selectTecModelLineList(tecModelLine);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecModelLine> util = new ExcelUtil<>(TecModelLine.class, dataMap);
        util.exportExcel(response, list, "版型线" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }


    /**
     * 获取版型线详细信息
     */
    @ApiOperation(value = "获取版型线详细信息", notes = "获取版型线详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecModelLine.class))
    @PreAuthorize(hasPermi = "ems:model:line:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long modelSid) {
        if (modelSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecModelLineService.selectTecModelLineById(modelSid));
    }

    /**
     * 新增版型线
     */
    @ApiOperation(value = "新增版型线", notes = "新增版型线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:model:line:add")
    @Log(title = "版型线", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecModelLine tecModelLine) {
        return toAjax(tecModelLineService.insertTecModelLine(tecModelLine));
    }

    /**
     * 修改版型线
     */
    @ApiOperation(value = "修改版型线", notes = "修改版型线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:model:line:edit")
    @Log(title = "版型线", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecModelLine tecModelLine) {
        return toAjax(tecModelLineService.updateTecModelLine(tecModelLine));
    }

    /**
     * 变更版型线
     */
    @ApiOperation(value = "变更版型线", notes = "变更版型线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:model:line:change")
    @Log(title = "版型线", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid TecModelLine tecModelLine) {
        return toAjax(tecModelLineService.changeTecModelLine(tecModelLine));
    }

    /**
     * 删除版型线
     */
    @ApiOperation(value = "删除版型线", notes = "删除版型线")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:model:line:remove")
    @Log(title = "版型线", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> modelLineSids) {
        if (CollectionUtils.isEmpty(modelLineSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecModelLineService.deleteTecModelLineByIds(modelLineSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:model:line:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "版型线", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecModelLine tecModelLine) {
        return toAjax(tecModelLineService.check(tecModelLine));
    }

    /**
     * 添加线部位时校验名称是否重复
     */
    @ApiOperation(value = "添加线部位时校验名称是否重复", notes = "添加线部位时校验名称是否重复")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "添加线部位时校验名称是否重复", businessType = BusinessType.DELETE)
    @PostMapping("/verifyPosition")
    public AjaxResult verifyProcess(@RequestBody TecModelLinePos tecModelLinePos) {
        return AjaxResult.success(tecModelLineService.verifyPosition(tecModelLinePos));
    }
}
