package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Idempotent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import com.platform.ems.domain.BasMaterialCertificateField;
import com.platform.ems.service.IBasMaterialCertificateFieldService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品合格证洗唛自定义字段Controller
 *
 * @author linhongwei
 * @date 2021-03-31
 */
@RestController
@RequestMapping("/material/certificate/field")
@Api(tags = "商品合格证洗唛自定义字段")
public class BasMaterialCertificateFieldController extends BaseController {

    @Autowired
    private IBasMaterialCertificateFieldService basMaterialCertificateFieldService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品合格证洗唛自定义字段列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品合格证洗唛自定义字段列表", notes = "查询商品合格证洗唛自定义字段列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateField.class))
    public TableDataInfo list(@RequestBody BasMaterialCertificateField field) {
        startPage(field);
        List<BasMaterialCertificateField> list = basMaterialCertificateFieldService.selectBasMaterialCertificateFieldList(field);
        return getDataTable(list);
    }

    /**
     * 导出商品合格证洗唛自定义字段列表
     */
    @Log(title = "商品合格证洗唛自定义字段", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品合格证洗唛自定义字段列表", notes = "导出商品合格证洗唛自定义字段列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialCertificateField field) throws IOException {
        List<BasMaterialCertificateField> list = basMaterialCertificateFieldService.selectBasMaterialCertificateFieldList(field);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialCertificateField> util = new ExcelUtil<>(BasMaterialCertificateField.class,dataMap);
        util.exportExcel(response, list, "合格证洗唛自定义字段");
    }

    /**
     * 获取商品合格证洗唛自定义字段详细信息
     */
    @ApiOperation(value = "获取商品合格证洗唛自定义字段详细信息", notes = "获取商品合格证洗唛自定义字段详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateField.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialCertificateFieldSid) {
        return AjaxResult.success(basMaterialCertificateFieldService.selectBasMaterialCertificateFieldById(materialCertificateFieldSid));
    }

    /**
     * 新增商品合格证洗唛自定义字段
     */
    @ApiOperation(value = "新增商品合格证洗唛自定义字段", notes = "新增商品合格证洗唛自定义字段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品合格证洗唛自定义字段", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialCertificateField basMaterialCertificateField) {
        return toAjax(basMaterialCertificateFieldService.insertBasMaterialCertificateField(basMaterialCertificateField));
    }

    /**
     * 修改商品合格证洗唛自定义字段
     */
    @ApiOperation(value = "修改商品合格证洗唛自定义字段", notes = "修改商品合格证洗唛自定义字段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "商品合格证洗唛自定义字段", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialCertificateField basMaterialCertificateField) {
        return toAjax(basMaterialCertificateFieldService.updateBasMaterialCertificateField(basMaterialCertificateField));
    }

    /**
     * 删除商品合格证洗唛自定义字段
     */
    @ApiOperation(value = "删除商品合格证洗唛自定义字段", notes = "删除商品合格证洗唛自定义字段")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品合格证洗唛自定义字段", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialCertificateFieldSids) {
        return toAjax(basMaterialCertificateFieldService.deleteBasMaterialCertificateFieldByIds(materialCertificateFieldSids));
    }

    /**
     * 修改启停状态（确认）
     */
    @ApiOperation(value = "修改启停状态（确认）", notes = "修改启停状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/status")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult status(@RequestBody BasMaterialCertificateField field) {
        if (ArrayUtil.isEmpty(field.getMaterialCertificateFieldSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(field.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basMaterialCertificateFieldService.status(field));
    }

    /**
     * 修改处理状态（确认）
     */
    @ApiOperation(value = "修改处理状态（确认）", notes = "修改处理状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody BasMaterialCertificateField field) {
        if (ArrayUtil.isEmpty(field.getMaterialCertificateFieldSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(field.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basMaterialCertificateFieldService.check(field));
    }
}
