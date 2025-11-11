package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConBuTypeDeliveryNote;
import com.platform.ems.plug.service.IConBuTypeDeliveryNoteService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 业务类型_采购交货单/销售发货单Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/buType/deliveryNote")
@Api(tags = "业务类型_采购交货单/销售发货单")
public class ConBuTypeDeliveryNoteController extends BaseController {

    @Autowired
    private IConBuTypeDeliveryNoteService conBuTypeDeliveryNoteService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询业务类型_采购交货单/销售发货单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询业务类型_采购交货单/销售发货单列表", notes = "查询业务类型_采购交货单/销售发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeDeliveryNote.class))
    public TableDataInfo list(@RequestBody ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        startPage(conBuTypeDeliveryNote);
        List<ConBuTypeDeliveryNote> list = conBuTypeDeliveryNoteService.selectConBuTypeDeliveryNoteList(conBuTypeDeliveryNote);
        return getDataTable(list);
    }

    /**
     * 导出业务类型_采购交货单/销售发货单列表
     */
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出业务类型_采购交货单/销售发货单列表", notes = "导出业务类型_采购交货单/销售发货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConBuTypeDeliveryNote conBuTypeDeliveryNote) throws IOException {
        List<ConBuTypeDeliveryNote> list = conBuTypeDeliveryNoteService.selectConBuTypeDeliveryNoteList(conBuTypeDeliveryNote);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConBuTypeDeliveryNote> util = new ExcelUtil<>(ConBuTypeDeliveryNote.class, dataMap);
        util.exportExcel(response, list, "业务类型_采购交货单(销售发货单)");
    }

    /**
     * 导入业务类型_采购交货单/销售发货单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入业务类型_采购交货单/销售发货单", notes = "导入业务类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        return AjaxResult.success();
    }


    @ApiOperation(value = "下载业务类型_采购交货单/销售发货单导入模板", notes = "下载业务类型_采购交货单/销售发货单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConBuTypeDeliveryNote> util = new ExcelUtil<>(ConBuTypeDeliveryNote.class);
        util.importTemplateExcel(response, "业务类型_采购交货单/销售发货单导入模板");
    }


    /**
     * 获取业务类型_采购交货单/销售发货单详细信息
     */
    @ApiOperation(value = "获取业务类型_采购交货单/销售发货单详细信息", notes = "获取业务类型_采购交货单/销售发货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeDeliveryNote.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conBuTypeDeliveryNoteService.selectConBuTypeDeliveryNoteById(sid));
    }

    /**
     * 新增业务类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "新增业务类型_采购交货单/销售发货单", notes = "新增业务类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return toAjax(conBuTypeDeliveryNoteService.insertConBuTypeDeliveryNote(conBuTypeDeliveryNote));
    }

    /**
     * 修改业务类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "修改业务类型_采购交货单/销售发货单", notes = "修改业务类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return toAjax(conBuTypeDeliveryNoteService.updateConBuTypeDeliveryNote(conBuTypeDeliveryNote));
    }

    /**
     * 变更业务类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "变更业务类型_采购交货单/销售发货单", notes = "变更业务类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return toAjax(conBuTypeDeliveryNoteService.changeConBuTypeDeliveryNote(conBuTypeDeliveryNote));
    }

    /**
     * 删除业务类型_采购交货单/销售发货单
     */
    @ApiOperation(value = "删除业务类型_采购交货单/销售发货单", notes = "删除业务类型_采购交货单/销售发货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conBuTypeDeliveryNoteService.deleteConBuTypeDeliveryNoteByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        return AjaxResult.success(conBuTypeDeliveryNoteService.changeStatus(conBuTypeDeliveryNote));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "业务类型_采购交货单/销售发货单", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConBuTypeDeliveryNote conBuTypeDeliveryNote) {
        conBuTypeDeliveryNote.setConfirmDate(new Date());
        conBuTypeDeliveryNote.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conBuTypeDeliveryNote.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conBuTypeDeliveryNoteService.check(conBuTypeDeliveryNote));
    }

    /**
     * 业务类型_采购交货单/销售发货单下拉列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "业务类型_采购交货单/销售发货单下拉列表", notes = "业务类型_采购交货单/销售发货单下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConBuTypeDeliveryNote.class))
    public AjaxResult getList(@RequestBody ConBuTypeDeliveryNote conBuTypeDeliveryNote ) {
        return AjaxResult.success(conBuTypeDeliveryNoteService.getList(conBuTypeDeliveryNote));
    }
}
