package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.TecBomAttachment;
import com.platform.ems.service.ITecBomAttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * BOM附件Controller
 *
 * @author qhq
 * @date 2021-03-15
 */
@RestController
@RequestMapping("/bom/attachment")
@Api(tags = "BOM附件")
public class TecBomAttachmentController extends BaseController {

    @Autowired
    private ITecBomAttachmentService tecBomAttachmentService;

    /**
     * 查询BOM附件列表
     * @PreAuthorize(hasPermi = "ems:attachment:list")
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询BOM附件列表", notes = "查询BOM附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomAttachment.class))
    public TableDataInfo list(@RequestBody TecBomAttachment tecBomAttachment) {
        startPage();
        List<TecBomAttachment> list = tecBomAttachmentService.selectTecBomAttachmentList(tecBomAttachment);
        return getDataTable(list);
    }

    /**
     * 导出BOM附件列表
     * @PreAuthorize(hasPermi = "ems:attachment:export")
     */
    @Log(title = "BOM附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出BOM附件列表", notes = "导出BOM附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecBomAttachment tecBomAttachment) throws IOException {
        List<TecBomAttachment> list = tecBomAttachmentService.selectTecBomAttachmentList(tecBomAttachment);
        ExcelUtil<TecBomAttachment> util = new ExcelUtil<TecBomAttachment>(TecBomAttachment.class);
        util.exportExcel(response, list, "attachment");
    }

    /**
     * 获取BOM附件详细信息
     * @PreAuthorize(hasPermi = "ems:attachment:query")
     */
    @ApiOperation(value = "获取BOM附件详细信息", notes = "获取BOM附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomAttachment.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String bomAttachmentSid) {
        return AjaxResult.success(tecBomAttachmentService.selectTecBomAttachmentById(bomAttachmentSid));
    }

    /**
     * 新增BOM附件
     * @PreAuthorize(hasPermi = "ems:attachment:add")
     */
    @ApiOperation(value = "新增BOM附件", notes = "新增BOM附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid TecBomAttachment tecBomAttachment) {
        return toAjax(tecBomAttachmentService.insertTecBomAttachment(tecBomAttachment));
    }

    /**
     * 修改BOM附件
     * @PreAuthorize(hasPermi = "ems:attachment:edit")
     */
    @ApiOperation(value = "修改BOM附件", notes = "修改BOM附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "BOM附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid TecBomAttachment tecBomAttachment) {
        return toAjax(tecBomAttachmentService.updateTecBomAttachment(tecBomAttachment));
    }

    /**
     * 删除BOM附件
     * @PreAuthorize(hasPermi = "ems:attachment:remove")
     */
    @ApiOperation(value = "删除BOM附件", notes = "删除BOM附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "BOM附件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  bomAttachmentSids) {
        return toAjax(tecBomAttachmentService.deleteTecBomAttachmentByIds(bomAttachmentSids));
    }
}
