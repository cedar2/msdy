package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
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
import com.platform.ems.domain.ManManufactureCompleteNote;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IManManufactureCompleteNoteService;
import com.platform.ems.service.ISystemDictDataService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产完工确认单Controller
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/manufacture/complete/note")
@Api(tags = "生产完工确认单")
public class ManManufactureCompleteNoteController extends BaseController {

    @Autowired
    private IManManufactureCompleteNoteService manManufactureCompleteNoteService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产完工确认单列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产完工确认单列表", notes = "查询生产完工确认单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureCompleteNote.class))
    public TableDataInfo list(@RequestBody ManManufactureCompleteNote manManufactureCompleteNote) {
        startPage(manManufactureCompleteNote);
        List<ManManufactureCompleteNote> list = manManufactureCompleteNoteService.selectManManufactureCompleteNoteList(manManufactureCompleteNote);
        return getDataTable(list);
    }

    /**
     * 导出生产完工确认单列表
     */
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:export")
    @Log(title = "生产完工确认单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产完工确认单列表", notes = "导出生产完工确认单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureCompleteNote manManufactureCompleteNote) throws IOException {
        List<ManManufactureCompleteNote> list = manManufactureCompleteNoteService.selectManManufactureCompleteNoteList(manManufactureCompleteNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureCompleteNote> util = new ExcelUtil<>(ManManufactureCompleteNote.class, dataMap);
        util.exportExcel(response, list, "生产完工确认单");
    }


    /**
     * 获取生产完工确认单详细信息
     */
    @ApiOperation(value = "获取生产完工确认单详细信息", notes = "获取生产完工确认单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureCompleteNote.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long manufactureCompleteNoteSid) {
        if (manufactureCompleteNoteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureCompleteNoteService.selectManManufactureCompleteNoteById(manufactureCompleteNoteSid));
    }

    /**
     * 新增生产完工确认单
     */
    @ApiOperation(value = "新增生产完工确认单", notes = "新增生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:add")
    @Log(title = "生产完工确认单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureCompleteNote manManufactureCompleteNote) {
        return toAjax(manManufactureCompleteNoteService.insertManManufactureCompleteNote(manManufactureCompleteNote));
    }

    /**
     * 修改生产完工确认单
     */
    @ApiOperation(value = "修改生产完工确认单", notes = "修改生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:edit")
    @Log(title = "生产完工确认单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureCompleteNote manManufactureCompleteNote) {
        return toAjax(manManufactureCompleteNoteService.updateManManufactureCompleteNote(manManufactureCompleteNote));
    }

    /**
     * 变更生产完工确认单
     */
    @ApiOperation(value = "变更生产完工确认单", notes = "变更生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:change")
    @Log(title = "生产完工确认单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManManufactureCompleteNote manManufactureCompleteNote) {
        return toAjax(manManufactureCompleteNoteService.changeManManufactureCompleteNote(manManufactureCompleteNote));
    }

    /**
     * 删除生产完工确认单
     */
    @ApiOperation(value = "删除生产完工确认单", notes = "删除生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:remove")
    @Log(title = "生产完工确认单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> manufactureCompleteNoteSids) {
        if (ArrayUtil.isEmpty(manufactureCompleteNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureCompleteNoteService.deleteManManufactureCompleteNoteByIds(manufactureCompleteNoteSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产完工确认单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManManufactureCompleteNote manManufactureCompleteNote) {
        manManufactureCompleteNote.setConfirmDate(new Date());
        manManufactureCompleteNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        manManufactureCompleteNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(manManufactureCompleteNoteService.check(manManufactureCompleteNote));
    }

    /**
     * 作废-生产完工确认单
     */
    @ApiOperation(value = "作废生产完工确认单", notes = "作废生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:manufacture:complete:note:cancellation")
    @Log(title = "生产完工确认单", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    public AjaxResult cancellation(Long manufactureCompleteNoteSid) {
        if (manufactureCompleteNoteSid == null) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureCompleteNoteService.cancellationManufactureCompleteNoteById(manufactureCompleteNoteSid));
    }

    /**
     * 提交前校验-生产完工确认单
     */
    @ApiOperation(value = "提交前校验-生产完工确认单", notes = "提交前校验-生产完工确认单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long manufactureCompleteNoteSid, String handleStatus) {
        if (manufactureCompleteNoteSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(manManufactureCompleteNoteService.verify(manufactureCompleteNoteSid, handleStatus));
    }
}
