package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.dto.request.form.FinBookReceiptPaymentFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookReceiptPaymentFormResponse;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.FinBookReceiptPayment;
import com.platform.ems.service.IFinBookReceiptPaymentService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-收款Controller
 *
 * @author qhq
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/book/receipt/payment")
@Api(tags = "财务流水账-收款")
public class FinBookReceiptPaymentController extends BaseController {

    @Autowired
    private IFinBookReceiptPaymentService finBookReceiptPaymentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 导出财务流水账-收款列表
     */
    @ApiOperation(value = "导出财务流水账-收款列表", notes = "导出财务流水账-收款列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookReceiptPaymentFormRequest request) throws IOException {
        //转换
        FinBookReceiptPayment finBookReceiptPayment = new FinBookReceiptPayment();
        BeanCopyUtils.copyProperties(request, finBookReceiptPayment);
        List<FinBookReceiptPayment> list = finBookReceiptPaymentService.getReportForm(finBookReceiptPayment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookReceiptPayment> util = new ExcelUtil<>(FinBookReceiptPayment.class, dataMap);
        util.exportExcel(response, list, "收款流水报表");
    }

    @ApiOperation(value = "收款流水报表查询", notes = "收款流水报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookReceiptPaymentFormResponse.class))
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookReceiptPaymentFormRequest request) {
        //转换
        FinBookReceiptPayment finBookReceiptPayment = new FinBookReceiptPayment();
        BeanCopyUtils.copyProperties(request, finBookReceiptPayment);
        startPage(finBookReceiptPayment);
        List<FinBookReceiptPayment> requestList = finBookReceiptPaymentService.getReportForm(finBookReceiptPayment);
        return getDataTable(requestList, FinBookReceiptPaymentFormResponse::new);
    }

    /**
     * 导入客户已收款流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入客户已收款流水", notes = "导入客户已收款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finBookReceiptPaymentService.importData(file);
    }

    /**
     * 导入客户已收款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入客户已收款流水", notes = "导入客户已收款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinBookReceiptPayment> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finBookReceiptPaymentService.addForm(request));
    }

    @ApiOperation(value = "下载客户已收款流水导入模板", notes = "下载客户已收款流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/待销已收账导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("待销已收账导入模板.xlsx", "UTF-8"));
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
