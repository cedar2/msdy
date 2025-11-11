package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import javax.validation.Valid;
import com.platform.ems.domain.BasMaterialCertificateAttachment;
import com.platform.ems.service.IBasMaterialCertificateAttachmentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品合格证洗唛-附件Controller
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@RestController
@RequestMapping("/certificate/attachment")
@Api(tags = "商品合格证洗唛-附件")
public class BasMaterialCertificateAttachmentController extends BaseController {

    @Autowired
    private IBasMaterialCertificateAttachmentService basMaterialCertificateAttachmentService;

    /**
     * 查询商品合格证洗唛-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品合格证洗唛-附件列表", notes = "查询商品合格证洗唛-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateAttachment.class))
    public TableDataInfo list(@RequestBody BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        startPage();
        List<BasMaterialCertificateAttachment> list = basMaterialCertificateAttachmentService.selectBasMaterialCertificateAttachmentList(basMaterialCertificateAttachment);
        return getDataTable(list);
    }

    /**
     * 导出商品合格证洗唛-附件列表
     */
    @PreAuthorize(hasPermi = "ems:attachment:export")
    @Log(title = "商品合格证洗唛-附件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品合格证洗唛-附件列表", notes = "导出商品合格证洗唛-附件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialCertificateAttachment basMaterialCertificateAttachment) throws IOException {
        List<BasMaterialCertificateAttachment> list = basMaterialCertificateAttachmentService.selectBasMaterialCertificateAttachmentList(basMaterialCertificateAttachment);
        ExcelUtil<BasMaterialCertificateAttachment> util = new ExcelUtil<BasMaterialCertificateAttachment>(BasMaterialCertificateAttachment.class);
        util.exportExcel(response, list, "商品合格证洗唛-附件");
    }

    /**
     * 获取商品合格证洗唛-附件详细信息
     */
    @ApiOperation(value = "获取商品合格证洗唛-附件详细信息", notes = "获取商品合格证洗唛-附件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateAttachment.class))
    @PreAuthorize(hasPermi = "ems:attachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String materialCertificateAttachmentSid) {
        return AjaxResult.success(basMaterialCertificateAttachmentService.selectBasMaterialCertificateAttachmentById(materialCertificateAttachmentSid));
    }

    /**
     * 新增商品合格证洗唛-附件
     */
    @ApiOperation(value = "新增商品合格证洗唛-附件", notes = "新增商品合格证洗唛-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:add")
    @Log(title = "商品合格证洗唛-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        return toAjax(basMaterialCertificateAttachmentService.insertBasMaterialCertificateAttachment(basMaterialCertificateAttachment));
    }

    /**
     * 修改商品合格证洗唛-附件
     */
    @ApiOperation(value = "修改商品合格证洗唛-附件", notes = "修改商品合格证洗唛-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:edit")
    @Log(title = "商品合格证洗唛-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialCertificateAttachment basMaterialCertificateAttachment) {
        return toAjax(basMaterialCertificateAttachmentService.updateBasMaterialCertificateAttachment(basMaterialCertificateAttachment));
    }

    /**
     * 删除商品合格证洗唛-附件
     */
    @ApiOperation(value = "删除商品合格证洗唛-附件", notes = "删除商品合格证洗唛-附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:attachment:remove")
    @Log(title = "商品合格证洗唛-附件", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  materialCertificateAttachmentSids) {
        return toAjax(basMaterialCertificateAttachmentService.deleteBasMaterialCertificateAttachmentByIds(materialCertificateAttachmentSids));
    }
}
