package com.platform.ems.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.CosProductCostLaborRequest;
import com.platform.ems.domain.dto.request.CosProductCostMaterialRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CosProductCostLaborResponse;
import com.platform.ems.domain.dto.response.CosProductCostMaterialResponse;
import com.platform.ems.domain.dto.response.export.CosProductCostSaleExport;
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
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.CosProductCost;
import com.platform.ems.domain.CosProductCostLaborOther;
import com.platform.ems.service.ICosProductCostService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 商品成本核算主Controller
 *
 * @author qhq
 * @date 2021-04-02
 */
@RestController
@RequestMapping("/cost")
@Api(tags = "商品成本核算主")
public class CosProductCostController extends BaseController {

    @Autowired
    private ICosProductCostService cosProductCostService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private final static String BUSS_TYPE_PC = "PC";

    private final static String BUSS_TYPE_SC = "SC";

    /**
     * 查询商品成本核算主列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品成本核算主列表", notes = "查询商品成本核算主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCost.class))
    public TableDataInfo list(@RequestBody CosProductCost cosProductCost) {
        startPage(cosProductCost);
        List<CosProductCost> list = cosProductCostService.selectCosProductCostList(cosProductCost);
        return getDataTable(list);
    }

    @PostMapping("/Product/material/report")
    @ApiOperation(value = "查询商品成本物料明细报表", notes = "查询商品成本物料明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostMaterialResponse.class))
    public TableDataInfo materialReport(@RequestBody CosProductCostMaterialRequest cosProductCost) {
        startPage(cosProductCost);
        List<CosProductCostMaterialResponse> list = cosProductCostService.reportMaterialList(cosProductCost);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出商品成本物料明细报表", notes = "导出商品成本物料明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/Product/material/export")
    public void exportMaterialReport(HttpServletResponse response, CosProductCostMaterialRequest cosProductCost) throws IOException {
        List<CosProductCostMaterialResponse> list = cosProductCostService.reportMaterialList(cosProductCost);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<CosProductCostMaterialResponse> util = new ExcelUtil<>(CosProductCostMaterialResponse.class,dataMap);
        util.exportExcel(response, list, "商品成本物料明细报表");
    }

    @PostMapping("/product/labor/report")
    @ApiOperation(value = "查询商品成本工价明细报表", notes = "查询商品成本工价明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCostLaborResponse.class))
    public TableDataInfo laborReport(@RequestBody CosProductCostLaborRequest cosProductCostLaborRequest) {
        startPage(cosProductCostLaborRequest);
        List<CosProductCostLaborResponse> list = cosProductCostService.reportProductCostLabor(cosProductCostLaborRequest);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出商品成本工价明细报表", notes = "导出商品成本工价明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/product/labor/export")
    public void exportlaborReport(HttpServletResponse response, CosProductCostLaborRequest cosProductCostLaborRequest) throws IOException {
        List<CosProductCostLaborResponse> list = cosProductCostService.reportProductCostLabor(cosProductCostLaborRequest);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<CosProductCostLaborResponse> util = new ExcelUtil<>(CosProductCostLaborResponse.class,dataMap);
        util.exportExcel(response, list, "商品成本工价明细报表");
    }

    /**
     * 导出商品成本核算主列表
     */
    @ApiOperation(value = "导出商品成本核算主列表", notes = "导出商品成本核算主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosProductCost cosProductCost) throws IOException {
        List<CosProductCost> list = cosProductCostService.selectCosProductCostList(cosProductCost);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        if (BUSS_TYPE_PC.equals(cosProductCost.getBusinessType())) {
            ExcelUtil<CosProductCost> util = new ExcelUtil<>(CosProductCost.class,dataMap);
            util.exportExcel(response, list, "采购成本核算");
        }
        else if (BUSS_TYPE_SC.equals(cosProductCost.getBusinessType())) {
            ExcelUtil<CosProductCostSaleExport> util = new ExcelUtil<>(CosProductCostSaleExport.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, CosProductCostSaleExport::new), "销售成本核算");
        }
    }

    /**
     * 获取商品成本核算主详细信息
     * 传入商品sid
     */
    @ApiOperation(value = "获取商品成本核算主详细信息", notes = "获取商品成本核算主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCost.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productCostSid) {
        if(productCostSid==null){
            throw new CheckedException("所需参数不可为空！");
        }
        return AjaxResult.success(cosProductCostService.selectCosProductCostById(productCostSid));
    }

    @ApiOperation(value = "更新清单列价格", notes = "更新清单列价格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCost.class))
    @PostMapping("/update/price")
    public AjaxResult updatePrice(Long productCostSid) {
        if(productCostSid==null){
            throw new CheckedException("所需参数不可为空！");
        }
        return AjaxResult.success(cosProductCostService.updatePrice(productCostSid));
    }

    @ApiOperation(value = "更新清单列", notes = "更新清单列")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = CosProductCost.class))
    @PostMapping("/update/bom")
    public AjaxResult updateBom(Long bomSid) {
        if(bomSid==null){
            throw new CheckedException("所需参数不可为空！");
        }
        return AjaxResult.success(cosProductCostService.updateBom(bomSid));
    }

    /**
     * 新增商品成本核算主
     */
    @ApiOperation(value = "新增商品成本核算主", notes = "新增商品成本核算主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid CosProductCost cost) {
        return toAjax(cosProductCostService.insertCosProductCost(cost));
    }

    /**
     * 修改商品成本核算主
     */
    @ApiOperation(value = "修改商品成本核算主", notes = "修改商品成本核算主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid CosProductCost cost) {
        return toAjax(cosProductCostService.updateCosProductCost(cost));
    }

    @ApiOperation(value = "更新成本价", notes = "更新成本价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/update/innerPriceTax")
    public AjaxResult updateInner(@RequestBody  CosProductCost cost) {
        return AjaxResult.success(cosProductCostService.updateCostPrice(cost));
    }
    /**
     * 删除商品成本核算主
     */
    @ApiOperation(value = "删除商品成本核算主", notes = "删除商品成本核算主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  materialSids) {
        if(ArrayUtil.isEmpty( materialSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(cosProductCostService.deleteCosProductCostByIds(materialSids));
    }

    /**
     * 确认商品成本核算主
     */
    @PostMapping("/handleStatus")
    @ApiOperation(value = "确认商品成本核算", notes = "确认商品成本核算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult handleStatus(@RequestBody List<Long> materialSids) {
    	if(ArrayUtil.isEmpty( materialSids)){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosProductCostService.handleStatus(materialSids));
    }

    @PostMapping("/processCheck")
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult processCheck(@RequestBody OrderErrRequest request) {
        if(request==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosProductCostService.processCheck(request));
    }

    /**
     * 啟停用
     */
    @PostMapping("/status")
    @ApiOperation(value = "启停商品成本核算", notes = "启停商品成本核算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody List<Long> materialSids , String status) {
    	if(ArrayUtil.isEmpty( materialSids)){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(cosProductCostService.status(materialSids,status));
    }

    /**
     * 根据商品code返回新增信息模板
     * 新建的时候输入商品编码应该调用该接口
     */
    @PostMapping("/getInsertInfo")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult getInsertInfo(@RequestBody BasMaterial basMaterial) {
    	return AjaxResult.success(cosProductCostService.getInsertInfo(basMaterial));
    }

    /**
     * 通过序列号查询出成本核算其他项
     */
    @ApiOperation(value = "通过序列号查询出成本核算其他项", notes = "通过序列号查询出成本核算其他项")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/getByNumOther")
    public  AjaxResult getByNum(int serialNum){
        List<CosProductCostLaborOther> list = cosProductCostService.getByNum(serialNum);
        return AjaxResult.success(list);
    }
}
