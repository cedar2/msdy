package com.platform.ems.controller;

import com.platform.common.annotation.Idempotent;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.export.FinVendorMonthAccountReportExport;
import com.platform.ems.service.IPurVendorMonthAccountBillService;
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
 * 供应商对账单Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/pur/vendor/month/account/bill")
@Api(tags = "供应商对账单")
public class PurVendorMonthAccountBillController extends BaseController {

    @Autowired
    private IPurVendorMonthAccountBillService purVendorMonthAccountBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 查询供应商对账单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商对账单列表", notes = "查询供应商对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurVendorMonthAccountBill.class))
    public TableDataInfo list(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        startPage(purVendorMonthAccountBill);
        List<PurVendorMonthAccountBill> list = purVendorMonthAccountBillService.selectPurVendorMonthAccountBillList(purVendorMonthAccountBill);
        return getDataTable(list);
    }

    /**
     * 导出供应商对账单列表
     */
    @ApiOperation(value = "导出供应商对账单列表", notes = "导出供应商对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurVendorMonthAccountBill purVendorMonthAccountBill) throws IOException {
        List<PurVendorMonthAccountBill> list = purVendorMonthAccountBillService.selectPurVendorMonthAccountBillList(purVendorMonthAccountBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurVendorMonthAccountBill> util = new ExcelUtil<>(PurVendorMonthAccountBill.class, dataMap);
        util.exportExcel(response, list, "供应商对账单");
    }

    /**
     * 导入供应商期初余额
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入供应商期初余额", notes = "导入供应商期初余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return purVendorMonthAccountBillService.importData(file);
    }

    /**
     * 导入供应商期初余额
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入供应商期初余额", notes = "导入供应商期初余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<PurVendorMonthAccountBill> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(purVendorMonthAccountBillService.addForm(request));
    }

    /**
     * 获取供应商对账单详细信息
     */
    @ApiOperation(value = "获取供应商对账单详细信息", notes = "获取供应商对账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurVendorMonthAccountBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long vendorMonthAccountBillSid) {
        if (vendorMonthAccountBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purVendorMonthAccountBillService.selectPurVendorMonthAccountBillById(vendorMonthAccountBillSid));
    }

    /**
     * 新增供应商对账单
     */
    @ApiOperation(value = "新增供应商对账单", notes = "新增供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PurVendorMonthAccountBill purVendorMonthAccountBill) {
        int row = purVendorMonthAccountBillService.insertPurVendorMonthAccountBill(purVendorMonthAccountBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new PurVendorMonthAccountBill().setVendorMonthAccountBillSid(purVendorMonthAccountBill.getVendorMonthAccountBillSid()));
        }
        return toAjax(row);
    }

    /**
     * 修改供应商对账单
     */
    @ApiOperation(value = "修改供应商对账单", notes = "修改供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        return toAjax(purVendorMonthAccountBillService.updatePurVendorMonthAccountBill(purVendorMonthAccountBill));
    }

    /**
     * 新增/编辑直接提交供应商对账单
     */
    @ApiOperation(value = "新增/编辑直接提交供应商对账单", notes = "新增/编辑直接提交供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submit(@Validated @RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill, String jump) {
        return purVendorMonthAccountBillService.submit(purVendorMonthAccountBill,jump);
    }

    /**
     * 变更供应商对账单
     */
    @ApiOperation(value = "变更供应商对账单", notes = "变更供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        return toAjax(purVendorMonthAccountBillService.changePurVendorMonthAccountBill(purVendorMonthAccountBill));
    }

    /**
     * 删除供应商对账单
     */
    @ApiOperation(value = "删除供应商对账单", notes = "删除供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> vendorMonthAccountBillSids) {
        if (CollectionUtils.isEmpty(vendorMonthAccountBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purVendorMonthAccountBillService.deletePurVendorMonthAccountBillByIds(vendorMonthAccountBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        return toAjax(purVendorMonthAccountBillService.check(purVendorMonthAccountBill));
    }

    /**
     * 提交校验
     */
    @ApiOperation(value = "提交校验", notes = "提交校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        return toAjax(purVendorMonthAccountBillService.processCheck(purVendorMonthAccountBill));
    }

    @ApiOperation(value = "变更所属账期", notes = "变更所属账期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeDate")
    public AjaxResult changeDate(@RequestBody PurVendorMonthAccountBill list) {
        return toAjax(purVendorMonthAccountBillService.changeYearMonth(list));
    }

    @ApiOperation(value = "入口", notes = "入口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/entrance")
    public AjaxResult entrance(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        List<PurVendorMonthAccountBill> list = purVendorMonthAccountBillService.selectPurVendorMonthAccountBillList(purVendorMonthAccountBill);
        if (CollectionUtils.isNotEmpty(list)) {
            return AjaxResult.success(EmsResultEntity.warning(String.valueOf(list.get(0).getVendorMonthAccountBillSid()), null, "该月账单已存在，正在跳转页面……"));
        }
        return AjaxResult.success(purVendorMonthAccountBillService.entrance(purVendorMonthAccountBill));
    }

    @ApiOperation(value = "台账报表查询", notes = "台账报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/report")
    public TableDataInfo report(@RequestBody PurVendorMonthAccountBill purVendorMonthAccountBill) {
        if (purVendorMonthAccountBill.getCompanySid() == null) {
            throw new BaseException("公司不能为空");
        }
        TableDataInfo tableDataInfo = purVendorMonthAccountBillService.selectReportList(purVendorMonthAccountBill);
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }

    /**
     * 导出供应商台账报表
     */
    @ApiOperation(value = "导出供应商台账报表", notes = "导出供应商台账报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/report/export")
    public void reportExport(HttpServletResponse response, PurVendorMonthAccountBill purVendorMonthAccountBill) throws IOException {
        TableDataInfo tableDataInfo = purVendorMonthAccountBillService.selectReportList(purVendorMonthAccountBill);
        List<PurVendorMonthAccountBillInfo> infoList = (List<PurVendorMonthAccountBillInfo>) tableDataInfo.getRows();
        List<FinVendorMonthAccountReportExport> responseList = BeanCopyUtils.copyListProperties(infoList, FinVendorMonthAccountReportExport::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinVendorMonthAccountReportExport> util = new ExcelUtil<>(FinVendorMonthAccountReportExport.class, dataMap);
        util.exportExcel(response, responseList, "供应商台账报表");
    }

    /**
     * 导出供应商对账单
     */
    @ApiOperation(value = "导出供应商对账单", notes = "导出供应商对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/report/exportDZD")
    public void reportExportDZD(HttpServletResponse response, PurVendorMonthAccountBill purVendorMonthAccountBill) throws IOException {
        purVendorMonthAccountBillService.exportPur(response,purVendorMonthAccountBill);
    }

    @ApiOperation(value = "下载供应商期初余额导入模板", notes = "下载供应商期初余额导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/供应商期初余额导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("供应商期初余额导入模板.xlsx", "UTF-8"));
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
