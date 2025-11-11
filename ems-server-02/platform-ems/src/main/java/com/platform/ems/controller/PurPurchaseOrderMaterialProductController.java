package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;
import com.platform.ems.domain.dto.request.PurPurchaseOrderMaterialProductAddRequest;
import com.platform.ems.service.IPurPurchaseOrderMaterialProductService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 物料所用商品信息
 *
 * @author yangqz
 * @date 2022-04-20
 */
@RestController
@RequestMapping("/product")
@Api(tags = "物料所用商品信息")
public class PurPurchaseOrderMaterialProductController extends BaseController {

    @Autowired
    private IPurPurchaseOrderMaterialProductService purPurchaseOrderMaterialProductService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询undefined列表
     */
    @PreAuthorize(hasPermi = "ems:product:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询undefined列表", notes = "查询undefined列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderMaterialProduct.class))
    public TableDataInfo list(@RequestBody PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        startPage(purPurchaseOrderMaterialProduct);
        List<PurPurchaseOrderMaterialProduct> list = purPurchaseOrderMaterialProductService.selectPurPurchaseOrderMaterialProductList(purPurchaseOrderMaterialProduct);
        return getDataTable(list);
    }

    /**
     * 导出undefined列表
     */
    @PreAuthorize(hasPermi = "ems:product:export")
    @Log(title = "物料所用商品信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "物料所用商品信息", notes = "物料所用商品信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) throws IOException {
        List<PurPurchaseOrderMaterialProduct> list = purPurchaseOrderMaterialProductService.selectPurPurchaseOrderMaterialProductList(purPurchaseOrderMaterialProduct);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseOrderMaterialProduct> util = new ExcelUtil<PurPurchaseOrderMaterialProduct>(PurPurchaseOrderMaterialProduct.class,dataMap);
        util.exportExcel(response, list, "undefined列表");
    }


    /**
     * 获取物料所用商品信息细信息
     */
    @ApiOperation(value = "获取物料所用商品信息详细信息", notes = "获取物料所用商品信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderMaterialProduct.class))
//    @PreAuthorize(hasPermi = "ems:product:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseOrderItemSid) {
        if (purchaseOrderItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderMaterialProductService.selectPurPurchaseOrderMaterialProductById(purchaseOrderItemSid));
    }

    /**
     * 新增物料所用商品信息
     */
    @ApiOperation(value = "新增/编辑 物料所用商品信息", notes = "新增/编辑 物料所用商品信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:product:add")
    @Log(title = "物料所用商品信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody  PurPurchaseOrderMaterialProductAddRequest request) {
        return toAjax(purPurchaseOrderMaterialProductService.insertPurPurchaseOrderMaterialProduct(request));
    }

    /**
     * 修改undefined
     */
    @ApiOperation(value = "修改undefined", notes = "修改undefined")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:edit")
    @Log(title = "undefined", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return toAjax(purPurchaseOrderMaterialProductService.updatePurPurchaseOrderMaterialProduct(purPurchaseOrderMaterialProduct));
    }

    /**
     * 变更undefined
     */
    @ApiOperation(value = "变更undefined", notes = "变更undefined")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:change")
    @Log(title = "undefined", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return toAjax(purPurchaseOrderMaterialProductService.changePurPurchaseOrderMaterialProduct(purPurchaseOrderMaterialProduct));
    }

    /**
     * 删除undefined
     */
    @ApiOperation(value = "删除undefined", notes = "删除undefined")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:remove")
    @Log(title = "undefined", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  itemMaterialProductSids) {
        if(CollectionUtils.isEmpty( itemMaterialProductSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderMaterialProductService.deletePurPurchaseOrderMaterialProductByIds(itemMaterialProductSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "undefined", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:product:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return AjaxResult.success(purPurchaseOrderMaterialProductService.changeStatus(purPurchaseOrderMaterialProduct));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:product:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "undefined", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return toAjax(purPurchaseOrderMaterialProductService.check(purPurchaseOrderMaterialProduct));
    }

}
