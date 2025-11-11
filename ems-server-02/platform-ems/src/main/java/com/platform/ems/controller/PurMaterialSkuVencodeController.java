package com.platform.ems.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
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
import com.platform.ems.domain.PurMaterialSkuVencode;
import com.platform.ems.service.IPurMaterialSkuVencodeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购货源供方SKU编码Controller
 *
 * @author linhongwei
 * @date 2021-03-29
 */
@RestController
@RequestMapping("/vencode")
@Api(tags = "采购货源供方SKU编码")
public class PurMaterialSkuVencodeController extends BaseController {

    @Autowired
    private IPurMaterialSkuVencodeService purMaterialSkuVencodeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询采购货源供方SKU编码列表
     */
    @PreAuthorize(hasPermi = "ems:vencode:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询采购货源供方SKU编码列表", notes = "查询采购货源供方SKU编码列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurMaterialSkuVencode.class))
    public TableDataInfo list(@RequestBody PurMaterialSkuVencode purMaterialSkuVencode) {
        startPage();
        List<PurMaterialSkuVencode> list = purMaterialSkuVencodeService.selectPurMaterialSkuVencodeList(purMaterialSkuVencode);
        return getDataTable(list);
    }

    /**
     * 导出采购货源供方SKU编码列表
     */
    @PreAuthorize(hasPermi = "ems:vencode:export")
    @Log(title = "采购货源供方SKU编码", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购货源供方SKU编码列表", notes = "导出采购货源供方SKU编码列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurMaterialSkuVencode purMaterialSkuVencode) throws IOException {
        List<PurMaterialSkuVencode> list = purMaterialSkuVencodeService.selectPurMaterialSkuVencodeList(purMaterialSkuVencode);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurMaterialSkuVencode> util = new ExcelUtil<PurMaterialSkuVencode>(PurMaterialSkuVencode.class,dataMap);
        util.exportExcel(response, list, "采购货源供方SKU编码");
    }

    /**
     * 获取采购货源供方SKU编码详细信息
     */
    @ApiOperation(value = "获取采购货源供方SKU编码详细信息", notes = "获取采购货源供方SKU编码详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurMaterialSkuVencode.class))
    @PreAuthorize(hasPermi = "ems:vencode:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialVendorSkuSid) {
        return AjaxResult.success(purMaterialSkuVencodeService.selectPurMaterialSkuVencodeById(materialVendorSkuSid));
    }

    /**
     * 新增采购货源供方SKU编码
     */
    @ApiOperation(value = "新增采购货源供方SKU编码", notes = "新增采购货源供方SKU编码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vencode:add")
    @Log(title = "采购货源供方SKU编码", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurMaterialSkuVencode purMaterialSkuVencode) {
        return toAjax(purMaterialSkuVencodeService.insertPurMaterialSkuVencode(purMaterialSkuVencode));
    }

    /**
     * 修改采购货源供方SKU编码
     */
    @ApiOperation(value = "修改采购货源供方SKU编码", notes = "修改采购货源供方SKU编码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vencode:edit")
    @Log(title = "采购货源供方SKU编码", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PurMaterialSkuVencode purMaterialSkuVencode) {
        return toAjax(purMaterialSkuVencodeService.updatePurMaterialSkuVencode(purMaterialSkuVencode));
    }

    /**
     * 删除采购货源供方SKU编码
     */
    @ApiOperation(value = "删除采购货源供方SKU编码", notes = "删除采购货源供方SKU编码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:vencode:remove")
    @Log(title = "采购货源供方SKU编码", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  materialVendorSkuSids) {
        return toAjax(purMaterialSkuVencodeService.deletePurMaterialSkuVencodeByIds(materialVendorSkuSids));
    }
}
