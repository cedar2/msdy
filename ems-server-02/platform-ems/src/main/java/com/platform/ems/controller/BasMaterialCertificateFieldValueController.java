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
import com.platform.ems.domain.BasMaterialCertificateFieldValue;
import com.platform.ems.service.IBasMaterialCertificateFieldValueService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品合格证洗唛自定义字段-值Controller
 *
 * @author linhongwei
 * @date 2021-03-20
 */
@RestController
@RequestMapping("/value")
@Api(tags = "商品合格证洗唛自定义字段-值")
public class BasMaterialCertificateFieldValueController extends BaseController {

    @Autowired
    private IBasMaterialCertificateFieldValueService basMaterialCertificateFieldValueService;

    /**
     * 查询商品合格证洗唛自定义字段-值列表
     */
    @PreAuthorize(hasPermi = "ems:value:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品合格证洗唛自定义字段-值列表", notes = "查询商品合格证洗唛自定义字段-值列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateFieldValue.class))
    public TableDataInfo list(@RequestBody BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        startPage();
        List<BasMaterialCertificateFieldValue> list = basMaterialCertificateFieldValueService.selectBasMaterialCertificateFieldValueList(basMaterialCertificateFieldValue);
        return getDataTable(list);
    }

    /**
     * 导出商品合格证洗唛自定义字段-值列表
     */
    @PreAuthorize(hasPermi = "ems:value:export")
    @Log(title = "商品合格证洗唛自定义字段-值", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品合格证洗唛自定义字段-值列表", notes = "导出商品合格证洗唛自定义字段-值列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) throws IOException {
        List<BasMaterialCertificateFieldValue> list = basMaterialCertificateFieldValueService.selectBasMaterialCertificateFieldValueList(basMaterialCertificateFieldValue);
        ExcelUtil<BasMaterialCertificateFieldValue> util = new ExcelUtil<BasMaterialCertificateFieldValue>(BasMaterialCertificateFieldValue.class);
        util.exportExcel(response, list, "商品合格证洗唛自定义字段-值");
    }

    /**
     * 获取商品合格证洗唛自定义字段-值详细信息
     */
    @ApiOperation(value = "获取商品合格证洗唛自定义字段-值详细信息", notes = "获取商品合格证洗唛自定义字段-值详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateFieldValue.class))
    @PreAuthorize(hasPermi = "ems:value:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        return AjaxResult.success(basMaterialCertificateFieldValueService.selectBasMaterialCertificateFieldValueById(clientId));
    }

    /**
     * 新增商品合格证洗唛自定义字段-值
     */
    @ApiOperation(value = "新增商品合格证洗唛自定义字段-值", notes = "新增商品合格证洗唛自定义字段-值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:value:add")
    @Log(title = "商品合格证洗唛自定义字段-值", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        return toAjax(basMaterialCertificateFieldValueService.insertBasMaterialCertificateFieldValue(basMaterialCertificateFieldValue));
    }

    /**
     * 修改商品合格证洗唛自定义字段-值
     */
    @ApiOperation(value = "修改商品合格证洗唛自定义字段-值", notes = "修改商品合格证洗唛自定义字段-值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:value:edit")
    @Log(title = "商品合格证洗唛自定义字段-值", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialCertificateFieldValue basMaterialCertificateFieldValue) {
        return toAjax(basMaterialCertificateFieldValueService.updateBasMaterialCertificateFieldValue(basMaterialCertificateFieldValue));
    }

    /**
     * 删除商品合格证洗唛自定义字段-值
     */
    @ApiOperation(value = "删除商品合格证洗唛自定义字段-值", notes = "删除商品合格证洗唛自定义字段-值")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:value:remove")
    @Log(title = "商品合格证洗唛自定义字段-值", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        return toAjax(basMaterialCertificateFieldValueService.deleteBasMaterialCertificateFieldValueByIds(clientIds));
    }
}
