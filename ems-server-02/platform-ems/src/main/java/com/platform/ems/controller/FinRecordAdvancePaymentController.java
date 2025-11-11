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
import com.platform.ems.domain.FinRecordAdvancePaymentItem;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;
import com.platform.ems.service.IFinRecordAdvancePaymentItemService;
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
import com.platform.ems.domain.FinRecordAdvancePayment;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.IFinRecordAdvancePaymentService;
import com.platform.ems.service.ISystemDictDataService;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 供应商业务台账-预付Controller
 *
 * @author qhq
 * @date 2021-05-29
 */
@RestController
@RequestMapping("/record/advance/payment")
@Api(tags = "供应商业务台账-预付")
public class FinRecordAdvancePaymentController extends BaseController {

    @Autowired
    private IFinRecordAdvancePaymentService finRecordAdvancePaymentService;
    @Autowired
    private IFinRecordAdvancePaymentItemService finRecordAdvancePaymentItemService;
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
     * 查询供应商业务台账-预付列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询供应商业务台账-预付列表", notes = "查询供应商业务台账-预付列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvancePayment.class))
    public TableDataInfo list(@RequestBody FinRecordAdvancePayment finRecordAdvancePayment) {
        startPage(finRecordAdvancePayment);
        List<FinRecordAdvancePayment> list = finRecordAdvancePaymentService.selectFinRecordAdvancePaymentList(finRecordAdvancePayment);
        return getDataTable(list);
    }

    /**
     * 导出供应商业务台账-预付列表
     */
    @ApiOperation(value = "导出供应商业务台账-预付列表", notes = "导出供应商业务台账-预付列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinRecordAdvancePayment finRecordAdvancePayment) throws IOException {
        List<FinRecordAdvancePayment> list = finRecordAdvancePaymentService.getReportForm(finRecordAdvancePayment);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinRecordAdvancePayment> util = new ExcelUtil<>(FinRecordAdvancePayment.class, dataMap);
        util.exportExcel(response, list, "供应商待付预付款报表");
    }


    /**
     * 获取供应商业务台账-预付详细信息
     */
    @ApiOperation(value = "获取供应商业务台账-预付详细信息", notes = "获取供应商业务台账-预付详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvancePayment.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long recordAdvancePaymentSid) {
        if (recordAdvancePaymentSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finRecordAdvancePaymentService.selectFinRecordAdvancePaymentById(recordAdvancePaymentSid));
    }

    /**
     * 新增供应商业务台账-预付
     */
    @ApiOperation(value = "新增供应商业务台账-预付", notes = "新增供应商业务台账-预付")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinRecordAdvancePayment finRecordAdvancePayment) {
        return toAjax(finRecordAdvancePaymentService.insertFinRecordAdvancePayment(finRecordAdvancePayment));
    }

    /**
     * 修改供应商业务台账-预付
     */
    @ApiOperation(value = "修改供应商业务台账-预付", notes = "修改供应商业务台账-预付")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinRecordAdvancePayment finRecordAdvancePayment) {
        return toAjax(finRecordAdvancePaymentService.updateFinRecordAdvancePayment(finRecordAdvancePayment));
    }

    /**
     * 变更供应商业务台账-预付
     */
    @ApiOperation(value = "变更供应商业务台账-预付", notes = "变更供应商业务台账-预付")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody FinRecordAdvancePayment finRecordAdvancePayment) {
        return toAjax(finRecordAdvancePaymentService.changeFinRecordAdvancePayment(finRecordAdvancePayment));
    }

    /**
     * 删除供应商业务台账-预付
     */
    @ApiOperation(value = "删除供应商业务台账-预付", notes = "删除供应商业务台账-预付")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> recordAdvancePaymentSids) {
        if (ArrayUtil.isEmpty(recordAdvancePaymentSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finRecordAdvancePaymentService.deleteFinRecordAdvancePaymentByIds(recordAdvancePaymentSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinRecordAdvancePayment finRecordAdvancePayment) {
        finRecordAdvancePayment.setConfirmDate(new Date());
        finRecordAdvancePayment.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        finRecordAdvancePayment.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(finRecordAdvancePaymentService.check(finRecordAdvancePayment));
    }

    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinRecordAdvancePayment.class))
    @ApiOperation(value = "供应商待付预付款报表查询", notes = "供应商待付预付款报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinRecordAdvancePayment request) {
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
        List<FinRecordAdvancePayment> requestList = finRecordAdvancePaymentService.getReportForm(request);
        return getDataTable(requestList);
    }

    @ApiOperation(value = "设置到期日", notes = "设置到期日")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setValidDate")
    public AjaxResult setAccountValidDate(@RequestBody FinRecordAdvancePaymentItem request) {
        if (request.getRecordAdvancePaymentItemSidList() == null || request.getRecordAdvancePaymentItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        return toAjax(finRecordAdvancePaymentItemService.setValidDate(request));
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
        return finRecordAdvancePaymentService.importData(file);
    }

    /**
     * 导入供应商待付预付款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "供应商待付预付款流水记账", notes = "供应商待付预付款流水记账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinRecordAdvancePayment> request) throws Exception {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finRecordAdvancePaymentService.addForm(request));
    }

    @ApiOperation(value = "下载供应商待付预付款流水导入模板", notes = "下载供应商待付预付款流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/待付预付账导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("待付预付账导入模板.xlsx", "UTF-8"));
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
