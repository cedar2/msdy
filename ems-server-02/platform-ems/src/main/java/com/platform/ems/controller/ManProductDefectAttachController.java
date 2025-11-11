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
import com.platform.ems.domain.ManProductDefectAttach;
import com.platform.ems.service.IManProductDefectAttachService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 生产产品缺陷登记-附件Controller
 *
 * @author zhuangyz
 * @date 2022-08-04
 */
@RestController
@RequestMapping("/manProductDefectAttach")
@Api(tags = "生产产品缺陷登记-附件")
public class ManProductDefectAttachController extends BaseController {

    @Autowired
    private IManProductDefectAttachService manProductDefectAttachService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产产品缺陷登记-附件列表
     */
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产产品缺陷登记-附件列表", notes = "查询生产产品缺陷登记-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductDefectAttach.class))
    public TableDataInfo list(@RequestBody ManProductDefectAttach manProductDefectAttach) {
        startPage(manProductDefectAttach);
        List<ManProductDefectAttach> list = manProductDefectAttachService.selectManProductDefectAttachList(manProductDefectAttach);
        return getDataTable(list);
    }

    /**
     * 导出生产产品缺陷登记-附件列表
     */
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:export")
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产产品缺陷登记-附件列表", notes = "导出生产产品缺陷登记-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManProductDefectAttach manProductDefectAttach) throws IOException {
        List<ManProductDefectAttach> list = manProductDefectAttachService.selectManProductDefectAttachList(manProductDefectAttach);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManProductDefectAttach> util = new ExcelUtil<>(ManProductDefectAttach.class, dataMap);
        util.exportExcel(response, list, "生产产品缺陷登记-附件");
    }


    /**
     * 获取生产产品缺陷登记-附件详细信息
     */
    @ApiOperation(value = "获取生产产品缺陷登记-附件详细信息", notes = "获取生产产品缺陷登记-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManProductDefectAttach.class))
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long attachSid) {
        if (attachSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manProductDefectAttachService.selectManProductDefectAttachById(attachSid));
    }

    /**
     * 新增生产产品缺陷登记-附件
     */
    @ApiOperation(value = "新增生产产品缺陷登记-附件", notes = "新增生产产品缺陷登记-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:add")
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid ManProductDefectAttach manProductDefectAttach) {
        return toAjax(manProductDefectAttachService.insertManProductDefectAttach(manProductDefectAttach));
    }

    @ApiOperation(value = "修改生产产品缺陷登记-附件", notes = "修改生产产品缺陷登记-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:edit")
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody ManProductDefectAttach manProductDefectAttach) {
        return toAjax(manProductDefectAttachService.updateManProductDefectAttach(manProductDefectAttach));
    }

    /**
     * 变更生产产品缺陷登记-附件
     */
    @ApiOperation(value = "变更生产产品缺陷登记-附件", notes = "变更生产产品缺陷登记-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:change")
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManProductDefectAttach manProductDefectAttach) {
        return toAjax(manProductDefectAttachService.changeManProductDefectAttach(manProductDefectAttach));
    }

    /**
     * 删除生产产品缺陷登记-附件
     */
    @ApiOperation(value = "删除生产产品缺陷登记-附件", notes = "删除生产产品缺陷登记-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:remove")
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> attachSids) {
        if (CollectionUtils.isEmpty(attachSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manProductDefectAttachService.deleteManProductDefectAttachByIds(attachSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ManProductDefectAttach manProductDefectAttach) {
        return AjaxResult.success(manProductDefectAttachService.changeStatus(manProductDefectAttach));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manProductDefectAttach:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产产品缺陷登记-附件", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody ManProductDefectAttach manProductDefectAttach) {
        return toAjax(manProductDefectAttachService.check(manProductDefectAttach));
    }

}
