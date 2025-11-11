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
import com.platform.ems.domain.CosProductCostMaterial;
import com.platform.ems.service.ICosProductCostMaterialService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 商品成本核算-物料成本明细Controller
 *
 * @author qhq
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/cost/material")
@Api(tags = "商品成本核算-物料成本明细")
public class CosProductCostMaterialController extends BaseController {

    @Autowired
    private ICosProductCostMaterialService cosProductCostMaterialService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询商品成本核算-物料成本明细列表
     * @PreAuthorize(hasPermi = "ems:material:list")
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品成本核算-物料成本明细列表", notes = "查询商品成本核算-物料成本明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostMaterial.class))
    public TableDataInfo list(@RequestBody CosProductCostMaterial cosProductCostMaterial) {
        startPage(cosProductCostMaterial);
        List<CosProductCostMaterial> list = cosProductCostMaterialService.selectCosProductCostMaterialList(cosProductCostMaterial);
        return getDataTable(list);
    }

    /**
     * 导出商品成本核算-物料成本明细列表
     * @PreAuthorize(hasPermi = "ems:material:export")
     */
    @Log(title = "商品成本核算-物料成本明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品成本核算-物料成本明细列表", notes = "导出商品成本核算-物料成本明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosProductCostMaterial cosProductCostMaterial) throws IOException {
        List<CosProductCostMaterial> list = cosProductCostMaterialService.selectCosProductCostMaterialList(cosProductCostMaterial);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<CosProductCostMaterial> util = new ExcelUtil<CosProductCostMaterial>(CosProductCostMaterial.class,dataMap);
        util.exportExcel(response, list, "商品成本核算-物料成本明细");
    }

    /**
     * 获取商品成本核算-物料成本明细详细信息
     * @PreAuthorize(hasPermi = "ems:material:query")
     */
    @ApiOperation(value = "获取商品成本核算-物料成本明细详细信息", notes = "获取商品成本核算-物料成本明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostMaterial.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productCostMaterialSid) {
        if(productCostMaterialSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosProductCostMaterialService.selectCosProductCostMaterialById(productCostMaterialSid));
    }

    /**
     * 新增商品成本核算-物料成本明细
     * @PreAuthorize(hasPermi = "ems:material:add")
     */
    @ApiOperation(value = "新增商品成本核算-物料成本明细", notes = "新增商品成本核算-物料成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品成本核算-物料成本明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid CosProductCostMaterial cosProductCostMaterial) {
        return toAjax(cosProductCostMaterialService.insertCosProductCostMaterial(cosProductCostMaterial));
    }

    /**
     * 修改商品成本核算-物料成本明细
     * @PreAuthorize(hasPermi = "ems:material:edit")
     */
    @ApiOperation(value = "修改商品成本核算-物料成本明细", notes = "修改商品成本核算-物料成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "商品成本核算-物料成本明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid CosProductCostMaterial cosProductCostMaterial) {
        return toAjax(cosProductCostMaterialService.updateCosProductCostMaterial(cosProductCostMaterial));
    }

    /**
     * 删除商品成本核算-物料成本明细
     * @PreAuthorize(hasPermi = "ems:material:remove")
     */
    @ApiOperation(value = "删除商品成本核算-物料成本明细", notes = "删除商品成本核算-物料成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品成本核算-物料成本明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  productCostMaterialSids) {
        if(ArrayUtil.isEmpty( productCostMaterialSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosProductCostMaterialService.deleteCosProductCostMaterialByIds(productCostMaterialSids));
    }
}
