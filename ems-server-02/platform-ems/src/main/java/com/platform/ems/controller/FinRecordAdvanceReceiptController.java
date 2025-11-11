package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.FinRecordAdvanceReceiptItem;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;
import com.platform.ems.service.IFinRecordAdvanceReceiptItemService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.FinRecordAdvanceReceipt;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IFinRecordAdvanceReceiptService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 客户业务台账-预收Controller
 *
 * @author qhq
 * @date 2021-06-16
 */
@RestController
@RequestMapping("/record/advance/receipt")
@Api(tags = "客户业务台账-预收")
public class FinRecordAdvanceReceiptController extends BaseController {

    @Autowired
    private IFinRecordAdvanceReceiptService finRecordAdvanceReceiptService;
    @Autowired
    private IFinRecordAdvanceReceiptItemService finRecordAdvanceReceiptItemService;
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
     * 查询客户业务台账-预收列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询客户业务台账-预收列表", notes = "查询客户业务台账-预收列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvanceReceipt.class))
    public TableDataInfo list(@RequestBody FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        startPage(finRecordAdvanceReceipt);
        List<FinRecordAdvanceReceipt> list = finRecordAdvanceReceiptService.selectFinRecordAdvanceReceiptList(finRecordAdvanceReceipt);
        return getDataTable(list);
    }

    /**
     * 导出客户业务台账-预收列表
     */
    @ApiOperation(value = "导出客户业务台账-预收列表", notes = "导出客户业务台账-预收列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinRecordAdvanceReceipt finRecordAdvanceReceipt) throws IOException {
        List<FinRecordAdvanceReceipt> requestList = finRecordAdvanceReceiptService.getReportForm(finRecordAdvanceReceipt);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinRecordAdvanceReceipt> util = new ExcelUtil<>(FinRecordAdvanceReceipt.class, dataMap);
        util.exportExcel(response, requestList, "客户待收预收款报表");
    }

    /**
     * 获取客户业务台账-预收详细信息
     */
    @ApiOperation(value = "获取客户业务台账-预收详细信息", notes = "获取客户业务台账-预收详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvanceReceipt.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordAdvanceReceiptSid) {
        if (recordAdvanceReceiptSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finRecordAdvanceReceiptService.selectFinRecordAdvanceReceiptById(recordAdvanceReceiptSid));
    }

    /**
     * 新增客户业务台账-预收
     */
    @ApiOperation(value = "新增客户业务台账-预收", notes = "新增客户业务台账-预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        return toAjax(finRecordAdvanceReceiptService.insertFinRecordAdvanceReceipt(finRecordAdvanceReceipt));
    }

    /**
     * 修改客户业务台账-预收
     */
    @ApiOperation(value = "修改客户业务台账-预收", notes = "修改客户业务台账-预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        return toAjax(finRecordAdvanceReceiptService.updateFinRecordAdvanceReceipt(finRecordAdvanceReceipt));
    }

    /**
     * 变更客户业务台账-预收
     */
    @ApiOperation(value = "变更客户业务台账-预收", notes = "变更客户业务台账-预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        return toAjax(finRecordAdvanceReceiptService.changeFinRecordAdvanceReceipt(finRecordAdvanceReceipt));
    }

    /**
     * 删除客户业务台账-预收
     */
    @ApiOperation(value = "删除客户业务台账-预收", notes = "删除客户业务台账-预收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordAdvanceReceiptSids) {
        if (ArrayUtil.isEmpty(recordAdvanceReceiptSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finRecordAdvanceReceiptService.deleteFinRecordAdvanceReceiptByIds(recordAdvanceReceiptSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinRecordAdvanceReceipt finRecordAdvanceReceipt) {
        finRecordAdvanceReceipt.setConfirmDate(new Date());
        finRecordAdvanceReceipt.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        finRecordAdvanceReceipt.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(finRecordAdvanceReceiptService.check(finRecordAdvanceReceipt));
    }

    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvanceReceipt.class))
    @ApiOperation(value = "客户待收预收款报表查询", notes = "客户待收预收款报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinRecordAdvanceReceipt request) {
        if (StrUtil.isNotBlank(request.getBuTypeCode())){
            List<ConBuTypeAccountCategory> categoryList = conBuTypeAccountCategoryService.getConBuTypeAccountCategoryList(new ConBuTypeAccountCategory().setBuTypeCode(request.getBuTypeCode())
                    .setBookTypeCode(request.getBookType()).setAccountCategoryCode(request.getAccountCategory()).setFinShoufukuanTypeCode(ConstantsFinance.BOOK_TYPE_FK));
            if (CollectionUtils.isNotEmpty(categoryList)){
                String[] bookSourceCategorys = categoryList.stream().map(ConBuTypeAccountCategory::getBookSourceCategory).toArray(String[]::new);
                bookSourceCategorys = Arrays.stream(bookSourceCategorys).filter(i -> i != null).toArray(String[]::new);
                request.setBookSourceCategoryList(bookSourceCategorys);
            }
        }
        startPage(request);
        List<FinRecordAdvanceReceipt> requestList = finRecordAdvanceReceiptService.getReportForm(request);
        return getDataTable(requestList);
    }

    @ApiOperation(value = "设置到期日", notes = "设置到期日")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setValidDate")
    public AjaxResult setAccountValidDate(@RequestBody FinRecordAdvanceReceiptItem request) {
        if (request.getRecordAdvanceReceiptItemSidList() == null || request.getRecordAdvanceReceiptItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        return toAjax(finRecordAdvanceReceiptItemService.setValidDate(request));
    }

    /**
     * 导入供应商待付预付款流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入供应商待付预付款流水", notes = "导入供应商待付预付款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finRecordAdvanceReceiptService.importData(file);
    }

    /**
     * 导入客户待收预收款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "客户待收预收款流水记账", notes = "客户待收预收款流水记账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinRecordAdvanceReceipt> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finRecordAdvanceReceiptService.addForm(request));
    }

    @ApiOperation(value = "下载客户待收预收款流水导入模板", notes = "下载客户待收预收款流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/待收预收账导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("待收预收账导入模板.xlsx", "UTF-8"));
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
