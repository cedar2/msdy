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
import com.platform.ems.domain.CosProductCostLabor;
import com.platform.ems.service.ICosProductCostLaborService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 商品成本核算-工价成本明细Controller
 *
 * @author qhq
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/cost/labor")
@Api(tags = "商品成本核算-工价成本明细")
public class CosProductCostLaborController extends BaseController {

    @Autowired
    private ICosProductCostLaborService cosProductCostLaborService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询商品成本核算-工价成本明细列表
     * @PreAuthorize(hasPermi = "ems:labor:list")
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品成本核算-工价成本明细列表", notes = "查询商品成本核算-工价成本明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostLabor.class))
    public TableDataInfo list(@RequestBody CosProductCostLabor cosProductCostLabor) {
        startPage(cosProductCostLabor);
        List<CosProductCostLabor> list = cosProductCostLaborService.selectCosProductCostLaborList(cosProductCostLabor);
        return getDataTable(list);
    }

    /**
     * 导出商品成本核算-工价成本明细列表
     * @PreAuthorize(hasPermi = "ems:labor:export")
     */
    @Log(title = "商品成本核算-工价成本明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品成本核算-工价成本明细列表", notes = "导出商品成本核算-工价成本明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosProductCostLabor cosProductCostLabor) throws IOException {
        List<CosProductCostLabor> list = cosProductCostLaborService.selectCosProductCostLaborList(cosProductCostLabor);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<CosProductCostLabor> util = new ExcelUtil<CosProductCostLabor>(CosProductCostLabor.class,dataMap);
        util.exportExcel(response, list, "商品成本核算-工价成本明细");
    }

    /**
     * 获取商品成本核算-工价成本明细详细信息
     * @PreAuthorize(hasPermi = "ems:labor:query")
     */
    @ApiOperation(value = "获取商品成本核算-工价成本明细详细信息", notes = "获取商品成本核算-工价成本明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostLabor.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productCostLaborSid) {
        if(productCostLaborSid==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosProductCostLaborService.selectCosProductCostLaborById(productCostLaborSid));
    }

    /**
     * 新增商品成本核算-工价成本明细
     * @PreAuthorize(hasPermi = "ems:labor:add")
     */
    @ApiOperation(value = "新增商品成本核算-工价成本明细", notes = "新增商品成本核算-工价成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品成本核算-工价成本明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid CosProductCostLabor cosProductCostLabor) {
        return toAjax(cosProductCostLaborService.insertCosProductCostLabor(cosProductCostLabor));
    }

    /**
     * 修改商品成本核算-工价成本明细
     * @PreAuthorize(hasPermi = "ems:labor:edit")
     */
    @ApiOperation(value = "修改商品成本核算-工价成本明细", notes = "修改商品成本核算-工价成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "商品成本核算-工价成本明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid CosProductCostLabor cosProductCostLabor) {
        return toAjax(cosProductCostLaborService.updateCosProductCostLabor(cosProductCostLabor));
    }

    /**
     * 删除商品成本核算-工价成本明细
     * @PreAuthorize(hasPermi = "ems:labor:remove")
     */
    @ApiOperation(value = "删除商品成本核算-工价成本明细", notes = "删除商品成本核算-工价成本明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品成本核算-工价成本明细", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  productCostLaborSids) {
        if(ArrayUtil.isEmpty( productCostLaborSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosProductCostLaborService.deleteCosProductCostLaborByIds(productCostLaborSids));
    }
}
