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
import com.platform.ems.domain.dto.request.form.FinBookAccountReceivableFormRequest;
import com.platform.ems.domain.dto.response.form.FinBookAccountReceivableFormResponse;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;
import com.platform.ems.service.IFinBookAccountReceivableItemService;
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
import com.platform.ems.domain.FinBookAccountReceivable;
import com.platform.ems.service.IFinBookAccountReceivableService;
import com.platform.ems.service.ISystemDictDataService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收Controller
 *
 * @author linhongwei
 * @date 2021-06-11
 */
@RestController
@RequestMapping("/book/account/receivable")
@Api(tags = "财务流水账-应收")
public class FinBookAccountReceivableController extends BaseController {

    @Autowired
    private IFinBookAccountReceivableService finBookAccountReceivableService;
    @Autowired
    private IFinBookAccountReceivableItemService finBookAccountReceivableItemService;
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
     * 导出财务流水账-应收列表
     */
    @ApiOperation(value = "导出财务流水账-应收列表", notes = "导出财务流水账-应收列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookAccountReceivableFormRequest request) throws IOException {
        //转换
        FinBookAccountReceivable finBookAccountReceivable = new FinBookAccountReceivable();
        BeanCopyUtils.copyProperties(request, finBookAccountReceivable);
        List<FinBookAccountReceivable> list = finBookAccountReceivableService.getReportForm(finBookAccountReceivable);
        List<FinBookAccountReceivableFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinBookAccountReceivableFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookAccountReceivableFormResponse> util = new ExcelUtil<>(FinBookAccountReceivableFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "应收流水报表");
    }

    @ApiOperation(value = "应收流水报表查询", notes = "应收流水报表查询")
    @PostMapping("/getReportForm")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookAccountReceivableFormResponse.class))
    public TableDataInfo getReportForm(@RequestBody FinBookAccountReceivableFormRequest request) {
        if (StrUtil.isNotBlank(request.getBuTypeCode())){
            List<ConBuTypeAccountCategory> categoryList = conBuTypeAccountCategoryService.getConBuTypeAccountCategoryList(new ConBuTypeAccountCategory().setBuTypeCode(request.getBuTypeCode())
                    .setBookTypeCode(request.getBookType()).setAccountCategoryCode(request.getAccountCategory()).setFinShoufukuanTypeCode(ConstantsFinance.BOOK_TYPE_SK));
            if (CollectionUtils.isNotEmpty(categoryList)){
                String[] bookSourceCategorys = categoryList.stream().map(ConBuTypeAccountCategory::getBookSourceCategory).toArray(String[]::new);
                bookSourceCategorys = Arrays.stream(bookSourceCategorys).filter(i -> i != null).toArray(String[]::new);
                request.setBookSourceCategoryList(bookSourceCategorys);
            }
        }
        //转换
        FinBookAccountReceivable finBookAccountReceivable = new FinBookAccountReceivable();
        BeanCopyUtils.copyProperties(request, finBookAccountReceivable);
        startPage(finBookAccountReceivable);
        List<FinBookAccountReceivable> requestList = finBookAccountReceivableService.getReportForm(finBookAccountReceivable);
        return getDataTable(requestList, FinBookAccountReceivableFormResponse::new);
    }

    @ApiOperation(value = "设置到期日前的校验", notes = "设置到期日前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/validDate/verify")
    public AjaxResult accountValidDate(@RequestBody FinBookAccountReceivableFormRequest request) {
        if (request.getBookAccountReceivableItemSidList() == null || request.getBookAccountReceivableItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        finBookAccountReceivableItemService.verifyValidDate(request);
        return AjaxResult.success();
    }

    @ApiOperation(value = "设置到期日", notes = "设置到期日")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setValidDate")
    public AjaxResult setAccountValidDate(@RequestBody FinBookAccountReceivableFormRequest request) {
        if (request.getBookAccountReceivableItemSidList() == null || request.getBookAccountReceivableItemSidList().length == 0){
            throw new BaseException("请选择行");
        }
        return toAjax(finBookAccountReceivableItemService.setValidDate(request));
    }

    /**
     * 导入客户应付款流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入客户应收款流水", notes = "导入客户应收款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finBookAccountReceivableService.importData(file);
    }

    /**
     * 导入客户应付款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入客户应付款流水", notes = "导入客户应付款流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinBookAccountReceivable> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finBookAccountReceivableService.addForm(request));
    }

    @ApiOperation(value = "下载客户应收款流水导入模板", notes = "下载客户应收款流水导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/待销应收账导入模板.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("待销应收账导入模板.xlsx", "UTF-8"));
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
