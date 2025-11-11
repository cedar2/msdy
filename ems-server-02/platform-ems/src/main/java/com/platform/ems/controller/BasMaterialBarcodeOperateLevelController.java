package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.dto.response.form.BasMatBarcodeOperLvlCategorySkuForm;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.BasMaterialBarcodeOperateLevel;
import com.platform.ems.service.IBasMaterialBarcodeOperateLevelService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品SKU条码-网店运营信息Controller
 *
 * @author chenkw
 * @date 2023-01-18
 */
@RestController
@RequestMapping("/material/barcode/operate/level")
@Api(tags = "商品SKU条码-网店运营信息")
public class BasMaterialBarcodeOperateLevelController extends BaseController {

    @Autowired
    private IBasMaterialBarcodeOperateLevelService basMaterialBarcodeOperateLevelService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询商品SKU条码-网店运营信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品SKU条码-网店运营信息列表", notes = "查询商品SKU条码-网店运营信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBarcodeOperateLevel.class))
    public TableDataInfo list(@RequestBody BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        startPage(basMaterialBarcodeOperateLevel);
        List<BasMaterialBarcodeOperateLevel> list = basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelList(basMaterialBarcodeOperateLevel);
        return getDataTable(list);
    }

    /**
     * 导出商品SKU条码-网店运营信息列表
     */
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品SKU条码-网店运营信息列表", notes = "导出商品SKU条码-网店运营信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) throws IOException {
        List<BasMaterialBarcodeOperateLevel> list = basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelList(basMaterialBarcodeOperateLevel);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialBarcodeOperateLevel> util = new ExcelUtil<>(BasMaterialBarcodeOperateLevel.class, dataMap);
        util.exportExcel(response, list, "商品SKU网店采购状态报表");
    }


    /**
     * 获取商品SKU条码-网店运营信息详细信息
     */
    @ApiOperation(value = "获取商品SKU条码-网店运营信息详细信息", notes = "获取商品SKU条码-网店运营信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBarcodeOperateLevel.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialBarcodeOperateLevelSid) {
        if (materialBarcodeOperateLevelSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelById(materialBarcodeOperateLevelSid));
    }

    /**
     * 新增商品SKU条码-网店运营信息
     */
    @ApiOperation(value = "新增商品SKU条码-网店运营信息", notes = "新增商品SKU条码-网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        return toAjax(basMaterialBarcodeOperateLevelService.insertBasMaterialBarcodeOperateLevel(basMaterialBarcodeOperateLevel));
    }

    /**
     * 编辑商品SKU条码-网店运营信息
     */
    @ApiOperation(value = "修改商品SKU条码-网店运营信息", notes = "修改商品SKU条码-网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        return toAjax(basMaterialBarcodeOperateLevelService.updateBasMaterialBarcodeOperateLevel(basMaterialBarcodeOperateLevel));
    }

    /**
     * 按钮设置采购状态
     */
    @ApiOperation(value = "按钮设置采购状态", notes = "按钮设置采购状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update/purchaseFlag")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult updatePurchaseFlag(@RequestBody BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        return toAjax(basMaterialBarcodeOperateLevelService.updatePurchaseFlag(basMaterialBarcodeOperateLevel));
    }


    /**
     * 变更商品SKU条码-网店运营信息
     */
    @ApiOperation(value = "变更商品SKU条码-网店运营信息", notes = "变更商品SKU条码-网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        return toAjax(basMaterialBarcodeOperateLevelService.changeBasMaterialBarcodeOperateLevel(basMaterialBarcodeOperateLevel));
    }

    /**
     * 删除商品SKU条码-网店运营信息
     */
    @ApiOperation(value = "删除商品SKU条码-网店运营信息", notes = "删除商品SKU条码-网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品SKU条码-网店运营信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialBarcodeOperateLevelSids) {
        if (CollectionUtils.isEmpty(materialBarcodeOperateLevelSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(basMaterialBarcodeOperateLevelService.deleteBasMaterialBarcodeOperateLevelByIds(materialBarcodeOperateLevelSids));
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入商品SKU网店采购信息表数据录入", notes = "导入商品SKU网店采购信息表数据录入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basMaterialBarcodeOperateLevelService.importData(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/协服SCM_导入模板_商品SKU网店采购信息表数据录入_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_运营级别_V0.1.xlsx", "UTF-8"));
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

    /**
     * 报表中心 类目SKU明细报表
     */
    @PostMapping("/category/sku/list")
    @ApiOperation(value = "报表中心类目明细报表", notes = "报表中心类目明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMatBarcodeOperLvlCategorySkuForm.class))
    public TableDataInfo saleStationCategorySku(@RequestBody BasMatBarcodeOperLvlCategorySkuForm request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        List<BasMatBarcodeOperLvlCategorySkuForm> total = basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelCategorySkuForm(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<BasMatBarcodeOperLvlCategorySkuForm> list = basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelCategorySkuForm(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    @ApiOperation(value = "导出报表中心类目SKU明细报表", notes = "导出报表中心类目SKU明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/category/sku/export")
    public void saleStationCategorySkuExport(HttpServletResponse response, BasMatBarcodeOperLvlCategorySkuForm request) throws IOException {
        request.setPageNum(null);
        List<BasMatBarcodeOperLvlCategorySkuForm> list = basMaterialBarcodeOperateLevelService.selectBasMaterialBarcodeOperateLevelCategorySkuForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMatBarcodeOperLvlCategorySkuForm> util = new ExcelUtil<>(BasMatBarcodeOperLvlCategorySkuForm.class, dataMap);
        util.exportExcel(response, list, "类目SKU汇总报表");
    }

    @ApiOperation(value = "设置产品级别", notes = "设置产品级别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setProductLevel")
    public AjaxResult barcodeSetProductLevel(@RequestBody BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel){
        if (ArrayUtil.isEmpty(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList())) {
            throw new BaseException("请选择行！");
        }
        return toAjax(basMaterialBarcodeOperateLevelService.setProductLevel(basMaterialBarcodeOperateLevel));
    }

    @ApiOperation(value = "设置商品MSKU编码(ERP)", notes = "设置商品MSKU编码(ERP)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setMsku")
    public AjaxResult setMskuCode(@RequestBody BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel){
        if (ArrayUtil.isEmpty(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList())) {
            throw new BaseException("请选择行！");
        }
        return toAjax(basMaterialBarcodeOperateLevelService.setMskuCode(basMaterialBarcodeOperateLevel));
    }

    /**
     * 更新数据导入 MSKU + 产品级别 + 运营级别
     */
    @PostMapping("/update/import")
    @ApiOperation(value = "更新数据导入", notes = "更新数据导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importUpdateData(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basMaterialBarcodeOperateLevelService.importUpdateData(file));
    }

    /**
     * 下载更新数据导入导入模板
     */
    @ApiOperation(value = "下载更新数据导入导入模板", notes = "下载更新数据导入导入模板")
    @PostMapping("/update/import/template")
    public void importUpdateTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/协服SCM_导入模板_商品SKU网店采购信息表数据录入_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("协服SCM_导入模板_商品SKU网店采购信息表数据录入_V1.0.xlsx", "UTF-8"));
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
}
