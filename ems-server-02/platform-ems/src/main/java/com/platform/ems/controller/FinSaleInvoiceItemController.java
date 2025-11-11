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
import com.platform.ems.domain.dto.request.financial.FinSaleInvoiceItemChildRequest;
import com.platform.ems.domain.dto.request.financial.FinSaleInvoiceItemInfoRequest;
import com.platform.ems.domain.dto.request.form.FinSaleInvoiceItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinSaleInvoiceItemFormResponse;
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

import com.platform.ems.domain.FinSaleInvoiceItem;
import com.platform.ems.service.IFinSaleInvoiceItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售发票明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-17
 */
@RestController
@RequestMapping("/fin/sale/invoice/item")
@Api(tags = "销售发票明细报表")
public class FinSaleInvoiceItemController extends BaseController {

    @Autowired
    private IFinSaleInvoiceItemService finSaleInvoiceItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询销售发票明细报表列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售发票明细报表列表", notes = "查询销售发票明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinSaleInvoiceItem.class))
    public TableDataInfo list(@RequestBody FinSaleInvoiceItem finSaleInvoiceItem) {
        startPage(finSaleInvoiceItem);
        List<FinSaleInvoiceItem> list = finSaleInvoiceItemService.selectFinSaleInvoiceItemList(finSaleInvoiceItem);
        return getDataTable(list);
    }

    /**
     * 导出销售发票明细报表列表
     */
    @ApiOperation(value = "导出销售发票明细报表列表", notes = "导出销售发票明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinSaleInvoiceItemFormRequest request) throws IOException {
        FinSaleInvoiceItem finSaleInvoiceItem = new FinSaleInvoiceItem();
        BeanUtil.copyProperties(request,finSaleInvoiceItem);
        List<FinSaleInvoiceItem> responseList = finSaleInvoiceItemService.getReportForm(finSaleInvoiceItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinSaleInvoiceItem> util = new ExcelUtil<>(FinSaleInvoiceItem.class,dataMap);
        util.exportExcel(response, responseList, "销售开票明细报表");

    }


    /**
     * 获取销售发票明细报表详细信息
     */
    @ApiOperation(value = "获取销售发票明细报表详细信息", notes = "获取销售发票明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinSaleInvoiceItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long saleInvoiceItemSid) {
        if (saleInvoiceItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finSaleInvoiceItemService.selectFinSaleInvoiceItemById(saleInvoiceItemSid));
    }

    @ApiOperation(value = "销售开票明细报表查询", notes = "销售开票明细报表查询")
    @PostMapping("/getReportForm")
    public TableDataInfo getReportForm(@RequestBody FinSaleInvoiceItemFormRequest request) {
        FinSaleInvoiceItem finSaleInvoiceItem = new FinSaleInvoiceItem();
        BeanUtil.copyProperties(request,finSaleInvoiceItem);
        startPage(finSaleInvoiceItem);
        List<FinSaleInvoiceItem> requestList = finSaleInvoiceItemService.getReportForm(finSaleInvoiceItem);
        return getDataTable(requestList, FinSaleInvoiceItemFormResponse::new);
    }

    @ApiOperation(value = "明细汇总页签", notes = "明细汇总页签")
    @PostMapping("/groupCount")
    public AjaxResult groupCount(@RequestBody List<FinSaleInvoiceItemInfoRequest> request) {
        List<FinSaleInvoiceItemInfoRequest> response = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(request)){
            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal amount = BigDecimal.ZERO;
            Map<String, FinSaleInvoiceItemInfoRequest> groupMap = new HashMap<>();
            Map<String, List<FinSaleInvoiceItemInfoRequest>> infoMap = new HashMap<>();
            List<FinSaleInvoiceItemInfoRequest> temp = new ArrayList<>();
            FinSaleInvoiceItemInfoRequest base = new FinSaleInvoiceItemInfoRequest();
            for (FinSaleInvoiceItemInfoRequest item : request) {
                item.setKey(String.valueOf(item.getMaterialSid())+"-"+String.valueOf(item.getPriceTax()));
                // 明细分组处理
                FinSaleInvoiceItemInfoRequest itemItem = new FinSaleInvoiceItemInfoRequest();
                BeanCopyUtils.copyProperties(item, itemItem);
                temp = infoMap.get(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()));
                if (temp != null){
                    temp.add(itemItem);
                    infoMap.put(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()), temp);
                }
                else {
                    infoMap.put(String.valueOf(itemItem.getMaterialSid())+"-"+String.valueOf(itemItem.getPriceTax()), new ArrayList<FinSaleInvoiceItemInfoRequest>(){{add(itemItem);}});
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
            for (FinSaleInvoiceItemInfoRequest value : groupMap.values()) {
                List<FinSaleInvoiceItemInfoRequest> list = infoMap.get(String.valueOf(value.getMaterialSid())+"-"+String.valueOf(value.getPriceTax()));
                List<FinSaleInvoiceItemChildRequest> listItem = BeanCopyUtils.copyListProperties(
                        list, FinSaleInvoiceItemChildRequest::new);
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
