package com.platform.ems.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.annotation.PreAuthorize;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.CosCostLaborTemplate;
import com.platform.ems.service.ICosCostLaborTemplateService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 成本核算工价模板Controller
 *
 * @author qhq
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/cost/labor/template")
@Api(tags = "成本核算工价模板")
public class CosCostLaborTemplateController extends BaseController {

    @Autowired
    private ICosCostLaborTemplateService cosCostLaborTemplateService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询成本核算工价模板列表
     */
    @PreAuthorize(hasPermi = "ems:labor:template:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询成本核算工价模板列表", notes = "查询成本核算工价模板列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosCostLaborTemplate.class))
    public TableDataInfo list(@RequestBody CosCostLaborTemplate cosCostLaborTemplate) {
        startPage(cosCostLaborTemplate);
        List<CosCostLaborTemplate> list = cosCostLaborTemplateService.selectCosCostLaborTemplateList(cosCostLaborTemplate);
        return getDataTable(list);
    }

    /**
     * 导出成本核算工价模板列表
     */
    @PreAuthorize(hasPermi = "ems:labor:template:export")
    @Log(title = "成本核算工价模板", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出成本核算工价模板列表", notes = "导出成本核算工价模板列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosCostLaborTemplate cosCostLaborTemplate) throws IOException {
        List<CosCostLaborTemplate> list = cosCostLaborTemplateService.selectCosCostLaborTemplateList(cosCostLaborTemplate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<CosCostLaborTemplate> util = new ExcelUtil<>(CosCostLaborTemplate.class, dataMap);
        util.exportExcel(response, list, "成本核算工价模板");
    }


    /**
     * 获取成本核算工价模板详细信息
     */
    @ApiOperation(value = "获取成本核算工价模板详细信息", notes = "获取成本核算工价模板详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosCostLaborTemplate.class))
    @PreAuthorize(hasPermi = "ems:labor:template:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long costLaborTemplateSid) {
        if (costLaborTemplateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosCostLaborTemplateService.selectCosCostLaborTemplateById(costLaborTemplateSid));
    }

    /**
     * 新增成本核算工价模板
     */
    @ApiOperation(value = "新增成本核算工价模板", notes = "新增成本核算工价模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:labor:template:add")
    @Log(title = "成本核算工价模板", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid CosCostLaborTemplate cosCostLaborTemplate) {
        AjaxResult result = cosCostLaborTemplateService.checkUnique(cosCostLaborTemplate);
        if (result != null){
            return result;
        }
        return AjaxResult.success("操作成功", cosCostLaborTemplateService.insertCosCostLaborTemplate(cosCostLaborTemplate));
    }

    /**
     * 修改成本核算工价模板
     */
    @ApiOperation(value = "修改成本核算工价模板", notes = "修改成本核算工价模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:labor:template:edit")
    @Log(title = "成本核算工价模板", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid CosCostLaborTemplate cosCostLaborTemplate) {
        AjaxResult result = cosCostLaborTemplateService.checkUnique(cosCostLaborTemplate);
        if (result != null){
            return result;
        }
        return AjaxResult.success("操作成功", cosCostLaborTemplateService.updateCosCostLaborTemplate(cosCostLaborTemplate));
    }

    /**
     * 变更成本核算工价模板
     */
    @ApiOperation(value = "变更成本核算工价模板", notes = "变更成本核算工价模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:labor:template:change")
    @Log(title = "成本核算工价模板", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid CosCostLaborTemplate cosCostLaborTemplate) {
        AjaxResult result = cosCostLaborTemplateService.checkUnique(cosCostLaborTemplate);
        if (result != null){
            return result;
        }
        return AjaxResult.success("操作成功", cosCostLaborTemplateService.changeCosCostLaborTemplate(cosCostLaborTemplate));
    }

    /**
     * 删除成本核算工价模板
     */
    @ApiOperation(value = "删除成本核算工价模板", notes = "删除成本核算工价模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:labor:template:remove")
    @Log(title = "成本核算工价模板", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> costLaborTemplateSids) {
        if (ArrayUtil.isEmpty(costLaborTemplateSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosCostLaborTemplateService.deleteCosCostLaborTemplateByIds(costLaborTemplateSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成本核算工价模板", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:labor:template:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody CosCostLaborTemplate cosCostLaborTemplate) {
        return AjaxResult.success(cosCostLaborTemplateService.changeStatus(cosCostLaborTemplate));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:labor:template:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成本核算工价模板", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody CosCostLaborTemplate cosCostLaborTemplate) {
        cosCostLaborTemplate.setConfirmerDate(new Date());
        cosCostLaborTemplate.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        cosCostLaborTemplate.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(cosCostLaborTemplateService.check(cosCostLaborTemplate));
    }

    @ApiOperation(value = "复制接口", notes = "复制接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "成本核算工价模板复制", businessType = BusinessType.QUERY)
    @PostMapping("/copy")
    public AjaxResult copy(@RequestParam Long costLaborTemplateSid, @RequestParam boolean type) {
        if (costLaborTemplateSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosCostLaborTemplateService.copy(costLaborTemplateSid, type));
    }
}
