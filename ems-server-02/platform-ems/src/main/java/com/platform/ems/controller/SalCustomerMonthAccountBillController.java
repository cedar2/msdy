package com.platform.ems.controller;

import com.platform.common.annotation.Idempotent;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.SalCustomerMonthAccountBill;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.ISalCustomerMonthAccountBillService;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import java.util.List;
import java.util.Map;

/**
 * 客户对账单Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/sal/customer/month/account/bill")
@Api(tags = "客户对账单")
public class SalCustomerMonthAccountBillController extends BaseController {

    @Autowired
    private ISalCustomerMonthAccountBillService salCustomerMonthAccountBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 查询客户对账单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户对账单列表", notes = "查询客户对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalCustomerMonthAccountBill.class))
    public TableDataInfo list(@RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        startPage(salCustomerMonthAccountBill);
        List<SalCustomerMonthAccountBill> list = salCustomerMonthAccountBillService.selectSalCustomerMonthAccountBillList(salCustomerMonthAccountBill);
        return getDataTable(list);
    }

    /**
     * 导出客户对账单列表
     */
    @ApiOperation(value = "导出客户对账单列表", notes = "导出客户对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalCustomerMonthAccountBill salCustomerMonthAccountBill) throws IOException {
        List<SalCustomerMonthAccountBill> list = salCustomerMonthAccountBillService.selectSalCustomerMonthAccountBillList(salCustomerMonthAccountBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalCustomerMonthAccountBill> util = new ExcelUtil<>(SalCustomerMonthAccountBill.class, dataMap);
        util.exportExcel(response, list, "客户对账单");
    }

    /**
     * 导入客户期初余额
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入客户期初余额", notes = "导入客户期初余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return salCustomerMonthAccountBillService.importData(file);
    }

    /**
     * 导入客户期初余额
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入客户期初余额", notes = "导入客户期初余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<SalCustomerMonthAccountBill> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(salCustomerMonthAccountBillService.addForm(request));
    }

    /**
     * 获取客户对账单详细信息
     */
    @ApiOperation(value = "获取客户对账单详细信息", notes = "获取客户对账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalCustomerMonthAccountBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerMonthAccountBillSid) {
        if (customerMonthAccountBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salCustomerMonthAccountBillService.selectSalCustomerMonthAccountBillById(customerMonthAccountBillSid));
    }

    /**
     * 新增客户对账单
     */
    @ApiOperation(value = "新增客户对账单", notes = "新增客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        int row = salCustomerMonthAccountBillService.insertSalCustomerMonthAccountBill(salCustomerMonthAccountBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new SalCustomerMonthAccountBill().setCustomerMonthAccountBillSid(salCustomerMonthAccountBill.getCustomerMonthAccountBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改客户对账单
     */
    @ApiOperation(value = "修改客户对账单", notes = "修改客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        return toAjax(salCustomerMonthAccountBillService.updateSalCustomerMonthAccountBill(salCustomerMonthAccountBill));
    }

    /**
     * 新增/编辑直接提交客户对账单
     */
    @ApiOperation(value = "新增/编辑直接提交客户对账单", notes = "新增/编辑直接提交客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submit(@Validated @RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill, String jump) {
        return salCustomerMonthAccountBillService.submit(salCustomerMonthAccountBill, jump);
    }

    /**
     * 变更客户对账单
     */
    @ApiOperation(value = "变更客户对账单", notes = "变更客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        return toAjax(salCustomerMonthAccountBillService.changeSalCustomerMonthAccountBill(salCustomerMonthAccountBill));
    }

    /**
     * 删除客户对账单
     */
    @ApiOperation(value = "删除客户对账单", notes = "删除客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerMonthAccountBillSids) {
        if (CollectionUtils.isEmpty(customerMonthAccountBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salCustomerMonthAccountBillService.deleteSalCustomerMonthAccountBillByIds(customerMonthAccountBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        return toAjax(salCustomerMonthAccountBillService.check(salCustomerMonthAccountBill));
    }

    /**
     * 提交校验
     */
    @ApiOperation(value = "提交校验", notes = "提交校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody SalCustomerMonthAccountBill salCustomerMonthAccountBill) {
        return toAjax(salCustomerMonthAccountBillService.processCheck(salCustomerMonthAccountBill));
    }

    /**
     * 导出客户对账单
     */
    @ApiOperation(value = "导出客户对账单", notes = "导出客户对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/report/exportDZD")
    public void reportExportDZD(HttpServletResponse response, SalCustomerMonthAccountBill salCustomerMonthAccountBill) throws IOException {
        salCustomerMonthAccountBillService.exportPur(response, salCustomerMonthAccountBill);
    }

    @ApiOperation(value = "下载客户期初余额导入模板", notes = "下载客户期初余额导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/客户期初余额导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("客户期初余额导入模板.xlsx", "UTF-8"));
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
