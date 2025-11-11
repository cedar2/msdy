package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.base.EmsResultEntity;
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
import com.platform.ems.domain.SysDocument;
import com.platform.ems.service.ISysDocumentService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 文档管理Controller
 *
 * @author chenkw
 * @date 2023-02-13
 */
@RestController
@RequestMapping("/sys/document")
@Api(tags = "文档管理")
public class SysDocumentController extends BaseController {

    @Autowired
    private ISysDocumentService sysDocumentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询文档管理列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询文档管理列表", notes = "查询文档管理列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDocument.class))
    public TableDataInfo list(@RequestBody SysDocument sysDocument) {
        startPage(sysDocument);
        List<SysDocument> list = sysDocumentService.selectSysDocumentList(sysDocument);
        return getDataTable(list);
    }

    /**
     * 导出文档管理列表
     */
    @Log(title = "文档管理", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出文档管理列表", notes = "导出文档管理列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDocument sysDocument) throws IOException {
        List<SysDocument> list = sysDocumentService.selectSysDocumentList(sysDocument);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysDocument> util = new ExcelUtil<>(SysDocument.class, dataMap);
        util.exportExcel(response, list, "文档管理");
    }


    /**
     * 获取文档管理详细信息
     */
    @ApiOperation(value = "获取文档管理详细信息", notes = "获取文档管理详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysDocument.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long documentSid) {
        if (documentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysDocumentService.selectSysDocumentById(documentSid));
    }

    /**
     * 新增文档管理
     */
    @ApiOperation(value = "新增文档管理", notes = "新增文档管理")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文档管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SysDocument sysDocument) {
        return AjaxResult.success(sysDocumentService.insertSysDocument(sysDocument));
    }

    @ApiOperation(value = "修改文档管理", notes = "修改文档管理")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文档管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody SysDocument sysDocument) {
        EmsResultEntity result = sysDocumentService.updateSysDocument(sysDocument);
        if (!EmsResultEntity.SUCCESS_TAG.equals(result.getTag())) {
            return AjaxResult.success(sysDocumentService.updateSysDocument(sysDocument));
        }
        else {
            return AjaxResult.success(EmsResultEntity.success(sysDocumentService.selectSysDocumentById(sysDocument.getDocumentSid())));
        }
    }

    /**
     * 变更文档管理
     */
    @ApiOperation(value = "变更文档管理", notes = "变更文档管理")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文档管理", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysDocument sysDocument) {
        return toAjax(sysDocumentService.changeSysDocument(sysDocument));
    }

    /**
     * 删除文档管理
     */
    @ApiOperation(value = "删除文档管理", notes = "删除文档管理")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文档管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> documentSids) {
        if (CollectionUtils.isEmpty(documentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(sysDocumentService.deleteSysDocumentByIds(documentSids));
    }

}
