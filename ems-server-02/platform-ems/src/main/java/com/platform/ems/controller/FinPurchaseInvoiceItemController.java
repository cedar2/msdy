package com.platform.ems.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.domain.dto.request.financial.FinPurchaseInvoiceItemChildRequest;
import com.platform.ems.domain.dto.request.financial.FinPurchaseInvoiceItemInfoRequest;
import com.platform.ems.domain.dto.request.form.FinPurchaseInvoiceItemFormRequest;
import com.platform.ems.domain.dto.response.financial.FinPurchaseInvoiceItemListResponse;
import com.platform.ems.domain.dto.response.form.FinPurchaseInvoiceItemFormResponse;
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

import com.platform.ems.domain.FinPurchaseInvoiceItem;
import com.platform.ems.service.IFinPurchaseInvoiceItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购发票明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@RestController
@RequestMapping("/fin/pur/invoice/item")
@Api(tags = "采购发票明细报表")
public class FinPurchaseInvoiceItemController extends BaseController {

    @Autowired
    private IFinPurchaseInvoiceItemService finPurchaseInvoiceItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购发票明细报表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购发票明细报表列表", notes = "查询采购发票明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceItem.class))
    public TableDataInfo list(@RequestBody FinPurchaseInvoiceItem finPurchaseInvoiceItem) {
        startPage(finPurchaseInvoiceItem);
        List<FinPurchaseInvoiceItem> list = finPurchaseInvoiceItemService.selectFinPurchaseInvoiceItemList(finPurchaseInvoiceItem);
        return getDataTable(list);
    }

    /**
     * 导出采购发票明细报表列表
     */
    @ApiOperation(value = "导出采购发票明细报表列表", notes = "导出采购发票明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinPurchaseInvoiceItemFormRequest request) throws IOException {
        FinPurchaseInvoiceItem finPurchaseInvoiceItem = new FinPurchaseInvoiceItem();
        BeanUtil.copyProperties(request,finPurchaseInvoiceItem);
        List<FinPurchaseInvoiceItem> responseList = finPurchaseInvoiceItemService.getReportForm(finPurchaseInvoiceItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinPurchaseInvoiceItem> util = new ExcelUtil<>(FinPurchaseInvoiceItem.class,dataMap);
        util.exportExcel(response, responseList, "采购发票明细报表");
    }


    /**
     * 获取采购发票明细报表详细信息
     */
    @ApiOperation(value = "获取采购发票明细报表详细信息", notes = "获取采购发票明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseInvoiceItemSid) {
        if (purchaseInvoiceItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finPurchaseInvoiceItemService.selectFinPurchaseInvoiceItemById(purchaseInvoiceItemSid));
    }

    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceItemFormResponse.class))
    @ApiOperation(value = "采购发票明细报表查询", notes = "采购发票明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinPurchaseInvoiceItemFormRequest request) {
        FinPurchaseInvoiceItem finPurchaseInvoiceItem = new FinPurchaseInvoiceItem();
        BeanUtil.copyProperties(request,finPurchaseInvoiceItem);
        startPage(finPurchaseInvoiceItem);
        List<FinPurchaseInvoiceItem> requestList = finPurchaseInvoiceItemService.getReportForm(finPurchaseInvoiceItem);
        return getDataTable(requestList, FinPurchaseInvoiceItemFormResponse::new);
    }

    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceItemListResponse.class))
    @ApiOperation(value = "明细汇总页签", notes = "明细汇总页签")
    @PostMapping("/groupCount")
    public AjaxResult groupCount(@RequestBody List<FinPurchaseInvoiceItemInfoRequest> request) {
        List<FinPurchaseInvoiceItemInfoRequest> response = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(request)){
            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal amount = BigDecimal.ZERO;
            FinPurchaseInvoiceItemInfoRequest base = new FinPurchaseInvoiceItemInfoRequest();
            List<FinPurchaseInvoiceItemInfoRequest> temp = new ArrayList<>();
            Map<String, FinPurchaseInvoiceItemInfoRequest> groupMap = new HashMap<>();
            Map<String, List<FinPurchaseInvoiceItemInfoRequest>> infoMap = new HashMap<>();
            for (FinPurchaseInvoiceItemInfoRequest item : request) {
                item.setKey(String.valueOf(item.getMaterialSid())+"-"+String.valueOf(item.getPriceTax()));
                // 明细分组处理
                FinPurchaseInvoiceItemInfoRequest itemItem = new FinPurchaseInvoiceItemInfoRequest();
                BeanCopyUtils.copyProperties(item, itemItem);
                temp = infoMap.get(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()));
                if (temp != null){
                    temp.add(itemItem);
                    infoMap.put(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()), temp);
                }
                else {
                    infoMap.put(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()), new ArrayList<FinPurchaseInvoiceItemInfoRequest>(){{add(itemItem);}});
                }
                // 金额和sku名称的处理
                base = groupMap.get(String.valueOf(item.getMaterialSid())+"-"+String.valueOf(item.getPriceTax()));
                if (base == null){
                    if (item.getQuantity() == null){
                        item.setQuantity(BigDecimal.ZERO);
                    }
                    if (item.getCurrencyAmountTax() == null){
                        item.setCurrencyAmountTax(BigDecimal.ZERO);
                    }
                    item.setSku1Name(null).setSku2Name(null).setSku1Sid(null).setSku2Sid(null);
                    groupMap.put(String.valueOf(item.getMaterialSid())+"-"+String.valueOf(item.getPriceTax()), item);
                }
                else {
                    quantity = base.getQuantity().add(item.getQuantity()==null?BigDecimal.ZERO:item.getQuantity());
                    base.setQuantity(quantity);
                    amount = base.getCurrencyAmountTax().add(item.getCurrencyAmountTax()==null?BigDecimal.ZERO:item.getCurrencyAmountTax());
                    base.setCurrencyAmountTax(amount);
                    groupMap.put(String.valueOf(item.getMaterialSid())+"-"+String.valueOf(item.getPriceTax()), base);
                }
            }
            for (FinPurchaseInvoiceItemInfoRequest value : groupMap.values()) {
                List<FinPurchaseInvoiceItemInfoRequest> list = infoMap.get(String.valueOf(value.getMaterialSid())+"-"+String.valueOf(value.getPriceTax()));
                List<FinPurchaseInvoiceItemChildRequest> listItem = BeanCopyUtils.copyListProperties(
                        list, FinPurchaseInvoiceItemChildRequest::new);
                value.setChildren(listItem);
                response.add(value);
            }
            if (CollectionUtil.isNotEmpty(response)){
                return AjaxResult.success(response);
            }
        }
        return AjaxResult.success(new ArrayList<>());
    }

}
