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
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.CosProductCostBom;
import com.platform.ems.service.ICosProductCostBomService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 商品成本核算-BOM主Controller
 *
 * @author qhq
 * @date 2021-04-25
 */
@RestController
@RequestMapping("/cost/bom")
@Api(tags = "商品成本核算-BOM主")
public class CosProductCostBomController extends BaseController {

    @Autowired
    private ICosProductCostBomService cosProductCostBomService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询商品成本核算-BOM主列表
     */
    @PreAuthorize(hasPermi = "ems:bom:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品成本核算-BOM主列表", notes = "查询商品成本核算-BOM主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostBom.class))
    public TableDataInfo list(@RequestBody CosProductCostBom cosProductCostBom) {
        startPage();
        List<CosProductCostBom> list = cosProductCostBomService.selectCosProductCostBomList(cosProductCostBom);
        return getDataTable(list);
    }

    /**
     * 导出商品成本核算-BOM主列表
     */
    @PreAuthorize(hasPermi = "ems:bom:export")
    @Log(title = "商品成本核算-BOM主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品成本核算-BOM主列表", notes = "导出商品成本核算-BOM主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosProductCostBom cosProductCostBom) throws IOException {
        List<CosProductCostBom> list = cosProductCostBomService.selectCosProductCostBomList(cosProductCostBom);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<CosProductCostBom> util = new ExcelUtil<CosProductCostBom>(CosProductCostBom.class,dataMap);
        util.exportExcel(response, list, "商品成本核算-BOM主");
    }

    /**
     * 获取商品成本核算-BOM主详细信息
     */
    @ApiOperation(value = "获取商品成本核算-BOM主详细信息", notes = "获取商品成本核算-BOM主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostBom.class))
    @PreAuthorize(hasPermi = "ems:bom:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productCostBomSid) {
                    if(productCostBomSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(cosProductCostBomService.selectCosProductCostBomById(productCostBomSid));
    }

    /**
     * 新增商品成本核算-BOM主
     */
    @ApiOperation(value = "新增商品成本核算-BOM主", notes = "新增商品成本核算-BOM主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:add")
    @Log(title = "商品成本核算-BOM主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid CosProductCostBom cosProductCostBom) {
        return toAjax(cosProductCostBomService.insertCosProductCostBom(cosProductCostBom));
    }

    /**
     * 修改商品成本核算-BOM主
     */
    @ApiOperation(value = "修改商品成本核算-BOM主", notes = "修改商品成本核算-BOM主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:edit")
    @Log(title = "商品成本核算-BOM主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody CosProductCostBom cosProductCostBom) {
        return toAjax(cosProductCostBomService.updateCosProductCostBom(cosProductCostBom));
    }

    /**
     * 删除商品成本核算-BOM主
     */
    @ApiOperation(value = "删除商品成本核算-BOM主", notes = "删除商品成本核算-BOM主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:bom:remove")
    @Log(title = "商品成本核算-BOM主", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  productCostBomSids) {
        if(ArrayUtil.isEmpty( productCostBomSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosProductCostBomService.deleteCosProductCostBomByIds(productCostBomSids));
    }
}
