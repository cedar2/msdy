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
import com.platform.ems.domain.DelOutsourceDeliveryNote;
import com.platform.ems.domain.DelOutsourceDeliveryNoteItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IDelOutsourceDeliveryNoteItemService;
import com.platform.ems.service.IDelOutsourceDeliveryNoteService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 外发加工收料单Controller
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@RestController
@RequestMapping("/outsource/delivery/note")
@Api(tags = "外发加工收料单")
public class DelOutsourceDeliveryNoteController extends BaseController {

    @Autowired
    private IDelOutsourceDeliveryNoteService delOutsourceDeliveryNoteService;
    @Autowired
    private IDelOutsourceDeliveryNoteItemService delOutsourceDeliveryNoteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询外发加工收料单列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询外发加工收料单列表", notes = "查询外发加工收料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceDeliveryNote.class))
    public TableDataInfo list(@RequestBody DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        startPage(delOutsourceDeliveryNote);
        List<DelOutsourceDeliveryNote> list = delOutsourceDeliveryNoteService.selectDelOutsourceDeliveryNoteList(delOutsourceDeliveryNote);
        return getDataTable(list);
    }

    /**
     * 导出外发加工收料单列表
     */
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:export")
    @Log(title = "外发加工收料单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工收料单列表", notes = "导出外发加工收料单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelOutsourceDeliveryNote delOutsourceDeliveryNote) throws IOException {
        List<DelOutsourceDeliveryNote> list = delOutsourceDeliveryNoteService.selectDelOutsourceDeliveryNoteList(delOutsourceDeliveryNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DelOutsourceDeliveryNote> util = new ExcelUtil<>(DelOutsourceDeliveryNote.class, dataMap);
        util.exportExcel(response, list, "外发加工收料单");
    }

    /**
     * 导入外发加工收料单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入外发加工收料单", notes = "导入外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<DelOutsourceDeliveryNote> util = new ExcelUtil<>(DelOutsourceDeliveryNote.class);
        List<DelOutsourceDeliveryNote> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(delOutsourceDeliveryNote -> {
                delOutsourceDeliveryNoteService.insertDelOutsourceDeliveryNote(delOutsourceDeliveryNote);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载外发加工收料单导入模板", notes = "下载外发加工收料单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<DelOutsourceDeliveryNote> util = new ExcelUtil<>(DelOutsourceDeliveryNote.class);
        util.importTemplateExcel(response, "外发加工收料单导入模板");
    }


    /**
     * 获取外发加工收料单详细信息
     */
    @ApiOperation(value = "获取外发加工收料单详细信息", notes = "获取外发加工收料单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceDeliveryNote.class))
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deliveryNoteSid) {
        if (deliveryNoteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delOutsourceDeliveryNoteService.selectDelOutsourceDeliveryNoteById(deliveryNoteSid));
    }

    /**
     * 新增外发加工收料单
     */
    @ApiOperation(value = "新增外发加工收料单", notes = "新增外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:add")
    @Log(title = "外发加工收料单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        return toAjax(delOutsourceDeliveryNoteService.insertDelOutsourceDeliveryNote(delOutsourceDeliveryNote));
    }

    /**
     * 修改外发加工收料单
     */
    @ApiOperation(value = "修改外发加工收料单", notes = "修改外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:edit")
    @Log(title = "外发加工收料单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        return toAjax(delOutsourceDeliveryNoteService.updateDelOutsourceDeliveryNote(delOutsourceDeliveryNote));
    }

    /**
     * 变更外发加工收料单
     */
    @ApiOperation(value = "变更外发加工收料单", notes = "变更外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:change")
    @Log(title = "外发加工收料单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        return toAjax(delOutsourceDeliveryNoteService.changeDelOutsourceDeliveryNote(delOutsourceDeliveryNote));
    }

    /**
     * 删除外发加工收料单
     */
    @ApiOperation(value = "删除外发加工收料单", notes = "删除外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:remove")
    @Log(title = "外发加工收料单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> deliveryNoteSids) {
        if (CollectionUtils.isEmpty(deliveryNoteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(delOutsourceDeliveryNoteService.deleteDelOutsourceDeliveryNoteByIds(deliveryNoteSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:check")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "外发加工收料单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody DelOutsourceDeliveryNote delOutsourceDeliveryNote) {
        delOutsourceDeliveryNote.setConfirmDate(new Date());
        delOutsourceDeliveryNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        delOutsourceDeliveryNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(delOutsourceDeliveryNoteService.check(delOutsourceDeliveryNote));
    }

    /**
     * 外发加工收料单明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:item:list")
    @PostMapping("/getItemList")
    @ApiOperation(value = "外发加工收料单明细报表", notes = "外发加工收料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelOutsourceDeliveryNoteItem.class))
    public TableDataInfo getItemList(@RequestBody DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        startPage(delOutsourceDeliveryNoteItem);
        List<DelOutsourceDeliveryNoteItem> list = delOutsourceDeliveryNoteItemService.getItemList(delOutsourceDeliveryNoteItem);
        return getDataTable(list);
    }

    /**
     * 导出外发加工收料单明细报表
     */
    @PreAuthorize(hasPermi = "ems:outsource:delivery:note:item:export")
    @Log(title = "外发加工收料单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出外发加工收料单明细报表", notes = "导出外发加工收料单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) throws IOException {
        List<DelOutsourceDeliveryNoteItem> list = delOutsourceDeliveryNoteItemService.getItemList(delOutsourceDeliveryNoteItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DelOutsourceDeliveryNoteItem> util = new ExcelUtil<>(DelOutsourceDeliveryNoteItem.class, dataMap);
        util.exportExcel(response, list, "外发加工收料单明细报表" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 提交前校验-外发加工收料单
     */
    @ApiOperation(value = "提交前校验-外发加工收料单", notes = "提交前校验-外发加工收料单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long deliveryNoteSid, String handleStatus) {
        if (deliveryNoteSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(delOutsourceDeliveryNoteService.verify(deliveryNoteSid, handleStatus));
    }
}
