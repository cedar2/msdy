package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvInventoryDocumentReportRequest;
import com.platform.ems.domain.dto.request.financial.FinBookReceiptEstimationItemListRequest;
import com.platform.ems.domain.dto.response.InvInventoryDocumentReportResponse;
import com.platform.ems.domain.dto.response.financial.FinBookReceiptEstimationItemListResponse;
import com.platform.ems.service.*;
import com.platform.system.service.ISysDictDataService;
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
import com.platform.ems.enums.HandleStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 财务流水账-应收暂估Controller
 *
 * @author qhq
 * @date 2021-06-08
 */
@RestController
@RequestMapping("/book/receipt/estimation")
@Api(tags = "财务流水账-应收暂估")
public class FinBookReceiptEstimationController extends BaseController {

    @Autowired
    private IFinBookReceiptEstimationService finBookReceiptEstimationService;
    @Autowired
    private IFinBookReceiptEstimationItemService finBookReceiptEstimationItemService;
    @Autowired
    private ISalSalePriceService salSalePriceService;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template/finance";

    /**
     * 导出财务流水账-应收暂估列表
     */
    @ApiOperation(value = "导出财务流水账-应收暂估列表", notes = "导出财务流水账-应收暂估列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookReceiptEstimation request) throws IOException {
        List<FinBookReceiptEstimation> list = finBookReceiptEstimationService.getReportForm(request);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinBookReceiptEstimation> util = new ExcelUtil<>(FinBookReceiptEstimation.class,dataMap);
        util.exportExcel(response, list, "应收暂估流水报表");
    }

    /**
     * 导出销售结算报表
     */
    @ApiOperation(value = "导出销售结算报表", notes = "导出销售结算报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportReport(HttpServletResponse response, FinBookReceiptEstimation request) throws IOException {
        if (ArrayUtil.isEmpty(request.getInventoryDocumentItemSidList())) {
            List<FinBookReceiptEstimation> list = finBookReceiptEstimationService.getReportForm(request);
            if (CollectionUtils.isNotEmpty(list)) {
                Long[] sids = list.stream().map(FinBookReceiptEstimation::getInventoryDocumentItemSid).toArray(Long[]::new);
                request.setInventoryDocumentItemSidList(sids);
            }
        }
        InvInventoryDocumentReportRequest invInventoryDocumentReportRequest = new InvInventoryDocumentReportRequest();
        invInventoryDocumentReportRequest.setInventoryDocumentItemSidList(request.getInventoryDocumentItemSidList());
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(invInventoryDocumentReportRequest);
        if (CollectionUtils.isNotEmpty(list)) {
            Optional<String> companyOpt = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCompanyName)
                    .filter(StrUtil::isNotBlank)
                    .findFirst();
            Optional<String> customerOpt = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCustomerName)
                    .filter(StrUtil::isNotBlank)
                    .findFirst();
            String company = companyOpt.orElseThrow(() -> new BaseException("存在公司为空的数据，导出失败！"));
            String customer = customerOpt.orElseThrow(() -> new BaseException("存在客户为空的数据，导出失败！"));
            boolean companyMismatch = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCompanyName)
                    .filter(StrUtil::isNotBlank)
                    .anyMatch(sid -> !sid.equals(company));
            boolean customerMismatch = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCustomerName)
                    .filter(StrUtil::isNotBlank)
                    .anyMatch(sid -> !sid.equals(customer));
            if (companyMismatch || customerMismatch) {
                throw new BaseException("导出数据的“客户、公司”不同，无法导出！");
            }
            invInventoryDocumentService.exportSal(response,list);
        }
        else {
            throw new BaseException("没有符合条件的数据，导出失败！");
        }
    }

    @ApiOperation(value = "应收暂估流水报表查询", notes = "应收暂估流水报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookReceiptEstimation request) {
        startPage(request);
        List<FinBookReceiptEstimation> requestList = finBookReceiptEstimationService.getReportForm(request);
        return getDataTable(requestList);
    }

    @ApiOperation(value = "设置是否业务对账", notes = "设置是否业务对账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/setBusinessVerify")
    public AjaxResult setBusinessVerify(@RequestBody FinBookReceiptEstimationItem request){
        return AjaxResult.success(finBookReceiptEstimationItemService.setBusinessVerify(request));
    }

    @ApiOperation(value = "设置对账账期", notes = "设置对账账期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/set/businessVerifyPeriod")
    public AjaxResult setBusinessVerifyPeriod(@RequestBody FinBookReceiptEstimationItem request){
        return AjaxResult.success(finBookReceiptEstimationItemService.setBusinessVerifyPeriod(request));
    }

    /**
     * 查询财务流水账-明细-应收暂估列表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "查询财务流水账-明细-应收暂估列表", notes = "查询财务流水账-明细-应收暂估列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookReceiptEstimationItemListResponse.class))
    public TableDataInfo list(@RequestBody FinBookReceiptEstimationItemListRequest request) {
        FinBookReceiptEstimationItem finBookReceiptEstimationItem = new FinBookReceiptEstimationItem();
        BeanCopyUtils.copyProperties(request, finBookReceiptEstimationItem);
        finBookReceiptEstimationItem.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
        finBookReceiptEstimationItem.setHandleStatusNotList(new String[]{HandleStatus.INVALID.getCode()});
        startPage(finBookReceiptEstimationItem);
        List<FinBookReceiptEstimationItem> list = finBookReceiptEstimationItemService.selectFinBookReceiptEstimationItemList(finBookReceiptEstimationItem);
        TableDataInfo rspData = getDataTable(list);
        try {
            List<FinBookReceiptEstimationItem> responseList = new ArrayList<>();
            list.forEach(item->{
                if (item.getSalesOrderSid() != null){
                    SalSalePrice price = new SalSalePrice();
                    price.setCustomerSid(item.getCustomerSid()).setCompanySid(item.getCompanySid()).setMaterialSid(item.getMaterialSid()).setSku2Sid(item.getSku2Sid())
                            .setSku1Sid(item.getSku1Sid()).setRawMaterialMode(item.getRawMaterialMode()).setSaleMode(item.getSaleMode());
                    SalSalePriceItem priceItem = salSalePriceService.getSalePrice(price);
                    if (priceItem != null){
                        if (priceItem.getSalePriceTax() != null){
                            item.setCurrentPriceTax(priceItem.getSalePriceTax());
                            if (priceItem.getTaxRate() != null) {
                                item.setCurrentPrice(priceItem.getSalePriceTax().divide(priceItem.getTaxRate().add(BigDecimal.ONE),4, BigDecimal.ROUND_HALF_UP));
                            }
                        }
                    }
                }
                responseList.add(item);
            });
            rspData.setRows(BeanCopyUtils.copyListProperties(responseList,FinBookReceiptEstimationItemListResponse::new));
        } catch (Exception e){
            throw new BaseException("获取当前销售价异常，请联系管理员!");
        }
        return rspData;
    }

    /**
     * 导入应收暂估账流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入应收暂估账流水", notes = "导入应收暂估账流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finBookReceiptEstimationService.importData(file);
    }

    /**
     * 导入供应商已收款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入应收暂估账流水", notes = "导入应收暂估账流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinBookReceiptEstimation> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finBookReceiptEstimationService.addForm(request));
    }

    @ApiOperation(value = "下载应收暂估账导入模板", notes = "下载应收暂估账导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_应收暂估账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_应收暂估账_V1.0.xlsx", "UTF-8"));
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
