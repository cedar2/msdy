package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.dto.request.form.FinBookAccountPayableFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookAccountPayableFormResponse;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;
import com.platform.ems.service.IFinBookAccountPayableItemService;
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
import com.platform.ems.domain.FinBookAccountPayable;
import com.platform.ems.service.IFinBookAccountPayableService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应付Controller
 *
 * @author qhq
 * @date 2021-06-03
 */
@RestController
@RequestMapping("/book/account/payable")
@Api(tags = "财务流水账-应付")
public class FinBookAccountPayableController extends BaseController {

    @Autowired
    private IFinBookAccountPayableService finBookAccountPayableService;
    @Autowired
    private IFinBookAccountPayableItemService finBookAccountPayableItemService;
    @Autowired
    private IConBuTypeAccountCategoryService conBuTypeAccountCategoryService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 导出财务流水账-应付列表
     */
    @ApiOperation(value = "导出财务流水账-应付列表", notes = "导出财务流水账-应付列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookAccountPayableFormRequest request) throws IOException {
        //转换
        FinBookAccountPayable finVendorDeductionBill = new FinBookAccountPayable();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        List<FinBookAccountPayable> list = finBookAccountPayableService.getReportForm(finVendorDeductionBill);
        List<FinBookAccountPayableFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinBookAccountPayableFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookAccountPayableFormResponse> util = new ExcelUtil<>(FinBookAccountPayableFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "应付流水报表");
    }

    @ApiOperation(value = "应付流水报表查询", notes = "应付流水报表查询")
    @PostMapping("/getReportForm")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookAccountPayableFormResponse.class))
    public TableDataInfo getReportForm(@RequestBody FinBookAccountPayableFormRequest request) {
        if (StrUtil.isNotBlank(request.getBuTypeCode())){
            List<ConBuTypeAccountCategory> categoryList = conBuTypeAccountCategoryService.getConBuTypeAccountCategoryList(new ConBuTypeAccountCategory().setBuTypeCode(request.getBuTypeCode())
                    .setBookTypeCode(request.getBookType()).setAccountCategoryCode(request.getAccountCategory()).setFinShoufukuanTypeCode(ConstantsFinance.BOOK_TYPE_FK));
            if (CollectionUtils.isNotEmpty(categoryList)){
                String[] bookSourceCategorys = categoryList.stream().map(ConBuTypeAccountCategory::getBookSourceCategory).toArray(String[]::new);
                bookSourceCategorys = Arrays.stream(bookSourceCategorys).filter(i -> i != null).toArray(String[]::new);
                request.setBookSourceCategoryList(bookSourceCategorys);
            }
        }
        //转换
        FinBookAccountPayable finVendorDeductionBill = new FinBookAccountPayable();
        BeanCopyUtils.copyProperties(request, finVendorDeductionBill);
        startPage(finVendorDeductionBill);
        List<FinBookAccountPayable> requestList = finBookAccountPayableService.getReportForm(finVendorDeductionBill);
        return getDataTable(requestList, FinBookAccountPayableFormResponse::new);
    }

    @ApiOperation(value = "设置到期日前的校验", notes = "设置到期日前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/validDate/verify")
    public AjaxResult validDate(@RequestBody FinBookAccountPayableFormRequest request) {
        if (request.getBookAccountPayableItemSidList() == null || request.getBookAccountPayableItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        finBookAccountPayableItemService.verifyValidDate(request);
        return AjaxResult.success();
    }

    @ApiOperation(value = "设置到期日", notes = "设置到期日")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setValidDate")
    public AjaxResult setAccountValidDate(@RequestBody FinBookAccountPayableFormRequest request) {
        if (request.getBookAccountPayableItemSidList() == null || request.getBookAccountPayableItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        return toAjax(finBookAccountPayableItemService.setValidDate(request));
    }

    /**
     * 导入供应商应付款流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入供应商应付款流水", notes = "导入供应商应付款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finBookAccountPayableService.importData(file);
    }

    /**
     * 导入供应商应付款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入供应商应付款流水", notes = "导入供应商应付款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinBookAccountPayable> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finBookAccountPayableService.addForm(request));
    }

    @ApiOperation(value = "下载供应商应付款流水导入模板", notes = "下载供应商应付款流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/待销应付账导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("待销应付账导入模板.xlsx", "UTF-8"));
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
