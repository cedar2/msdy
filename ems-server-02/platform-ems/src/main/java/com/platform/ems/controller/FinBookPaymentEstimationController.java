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
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvInventoryDocumentReportRequest;
import com.platform.ems.domain.dto.request.financial.FinBookPaymentEstimationItemListRequest;
import com.platform.ems.domain.dto.request.form.FinBookPaymentEstimationFormRequest;
import com.platform.ems.domain.dto.response.InvInventoryDocumentReportResponse;
import com.platform.ems.domain.dto.response.financial.FinBookPaymentEstimationItemListResponse;
import com.platform.ems.domain.dto.response.form.FinBookPaymentEstimationFormResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
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
 * 财务流水账-应付暂估Controller
 *
 * @author qhq
 * @date 2021-05-31
 */
@RestController
@RequestMapping("/book/payment/estimation")
@Api(tags = "财务流水账-应付暂估")
public class FinBookPaymentEstimationController extends BaseController {

    @Autowired
    private IFinBookPaymentEstimationService finBookPaymentEstimationService;
    @Autowired
    private IFinBookPaymentEstimationItemService finBookPaymentEstimationItemService;
    @Autowired
    private IPurPurchasePriceService purPurchasePriceService;
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
     * 查询财务流水账-应付暂估列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询财务流水账-应付暂估列表", notes = "查询财务流水账-应付暂估列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookPaymentEstimation.class))
    public TableDataInfo list(@RequestBody FinBookPaymentEstimation finBookPaymentEstimation) {
        startPage(finBookPaymentEstimation);
        List<FinBookPaymentEstimation> list = finBookPaymentEstimationService.selectFinBookPaymentEstimationList(finBookPaymentEstimation);
        return getDataTable(list);
    }

    /**
     * 导出财务流水账-应付暂估列表
     */
    @ApiOperation(value = "导出财务流水账-应付暂估列表", notes = "导出财务流水账-应付暂估列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBookPaymentEstimation finBookPaymentEstimation) throws IOException {
        List<FinBookPaymentEstimation> list = finBookPaymentEstimationService.getReportForm(finBookPaymentEstimation);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinBookPaymentEstimation> util = new ExcelUtil<>(FinBookPaymentEstimation.class, dataMap);
        util.exportExcel(response, list, "应付暂估流水报表");
    }

    /**
     * 导出采购结算报表
     */
    @ApiOperation(value = "导出采购结算报表", notes = "导出采购结算报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/report")
    public void exportReport(HttpServletResponse response, FinBookPaymentEstimation finBookPaymentEstimation) throws IOException {
        if (ArrayUtil.isEmpty(finBookPaymentEstimation.getInventoryDocumentItemSidList())) {
            List<FinBookPaymentEstimation> list = finBookPaymentEstimationService.getReportForm(finBookPaymentEstimation);
            if (CollectionUtils.isNotEmpty(list)) {
                Long[] sids = list.stream().map(FinBookPaymentEstimation::getInventoryDocumentItemSid).toArray(Long[]::new);
                finBookPaymentEstimation.setInventoryDocumentItemSidList(sids);
            }
        }
        InvInventoryDocumentReportRequest invInventoryDocumentReportRequest = new InvInventoryDocumentReportRequest();
        invInventoryDocumentReportRequest.setInventoryDocumentItemSidList(finBookPaymentEstimation.getInventoryDocumentItemSidList());
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(invInventoryDocumentReportRequest);
        if (CollectionUtils.isNotEmpty(list)) {
            Optional<String> companyOpt = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCompanyName)
                    .filter(StrUtil::isNotBlank)
                    .findFirst();
            Optional<String> vendorOpt = list.stream()
                    .map(InvInventoryDocumentReportResponse::getVendorName)
                    .filter(StrUtil::isNotBlank)
                    .findFirst();
            String company = companyOpt.orElseThrow(() -> new BaseException("存在公司为空的数据，导出失败！"));
            String vendor = vendorOpt.orElseThrow(() -> new BaseException("存在供应商为空的数据，导出失败！"));
            boolean companyMismatch = list.stream()
                    .map(InvInventoryDocumentReportResponse::getCompanyName)
                    .filter(StrUtil::isNotBlank)
                    .anyMatch(sid -> !sid.equals(company));
            boolean vendorMismatch = list.stream()
                    .map(InvInventoryDocumentReportResponse::getVendorName)
                    .filter(StrUtil::isNotBlank)
                    .anyMatch(sid -> !sid.equals(vendor));
            if (companyMismatch || vendorMismatch) {
                throw new BaseException("导出数据的“供应商、公司”不同，无法导出！");
            }
            invInventoryDocumentService.exportPur(response,list);
        }
        else {
            throw new BaseException("没有符合条件的数据，导出失败！");
        }
    }

    @ApiOperation(value = "应付暂估流水报表查询", notes = "应付暂估流水报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinBookPaymentEstimationFormRequest request) {
        FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
        BeanCopyUtils.copyProperties(request, finBookPaymentEstimation);
        startPage(finBookPaymentEstimation);
        List<FinBookPaymentEstimation> list = finBookPaymentEstimationService.getReportForm(finBookPaymentEstimation);
        return getDataTable(list,FinBookPaymentEstimationFormResponse::new);
    }

    @ApiOperation(value = "设置是否业务对账", notes = "设置是否业务对账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/setBusinessVerify")
    public AjaxResult setBusinessVerify(@RequestBody FinBookPaymentEstimationItem request){
        return AjaxResult.success(finBookPaymentEstimationItemService.setBusinessVerify(request));
    }

    @ApiOperation(value = "设置对账账期", notes = "设置对账账期")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/set/businessVerifyPeriod")
    public AjaxResult setBusinessVerifyPeriod(@RequestBody FinBookPaymentEstimationItem request){
        return AjaxResult.success(finBookPaymentEstimationItemService.setBusinessVerifyPeriod(request));
    }

    /**
     * 查询财务流水账-明细-应付暂估列表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "查询财务流水账-明细-应付暂估列表", notes = "查询财务流水账-明细-应付暂估列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinBookPaymentEstimationItemListResponse.class))
    public TableDataInfo list(@RequestBody FinBookPaymentEstimationItemListRequest request) {
        FinBookPaymentEstimationItem finBookPaymentEstimationItem = new FinBookPaymentEstimationItem();
        BeanCopyUtils.copyProperties(request, finBookPaymentEstimationItem);
        finBookPaymentEstimationItem.setClearStatusNot(ConstantsFinance.CLEAR_STATUS_QHX);
        finBookPaymentEstimationItem.setHandleStatusNotList(new String[]{HandleStatus.INVALID.getCode()});
        startPage(finBookPaymentEstimationItem);
        List<FinBookPaymentEstimationItem> list = finBookPaymentEstimationItemService.selectFinBookPaymentEstimationItemList(finBookPaymentEstimationItem);
        TableDataInfo rspData = getDataTable(list);
        try {
            List<FinBookPaymentEstimationItem> responseList = new ArrayList<>();
            list.forEach(item->{
                if (item.getPurchaseOrderSid() != null){
                    PurPurchasePrice price = new PurPurchasePrice();
                    price.setVendorSid(item.getVendorSid()).setCompanySid(item.getCompanySid()).setMaterialSid(item.getMaterialSid())
                            .setSku1Sid(item.getSku1Sid()).setSku2Sid(item.getSku2Sid())
                            .setRawMaterialMode(item.getRawMaterialMode()).setPurchaseMode(item.getPurchaseMode());
                    PurPurchasePriceItem priceItem = purPurchasePriceService.getNewPurchase(price);
                    if (priceItem != null){
                        if (priceItem.getPurchasePriceTax() != null){
                            item.setCurrentPriceTax(priceItem.getPurchasePriceTax());
                            if (priceItem.getTaxRate() != null) {
                                item.setCurrentPrice(priceItem.getPurchasePriceTax().divide(priceItem.getTaxRate().add(BigDecimal.ONE),4, BigDecimal.ROUND_HALF_UP));
                            }
                        }
                    }
                }
                responseList.add(item);
            });
            rspData.setRows(BeanCopyUtils.copyListProperties(responseList,FinBookPaymentEstimationItemListResponse::new));
        } catch (Exception e){
            throw new BaseException("获取当前采购价异常，请联系管理员");
        }
        return rspData;
    }

    /**
     * 导入应付暂估账流水
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入应付暂估账流水", notes = "导入应付暂估账流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return finBookPaymentEstimationService.importData(file);
    }

    /**
     * 导入供应商已付款流水
     */
    @PostMapping("/addForm")
    @ApiOperation(value = "导入应付暂估账流水", notes = "导入应付暂估账流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addForm(@RequestBody List<FinBookPaymentEstimation> request) {
        if (CollectionUtils.isEmpty(request)) {
            return null;
        }
        return AjaxResult.success(finBookPaymentEstimationService.addForm(request));
    }

    @ApiOperation(value = "下载应付暂估账导入模板", notes = "下载应付暂估账导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_应付暂估账_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_应付暂估账_V1.0.xlsx", "UTF-8"));
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
