package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManManufactureOrderComponent;
import com.platform.ems.service.IManManufactureOrderComponentService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 生产订单-组件Controller
 *
 * @author qhq
 * @date 2021-04-13
 */
@RestController
@RequestMapping("/manManufactureOrderComponent")
@Api(tags = "生产订单-组件")
public class ManManufactureOrderComponentController extends BaseController {

    @Autowired
    private IManManufactureOrderComponentService manManufactureOrderComponentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产订单-组件列表
     */
//    @PreAuthorize(hasPermi = "ems:component:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询生产订单-组件列表", notes = "查询生产订单-组件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderComponent.class))
    public TableDataInfo list(@RequestBody ManManufactureOrderComponent manManufactureOrderComponent) {
        startPage();
        List<ManManufactureOrderComponent> list = manManufactureOrderComponentService.selectManManufactureOrderComponentList(manManufactureOrderComponent);
        return getDataTable(list);
    }

    /**
     * 导出生产订单-组件列表
     */
//    @PreAuthorize(hasPermi = "ems:component:export")
    @Log(title = "生产订单-组件", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产订单-组件列表", notes = "导出生产订单-组件列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManManufactureOrderComponent manManufactureOrderComponent) throws IOException {
        List<ManManufactureOrderComponent> list = manManufactureOrderComponentService.selectManManufactureOrderComponentList(manManufactureOrderComponent);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManManufactureOrderComponent> util = new ExcelUtil<ManManufactureOrderComponent>(ManManufactureOrderComponent.class, dataMap);
        util.exportExcel(response, list, "生产订单-组件");
    }

    /**
     * 获取生产订单-组件详细信息
     */
    @ApiOperation(value = "获取生产订单-组件详细信息", notes = "获取生产订单-组件详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrderComponent.class))
//    @PreAuthorize(hasPermi = "ems:component:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String manufactureOrderComponentSid) {
        if (StrUtil.isEmpty(manufactureOrderComponentSid)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manManufactureOrderComponentService.selectManManufactureOrderComponentById(manufactureOrderComponentSid));
    }

    /**
     * 新增生产订单-组件
     */
    @ApiOperation(value = "新增生产订单-组件", notes = "新增生产订单-组件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:component:add")
    @Log(title = "生产订单-组件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManManufactureOrderComponent manManufactureOrderComponent) {
        return toAjax(manManufactureOrderComponentService.insertManManufactureOrderComponent(manManufactureOrderComponent));
    }

    /**
     * 修改生产订单-组件
     */
    @ApiOperation(value = "修改生产订单-组件", notes = "修改生产订单-组件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:component:edit")
    @Log(title = "生产订单-组件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManManufactureOrderComponent manManufactureOrderComponent) {
        return toAjax(manManufactureOrderComponentService.updateManManufactureOrderComponent(manManufactureOrderComponent));
    }

    /**
     * 删除生产订单-组件
     */
    @ApiOperation(value = "删除生产订单-组件", notes = "删除生产订单-组件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:component:remove")
    @Log(title = "生产订单-组件", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> manufactureOrderComponentSids) {
        if (ArrayUtil.isEmpty(manufactureOrderComponentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manManufactureOrderComponentService.deleteManManufactureOrderComponentByIds(manufactureOrderComponentSids));
    }
}
