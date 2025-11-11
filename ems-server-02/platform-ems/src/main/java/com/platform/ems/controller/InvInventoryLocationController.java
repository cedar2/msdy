package com.platform.ems.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.InvInventoryLocation;
import com.platform.ems.domain.InvStorehouseMaterial;
import com.platform.ems.domain.dto.request.InvInventoryLocationMaterialRequest;
import com.platform.ems.domain.dto.request.InvInventoryLocationRequest;
import com.platform.ems.domain.dto.request.InvReserveInventoryRequest;
import com.platform.ems.domain.dto.response.InvInventoryLocationResponse;
import com.platform.ems.domain.dto.response.InvReserveInventoryResponse;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationStoreStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialStoreStatisticsForm;
import com.platform.ems.service.IInvInventoryLocationService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteFileService;
import com.platform.framework.web.domain.server.SysFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 仓库库位库存Controller
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@RestController
@RequestMapping("/invInventoryLocation")
@Api(tags = "仓库库位库存")
public class InvInventoryLocationController extends BaseController {

    @Autowired
    private IInvInventoryLocationService invInventoryLocationService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;

    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";
    /**
     * 查询仓库库位库存列表
     */
//    @PreAuthorize(hasPermi = "ems:location:list")
    @PostMapping("/report")
    @ApiOperation(value = "查询仓库库位库存列表", notes = "查询仓库库位库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocationResponse.class))
    public TableDataInfo list(@RequestBody InvInventoryLocationRequest request) {
        InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
        BeanCopyUtils.copyProperties(request,invInventoryLocation);
        startPage(invInventoryLocation);
        List<InvInventoryLocation> list = invInventoryLocationService.selectInvInventoryLocationList(invInventoryLocation);
        return getDataTable(list,InvInventoryLocationResponse::new);
    }

    @PostMapping("/report/invReserve")
    @ApiOperation(value = "查询库存预留明细报表", notes = "查询库存预留明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocationResponse.class))
    public TableDataInfo invReserve(@RequestBody InvReserveInventoryRequest request) {
        startPage(request);
        List<InvReserveInventoryResponse> list = invInventoryLocationService.report(request);
        return getDataTable(list);
    }

    @PostMapping("/delete/invReserve")
    @ApiOperation(value = "释放预留库存", notes = "释放预留库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocationResponse.class))
    public AjaxResult invReserve(@RequestBody List<Long> sids) {
        return AjaxResult.success(invInventoryLocationService.deleteInvReserve(sids));
    }

    @ApiOperation(value = "导出库存预留明细报表", notes = "导出库存预留明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/invReserve")
    public void export(HttpServletResponse response, InvReserveInventoryRequest request) throws IOException {
        List<InvReserveInventoryResponse> list = invInventoryLocationService.report(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvReserveInventoryResponse> util = new ExcelUtil<>(InvReserveInventoryResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvReserveInventoryResponse::new), "库存预留明细报表"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }
    /**
     * 导出仓库库位库存列表
     */
    @PreAuthorize(hasPermi = "ems:location:export")
    @Log(title = "仓库库位库存", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出仓库库位库存列表", notes = "导出仓库库位库存列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventoryLocation invInventoryLocation) throws IOException {
        List<InvInventoryLocation> list = invInventoryLocationService.selectInvInventoryLocationList(invInventoryLocation);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryLocationResponse> util = new ExcelUtil<>(InvInventoryLocationResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list,InvInventoryLocationResponse::new), "仓库库位库存");
    }


    /**
     * 获取仓库库位库存详细信息
     */
    @ApiOperation(value = "获取仓库库位库存详细信息", notes = "获取仓库库位库存详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocation.class))
    @PreAuthorize(hasPermi = "ems:location:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long locationStockSid) {
        if (locationStockSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryLocationService.selectInvInventoryLocationById(locationStockSid));
    }

    /**
     * 获取物料的仓库信息
     */
    @ApiOperation(value = "获取物料的仓库信息", notes = "获取物料的仓库信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocation.class))
    @PostMapping("/getInfo/material")
    public AjaxResult getInfoMa(@RequestBody  List<InvInventoryLocation> list) {
        if(CollectionUtils.isEmpty(list)){
            throw new CustomException("参数确实");
        }
        return AjaxResult.success(invInventoryLocationService.getMaterialLocation(list));
    }
    /**
     * 新增仓库库位库存
     */
    @ApiOperation(value = "新增仓库库位库存", notes = "新增仓库库位库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:location:add")
    @Log(title = "仓库库位库存", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid InvInventoryLocation invInventoryLocation) {
        return toAjax(invInventoryLocationService.insertInvInventoryLocation(invInventoryLocation));
    }

    /**
     * 修改仓库库位库存
     */
    @ApiOperation(value = "修改仓库库位库存", notes = "修改仓库库位库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:location:edit")
    @Log(title = "仓库库位库存", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvInventoryLocation invInventoryLocation) {
        return toAjax(invInventoryLocationService.updateInvInventoryLocation(invInventoryLocation));
    }

    /**
     * 变更仓库库位库存
     */
    @ApiOperation(value = "变更仓库库位库存", notes = "变更仓库库位库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:location:change")
    @Log(title = "仓库库位库存", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody InvInventoryLocation invInventoryLocation) {
        return toAjax(invInventoryLocationService.changeInvInventoryLocation(invInventoryLocation));
    }

    /**
     * 删除仓库库位库存
     */
    @ApiOperation(value = "删除仓库库位库存", notes = "删除仓库库位库存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:location:remove")
    @Log(title = "仓库库位库存", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  locationStockSids) {
        if(ArrayUtil.isEmpty( locationStockSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryLocationService.deleteInvInventoryLocationByIds(locationStockSids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "仓库库位库存", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:location:enbleordisable")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody InvInventoryLocation invInventoryLocation) {
        return AjaxResult.success(invInventoryLocationService.changeStatus(invInventoryLocation));
    }

    @ApiOperation(value = "添加样品获取库存数量和库存价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get/location/material")
    public AjaxResult getLocationMaterial(@RequestBody InvInventoryLocationMaterialRequest request) {
        return AjaxResult.success(invInventoryLocationService.getLocationMaterial(request));
    }

    @ApiOperation(value = "盘点 添加明细时获取数量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get/location/quantity")
    public AjaxResult getLocationMaterialQu(@RequestBody InvInventoryLocationMaterialRequest request) {
        return AjaxResult.success(invInventoryLocationService.getLocationMaterialQu(request));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:location:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "仓库库位库存", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody InvInventoryLocation invInventoryLocation) {
        invInventoryLocation.setConfirmDate(new Date());
        invInventoryLocation.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        return toAjax(invInventoryLocationService.check(invInventoryLocation));
    }

    /**
     * 导入-库存初始化
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入-库存初始化", notes = "导入-库存初始化")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }

        return invInventoryLocationService.importDataInv(file);
    }

    /**
     * 库存出库初始化导入
     */
    @PostMapping("/import/ck")
    @ApiOperation(value = "库存出库初始化导入", notes = "库存出库初始化导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataGCK(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return invInventoryLocationService.importDataInvCHK(file);
    }

//    @PreAuthorize(hasPermi = "ems::bas:material:import")
    @ApiOperation(value = "下载库存初始化导入模板", notes = "下载库存初始化导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_库存初始化_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_库存初始化_V0.1.xlsx", "UTF-8"));
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

    @ApiOperation(value = "下载库存出库初始化导入", notes = "下载库存出库初始化导入")
    @PostMapping("/importTemplate/ck")
    public void importTemplateCHK(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_库存出库初始化_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_库存出库初始化_V0.1.xlsx", "UTF-8"));
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
     * 导入甲供料结算单
     */
//    @PreAuthorize(hasPermi = "ems:transfer:import")
    @PostMapping("/document/import")
    @ApiOperation(value = "导入甲供料结算单", notes = "导入甲供料结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(invInventoryLocationService.importData(file));
    }

    /**
     * 上传甲供料结算单导入模板
     */
    @PostMapping("/uploadTemplate")
    @ApiOperation(value = "上传甲供料结算单导入模板", notes = "上传甲供料结算单导入模板")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult uploadTemplate(MultipartFile file) throws Exception {
        R<SysFile> r = remoteFileService.uploadTemplate(file, FILLE_PATH + "/EMS软件_导入模板_甲供料结算单_V0.1.xlsx");
        if (r.getCode() != R.SUCCESS) {
            return AjaxResult.error("上传失败");
        }
        return AjaxResult.success("上传成功");
    }

    @ApiOperation(value = "下载甲供料结算单导入模板", notes = "下载甲供料结算单导入模板")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_甲供料结算单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_甲供料结算单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    @PostMapping("/statistic/barcode/form")
    @ApiOperation(value = "查询库存统计报表按SKU", notes = "查询库存统计报表按SKU")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocationBarcodeStatisticsForm.class))
    public TableDataInfo statisticBarcodeFormList(@RequestBody InvInventoryLocationBarcodeStatisticsForm request) {
        startPage(request);
        List<InvInventoryLocationBarcodeStatisticsForm> list = invInventoryLocationService.selectInvInventoryLocationStatisticsForm(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出库存统计报表按SKU", notes = "导出库存统计报表按SKU")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/statistic/barcode/form/export")
    public void statisticBarcodeFormExport(HttpServletResponse response, InvInventoryLocationBarcodeStatisticsForm request) throws IOException {
        List<InvInventoryLocationBarcodeStatisticsForm> list = invInventoryLocationService.selectInvInventoryLocationStatisticsForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryLocationBarcodeStatisticsForm> util = new ExcelUtil<>(InvInventoryLocationBarcodeStatisticsForm.class, dataMap);
        util.exportExcel(response, list, "库存统计报表按SKU");
    }

    @PostMapping("/statistic/store/form")
    @ApiOperation(value = "查询库存统计报表按仓库", notes = "查询库存统计报表按仓库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocationStoreStatisticsForm.class))
    public TableDataInfo statisticStoreFormList(@RequestBody InvInventoryLocationStoreStatisticsForm request) {
        startPage(request);
        List<InvInventoryLocationStoreStatisticsForm> list = invInventoryLocationService.selectInvInventoryLocationStatisticsForm(request);
        return getDataTable(list);
    }

    @PostMapping("/statistic/store/form/updateDate")
    @ApiOperation(value = "查询库存统计报表按仓库中更新日期按钮", notes = "查询库存统计报表按仓库中更新日期按钮")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult statisticStoreFormUpdateDate(@RequestBody InvStorehouseMaterial request) {
        return AjaxResult.success(invInventoryLocationService.updateInvStorehouseMaterial(request));
    }

    @ApiOperation(value = "导出库存统计报表按仓库", notes = "导出库存统计报表按仓库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/statistic/store/form/export")
    public void statisticStoreFormExport(HttpServletResponse response, InvInventoryLocationStoreStatisticsForm request) throws IOException {
        List<InvInventoryLocationStoreStatisticsForm> list = invInventoryLocationService.selectInvInventoryLocationStatisticsForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryLocationStoreStatisticsForm> util = new ExcelUtil<>(InvInventoryLocationStoreStatisticsForm.class, dataMap);
        util.exportExcel(response, list, "库存统计报表按仓库");
    }

    @PostMapping("/special/statistic/barcode/form")
    @ApiOperation(value = "查询特殊库存统计报表按SKU", notes = "查询特殊库存统计报表按SKU")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySpecialBarcodeStatisticsForm.class))
    public TableDataInfo specialStatisticBarcodeFormList(@RequestBody InvInventorySpecialBarcodeStatisticsForm request) {
        startPage(request);
        List<InvInventorySpecialBarcodeStatisticsForm> list = invInventoryLocationService.selectInvInventorySpecialStatisticsForm(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出特殊库存统计报表按SKU", notes = "导出特殊库存统计报表按SKU")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/special/statistic/barcode/form/export")
    public void specialStatisticBarcodeFormExport(HttpServletResponse response, InvInventorySpecialBarcodeStatisticsForm request) throws IOException {
        List<InvInventorySpecialBarcodeStatisticsForm> list = invInventoryLocationService.selectInvInventorySpecialStatisticsForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventorySpecialBarcodeStatisticsForm> util = new ExcelUtil<>(InvInventorySpecialBarcodeStatisticsForm.class, dataMap);
        util.exportExcel(response, list, "特殊库存统计报表按SKU");
    }

    @PostMapping("/special/statistic/store/form")
    @ApiOperation(value = "查询特殊库存统计报表按仓库", notes = "查询特殊库存统计报表按仓库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventorySpecialStoreStatisticsForm.class))
    public TableDataInfo specialStatisticStoreFormList(@RequestBody InvInventorySpecialStoreStatisticsForm request) {
        startPage(request);
        List<InvInventorySpecialStoreStatisticsForm> list = invInventoryLocationService.selectInvInventorySpecialStatisticsForm(request);
        return getDataTable(list);
    }

    @ApiOperation(value = "导出特殊库存统计报表按仓库", notes = "导出特殊库存统计报表按仓库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/special/statistic/store/form/export")
    public void specialStatisticStoreFormExport(HttpServletResponse response, InvInventorySpecialStoreStatisticsForm request) throws IOException {
        List<InvInventorySpecialStoreStatisticsForm> list = invInventoryLocationService.selectInvInventorySpecialStatisticsForm(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventorySpecialStoreStatisticsForm> util = new ExcelUtil<>(InvInventorySpecialStoreStatisticsForm.class, dataMap);
        util.exportExcel(response, list, "特殊库存统计报表按仓库");
    }

    /**
     * 移动端库存报表
     */
    @PostMapping("/mob/invLocForm")
    @ApiOperation(value = "移动端库存报表", notes = "移动端库存报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryLocation.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo mobInvForm(@RequestBody InvInventoryLocation inventory) {
        startPage(inventory);
        List<InvInventoryLocation> list = invInventoryLocationService.selectMobInvLocFormList(inventory);
        return getDataTable(list);
    }

}
