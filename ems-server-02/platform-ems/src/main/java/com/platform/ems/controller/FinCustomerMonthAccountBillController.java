package com.platform.ems.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.FinCustomerMonthAccountBillInfo;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.export.FinCustomerMonthAccountReportExport;
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
import com.platform.ems.domain.FinCustomerMonthAccountBill;
import com.platform.ems.service.IFinCustomerMonthAccountBillService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户月对账单Controller
 *
 * @author chenkw
 * @date 2021-09-22
 */
@RestController
@RequestMapping("/fin/customer/month/account/bill")
@Api(tags = "客户月对账单")
public class FinCustomerMonthAccountBillController extends BaseController {

    @Autowired
    private IFinCustomerMonthAccountBillService finCustomerMonthAccountBillService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 查询客户月对账单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户月对账单列表", notes = "查询客户月对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerMonthAccountBill.class))
    public TableDataInfo list(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        startPage(finCustomerMonthAccountBill);
        List<FinCustomerMonthAccountBill> list = finCustomerMonthAccountBillService.selectFinCustomerMonthAccountBillList(finCustomerMonthAccountBill);
        return getDataTable(list);
    }

    /**
     * 导出客户月对账单列表
     */
    @ApiOperation(value = "导出客户月对账单列表", notes = "导出客户月对账单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCustomerMonthAccountBill finCustomerMonthAccountBill) throws IOException {
        List<FinCustomerMonthAccountBill> list = finCustomerMonthAccountBillService.selectFinCustomerMonthAccountBillList(finCustomerMonthAccountBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerMonthAccountBill> util = new ExcelUtil<>(FinCustomerMonthAccountBill.class, dataMap);
        util.exportExcel(response, list, "客户月对账单");
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
        return finCustomerMonthAccountBillService.importData(file);
    }

    /**
     * 导入客户期初余额
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入客户期初余额", notes = "导入客户期初余额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinCustomerMonthAccountBill> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finCustomerMonthAccountBillService.addForm(request));
    }

    /**
     * 获取客户月对账单详细信息
     */
    @ApiOperation(value = "获取客户月对账单详细信息", notes = "获取客户月对账单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinCustomerMonthAccountBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long customerMonthAccountBillSid) {
        if (customerMonthAccountBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finCustomerMonthAccountBillService.selectFinCustomerMonthAccountBillById(customerMonthAccountBillSid));
    }

    /**
     * 新增客户月对账单
     */
    @ApiOperation(value = "新增客户月对账单", notes = "新增客户月对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        return toAjax(finCustomerMonthAccountBillService.insertFinCustomerMonthAccountBill(finCustomerMonthAccountBill));
    }

    /**
     * 修改客户月对账单
     */
    @ApiOperation(value = "修改客户月对账单", notes = "修改客户月对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        return toAjax(finCustomerMonthAccountBillService.updateFinCustomerMonthAccountBill(finCustomerMonthAccountBill));
    }

    /**
     * 变更客户月对账单
     */
    @ApiOperation(value = "变更客户月对账单", notes = "变更客户月对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        return toAjax(finCustomerMonthAccountBillService.changeFinCustomerMonthAccountBill(finCustomerMonthAccountBill));
    }

    /**
     * 删除客户月对账单
     */
    @ApiOperation(value = "删除客户月对账单", notes = "删除客户月对账单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> customerMonthAccountBillSids) {
        if (CollectionUtils.isEmpty(customerMonthAccountBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finCustomerMonthAccountBillService.deleteFinCustomerMonthAccountBillByIds(customerMonthAccountBillSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        return toAjax(finCustomerMonthAccountBillService.check(finCustomerMonthAccountBill));
    }

    @ApiOperation(value = "入口", notes = "入口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/entrance")
    public AjaxResult entrance(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        List<FinCustomerMonthAccountBill> list = finCustomerMonthAccountBillService.selectFinCustomerMonthAccountBillList(finCustomerMonthAccountBill);
        if (CollectionUtils.isNotEmpty(list)){
            return AjaxResult.success(EmsResultEntity.warning(String.valueOf(list.get(0).getCustomerMonthAccountBillSid()),null,"该月账单已存在，正在跳转页面……"));
        }
        return AjaxResult.success(finCustomerMonthAccountBillService.entrance(finCustomerMonthAccountBill));
    }

    @ApiOperation(value = "变更所属账期", notes = "变更所属账期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeDate")
    public AjaxResult changeDate(@RequestBody FinCustomerMonthAccountBill list) {
        return toAjax(finCustomerMonthAccountBillService.changeYearMonth(list));
    }


    @ApiOperation(value = "台账报表查询", notes = "台账报表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/report")
    public TableDataInfo report(@RequestBody FinCustomerMonthAccountBill finCustomerMonthAccountBill) {
        if (finCustomerMonthAccountBill.getCompanySid() == null){
            throw new BaseException("公司不能为空");
        }
        TableDataInfo tableDataInfo = finCustomerMonthAccountBillService.selectReportList(finCustomerMonthAccountBill);
        tableDataInfo.setCode(HttpStatus.SUCCESS);
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }


    /**
     * 导出客户台账报表
     */
    @ApiOperation(value = "导出客户台账报表", notes = "导出客户台账报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/report/export")
    public void reportExport(HttpServletResponse response, FinCustomerMonthAccountBill finCustomerMonthAccountBill) throws IOException {
        TableDataInfo tableDataInfo = finCustomerMonthAccountBillService.selectReportList(finCustomerMonthAccountBill);
        List<FinCustomerMonthAccountBillInfo> infoList = (List<FinCustomerMonthAccountBillInfo>) tableDataInfo.getRows();
        List<FinCustomerMonthAccountReportExport> responseList = BeanCopyUtils.copyListProperties(infoList,FinCustomerMonthAccountReportExport::new);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinCustomerMonthAccountReportExport> util = new ExcelUtil<>(FinCustomerMonthAccountReportExport.class,dataMap);
        util.exportExcel(response, responseList, "客户台账报表");
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
