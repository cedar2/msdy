package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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
import cn.hutool.core.util.ArrayUtil;
import javax.validation.Valid;
import com.platform.ems.domain.FinPurchaseInvoiceDiscount;
import com.platform.ems.service.IFinPurchaseInvoiceDiscountService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 采购发票-折扣Controller
 *
 * @author linhongwei
 * @date 2021-04-20
 */
@RestController
@RequestMapping("/purchase/discount")
@Api(tags = "采购发票-折扣")
public class FinPurchaseInvoiceDiscountController extends BaseController {

    @Autowired
    private IFinPurchaseInvoiceDiscountService finPurchaseInvoiceDiscountService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    /**
     * 查询采购发票-折扣列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购发票-折扣列表", notes = "查询采购发票-折扣列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscount.class))
    public TableDataInfo list(@RequestBody FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        startPage();
        List<FinPurchaseInvoiceDiscount> list = finPurchaseInvoiceDiscountService.selectFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscount);
        return getDataTable(list);
    }

    /**
     * 导出采购发票-折扣列表
     */
    @ApiOperation(value = "导出采购发票-折扣列表", notes = "导出采购发票-折扣列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) throws IOException {
        List<FinPurchaseInvoiceDiscount> list = finPurchaseInvoiceDiscountService.selectFinPurchaseInvoiceDiscountList(finPurchaseInvoiceDiscount);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<FinPurchaseInvoiceDiscount> util = new ExcelUtil<FinPurchaseInvoiceDiscount>(FinPurchaseInvoiceDiscount.class,dataMap);
        util.exportExcel(response, list, "采购发票-折扣");
    }

    /**
     * 获取采购发票-折扣详细信息
     */
    @ApiOperation(value = "获取采购发票-折扣详细信息", notes = "获取采购发票-折扣详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPurchaseInvoiceDiscount.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseInvoiceDiscountSid) {
                    if(purchaseInvoiceDiscountSid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(finPurchaseInvoiceDiscountService.selectFinPurchaseInvoiceDiscountById(purchaseInvoiceDiscountSid));
    }

    /**
     * 新增采购发票-折扣
     */
    @ApiOperation(value = "新增采购发票-折扣", notes = "新增采购发票-折扣")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        return toAjax(finPurchaseInvoiceDiscountService.insertFinPurchaseInvoiceDiscount(finPurchaseInvoiceDiscount));
    }

    /**
     * 修改采购发票-折扣
     */
    @ApiOperation(value = "修改采购发票-折扣", notes = "修改采购发票-折扣")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody FinPurchaseInvoiceDiscount finPurchaseInvoiceDiscount) {
        return toAjax(finPurchaseInvoiceDiscountService.updateFinPurchaseInvoiceDiscount(finPurchaseInvoiceDiscount));
    }

    /**
     * 删除采购发票-折扣
     */
    @ApiOperation(value = "删除采购发票-折扣", notes = "删除采购发票-折扣")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  purchaseInvoiceDiscountSids) {
        if(ArrayUtil.isEmpty( purchaseInvoiceDiscountSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(finPurchaseInvoiceDiscountService.deleteFinPurchaseInvoiceDiscountByIds(purchaseInvoiceDiscountSids));
    }
}
