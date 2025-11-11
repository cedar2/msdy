package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.BasMaterialCertificate;
import com.platform.ems.domain.dto.response.external.BasMaterialCertificateExternal;
import com.platform.ems.service.IBasMaterialCertificateService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
 * 商品合格证洗唛信息Controller
 *
 * @author linhongwei
 * @date 2021-03-19
 */
@RestController
@RequestMapping("/certificate")
@Api(tags = "商品合格证洗唛信息")
public class BasMaterialCertificateController extends BaseController {

    @Autowired
    private IBasMaterialCertificateService basMaterialCertificateService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询商品合格证洗唛信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品合格证洗唛信息列表", notes = "查询商品合格证洗唛信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificate.class))
    public TableDataInfo list(@RequestBody BasMaterialCertificate basMaterialCertificate) {
        startPage(basMaterialCertificate);
        List<BasMaterialCertificate> list = basMaterialCertificateService.selectBasMaterialCertificateList(basMaterialCertificate);
        return getDataTable(list);
    }

    /**
     * 导出商品合格证洗唛信息列表
     */
    @ApiOperation(value = "导出商品合格证洗唛信息列表", notes = "导出商品合格证洗唛信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialCertificate basMaterialCertificate) throws IOException {
        List<BasMaterialCertificate> list = basMaterialCertificateService.selectBasMaterialCertificateList(basMaterialCertificate);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialCertificate> util = new ExcelUtil<>(BasMaterialCertificate.class, dataMap);
        util.exportExcel(response, list, "合格证洗唛");
    }

    /**
     * 获取商品合格证洗唛信息详细信息
     */
    @ApiOperation(value = "获取商品合格证洗唛信息详细信息", notes = "获取商品合格证洗唛信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificate.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialCertificateSid) {
        return AjaxResult.success(basMaterialCertificateService.selectBasMaterialCertificateById(materialCertificateSid));
    }

    /**
     * 外部打印厂获取商品合格证洗唛信息详细信息
     */
    @ApiOperation(value = "外部打印厂获取商品合格证洗唛信息详细信息", notes = "外部打印厂获取商品合格证洗唛信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialCertificateExternal.class))
    @PostMapping("/external/getInfo")
    public AjaxResult getInfoToExternal(Long materialCertificateSid) {
        if (materialCertificateSid == null) {
            return AjaxResult.error("参数缺失");
        }
        return AjaxResult.success(basMaterialCertificateService.selectForExternalById(materialCertificateSid));
    }

    /**
     * 新增商品合格证洗唛信息
     */
    @ApiOperation(value = "新增商品合格证洗唛信息", notes = "新增商品合格证洗唛信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterialCertificate basMaterialCertificate) {
        int row = basMaterialCertificateService.insertBasMaterialCertificate(basMaterialCertificate);
        return AjaxResult.success(null, String.valueOf(basMaterialCertificate.getMaterialCertificateSid()));
    }

    /**
     * 修改商品合格证洗唛信息
     */
    @ApiOperation(value = "修改商品合格证洗唛信息", notes = "修改商品合格证洗唛信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterialCertificate basMaterialCertificate) {
        return AjaxResult.success(basMaterialCertificateService.updateBasMaterialCertificate(basMaterialCertificate));
    }

    /**
     * 删除商品合格证洗唛信息
     */
    @ApiOperation(value = "删除商品合格证洗唛信息", notes = "删除商品合格证洗唛信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] materialCertificateSidList) {
        return AjaxResult.success(basMaterialCertificateService.deleteBasMaterialCertificateByIds(materialCertificateSidList));
    }

    /**
     * 商品合格证洗唛信息确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "商品合格证洗唛信息确认", notes = "商品合格证洗唛信息确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody BasMaterialCertificate basMaterialCertificate) {
        return AjaxResult.success(basMaterialCertificateService.confirm(basMaterialCertificate));
    }

    /**
     * 商品合格证洗唛信息变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "商品合格证洗唛信息变更", notes = "商品合格证洗唛信息变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody BasMaterialCertificate basMaterialCertificate) {
        return AjaxResult.success(basMaterialCertificateService.change(basMaterialCertificate));
    }

}
