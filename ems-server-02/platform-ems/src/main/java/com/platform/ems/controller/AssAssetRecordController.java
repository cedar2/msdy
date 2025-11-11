package com.platform.ems.controller;

import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.AssAssetRecord;
import com.platform.ems.domain.AssAssetStatisticalRecord;
import com.platform.ems.service.IAssAssetRecordService;
import com.platform.ems.service.ISystemDictDataService;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 资产台账Controller
 *
 * @author chenkw
 * @date 2022-03-01
 */
@RestController
@RequestMapping("/ass/asset/record")
@Api(tags = "资产台账")
public class AssAssetRecordController extends BaseController {

    @Autowired
    private IAssAssetRecordService assAssetRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";

    /**
     * 查询资产台账列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询资产台账列表", notes = "查询资产台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AssAssetRecord.class))
    public TableDataInfo list(@RequestBody AssAssetRecord assAssetRecord) {
        startPage(assAssetRecord);
        List<AssAssetRecord> list = assAssetRecordService.selectAssAssetRecordList(assAssetRecord);
        return getDataTable(list);
    }

    /**
     * 查询资产统计台账列表
     */
    @PostMapping("/statisticalList")
    @ApiOperation(value = "查询资产统计台账列表", notes = "查询资产统计台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AssAssetRecord.class))
    public TableDataInfo statisticalList(@RequestBody AssAssetRecord assAssetRecord) {
        startPage(assAssetRecord);
        List<AssAssetRecord> list = assAssetRecordService.selectAssAssetStatisticalRecordList(assAssetRecord);
        return getDataTable(list);
    }

    /**
     * 查询资产统计台账明细
     */
    @PostMapping("/statisticalListDetail")
    @ApiOperation(value = "查询资产统计台账明细", notes = "查询资产统计台账明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AssAssetRecord.class))
    public TableDataInfo statisticalListDetail(@RequestBody AssAssetRecord assAssetRecord) {
        startPage(assAssetRecord);
        List<AssAssetRecord> list = assAssetRecordService.selectAssAssetStatisticalRecordListDetail(assAssetRecord);
        return getDataTable(list);
    }


    /**
     * 导出资产台账列表
     */
    @Log(title = "资产台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出资产台账列表", notes = "导出资产台账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/statisticalExport")
    public void statisticalExport(HttpServletResponse response, AssAssetRecord assAssetRecord) throws IOException {
        List<AssAssetRecord> list = assAssetRecordService.selectAssAssetStatisticalRecordList(assAssetRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        List<AssAssetStatisticalRecord> assAssetStatisticalRecordList = new ArrayList<>();
        list.forEach((item) -> {
            AssAssetStatisticalRecord record = AssAssetStatisticalRecord
                    .builder().assetCount(item.getAssetCount())
                    .companyName(item.getCompanyName())
                    .assetType(item.getAssetType())
                    .currencyAmount(item.getCurrencyAmount())
                    .build();
            assAssetStatisticalRecordList.add(record);
        });
        ExcelUtil<AssAssetStatisticalRecord> util = new ExcelUtil<>(AssAssetStatisticalRecord.class, dataMap);
        util.exportExcel(response, assAssetStatisticalRecordList, "资产统计台账");
    }

    /**
     * 导出资产台账列表
     */
    @Log(title = "资产统计台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出资产台统计账列表", notes = "导出资产台统计账列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, AssAssetRecord assAssetRecord) throws IOException {
        List<AssAssetRecord> list = assAssetRecordService.selectAssAssetRecordList(assAssetRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<AssAssetRecord> util = new ExcelUtil<>(AssAssetRecord.class, dataMap);
        util.exportExcel(response, list, "资产台账");
    }


    /**
     * 导出选中的资产卡片列表
     */
    @Log(title = "资产统计台账", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出资产卡片列表", notes = "导出资产卡片列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/exportAssetCard")
    public void exportAssetCard(HttpServletResponse response, AssAssetRecord assAssetRecord) throws IOException {
        assAssetRecordService.exportAssetCardList(response, assAssetRecord);
    }
    /**
     * 获取资产台账详细信息
     */
    @ApiOperation(value = "获取资产台账详细信息", notes = "获取资产台账详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AssAssetRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long assetSid) {
        if (assetSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(assAssetRecordService.selectAssAssetRecordById(assetSid));
    }

    /**
     * 新增资产台账
     */
    @ApiOperation(value = "新增资产台账", notes = "新增资产台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid AssAssetRecord assAssetRecord) {
        int row = assAssetRecordService.insertAssAssetRecord(assAssetRecord);
        if (row > 0) {
            return AjaxResult.success(null, new AssAssetRecord().setAssetSid(assAssetRecord.getAssetSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改资产台账
     */
    @ApiOperation(value = "修改资产台账", notes = "修改资产台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid AssAssetRecord assAssetRecord) {
        return toAjax(assAssetRecordService.updateAssAssetRecord(assAssetRecord));
    }

    /**
     * 修改资产台账
     */
    @ApiOperation(value = "修改资产台账", notes = "修改资产台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody AssAssetRecord assAssetRecord) {
        return toAjax(assAssetRecordService.cancel(assAssetRecord));
    }

    /**
     * 变更资产台账
     */
    @ApiOperation(value = "变更资产台账", notes = "变更资产台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid AssAssetRecord assAssetRecord) {
        return toAjax(assAssetRecordService.changeAssAssetRecord(assAssetRecord));
    }

    /**
     * 删除资产台账
     */
    @ApiOperation(value = "删除资产台账", notes = "删除资产台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> assetSids) {
        if (CollectionUtils.isEmpty(assetSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(assAssetRecordService.deleteAssAssetRecordByIds(assetSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "资产台账", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody AssAssetRecord assAssetRecord) {
        return toAjax(assAssetRecordService.check(assAssetRecord));
    }


    /**
     * 导入资产台账
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入资产台账信息", notes = "导入资产台账信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        Object response = assAssetRecordService.importData(file);
        if (response instanceof Collection) {
            return AjaxResult.error("导入错误", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    @ApiOperation(value = "下载资产台账导入模板", notes = "下载资产台账导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_资产台账_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_资产台账_V0.1.xlsx", "UTF-8"));
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
}
