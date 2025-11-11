package com.platform.ems.controller;

import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.TecBomSizeQuantity;
import com.platform.ems.domain.dto.request.TecBomSizeAddRequest;
import com.platform.ems.domain.dto.request.TecBomSizeSkuInsertRequest;
import com.platform.ems.domain.dto.request.TecBomSizeUpdateRequest;
import com.platform.ems.domain.dto.response.TecBomSizeRequestResponse;
import com.platform.ems.service.ITecBomSizeQuantityService;
import com.platform.common.utils.bean.BeanCopyUtils;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 物料清单（BOM）组件具体尺码用量Controller
 *
 * @author qhq
 * @date 2021-03-15
 */
@RestController
@RequestMapping("/quantity")
@Api(tags = "物料清单（BOM）组件具体尺码用量")
public class TecBomSizeQuantityController extends BaseController {

    @Autowired
    private ITecBomSizeQuantityService tecBomSizeQuantityService;

    /**
     * 查询物料清单（BOM）组件具体尺码用量列表
     */
    @PreAuthorize(hasPermi = "ems:quantity:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询物料清单（BOM）组件具体尺码用量列表", notes = "查询物料清单（BOM）组件具体尺码用量列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomSizeQuantity.class))
    public TableDataInfo list(@RequestBody TecBomSizeQuantity tecBomSizeQuantity) {
        startPage();
        List<TecBomSizeQuantity> list = tecBomSizeQuantityService.selectTecBomSizeQuantityList(tecBomSizeQuantity);
        return getDataTable(list);
    }

    /**
     * 导出物料清单（BOM）组件具体尺码用量列表
     */
    @PreAuthorize(hasPermi = "ems:quantity:export")
    @Log(title = "物料清单（BOM）组件具体尺码用量", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料清单（BOM）组件具体尺码用量列表", notes = "导出物料清单（BOM）组件具体尺码用量列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecBomSizeQuantity tecBomSizeQuantity) throws IOException {
        List<TecBomSizeQuantity> list = tecBomSizeQuantityService.selectTecBomSizeQuantityList(tecBomSizeQuantity);
        ExcelUtil<TecBomSizeQuantity> util = new ExcelUtil<TecBomSizeQuantity>(TecBomSizeQuantity.class);
        util.exportExcel(response, list, "quantity");
    }

    /**
     * 获取物料清单（BOM）组件具体尺码用量详细信息
     */
    @ApiOperation(value = "获取物料清单（BOM）组件具体尺码用量详细信息", notes = "获取物料清单（BOM）组件具体尺码用量详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomSizeQuantity.class))
//    @PreAuthorize(hasPermi = "ems:quantity:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody  List<TecBomSizeSkuInsertRequest> request) {
        List<TecBomSizeQuantity> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(request)){
            list=BeanCopyUtils.copyListProperties(request, TecBomSizeQuantity::new);
        }else{
            throw new CustomException("没有参数值");
        }
        List<TecBomSizeQuantity> listTecBomSize = tecBomSizeQuantityService.selectTecBomSizeQuantityById(list);
        return AjaxResult.success( BeanCopyUtils.copyListProperties(listTecBomSize, TecBomSizeRequestResponse::new));
    }

    /**
     * 新增物料清单（BOM）组件具体尺码用量
     */
    @ApiOperation(value = "新增物料清单（BOM）组件具体尺码用量", notes = "新增物料清单（BOM）组件具体尺码用量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:quantity:add")
    @Log(title = "物料清单（BOM）组件具体尺码用量", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid List<TecBomSizeAddRequest> request) {
        if(CollectionUtils.isEmpty(request)){
            throw new CustomException("没有参数值");
        }
        List<TecBomSizeQuantity> list = BeanCopyUtils.copyListProperties(request, TecBomSizeQuantity::new);
        return toAjax(tecBomSizeQuantityService.insertTecBomSizeQuantity(list));
    }

    /**
     * 修改物料清单（BOM）组件具体尺码用量
     */
    @ApiOperation(value = "修改物料清单（BOM）组件具体尺码用量", notes = "修改物料清单（BOM）组件具体尺码用量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
//    @PreAuthorize(hasPermi = "ems:quantity:edit")
    @Log(title = "物料清单（BOM）组件具体尺码用量", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid List<TecBomSizeUpdateRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            throw new CustomException("没有参数值");
        }
        List<TecBomSizeQuantity> list = BeanCopyUtils.copyListProperties(request, TecBomSizeQuantity::new);
        return toAjax(tecBomSizeQuantityService.updateTecBomSizeQuantity(list));
    }

    /**
     * 删除物料清单（BOM）组件具体尺码用量
     */
    @ApiOperation(value = "删除物料清单（BOM）组件具体尺码用量", notes = "删除物料清单（BOM）组件具体尺码用量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:quantity:remove")
    @Log(title = "物料清单（BOM）组件具体尺码用量", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String>  clientIds) {
        return toAjax(tecBomSizeQuantityService.deleteTecBomSizeQuantityByIds(clientIds));
    }
}
