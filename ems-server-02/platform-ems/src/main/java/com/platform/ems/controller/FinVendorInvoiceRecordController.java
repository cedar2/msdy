package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.exception.base.BaseException;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FinVendorInvoiceRecord;
import com.platform.ems.service.IFinVendorInvoiceRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商发票台账表Controller
 *
 * @author platform
 * @date 2024-03-12
 */
@Api(tags = "供应商发票台账表")
@RestController
@RequestMapping("/fin/vendor/invoice/record")
public class FinVendorInvoiceRecordController extends BaseController {

    @Autowired
    private IFinVendorInvoiceRecordService finVendorInvoiceRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询供应商发票台账表列表
     */
    @ApiOperation(value = "查询供应商发票台账表列表", notes = "查询供应商发票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorInvoiceRecord.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinVendorInvoiceRecord finVendorInvoiceRecord) {
        startPage(finVendorInvoiceRecord);
        List<FinVendorInvoiceRecord> list = finVendorInvoiceRecordService.selectFinVendorInvoiceRecordList(finVendorInvoiceRecord);
        return getDataTable(list);
    }

    /**
     * 导出供应商发票台账表列表
     */
    @ApiOperation(value = "导出供应商发票台账表列表", notes = "导出供应商发票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVendorInvoiceRecord finVendorInvoiceRecord) throws IOException {
        List<FinVendorInvoiceRecord> list = finVendorInvoiceRecordService.selectFinVendorInvoiceRecordList(finVendorInvoiceRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorInvoiceRecord> util = new ExcelUtil<>(FinVendorInvoiceRecord.class, dataMap);
        util.exportExcel(response, list, "供应商发票台账表");
    }

    /**
     * 获取供应商发票台账表详细信息
     */
    @ApiOperation(value = "获取供应商发票台账表详细信息", notes = "获取供应商发票台账表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinVendorInvoiceRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorInvoiceRecordSid) {
        if (vendorInvoiceRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finVendorInvoiceRecordService.selectFinVendorInvoiceRecordById(vendorInvoiceRecordSid));
    }

    /**
     * 新增供应商发票台账表
     */
    @ApiOperation(value = "新增供应商发票台账表", notes = "新增供应商发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinVendorInvoiceRecord finVendorInvoiceRecord) {
        int row = finVendorInvoiceRecordService.insertFinVendorInvoiceRecord(finVendorInvoiceRecord);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinVendorInvoiceRecord()
                    .setVendorInvoiceRecordSid(finVendorInvoiceRecord.getVendorInvoiceRecordSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改供应商发票台账表", notes = "修改供应商发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinVendorInvoiceRecord finVendorInvoiceRecord) {
        return toAjax(finVendorInvoiceRecordService.updateFinVendorInvoiceRecord(finVendorInvoiceRecord));
    }

    /**
     * 变更供应商发票台账表
     */
    @ApiOperation(value = "变更供应商发票台账表", notes = "变更供应商发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinVendorInvoiceRecord finVendorInvoiceRecord) {
        return toAjax(finVendorInvoiceRecordService.changeFinVendorInvoiceRecord(finVendorInvoiceRecord));
    }

    /**
     * 删除供应商发票台账表
     */
    @ApiOperation(value = "删除供应商发票台账表", notes = "删除供应商发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorInvoiceRecordSids) {
        if (CollectionUtils.isEmpty(vendorInvoiceRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finVendorInvoiceRecordService.deleteFinVendorInvoiceRecordByIds(vendorInvoiceRecordSids));
    }

    @ApiOperation(value = "修改处理状态接口前校验", notes = "修改处理状态接口前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check/judge")
    public AjaxResult checkJudge(@RequestBody FinVendorInvoiceRecord finVendorInvoiceRecord) {
        finVendorInvoiceRecordService.checkJudge(finVendorInvoiceRecord);
        return AjaxResult.success();
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinVendorInvoiceRecord finVendorInvoiceRecord) {
        return AjaxResult.success(finVendorInvoiceRecordService.check(finVendorInvoiceRecord));
    }

    @ApiOperation(value = "设置发票签收状态", notes = "设置发票签收状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/sendFlag")
    public AjaxResult sendFlag(@RequestBody FinVendorInvoiceRecord finVendorInvoiceRecord) {
        return AjaxResult.success(finVendorInvoiceRecordService.updateSendFlag(finVendorInvoiceRecord));
    }

    @ApiOperation(value = "设置更改对账账期", notes = "设置更改对账账期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/period")
    public AjaxResult period(@RequestBody FinVendorInvoiceRecord finVendorInvoiceRecord) {
        return AjaxResult.success(finVendorInvoiceRecordService.updatePeriod(finVendorInvoiceRecord));
    }

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importInvoiceRecord(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(finVendorInvoiceRecordService.importRecord(file));
    }

    /**
     * 下载供应商发票台账导入模板
     */
    @ApiOperation(value = "下载供应商发票台账导入模板", notes = "下载供应商发票台账导入模板")
    @PostMapping("/import/template")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_供应商发票台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_供应商发票台账_V1.0.xlsx", "UTF-8"));
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
