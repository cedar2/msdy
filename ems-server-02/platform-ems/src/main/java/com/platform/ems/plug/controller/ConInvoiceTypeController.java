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
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.util.ArrayUtil;

import javax.validation.Valid;

import com.platform.ems.plug.domain.ConInvoiceType;
import com.platform.ems.plug.service.IConInvoiceTypeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 发票类型Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/invoice/type")
@Api(tags = "发票类型")
public class ConInvoiceTypeController extends BaseController {

    @Autowired
    private IConInvoiceTypeService conInvoiceTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询发票类型列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询发票类型列表", notes = "查询发票类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceType.class))
    public TableDataInfo list(@RequestBody ConInvoiceType conInvoiceType) {
        startPage(conInvoiceType);
        List<ConInvoiceType> list = conInvoiceTypeService.selectConInvoiceTypeList(conInvoiceType);
        return getDataTable(list);
    }

    /**
     * 导出发票类型列表
     */
    @ApiOperation(value = "导出发票类型列表", notes = "导出发票类型列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConInvoiceType conInvoiceType) throws IOException {
        List<ConInvoiceType> list = conInvoiceTypeService.selectConInvoiceTypeList(conInvoiceType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConInvoiceType> util = new ExcelUtil<>(ConInvoiceType.class, dataMap);
        util.exportExcel(response, list, "发票类型");
    }

    /**
     * 获取发票类型详细信息
     */
    @ApiOperation(value = "获取发票类型详细信息", notes = "获取发票类型详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conInvoiceTypeService.selectConInvoiceTypeById(sid));
    }

    /**
     * 新增发票类型
     */
    @ApiOperation(value = "新增发票类型", notes = "新增发票类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConInvoiceType conInvoiceType) {
        return toAjax(conInvoiceTypeService.insertConInvoiceType(conInvoiceType));
    }

    /**
     * 修改发票类型
     */
    @ApiOperation(value = "修改发票类型", notes = "修改发票类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConInvoiceType conInvoiceType) {
        return toAjax(conInvoiceTypeService.updateConInvoiceType(conInvoiceType));
    }

    /**
     * 变更发票类型
     */
    @ApiOperation(value = "变更发票类型", notes = "变更发票类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConInvoiceType conInvoiceType) {
        return toAjax(conInvoiceTypeService.changeConInvoiceType(conInvoiceType));
    }

    /**
     * 删除发票类型
     */
    @ApiOperation(value = "删除发票类型", notes = "删除发票类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conInvoiceTypeService.deleteConInvoiceTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConInvoiceType conInvoiceType) {
        return AjaxResult.success(conInvoiceTypeService.changeStatus(conInvoiceType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConInvoiceType conInvoiceType) {
        conInvoiceType.setConfirmDate(new Date());
        conInvoiceType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conInvoiceType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conInvoiceTypeService.check(conInvoiceType));
    }

    @PostMapping("/getConInvoiceTypeList")
    @ApiOperation(value = "发票类型下拉列表", notes = "发票类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceType.class))
    public AjaxResult getConInvoiceTypeList() {
        ConInvoiceType conInvoiceType = new ConInvoiceType();
        conInvoiceType.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setStatus(ConstantsEms.ENABLE_STATUS);
        return AjaxResult.success(conInvoiceTypeService.getConInvoiceTypeList(conInvoiceType));
    }

    @PostMapping("/getList")
    @ApiOperation(value = "发票类型下拉列表", notes = "发票类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConInvoiceType.class))
    public AjaxResult getConInvoiceTypeList(@RequestBody ConInvoiceType conInvoiceType) {
        return AjaxResult.success(conInvoiceTypeService.getConInvoiceTypeList(conInvoiceType));
    }
}
