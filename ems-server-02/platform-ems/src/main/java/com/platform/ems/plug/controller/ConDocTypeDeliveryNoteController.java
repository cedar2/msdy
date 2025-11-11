package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConDocTypeDeliveryNote;
import com.platform.ems.plug.service.IConDocTypeDeliveryNoteService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 单据类型_采购交货单/销售发货单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/docType/deliveryNote")
@Api(tags = "单据类型_采购交货单/销售发货单")
public class ConDocTypeDeliveryNoteController extends BaseController {

    @Autowired
    private IConDocTypeDeliveryNoteService conDocTypeDeliveryNoteService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询单据类型_采购交货单/销售发货单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询单据类型_采购交货单/销售发货单列表", notes = "查询单据类型_采购交货单/销售发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeDeliveryNote.class))
    public TableDataInfo list(@RequestBody ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        startPage(conDocTypeDeliveryNote);
        List<ConDocTypeDeliveryNote> list = conDocTypeDeliveryNoteService.selectConDocTypeDeliveryNoteList(conDocTypeDeliveryNote);
        return getDataTable(list);
    }

    /**
     * 导出单据类型_采购交货单/销售发货单列表
     */
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出单据类型_采购交货单/销售发货单列表", notes = "导出单据类型_采购交货单/销售发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConDocTypeDeliveryNote conDocTypeDeliveryNote) throws IOException {
        List<ConDocTypeDeliveryNote> list = conDocTypeDeliveryNoteService.selectConDocTypeDeliveryNoteList(conDocTypeDeliveryNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConDocTypeDeliveryNote> util = new ExcelUtil<>(ConDocTypeDeliveryNote.class, dataMap);
        util.exportExcel(response, list, "单据类型_采购交货单(销售发货单)");
    }

    /**
     * 导入单据类型_采购交货单/销售发货单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入单据类型_采购交货单/销售发货单", notes = "导入单据类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConDocTypeDeliveryNote> util = new ExcelUtil<>(ConDocTypeDeliveryNote.class);
        List<ConDocTypeDeliveryNote> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conDocTypeDeliveryNote -> {
                conDocTypeDeliveryNoteService.insertConDocTypeDeliveryNote(conDocTypeDeliveryNote);
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


    @ApiOperation(value = "下载单据类型_采购交货单/销售发货单导入模板", notes = "下载单据类型_采购交货单/销售发货单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConDocTypeDeliveryNote> util = new ExcelUtil<>(ConDocTypeDeliveryNote.class);
        util.importTemplateExcel(response, "单据类型_采购交货单/销售发货单导入模板");
    }


    /**
     * 获取单据类型_采购交货单/销售发货单详细信息
     */
    @ApiOperation(value = "获取单据类型_采购交货单/销售发货单详细信息", notes = "获取单据类型_采购交货单/销售发货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeDeliveryNote.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conDocTypeDeliveryNoteService.selectConDocTypeDeliveryNoteById(sid));
    }

    /**
     * 新增单据类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "新增单据类型_采购交货单/销售发货单", notes = "新增单据类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return toAjax(conDocTypeDeliveryNoteService.insertConDocTypeDeliveryNote(conDocTypeDeliveryNote));
    }

    /**
     * 修改单据类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "修改单据类型_采购交货单/销售发货单", notes = "修改单据类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return toAjax(conDocTypeDeliveryNoteService.updateConDocTypeDeliveryNote(conDocTypeDeliveryNote));
    }

    /**
     * 变更单据类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "变更单据类型_采购交货单/销售发货单", notes = "变更单据类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return toAjax(conDocTypeDeliveryNoteService.changeConDocTypeDeliveryNote(conDocTypeDeliveryNote));
    }

    /**
     * 删除单据类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "删除单据类型_采购交货单/销售发货单", notes = "删除单据类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conDocTypeDeliveryNoteService.deleteConDocTypeDeliveryNoteByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        return AjaxResult.success(conDocTypeDeliveryNoteService.changeStatus(conDocTypeDeliveryNote));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "单据类型_采购交货单/销售发货单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        conDocTypeDeliveryNote.setConfirmDate(new Date());
        conDocTypeDeliveryNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conDocTypeDeliveryNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conDocTypeDeliveryNoteService.check(conDocTypeDeliveryNote));
    }

    /**
     * 单据类型_采购交货单/销售发货单下拉列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "单据类型_采购交货单/销售发货单下拉列表", notes = "单据类型_采购交货单/销售发货单下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConDocTypeDeliveryNote.class))
    public AjaxResult getList(@RequestBody ConDocTypeDeliveryNote conDocTypeDeliveryNote) {
        conDocTypeDeliveryNote.setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conDocTypeDeliveryNoteService.getList(conDocTypeDeliveryNote));
    }
}
