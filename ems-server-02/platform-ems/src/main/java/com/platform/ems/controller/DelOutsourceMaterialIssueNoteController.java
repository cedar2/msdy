package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.DelOutsourceMaterialIssueNote;
import com.platform.ems.domain.DelOutsourceMaterialIssueNoteItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IDelOutsourceMaterialIssueNoteItemService;
import com.platform.ems.service.IDelOutsourceMaterialIssueNoteService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 外发加工发料单Controller
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@RestController
@RequestMapping("/outsource/material/note")
@Api(tags = "外发加工发料单")
public class DelOutsourceMaterialIssueNoteController extends BaseController {

    @Autowired
    private IDelOutsourceMaterialIssueNoteService delOutsourceMaterialIssueNoteService;
    @Autowired
    private IDelOutsourceMaterialIssueNoteItemService delOutsourceMaterialIssueNoteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询外发加工发料单列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:material:note:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询外发加工发料单列表", notes = "查询外发加工发料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceMaterialIssueNote.class))
    public TableDataInfo list(@RequestBody DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        startPage(delOutsourceMaterialIssueNote);
        List<DelOutsourceMaterialIssueNote> list = delOutsourceMaterialIssueNoteService.selectDelOutsourceMaterialIssueNoteList(delOutsourceMaterialIssueNote);
        return getDataTable(list);
    }

    /**
     * 导出外发加工发料单列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:material:note:export")
    @Log(title = "外发加工发料单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工发料单列表", notes = "导出外发加工发料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) throws IOException {
        List<DelOutsourceMaterialIssueNote> list = delOutsourceMaterialIssueNoteService.selectDelOutsourceMaterialIssueNoteList(delOutsourceMaterialIssueNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DelOutsourceMaterialIssueNote> util = new ExcelUtil<DelOutsourceMaterialIssueNote>(DelOutsourceMaterialIssueNote.class, dataMap);
        util.exportExcel(response, list, "外发加工发料单");
    }


    /**
     * 获取外发加工发料单详细信息
     */
    @ApiOperation(value = "获取外发加工发料单详细信息", notes = "获取外发加工发料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceMaterialIssueNote.class))
    @PreAuthorize(hasPermi = "ems:outsource:material:note:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long issueNoteSid) {
        if (issueNoteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delOutsourceMaterialIssueNoteService.selectDelOutsourceMaterialIssueNoteById(issueNoteSid));
    }

    /**
     * 新增外发加工发料单
     */
    @ApiOperation(value = "新增外发加工发料单", notes = "新增外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:material:note:add")
    @Log(title = "外发加工发料单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        return toAjax(delOutsourceMaterialIssueNoteService.insertDelOutsourceMaterialIssueNote(delOutsourceMaterialIssueNote));
    }

    /**
     * 修改外发加工发料单
     */
    @ApiOperation(value = "修改外发加工发料单", notes = "修改外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:material:note:edit")
    @Log(title = "外发加工发料单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        return toAjax(delOutsourceMaterialIssueNoteService.updateDelOutsourceMaterialIssueNote(delOutsourceMaterialIssueNote));
    }

    /**
     * 变更外发加工发料单
     */
    @ApiOperation(value = "变更外发加工发料单", notes = "变更外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:material:note:change")
    @Log(title = "外发加工发料单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        return toAjax(delOutsourceMaterialIssueNoteService.changeDelOutsourceMaterialIssueNote(delOutsourceMaterialIssueNote));
    }

    /**
     * 删除外发加工发料单
     */
    @ApiOperation(value = "删除外发加工发料单", notes = "删除外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:material:note:remove")
    @Log(title = "外发加工发料单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> issueNoteSids) {
        if (CollectionUtils.isEmpty(issueNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(delOutsourceMaterialIssueNoteService.deleteDelOutsourceMaterialIssueNoteByIds(issueNoteSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:outsource:material:note:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工发料单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote) {
        delOutsourceMaterialIssueNote.setConfirmDate(new Date());
        delOutsourceMaterialIssueNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        delOutsourceMaterialIssueNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(delOutsourceMaterialIssueNoteService.check(delOutsourceMaterialIssueNote));
    }

    /**
     * 外发加工发料单明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:material:note:item:list")
    @PostMapping("/getItemList")
    @ApiOperation(value = "外发加工发料单明细报表", notes = "外发加工发料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceMaterialIssueNoteItem.class))
    public TableDataInfo getItemList(@RequestBody DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) {
        startPage(delOutsourceMaterialIssueNoteItem);
        List<DelOutsourceMaterialIssueNoteItem> list = delOutsourceMaterialIssueNoteItemService.getItemList(delOutsourceMaterialIssueNoteItem);
        return getDataTable(list);
    }

    /**
     * 导出外发加工发料单明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:material:note:item:export")
    @Log(title = "外发加工发料单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工发料单明细报表", notes = "导出外发加工发料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, DelOutsourceMaterialIssueNoteItem delOutsourceMaterialIssueNoteItem) throws IOException {
        List<DelOutsourceMaterialIssueNoteItem> list = delOutsourceMaterialIssueNoteItemService.getItemList(delOutsourceMaterialIssueNoteItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DelOutsourceMaterialIssueNoteItem> util = new ExcelUtil<>(DelOutsourceMaterialIssueNoteItem.class, dataMap);
        util.exportExcel(response, list, "外发加工发料单明细报表" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 提交前校验-外发加工发料单
     */
    @ApiOperation(value = "提交前校验-外发加工发料单", notes = "提交前校验-外发加工发料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long issueNoteSid, String handleStatus) {
        if (issueNoteSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(delOutsourceMaterialIssueNoteService.verify(issueNoteSid, handleStatus));
    }
}
