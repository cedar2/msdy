package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.CustomException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.ProcessHeadExportDto;
import com.platform.ems.domain.dto.SalSaleOrderReportProductResponseXX;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.form.SalSaleOrderProcessTracking;
import com.platform.ems.domain.dto.response.form.SalSaleProductCostForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.SalSalesOrderItemMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.ExcelStyleUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.config.MinioConfig;

import cn.hutool.core.util.ArrayUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 销售订单Controller
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/sales/order")
@Api(tags = "销售订单")
public class SalSalesOrderController extends BaseController {

    @Autowired
    private ISalSalesOrderService salSalesOrderService;
    @Autowired
    private ISalSalesOrderItemService salSalesOrderItemService;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private ISalSalesOrderAttachmentService salSalesOrderAttachmentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询销售订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询销售订单列表", notes = "查询销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public TableDataInfo list(@RequestBody SalSalesOrder salSalesOrder) {
        startPage(salSalesOrder);
        List<SalSalesOrder> list = salSalesOrderService.selectSalSalesOrderList(salSalesOrder);
        return getDataTable(list);
    }

    @PostMapping("/total/head")
    @ApiOperation(value = "查询销售统计报表-主", notes = "查询销售统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = OrderTotalResponse.class))
    public TableDataInfo getTotal(@RequestBody OrderTotalRequest request) {
        startPage(request);
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        List<OrderTotalResponse> list = salSalesOrderItemService.getTotal(request);
        return getDataTable(list);
    }

    @PostMapping("/total/head/export")
    @ApiOperation(value = "销售统计报表导出", notes = "销售统计报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public void totalExport(HttpServletResponse response , OrderTotalRequest request) throws IOException {
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        List<OrderTotalResponse> list = salSalesOrderItemService.getTotal(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<OrderTotalResponse> util = new ExcelUtil<>(OrderTotalResponse.class, dataMap);
        util.exportExcel(response, list, "销售统计报表");
    }

    @PostMapping("/count/refresh")
    @ApiOperation(value = "订单明细页签-合计字段刷新", notes = "订单明细页签-合计字段刷新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public SalSalesOrder getCount(@RequestBody List<SalSalesOrderItem>  items) {
        return salSalesOrderService.getCount(items);
    }

    @PostMapping("/best/sell/head")
    @ApiOperation(value = "查询销售畅销报表-主", notes = "查询销售统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = OrderBestSellingResponse.class))
    public TableDataInfo getBestSell(@RequestBody OrderBestSellingRequest request) {
        startPage(request);
        if (request.getDocumentBeginTime() == null && request.getDocumentEndTime() == null) {
            LocalDate localDateBegin = LocalDate.now().minusDays(90);
            LocalDate localDateEnd = LocalDate.now();
            request.setDocumentBeginTime(localDateBegin.toString()).setDocumentEndTime(localDateEnd.toString());
        }
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        request.setDocumentTypeList(new String[]{"SO","CSB"});
        List<OrderBestSellingResponse> list = salSalesOrderItemService.getBestSell(request);
        return getDataTable(list);
    }

    @PostMapping("/best/sell/item")
    @ApiOperation(value = "查询销售畅销报表-明细", notes = "查询销售统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = OrderBestSellingResponse.class))
    public TableDataInfo getBestSellItem(@RequestBody OrderBestSellingRequest request) {
        startPage(request);
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        request.setDocumentTypeList(new String[]{"SO","CSB"});
        if(request.getDimension()==null){
            request.setDimension("quantity");
        }
        List<OrderBestSellingResponse> list = salSalesOrderItemService.getBestSellItem(request);
        return getDataTable(list);
    }

    @PostMapping("/best/sell/item/export")
    @ApiOperation(value = "导出销售畅销报表明细数据", notes = "导出销售畅销报表明细数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public void bestSellItemExport(HttpServletResponse response ,OrderBestSellingRequest request) throws IOException {
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        request.setDocumentTypeList(new String[]{"SO","CSB"});
        if(request.getDimension()==null){
            request.setDimension("quantity");
        }
        List<OrderBestSellingResponse> list = salSalesOrderItemService.getBestSellItem(request);
        if(CollectionUtil.isNotEmpty(list)){
            list.forEach(li->{
                li.setDocumentBeginTime(request.getDocumentBeginTime())
                        .setDocumentEndTime(request.getDocumentEndTime());
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<OrderBestSellingResponse> util = new ExcelUtil<>(OrderBestSellingResponse.class, dataMap);
        util.exportExcel(response, list, "畅销款排行榜明细");
    }

    @PostMapping("/total/item")
    @ApiOperation(value = "查询销售统计报表-明细", notes = "查询销售统计报表明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getTotalItem(@RequestBody OrderTotalRequest request) {
        startPage(request);
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(),HandleStatus.CONFIRMED.getCode()});
        List<OrderTotalResponse> list = salSalesOrderItemService.getTotalItem(request);
        return getDataTable(list);
    }

    @PostMapping("/process/head")
    @ApiOperation(value = "查询销售进度报表-主", notes = "查询销售进度报表-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public TableDataInfo processHead(@RequestBody SaleOrderProgressRequest request) {
        startPage(request);
        return getDataTable(salSalesOrderItemService.getProcessHead(request));
    }

    @PostMapping("/process/head/export")
    @ApiOperation(value = "销售进度报表导出", notes = "销售进度报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public void processHeadExport(HttpServletResponse response , SaleOrderProgressRequest request) throws IOException {
        List<SaleOrderProgressResponse> list = salSalesOrderItemService.getProcessHead(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ProcessHeadExportDto> util = new ExcelUtil<>(ProcessHeadExportDto.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, ProcessHeadExportDto::new), "销售进度");
    }

    @PostMapping("/delivery/Process")
    @ApiOperation(value = "查询销售状况交期报表-主", notes = "查询销售状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getDeliveryProcess(@RequestBody OrderProgressRequest request) {
        startPage(request);
        List<OrderProgressResponse> list = salSalesOrderItemService.getDeliveryProcess(request);
        return getDataTable(list);
    }

    @PostMapping("/delivery/Process/item")
    @ApiOperation(value = "查询销售状况交期报表-明细", notes = "查询销售状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getDeliveryProcessItem(@RequestBody OrderProgressRequest request) {
        startPage(request);
        List<OrderProgressItemResponse> list = salSalesOrderItemService.getDeliveryProcessItem(request);
        TableDataInfo dataTable = getDataTable(list);
        if(CollectionUtil.isNotEmpty(list)){
            list = salSalesOrderItemService.sortProgressItem(list);
            dataTable.setRows(list);
        }
        return dataTable;
    }

    @PostMapping("/delivery/Process/item/export")
    @ApiOperation(value = "导出销售状况交期报表", notes = "导出销售状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public void ProgressExport(HttpServletResponse response , OrderProgressRequest request) throws IOException {
        List<OrderProgressItemResponse> list = salSalesOrderItemService.getDeliveryProcessItem(request);
        if(CollectionUtil.isNotEmpty(list)){
            list = salSalesOrderItemService.sortProgressItem(list);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<OrderProgressItemResponse> util = new ExcelUtil<>(OrderProgressItemResponse.class, dataMap);
        util.exportExcel(response, list, "销售交期状况报表明细");
    }

    @PostMapping("/process/item")
    @ApiOperation(value = "查询销售进度报表-明细", notes = "查询销售进度报表-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public TableDataInfo processItem(@RequestBody SaleOrderProgressRequest request) {
        startPage(request);
        return getDataTable(salSalesOrderItemService.getProcessItem(request));
    }

    /**
     * 录入合同号
     */
    @ApiOperation(value = "录入合同号", notes = "录入合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/contract")
    public AjaxResult setConstract(@RequestBody SalSaleOrderSetRequest request) {
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        BeanCopyUtils.copyProperties(request,salSalesOrder);
        return AjaxResult.success(salSalesOrderService.setConstract(salSalesOrder));
    }

    /**
     * 变更合同号
     */
    @ApiOperation(value = "变更合同号", notes = "变更合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/contractCode")
    public AjaxResult setConstractCode(@RequestBody SalSaleOrderSetRequest request) {
        if (request.getSalesOrderSid() == null) {
            throw new BaseException("请选择行");
        }
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        BeanCopyUtils.copyProperties(request,salSalesOrder);
        return AjaxResult.success(salSalesOrderService.setConstractCode(salSalesOrder));
    }

    /**
     *设置委托人
     */
    @ApiOperation(value = "设置委托人", notes = "设置委托人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/set/trustorAccount")
    public AjaxResult setTrustor(@RequestBody OrderTrustorAccountRequest request) {
        return AjaxResult.success(salSalesOrderService.setTrustor(request));
    }

    /**
     * 录入合同号
     */
    @ApiOperation(value = "创建合同信息校验", notes = "创建合同信息校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/add/contract")
    public AjaxResult addConstract(@RequestBody List<Long> salesOrderSids) {
        return AjaxResult.success(salSalesOrderService.addConstract(salesOrderSids));
    }

    /**
     * 创建合同信息
     */
    @ApiOperation(value = "创建合同信息", notes = "创建合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/contract/add")
    public AjaxResult toConstract(@RequestBody List<SalSalesOrder> salSalesOrderList, String jump) {
        return AjaxResult.success(salSalesOrderService.constractAdd(salSalesOrderList, jump));
    }

    /**
     * 校验核销状态
     */
    @ApiOperation(value = "校验核销状态", notes = "校验核销状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/judge")
    public AjaxResult judge(@RequestBody orderJudgeRequest request) {
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        salSalesOrder.setSalesOrderSid(request.getSalesOrderSid())
                .setItemNumList(request.getItemNumList());
        return AjaxResult.success(salSalesOrderService.judgeReceipt(salSalesOrder));
    }

    /**
     * 导出销售订单列表
     */
    @ApiOperation(value = "导出物料销售订单、商品销售订单、客户寄售结算单列表", notes = "导出销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SalSalesOrder salSalesOrder) throws IOException {
        List<SalSalesOrder> list = salSalesOrderService.selectSalSalesOrderList(salSalesOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SaleOrderExResponse> util = new ExcelUtil<>(SaleOrderExResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SaleOrderExResponse::new), "销售订单");
    }

    @ApiOperation(value = "导出销售订单排采进度报表", notes = "导出销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/product")
    public void exportP(HttpServletResponse response, SalSalesOrderItem salSalesOrderItem) throws IOException {
        salSalesOrderItem.setIsManufacture(ConstantsEms.YES);
        salSalesOrderItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        List<SalSalesOrderItem> list = salSalesOrderItemService.getItemListProduct(salSalesOrderItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleOrderReportProductResponse> util = new ExcelUtil<>(SalSaleOrderReportProductResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SalSaleOrderReportProductResponse::new), "销售订单排采进度");
    }

    /**
     * 导出销售订单明细
     */
    @ApiOperation(value = "导出销售订单明细", notes = "导出销售订单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, SalSalesOrder salSalesOrder) throws IOException {
        String materialCategory = salSalesOrder.getMaterialCategory();
        if(ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
            salSalesOrderService.exportWl(response,salSalesOrder.getSalesOrderSids());
        }else{
            salSalesOrderService.exportGood(response,salSalesOrder.getSalesOrderSids());
        }
    }

    /**
     * 获取销售订单详细信息
     */
    @ApiOperation(value = "获取销售订单详细信息", notes = "获取销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long salesOrderSid) {
        if (salesOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        SalSalesOrder salSalesOrder = salSalesOrderService.selectSalSalesOrderById(salesOrderSid);
        return AjaxResult.success(salSalesOrder);
    }

    /**
     * 获取销售订单详细信息的明细汇总页签
     */
    @ApiOperation(value = "获取销售订单详细信息的明细汇总页签", notes = "获取销售订单详细信息的明细汇总页签")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/getItemTotalList")
    public AjaxResult getItemTotalList(@RequestBody SalSalesOrder request) {
        if (request != null && CollectionUtil.isNotEmpty(request.getSalSalesOrderItemList())) {
            return AjaxResult.success(salSalesOrderService.getItemTotalList(request, request.getSalSalesOrderItemList()));
        }
        return AjaxResult.success(new SalSalesOrder());
    }

    /**
     * 获取销售订单详细信息
     */
    @ApiOperation(value = "销售发货单获取销售订单详细信息", notes = "销售发货单获取销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/getInfo/delivery")
    public AjaxResult getInfoDelievery(Long salesOrderSid) {
        if (salesOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        SalSalesOrder salSalesOrder = salSalesOrderService.selectSalSalesOrderById(salesOrderSid);
        SalSalesOrder order = salSalesOrderItemService.handleIndexDelievery(salSalesOrder);
        return AjaxResult.success(order);
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/processCheck")
    public AjaxResult processCheck(Long salesOrderSid) {
        if (salesOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesOrderService.processCheck(salesOrderSid));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "多笔提交校验-最新版（批量报错）", notes = "多笔提交校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/processCheck/list")
    public AjaxResult processCheck(@RequestBody OrderErrRequest request) {
        return AjaxResult.success(salSalesOrderService.checkList(request));
    }

    /**
     * 撤回保存前的校验
     */
    @ApiOperation(value = "撤回保存前的校验", notes = "撤回保存前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/back/save/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSaveVerify(@RequestBody SalSalesOrder salesOrder) {
        if (salesOrder.getSalesOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesOrderService.backSaveVerify(salesOrder));
    }

    /**
     * 撤回保存
     */
    @ApiOperation(value = "撤回保存", notes = "撤回保存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/back/save")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSave(@RequestBody SalSalesOrder salesOrder) {
        if (salesOrder.getSalesOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        if (StrUtil.isBlank(salesOrder.getComment())) {
            throw new CheckedException("撤回说明不能为空");
        }
        return toAjax(salSalesOrderService.backSave(salesOrder));
    }

    /**
     * 维护物流信息
     */
    @ApiOperation(value = "维护物流信息", notes = "维护物流信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/set/carrier")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult setCarrier(@RequestBody SalSalesOrder salesOrder) {
        if (salesOrder.getSalesOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.setCarrier(salesOrder));
    }

    /**
     * 维护纸质合同号
     */
    @ApiOperation(value = "维护纸质合同号", notes = "维护纸质合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/set/paperContract")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult setPaperContract(@RequestBody SalSalesOrder salesOrder) {
        if (salesOrder.getSalesOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesOrderService.setPaperContract(salesOrder));
    }

    /**
     * 新建直接点提交/编辑点提交
     */
    @ApiOperation(value = "新建直接点提交/编辑点提交", notes = "新建直接点提交/编辑点提交")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody SalSalesOrder salSalesOrder, String jump) {
        return salSalesOrderService.submit(salSalesOrder, jump);
    }

    @ApiOperation(value = "设置是否首缸", notes = "设置是否首缸")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/set/shougang")
    public AjaxResult setShougang(@RequestBody SalSalesOrderItemSetRequest request) {
        return AjaxResult.success(salSalesOrderService.setShouGang(request));
    }

    @ApiOperation(value = "设置到期天数", notes = "设置到期天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/set/toexpire")
    public AjaxResult setToexpire(@RequestBody OrderItemToexpireRequest request) {
        return AjaxResult.success(salSalesOrderService.setToexpireDays(request));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "多笔提交-有免费行提醒", notes = "多笔提交校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/processCheck/list/freeFlag")
    public AjaxResult processCheckFlag(@RequestBody List<Long> sidList) {
        if (CollectionUtil.isEmpty(sidList)) {
            throw new CheckedException("参数缺失");
        }
        return salSalesOrderService.checkListFree(sidList);
    }

    /**
     * 物料需求测算报表 销售订单
     */
    @ApiOperation(value = "物料需求测算报表-销售订单", notes = "物料需求测算报表-销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(salSalesOrderService.getOrder(orderList));
    }

    @ApiOperation(value = "物料需求测算报表-跳转出库", notes = "物料需求测算报表-跳转出库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/skip/ck")
    public AjaxResult skipCHK(@RequestBody TecBomItemReportRequest request) {
        return AjaxResult.success(salSalesOrderService.hadnleItem(request));
    }

    /**
     * 拷贝销售订单详细信息
     */
    @ApiOperation(value = "拷贝销售订单详细信息", notes = "拷贝销售订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long salesOrderSid) {
        if (salesOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(salSalesOrderService.copy(salesOrderSid));
    }

    /**
     * 新增销售订单
     */
    @ApiOperation(value = "新增销售订单", notes = "新增销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid SalSalesOrder salSalesOrder) {
        int row = salSalesOrderService.insertSalSalesOrder(salSalesOrder);
        if (row > 0) {
            return AjaxResult.success("操作成功", new SalSalesOrder().setSalesOrderSid(salSalesOrder.getSalesOrderSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改面辅料状态、客供料状态", notes = "修改面辅料状态、客供料状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/status")
    public AjaxResult editStatus(@RequestBody  OrderItemStatusRequest order) {
        return toAjax(salSalesOrderService.changeStatus(order));
    }

    /**
     * 修改销售订单
     */
    @ApiOperation(value = "修改销售订单", notes = "修改销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮",interval=3000)
    public AjaxResult edit(@RequestBody @Valid SalSalesOrder salSalesOrder) {
        return toAjax(salSalesOrderService.updateSalSalesOrder(salSalesOrder));
    }

    /**
     * 删除销售订单
     */
    @ApiOperation(value = "删除销售订单", notes = "删除销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] salesOrderSids) {
        if (ArrayUtil.isEmpty(salesOrderSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.deleteSalSalesOrderByIds(salesOrderSids));
    }

    /**
     * 作废销售订单校验
     */
    @ApiOperation(value = "作废销售订单校验", notes = "作废销售订单校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/disuse/judge")
    public AjaxResult disuseJudge(@RequestBody List<Long> salesOrderSids) {
        if (ArrayUtil.isEmpty(salesOrderSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.disuseJudge(salesOrderSids));
    }

    /**
     * 作废销售订单
     */
    @ApiOperation(value = "作废销售订单", notes = "作废销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/disuse")
    public AjaxResult disuse(@RequestBody OrderInvalidRequest request) {
        if (request==null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.disuse(request));
    }

    /**
     * 作废销售订单明细
     */
    @ApiOperation(value = "作废销售订单明细", notes = "作废销售订单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/disuse")
    public AjaxResult itemDisuse(@RequestBody OrderInvalidRequest request) {
        if (request == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.itemDisuse(request));
    }

    /**
     * 销售订单确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "销售订单确认", notes = "销售订单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult confirm(@RequestBody SalSalesOrder salSalesOrder) {
        return AjaxResult.success(salSalesOrderService.confirm(salSalesOrder));
    }

    /**
     * 销售订单关闭
     */
    @PostMapping("/close")
    @ApiOperation(value = "销售订单关闭", notes = "销售订单关闭")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult close(@RequestBody SalSalesOrder salSalesOrder) {
        return AjaxResult.success(salSalesOrderService.close(salSalesOrder));
    }

    /**
     * 销售订单明细关闭
     */
    @PostMapping("/item/close")
    @ApiOperation(value = "销售订单明细关闭", notes = "销售订单明细关闭")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult itemClose(@RequestBody SalSalesOrderItem salSalesOrderItem) {
        if (salSalesOrderItem == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(salSalesOrderService.itemClose(salSalesOrderItem));
    }

    @PostMapping("/set/ProducePlant")
    @ApiOperation(value = "设置负责生产工厂", notes = "设置负责生产工厂")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult updateOrderProducePlant(@RequestBody OrderProducePlantRequest requet) {
        return AjaxResult.success(salSalesOrderService.updateOrderProducePlant(requet));
    }

    /**
     * 销售订单变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "销售订单变更", notes = "销售订单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid SalSalesOrder salSalesOrder) {
        return AjaxResult.success(salSalesOrderService.change(salSalesOrder));
    }

    @PostMapping("/getMaterialInfo")
    @ApiOperation(value = "获取物料档案明细", notes = "获取物料档案明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public AjaxResult getMaterialInfo(@RequestBody BasSaleOrderRequest basSaleOrderRequest) {
        return AjaxResult.success(salSalesOrderService.getMaterialInfo(basSaleOrderRequest));
    }

    @PostMapping("/get/price")
    @ApiOperation(value = "获取价格", notes = "获取价格")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public AjaxResult getPrice(@RequestBody BasSaleOrderRequest basSaleOrderRequest) {
        return salSalesOrderService.updatePrice(basSaleOrderRequest);
    }

    /**
     * 报表更新采购价
     */
    @ApiOperation(value = "报表更新销售价", notes = "报表更新销售价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/update/price")
    public AjaxResult updatePrice(@RequestBody List<SalSalesOrderItem> request) {
        return salSalesOrderService.updatePrice(request);
    }

    /**
     * 销售订单明细报表
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "销售订单明细报表", notes = "销售订单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    public TableDataInfo list(@RequestBody SalSalesOrderItem salSalesOrderItem) {
        startPage(salSalesOrderItem);
        List<SalSalesOrderItem> list = salSalesOrderItemService.getItemList(salSalesOrderItem);
        TableDataInfo data = getDataTable(list);
        List<SalSalesOrderItem> salSalesOrderItems = salSalesOrderItemService.handleIndex(list);
        data.setRows(salSalesOrderItems);
        return data;
    }

    /**
     * 销售订单明细报表
     */
    @PostMapping("/getItemList/product")
    @ApiOperation(value = "销售订单排产进度报表", notes = "销售订单排产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    public TableDataInfo listProduct(@RequestBody SalSalesOrderItem salSalesOrderItem) {
        if (salSalesOrderItem.getSort() != null && "mob".equals(salSalesOrderItem.getSort())) {
            salSalesOrderItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            salSalesOrderItem.setIsManufacture(ConstantsEms.YES);
            startPage(salSalesOrderItem);
            List<SalSalesOrderItem> list = salSalesOrderItemService.mobPaichan(salSalesOrderItem);
            return  getDataTable(list);
        }
        salSalesOrderItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        salSalesOrderItem.setIsManufacture(ConstantsEms.YES);
        startPage(salSalesOrderItem);
        List<SalSalesOrderItem> list = salSalesOrderItemService.getItemListProduct(salSalesOrderItem);
        return  getDataTable(list);
    }

    /**
     * 销售订单明细报表
     */
    @PostMapping("/paichan/mob")
    @ApiOperation(value = "销售订单排产进度报表", notes = "销售订单排产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    public TableDataInfo mobPaichan(@RequestBody SalSalesOrderItem salSalesOrderItem) {
        salSalesOrderItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        startPage(salSalesOrderItem);
        List<SalSalesOrderItem> list = salSalesOrderItemService.mobPaichan(salSalesOrderItem);
        return  getDataTable(list);
    }

    @ApiOperation(value = "导出销售订单排产进度报表", notes = "导出销售订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/getItemList/product")
    public void exportPX(HttpServletResponse response, SalSalesOrderItem salSalesOrderItem) throws IOException {
        salSalesOrderItem.setIsManufacture(ConstantsEms.YES);
        salSalesOrderItem.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        List<SalSalesOrderItem> list = salSalesOrderItemService.getItemListProduct(salSalesOrderItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleOrderReportProductResponseXX> util = new ExcelUtil<>(SalSaleOrderReportProductResponseXX.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SalSaleOrderReportProductResponseXX::new), "销售订单排产进度");
    }

    @ApiOperation(value = "设置盖章件签收", notes = "设置盖章件签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign/status")
    public AjaxResult signStsu(@RequestBody  OrderItemStatusSignRequest order) {
        return toAjax(salSalesOrderService.setSignStatus(order));
    }

    @ApiOperation(value = "设置下单状态", notes = "设置下单状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/material/order/status")
    public AjaxResult setStatus(@RequestBody  MaterialOrderRequest order) {
        return toAjax(salSalesOrderService.setMaterialOrder(order));
    }

    @ApiOperation(value = "设置首批", notes = "设置首批")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/shoupi")
    public AjaxResult setShouPi(@RequestBody  OrderItemShouPiRequest order) {
        return toAjax(salSalesOrderService.setShouPi(order));
    }

    /**
     * 导出销售订单明细报表
     */
    @ApiOperation(value = "导出销售订单明细报表", notes = "导出销售订单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, SalSalesOrderItem salSalesOrderItem) throws IOException {
        String specialBusCategory = salSalesOrderItem.getSpecialBusCategory();
        List<SalSalesOrderItem> list = salSalesOrderItemService.getItemList(salSalesOrderItem);
        list = salSalesOrderItemService.handleIndex(list);
        String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPrice();
        list.forEach(li->{
            if(!ConstantsEms.YES.equals(isViewPrice)){
                li.setSalePriceTax(null)
                        .setSalePrice(null)
                        .setPriceTax(null)
                        .setPrice(null);
            }
        });
        if(ConstantsEms.CUSTOMER_SPECIAL_BUS_CATEGORY.equals(specialBusCategory)){
            Map<String, Object> dataMap = sysDictDataService.getDictDataList();
            ExcelUtil<SalSaleOrderReportCusResponse> util = new ExcelUtil<>(SalSaleOrderReportCusResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SalSaleOrderReportCusResponse::new), "客户寄售结算单明细报表");
        }else{
            Map<String, Object> dataMap = sysDictDataService.getDictDataList();
            ExcelUtil<SalSaleOrderReportResponse> util = new ExcelUtil<>(SalSaleOrderReportResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SalSaleOrderReportResponse::new), "销售订单明细报表");
        }
    }

    /**
     * 物料需求报表(商品销售订单)
     */
    @ApiOperation(value = "物料需求报表(商品销售订单)", notes = "物料需求报表(商品销售订单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/getListByCode")
    public AjaxResult getMaterialRequireListByCode(Long salesOrderCode) {
        if (salesOrderCode == null) {
            throw new CheckedException("请输入销售订单号");
        }
        return AjaxResult.success(salSalesOrderService.getMaterialRequireListByCode(salesOrderCode));
    }

    /**
     * 物料需求报表(商品销售订单)
     */
    @ApiOperation(value = "物料需求报表按款色", notes = "物料需求报表按款色")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomItemReport.class))
    @PostMapping("/getMaterialRequireRportFormsList")
    public AjaxResult getMaterialRequireRportFormsList(@RequestBody List<MaterialReportFormsRequest> request) {
        List<TecBomItem> tecBomItems = BeanCopyUtils.copyListProperties(request, TecBomItem::new);
        tecBomItems.stream().forEach(materialReportFormsRequest -> {
            if (null == materialReportFormsRequest.getMaterialSid() || null == materialReportFormsRequest.getSku1Sid()) {
                throw new CheckedException("参数不能为空");
            }
        });
        List<TecBomItem> list = salSalesOrderService.getMaterialRequireListByCode2(tecBomItems);
        List<TecBomItemReport> tecBomItemReports = BeanCopyUtils.copyListProperties(list, TecBomItemReport::new);
        tecBomItemReports.forEach(li->{
            li.setRequireQuantity(li.getRequireQuantityView())
                    .setLossRequireQuantity(li.getLossRequireQuantityView());
            if(li.getLossInnerQuantity()!=null){
                li.setQuantityLossRate(li.getLossInnerQuantity().divide(BigDecimal.ONE,BigDecimal.ROUND_HALF_UP,4))   ;
            }
            if(li.getQuantity()!=null){
                li.setQuantity(li.getQuantity().divide(BigDecimal.ONE,BigDecimal.ROUND_HALF_UP,4));
            }
            li.setProductQuantity(null);
        });
        return AjaxResult.success(tecBomItemReports);
    }

    /**
     * 物料需求报表(商品销售订单)
     */
    @ApiOperation(value = "物料需求报表按款色码", notes = "物料需求报表按款色码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomZipperItemReport.class))
    @PostMapping("/getMaterialRequireRportZipperFormsList")
    public AjaxResult getMaterialRequireRportFormsZipperList(@RequestBody List<MaterialReportZipperFormsRequest> request) {
        List<TecBomItem> tecBomItems = BeanCopyUtils.copyListProperties(request, TecBomItem::new);
        tecBomItems.stream().forEach(materialReportFormsRequest -> {
            if (null == materialReportFormsRequest.getMaterialSid() || null == materialReportFormsRequest.getSku1Sid()) {
                throw new CheckedException("参数不能为空");
            }
        });
        List<TecBomItem> list = salSalesOrderService.getMaterialZipper(tecBomItems);
        List<TecBomZipperItemReport> tecBomZipperItemReports = BeanCopyUtils.copyListProperties(list, TecBomZipperItemReport::new);
        tecBomZipperItemReports.forEach(li->{
            li.setRequireQuantity(li.getRequireQuantityView())
                    .setLossRequireQuantity(li.getLossRequireQuantityView());
        });
        return AjaxResult.success(tecBomZipperItemReports);
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody SalSalesOrderAttachment salSalesOrderAttachment) {
        return salSalesOrderAttachmentService.check(salSalesOrderAttachment);
    }

    @ApiOperation(value = "新增销售订单查询页面上传附件", notes = "新增销售订单查询页面上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售合同 - 上传附件", businessType = BusinessType.INSERT)
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid SalSalesOrderAttachment salSalesOrderAttachment) {
        int row = salSalesOrderAttachmentService.insertSalSalesOrderAttachment(salSalesOrderAttachment);
        if (row > 0) {
            if (ConstantsOrder.PAPER_CONTRACT_XSDDHT.equals(salSalesOrderAttachment.getFileType())) {
                salSalesOrderService.update(new UpdateWrapper<SalSalesOrder>().lambda()
                        .eq(SalSalesOrder::getSalesOrderSid, salSalesOrderAttachment.getSalesOrderSid())
                        .set(SalSalesOrder::getUploadStatus, ConstantsEms.CONTRACT_UPLOAD_STATUS_Y));
            }
        }
        return AjaxResult.success(row);
    }

    /**
     * 导入-商品销售订单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入-商品销售订单", notes = "导入-商品销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataSale(file));
    }

    @ApiOperation(value = "下载商品销售订单导入模板", notes = "下载商品销售订单导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_商品销售订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_商品销售订单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    /**
     * 导入-商品销售退货订单
     */
    @PostMapping("/import/re")
    @ApiOperation(value = "导入-商品销售退货订单", notes = "导入-商品销售退货订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataGBack(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataSaleBACK(file));
    }

    @ApiOperation(value = "下载商品销售订单导入模板", notes = "下载商品销售订单导入模板")
    @PostMapping("/importTemplate/re")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_商品销售退货订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_商品销售退货订单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    /**
     * 导入-客户寄售退结算单
     */
    @PostMapping("/import/cu")
    @ApiOperation(value = "导入-客户寄售退结算单", notes = "导入-客户寄售退结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataCu(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataSaleCus(file));
    }

    /**
     * 导入-物料销售订单
     */
    @PostMapping("/import/wl")
    @ApiOperation(value = "导入-物料销售订单", notes = "导入-物料销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataWl(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataSaleWl(file));
    }

    /**
     * 导入-物料销售退货单 --物料销售订单
     */
    @PostMapping("/import/wl/re")
    @ApiOperation(value = "导入-物料销售退货单", notes = "导入-物料销售退货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataSa(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataSaleWl(file));
    }

    @ApiOperation(value = "下载客户寄售退结算单导入模板", notes = "下载商品客户寄售退结算单导入模板")
    @PostMapping("/importTemplate/cu")
    public void importTemplateCu(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_客户寄售结算单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_客户寄售结算单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    @ApiOperation(value = "下载物料销售订单导入模板", notes = "下载物料销售订单导入模板")
    @PostMapping("/importTemplate/wl")
    public void importTemplateWl(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_物料销售订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料销售订单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    @ApiOperation(value = "下载物料销售退货订单导入模板", notes = "下载物料销售退货订单导入模板")
    @PostMapping("/importTemplate/wl/re")
    public void importTemplateWlR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_物料销售退货订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料销售退货订单_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    /**
     * 导入-物料需求测算商品明细
     */
    @PostMapping("/import/Material")
    @ApiOperation(value = "导入-物料需求测算商品明细", notes = "导入-物料需求测算商品明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataMaterial(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(salSalesOrderService.importDataMaterial(file));
    }

    @ApiOperation(value = "下载商品明细导入模板", notes = "下载商品明细导入模板")
    @PostMapping("/importTemplate/material")
    public void importTemplateMate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_商品明细_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_商品明细_V0.1.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    /**
     * 导出物料需求报表测算
     */
    @ApiOperation(value = "导出销售订单明细报表", notes = "导出销售订单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export/M")
    public void exportM(HttpServletResponse response, @RequestBody BasMaterialRequest basMaterialRequest) throws IOException {
        try {
            XSSFWorkbook  workbook = new XSSFWorkbook();
            CellStyle style = workbook.createCellStyle();
            Sheet sheet = workbook.createSheet("商品明细");
            sheet.setDefaultColumnWidth(15);
            String[] titles={"商品编码","SKU1名称","SKU2名称","数量 "};
            CellStyle cellStyle= ExcelStyleUtil.getDefaultCellStyle(workbook);
            ExcelStyleUtil.setCellStyleLime(cellStyle);
            //第一行数据
            Row rowBOMHead = sheet.createRow(0);
            rowBOMHead.setRowStyle(cellStyle);
            for (int i=0;i<titles.length;i++) {
                Cell cell = rowBOMHead.createCell(i);
                cell.setCellValue(titles[i]);
                cell.setCellStyle(cellStyle);
            }
            //物料需求
            String[] titleWLs={"商品编码","物料名称","物料sku1","采购类型","供应商","需求量（含损耗率）","需求量（不含损耗率）","可用库存量","bom用量单位","基本计量单位","单位换算比例","供方编码","幅宽","克重","成分","纱支","密度","规格尺寸","材质","物料类型"};
            Sheet sheet2 = workbook.createSheet("物料需求");
            //第一行数据 标题
            Row rowWL = sheet2.createRow(0);
            for (int i=0;i<titleWLs.length;i++) {
                Cell cell = rowWL.createCell(i);
                cell.setCellValue(titleWLs[i]);
            }
            //物料需求(按款色码)
            String[] titleKs={"款号","款名称","款颜色","款尺码","物料编码","物料名称","物料sku1","物料sku2","需求量（含损耗率）","需求量（不含损耗率）","可用库存量","bom用量单位","基本计量单位","采购类型","物料供应商","单位换算比例","供方编码","规格尺寸","材质","物料类型"};
            Sheet sheet3 = workbook.createSheet("物料需求（按款色码）");
            //第一行数据 标题
            Row rowK= sheet3.createRow(0);
            for (int i=0;i<titleKs.length;i++) {
                Cell cell = rowK.createCell(i);
                cell.setCellValue(titleKs[i]);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        }catch (Exception e){
            throw new CustomException("导出失败");
        }
    }

    /**
     * 移动端销售进度
     */
    @PostMapping("/mob/process")
    @ApiOperation(value = "移动端销售进度", notes = "移动端销售进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo mobProcess(@RequestBody SalSalesOrderItem order) {
        startPage(order);
        List<SalSalesOrderItem> list = salSalesOrderItemService.selectMobProcessList(order);
        return getDataTable(list);
    }

    /**
     * 销售出库进度跟踪报表
     */
    @PostMapping("/process/tracking")
    @ApiOperation(value = "销售出库进度跟踪报表", notes = "销售出库进度跟踪报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleOrderProcessTracking.class))
    @Idempotent(interval = 3000,message = "请勿重复查询")
    public TableDataInfo processTracking(@RequestBody SalSaleOrderProcessTracking request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<SalSaleOrderProcessTracking> total = salSalesOrderService.selectSalSaleProcessTrackingList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<SalSaleOrderProcessTracking> list = salSalesOrderService.selectSalSaleProcessTrackingList(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出销售出库进度跟踪报表
     */
    @ApiOperation(value = "导出销售出库进度跟踪报表", notes = "导出销售出库进度跟踪报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/process/tracking/export")
    public void overDueExport(HttpServletResponse response, SalSaleOrderProcessTracking request) throws IOException {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<SalSaleOrderProcessTracking> list = salSalesOrderService.selectSalSaleProcessTrackingList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleOrderProcessTracking> util = new ExcelUtil<>(SalSaleOrderProcessTracking.class, dataMap);
        util.exportExcel(response, list, "销售出库进度跟踪报表");
    }

    /**
     * 商品销售成本报表
     */
    @PostMapping("/product/cost")
    @ApiOperation(value = "商品销售成本报表", notes = "商品销售成本报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrderItem.class))
    public TableDataInfo productCost(@RequestBody SalSaleProductCostForm salSalesOrder) {
        int pageNum = salSalesOrder.getPageNum();
        salSalesOrder.setPageNum(null);
        List<SalSaleProductCostForm> total = salSalesOrderItemService.selectSalSalesProductCostList(salSalesOrder);
        if (CollectionUtil.isNotEmpty(total)) {
            salSalesOrder.setPageNum(pageNum);
            List<SalSaleProductCostForm> list = salSalesOrderItemService.selectSalSalesProductCostList(salSalesOrder);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, total.size());
    }

    /**
     * 导出商品销售成本报表
     */
    @ApiOperation(value = "导出商品销售成本报表", notes = "导出商品销售成本报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/product/cost/export")
    public void productCostExport(HttpServletResponse response, SalSaleProductCostForm request) throws IOException {
        List<SalSaleProductCostForm> list = salSalesOrderItemService.selectSalSalesProductCostList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SalSaleProductCostForm> util = new ExcelUtil<>(SalSaleProductCostForm.class, dataMap);
        util.exportExcel(response, list, "商品销售成本报表");
    }

    /**
     * 订单明细报表跳转出入库
     */
    @PostMapping("/jumpToInv")
    @ApiOperation(value = "订单明细报表跳转出入库", notes = "订单明细报表跳转出入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public AjaxResult getByCodeByList(@RequestBody String[] orderItemSid) {
        if (orderItemSid == null || orderItemSid.length == 0) {
            throw new BaseException("参数缺失");
        }
        List<SalSalesOrderItem> itemList = salSalesOrderItemMapper.getItemList(new SalSalesOrderItem().setItemSidList(orderItemSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            String orderCode = itemList.get(0).getSalesOrderCode();
            for (int i = 0; i < itemList.size(); i++) {
                if (!orderCode.equals(itemList.get(i).getSalesOrderCode())) {
                    return AjaxResult.error("请选择相同销售订单的明细！");
                }
                if (!ConstantsEms.CHECK_STATUS.equals(itemList.get(i).getHandleStatus())
                        || !ConstantsInventory.INV_CONTROL_MODE_GX.equals(itemList.get(i).getInventoryControlMode())
                        || !ConstantsOrder.DELIVERY_TYPE_DD.equals(itemList.get(i).getDeliveryType())
                        || !ConstantsEms.YES.equals(itemList.get(i).getIsReturnGoods())
                        || !ConstantsEms.NO.equals(itemList.get(i).getIsConsignmentSettle())) {
                    return AjaxResult.error("勾选的销售订单不符合入库条件，请核实");
                }
            }
            String[] codes = itemList.stream().map(SalSalesOrderItem::getSalesOrderCode).distinct().toArray(String[]::new);
            List<Long> sids = Arrays.asList(Arrays.stream(orderItemSid).map(Long::valueOf).toArray(Long[]::new));
            InvInventoryDocument invInventoryDocument = invInventoryDocumentService.getInvInventoryDocument(codes[0], ConstantsOrder.ORDER_DOC_TYPE_RSO,
                    null, ConstantsInventory.MOVEMENT_TYPE_SR03, ConstantsInventory.DOCUMENT_CATEGORY_IN, null);
            if (CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
                List<InvInventoryDocumentItem> list = invInventoryDocument.getInvInventoryDocumentItemList()
                        .stream().filter(o->sids.contains(o.getReferDocumentItemSid())).collect(Collectors.toList());
                invInventoryDocument.setInvInventoryDocumentItemList(list);
            }
            return AjaxResult.success(invInventoryDocument);
        }
        return AjaxResult.error("请重新刷新页面");
    }

}
