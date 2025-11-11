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
import com.platform.ems.domain.FinCustomerInvoiceRecord;
import com.platform.ems.service.IFinCustomerInvoiceRecordService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户发票台账表Controller
 *
 * @author platform
 * @date 2024-03-12
 */
@Api(tags = "客户发票台账表")
@RestController
@RequestMapping("/fin/customer/invoice/record")
public class FinCustomerInvoiceRecordController extends BaseController {

    @Autowired
    private IFinCustomerInvoiceRecordService finCustomerInvoiceRecordService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询客户发票台账表列表
     */
    @ApiOperation(value = "查询客户发票台账表列表", notes = "查询客户发票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerInvoiceRecord.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        startPage(finCustomerInvoiceRecord);
        List<FinCustomerInvoiceRecord> list = finCustomerInvoiceRecordService.selectFinCustomerInvoiceRecordList(finCustomerInvoiceRecord);
        return getDataTable(list);
    }

    /**
     * 导出客户发票台账表列表
     */
    @ApiOperation(value = "导出客户发票台账表列表", notes = "导出客户发票台账表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerInvoiceRecord finCustomerInvoiceRecord) throws IOException {
        List<FinCustomerInvoiceRecord> list = finCustomerInvoiceRecordService.selectFinCustomerInvoiceRecordList(finCustomerInvoiceRecord);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerInvoiceRecord> util = new ExcelUtil<>(FinCustomerInvoiceRecord.class, dataMap);
        util.exportExcel(response, list, "客户发票台账表");
    }

    /**
     * 获取客户发票台账表详细信息
     */
    @ApiOperation(value = "获取客户发票台账表详细信息", notes = "获取客户发票台账表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerInvoiceRecord.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerInvoiceRecordSid) {
        if (customerInvoiceRecordSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerInvoiceRecordService.selectFinCustomerInvoiceRecordById(customerInvoiceRecordSid));
    }

    /**
     * 新增客户发票台账表
     */
    @ApiOperation(value = "新增客户发票台账表", notes = "新增客户发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        int row = finCustomerInvoiceRecordService.insertFinCustomerInvoiceRecord(finCustomerInvoiceRecord);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinCustomerInvoiceRecord()
                    .setCustomerInvoiceRecordSid(finCustomerInvoiceRecord.getCustomerInvoiceRecordSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改客户发票台账表", notes = "修改客户发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return toAjax(finCustomerInvoiceRecordService.updateFinCustomerInvoiceRecord(finCustomerInvoiceRecord));
    }

    /**
     * 变更客户发票台账表
     */
    @ApiOperation(value = "变更客户发票台账表", notes = "变更客户发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return toAjax(finCustomerInvoiceRecordService.changeFinCustomerInvoiceRecord(finCustomerInvoiceRecord));
    }

    /**
     * 删除客户发票台账表
     */
    @ApiOperation(value = "删除客户发票台账表", notes = "删除客户发票台账表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerInvoiceRecordSids) {
        if (CollectionUtils.isEmpty(customerInvoiceRecordSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerInvoiceRecordService.deleteFinCustomerInvoiceRecordByIds(customerInvoiceRecordSids));
    }

    @ApiOperation(value = "修改处理状态接口前校验", notes = "修改处理状态接口前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check/judge")
    public AjaxResult checkJudge(@RequestBody FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        finCustomerInvoiceRecordService.checkJudge(finCustomerInvoiceRecord);
        return AjaxResult.success();
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return AjaxResult.success(finCustomerInvoiceRecordService.check(finCustomerInvoiceRecord));
    }

    @ApiOperation(value = "设置发票寄出状态", notes = "设置发票寄出状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/sendFlag")
    public AjaxResult sendFlag(@RequestBody FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return AjaxResult.success(finCustomerInvoiceRecordService.updateSendFlag(finCustomerInvoiceRecord));
    }

    @ApiOperation(value = "设置对账日期", notes = "设置对账日期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/period")
    public AjaxResult period(@RequestBody FinCustomerInvoiceRecord finCustomerInvoiceRecord) {
        return AjaxResult.success(finCustomerInvoiceRecordService.updatePeriod(finCustomerInvoiceRecord));
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
        return AjaxResult.success(finCustomerInvoiceRecordService.importRecord(file));
    }

    /**
     * 下载客户发票台账导入模板
     */
    @ApiOperation(value = "下载客户发票台账导入模板", notes = "下载客户发票台账导入模板")
    @PostMapping("/import/template")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String fileName = FILLE_PATH + "/" + "SCM_导入模板_客户发票台账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            InputStream inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" +
                    URLEncoder.encode("SCM_导入模板_客户发票台账_V1.0.xlsx", "UTF-8"));
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
