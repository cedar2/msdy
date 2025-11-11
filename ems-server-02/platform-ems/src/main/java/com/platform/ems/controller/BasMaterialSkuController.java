package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.BasSkuGroup;
import com.platform.ems.domain.dto.request.form.BasMaterialSkuFormRequest;
import com.platform.ems.domain.dto.response.form.BasMaterialSkuFormResponse;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasMaterialSku;
import com.platform.ems.service.IBasMaterialSkuService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物料&商品-SKU明细Controller
 *
 * @author linhongwei
 * @date 2021-01-22
 */
@RestController
@RequestMapping("/archive/sku")
@Api(tags = "物料&商品-SKU明细")
public class BasMaterialSkuController extends BaseController {

    @Autowired
    private IBasMaterialSkuService basMaterialSkuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    @ApiOperation(value = "查询物料&商品&服务档案sku列表", notes = "查询物料&商品&服务档案sku列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSku.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody BasMaterialSku basMaterialSku) {
        startPage(basMaterialSku);
        List<BasMaterialSku> list = basMaterialSkuService.selectBasMaterialSkuList(basMaterialSku);
        return getDataTable(list);
    }

    @Log(title = "启停物料&商品SKU明细档案", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/status")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案启用/停用", notes = "物料&商品&服务档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasMaterialSku basMaterialSku) {
        HashMap<String, Object> response = basMaterialSkuService.status(basMaterialSku);
        if (response.get("errList") != null){
            return AjaxResult.success("操作失败", response);
        }
        return AjaxResult.success(response);
    }

    @PostMapping("/report")
    @ApiOperation(value = "查询物料商品sku细报表", notes = "查询物料商品sku细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasSkuGroup.class))
    public TableDataInfo report(@RequestBody BasMaterialSkuFormRequest request) {
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        BeanUtil.copyProperties(request, basMaterialSku);
        startPage(request);
        List<BasMaterialSku> list = basMaterialSkuService.getReportForm(basMaterialSku);
        return getDataTable(list, BasMaterialSkuFormResponse::new);
    }

    @PostMapping("/changeStatus")
    @Log(title = "sku明细报表启停物料&商品SKU明细档案", businessType = BusinessType.ENBLEORDISABLE)
    @ApiOperation(value = "sku明细报表启停物料&商品SKU明细档案", notes = "sku明细报表启停物料&商品SKU明细档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult changeStatus(@RequestBody BasMaterialSku basMaterialSku) {
        return AjaxResult.success(basMaterialSkuService.changeStatus(basMaterialSku));
    }

    @Log(title = "导出物料&商品-SKU明细报表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出物料&商品&服务SKU明细报表", notes = "导出物料&商品&服务SKU明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, BasMaterialSkuFormRequest request) throws IOException {
        BasMaterialSku basMaterialSku = new BasMaterialSku();
        BeanUtil.copyProperties(request, basMaterialSku);
        List<BasMaterialSku> list = basMaterialSkuService.getReportForm(basMaterialSku);
        List<BasMaterialSkuFormResponse> basMaterialSkuFormResponse = BeanCopyUtils.copyListProperties(list, BasMaterialSkuFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialSkuFormResponse> util = new ExcelUtil<>(BasMaterialSkuFormResponse.class, dataMap);
        util.exportExcel(response, basMaterialSkuFormResponse, "物料商品SKU明细报表");
    }

    @ApiOperation(value = "下载物料SKU明细档案导入模板", notes = "下载物料SKU明细档案导入模板")
    @PostMapping("/importTemplate/WL")
    public void importTemplateWL(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/物料SKU明细导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料(辅料)_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    @ApiOperation(value = "下载商品SKU明细档案导入模板", notes = "下载商品SKU明细档案导入模板")
    @PostMapping("/importTemplate/SP")
    public void importTemplateSP(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/商品SKU明细导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料(辅料)_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    @PostMapping("/import")
    @Log(title = "导入物料&商品-SKU明细", businessType = BusinessType.IMPORT)
    @ApiOperation(value = "导入物料/商品SKU明细", notes = "导入物料/商品SKU明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    public AjaxResult importData(@RequestParam MultipartFile file, @RequestParam String materialCategory) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialSkuService.importData(file, materialCategory);
        if (null != response.get("errList")) {
            return AjaxResult.error("导入错误", response);
        }else {
            if (null != response.get("warn")){
                return AjaxResult.error("导入提示", response);
            }else {
                return AjaxResult.success(response);
            }
        }
    }

    @Log(title = "批量物料/商品SKU图片上传", businessType = BusinessType.IMPORT)
    @ApiOperation(value = "批量物料/商品SKU图片上传", notes = "批量物料/商品SKU图片上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPictureList")
    public AjaxResult setPicturePathList(@RequestBody List<BasMaterialSku> basMaterialSkuList) {
        return toAjax(basMaterialSkuService.setPictureList(basMaterialSkuList));
    }

    @Log(title = "物料/商品SKU图片上传", businessType = BusinessType.IMPORT)
    @ApiOperation(value = "物料/商品SKU图片上传", notes = "物料/商品SKU图片上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPicture")
    public AjaxResult setPicturePath(@RequestBody BasMaterialSku basMaterialSku) {
        return toAjax(basMaterialSkuService.setPicture(basMaterialSku));
    }

    /**
     * 批量新增物料&商品&服务档案sku明细
     */
    @ApiOperation(value = "批量新增物料&商品&服务档案sku明细", notes = "批量新增物料&商品&服务档案sku明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @PostMapping("/addList")
    public AjaxResult addList(@RequestBody @Valid List<BasMaterialSku> basMaterialSkuList) {
        if (CollectionUtil.isNotEmpty(basMaterialSkuList)) {
            Map<Long, List<BasMaterialSku>> itemList = basMaterialSkuList.stream()
                    .collect(Collectors.groupingBy(o -> o.getMaterialSid()));
            for (Long key : itemList.keySet()) {
                basMaterialSkuService.insertBasMaterialSkuList(key, itemList.get(key));
            }
        }
        return AjaxResult.success();
    }
}
