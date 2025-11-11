package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.export.PurPurchaseOrderVendorJsExport;
import com.platform.ems.domain.dto.response.form.PurPurchaseOrderProcessTracking;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.PurPurchaseOrderItemMapper;
import com.platform.ems.service.*;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购订单Controller
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@RestController
@RequestMapping("/purchase/order")
@Api(tags = "采购订单")
public class PurPurchaseOrderController extends BaseController {

    @Autowired
    private IPurPurchaseOrderService purPurchaseOrderService;
    @Autowired
    private IPurPurchaseOrderAttachmentService purPurchaseOrderAttachmentService;
    @Autowired
    private IPurPurchaseOrderItemService purPurchaseOrderItemService;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询采购订单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购订单列表", notes = "查询采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @Idempotent(interval = 3000, message = "请勿重复查询")
    public TableDataInfo list(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        startPage(purPurchaseOrder);
        List<PurPurchaseOrder> list = purPurchaseOrderService.selectPurPurchaseOrderList(purPurchaseOrder);
        return getDataTable(list);
    }

    @PostMapping("/count/refresh")
    @ApiOperation(value = "订单明细页签-合计字段刷新", notes = "订单明细页签-合计字段刷新")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public PurPurchaseOrder getCount(@RequestBody List<PurPurchaseOrderItem> items) {
        return purPurchaseOrderService.getCount(items);
    }

    @PostMapping("/total/head")
    @ApiOperation(value = "查询采购统计报表-主", notes = "查询采购统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getTotal(@RequestBody OrderTotalRequest request) {
        startPage(request);
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(), HandleStatus.CONFIRMED.getCode()});
        List<OrderTotalResponse> list = purPurchaseOrderItemService.getTotal(request);
        return getDataTable(list);
    }

    @PostMapping("/total/item")
    @ApiOperation(value = "查询采购统计报表-明细", notes = "查询采购统计明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getTotalItem(@RequestBody OrderTotalRequest request) {
        startPage(request);
        request.setHandleStatusList(new String[]{HandleStatus.CLOSED.getCode(), HandleStatus.CONFIRMED.getCode()});
        List<OrderTotalResponse> list = purPurchaseOrderItemService.getTotalItem(request);
        return getDataTable(list);
    }

    @PostMapping("/delivery/Process")
    @ApiOperation(value = "查询采购状况交期报表-主", notes = "查询采购状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getDeliveryProcess(@RequestBody OrderProgressRequest request) {
        startPage(request);
        List<OrderProgressResponse> list = purPurchaseOrderItemService.getDeliveryProcess(request);
        return getDataTable(list);
    }

    @PostMapping("/delivery/Process/item")
    @ApiOperation(value = "查询采购状况交期报表-明细", notes = "查询采购状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getDeliveryProcessItem(@RequestBody OrderProgressRequest request) {
        startPage(request);
        List<OrderProgressItemResponse> list = purPurchaseOrderItemService.getDeliveryProcessItem(request);
        TableDataInfo dataTable = getDataTable(list);
        if (CollectionUtil.isNotEmpty(list)) {
            list = purPurchaseOrderItemService.sortProgressItem(list);
            dataTable.setRows(list);
        }
        return dataTable;
    }

    @PostMapping("/delivery/Process/item/export")
    @ApiOperation(value = "导出采购况交期报表", notes = "导出采购状况交期报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    public void ProgressExport(HttpServletResponse response, OrderProgressRequest request) throws IOException {
        List<OrderProgressItemResponse> list = purPurchaseOrderItemService.getDeliveryProcessItem(request);
        if (CollectionUtil.isNotEmpty(list)) {
            list = purPurchaseOrderItemService.sortProgressItem(list);
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurOrderProgressItemResponse> util = new ExcelUtil<>(PurOrderProgressItemResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurOrderProgressItemResponse::new), "采购交期状况报表明细");
    }

    @PostMapping("/getProcess/head")
    @ApiOperation(value = "采购进度报表-主", notes = "采购进度报表-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getProcessHead(@RequestBody PurchaseOrderProgressRequest request) {
        startPage(request);
        List<PurchaseOrderProgressResponse> list = purPurchaseOrderService.getProcessHead(request);
        return getDataTable(list);
    }

    @PostMapping("/getProcess/item")
    @ApiOperation(value = "采购进度报表-采购明细", notes = "采购进度报表-采购明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    public TableDataInfo getProcessItem(@RequestBody PurchaseOrderProgressRequest request) {
        startPage(request);
        List<PurchaseOrderProgressItemResponse> list = purPurchaseOrderService.getProcessItem(request);
        return getDataTable(list);
    }

    /**
     * 导出采购订单列表
     */
    @ApiOperation(value = "导出采购订单列表", notes = "导出采购订单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurPurchaseOrder purPurchaseOrder) throws IOException {
        List<PurPurchaseOrder> list = purPurchaseOrderService.selectPurPurchaseOrderList(purPurchaseOrder);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        // 供应商寄售结算单
        if (ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(purPurchaseOrder.getSpecialBusCategory())
                && ConstantsEms.YES.equals(purPurchaseOrder.getIsConsignmentSettle())
                && ConstantsOrder.PURCHASE_SALE_MODE_JS.equals(purPurchaseOrder.getPurchaseMode())) {
            ExcelUtil<PurPurchaseOrderVendorJsExport> util = new ExcelUtil<>(PurPurchaseOrderVendorJsExport.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurPurchaseOrderVendorJsExport::new), "供应商寄售结算单");
        } else {
            ExcelUtil<purPurchaseOrderExResponse> util = new ExcelUtil<>(purPurchaseOrderExResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, purPurchaseOrderExResponse::new), "采购订单");
        }
    }

    /**
     * 导出选中的商品采购订单合同列表
     */
    @ApiOperation(value = "导出采购订单合同", notes = "导出采购订单合同")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/exportHetong")
    public void exportHetong(HttpServletResponse response, PurPurchaseOrder purPurchaseOrder) {
        purPurchaseOrderService.exportHetongList(response, purPurchaseOrder);
    }


    /**
     * 设置委托人
     */
    @ApiOperation(value = "设置委托人", notes = "设置委托人")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/set/trustorAccount")
    public AjaxResult setTrustor(@RequestBody OrderTrustorAccountRequest request) {
        return AjaxResult.success(purPurchaseOrderService.setTrustor(request));
    }

    /**
     * 导出物料清单（BOM）主列表
     */
    @ApiOperation(value = "导出采购订单明细", notes = "导出采购订单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, PurPurchaseOrder request) throws IOException {
        String materialCategory = request.getMaterialCategory();
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)) {
            purPurchaseOrderService.export(response, request.getPurchaseOrderSids());
        } else {
            purPurchaseOrderService.exportGood(response, request.getPurchaseOrderSids());
        }
    }

    /**
     * 获取采购订单详细信息
     */
    @ApiOperation(value = "获取采购订单详细信息", notes = "获取采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        PurPurchaseOrder purPurchaseOrder = purPurchaseOrderService.selectPurPurchaseOrderById(purchaseOrderSid);
        return AjaxResult.success(purPurchaseOrder);
    }

    /**
     * 获取采购订单详细信息的明细汇总页签
     */
    @ApiOperation(value = "获取采购订单详细信息的明细汇总页签", notes = "获取采购订单详细信息的明细汇总页签")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/getItemTotalList")
    public AjaxResult getItemTotalList(@RequestBody PurPurchaseOrder request) {
        if (request != null && CollectionUtil.isNotEmpty(request.getPurPurchaseOrderItemList())) {
            if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(request.getMaterialCategory())) {
                return AjaxResult.success(purPurchaseOrderService.getItemTotalListWl(request, request.getPurPurchaseOrderItemList()));
            } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(request.getMaterialCategory())) {
                return AjaxResult.success(purPurchaseOrderService.getItemTotalList(request, request.getPurPurchaseOrderItemList()));
            }
        }
        return AjaxResult.success(new PurPurchaseOrder());
    }

    /**
     * 获取采购订单详细信息
     */
    @ApiOperation(value = "外部接口-推送采购订单详细信息", notes = "外部接口-推送采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderOutResponse.class))
    @PostMapping("/getInfo/out")
    public AjaxResult getInfoOut(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        PurPurchaseOrderOutResponse purPurchaseOrder = purPurchaseOrderService.getOutOrder(purchaseOrderSid);
        return AjaxResult.success(purPurchaseOrder);
    }

    /**
     * 获取采购订单详细信息
     */
    @ApiOperation(value = "外部接口-修改采购订单处理状态", notes = "外部接口-修改采购订单处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/edit/handle/out")
    public AjaxResult changeHandleOut(@RequestBody List<PurPurchaseOrderHandleRequest> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderService.changeHandleOut(list));
    }

    @ApiOperation(value = "校验-作废采购订单", notes = "校验-作废采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/disuse/judge")
    public AjaxResult disuse(@RequestBody List<Long> purPurchaseOrdersids) {
        if (ArrayUtil.isEmpty(purPurchaseOrdersids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.disusejudge(purPurchaseOrdersids));
    }

    @ApiOperation(value = "作废采购订单", notes = "作废采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/disuse")
    public AjaxResult disuse(@RequestBody OrderInvalidRequest request) {
        if (ArrayUtil.isEmpty(request)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.disuse(request));
    }

    @ApiOperation(value = "作废采购订单明细", notes = "作废采购订单明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/item/disuse")
    public AjaxResult itemDisuse(@RequestBody OrderInvalidRequest request) {
        if (request == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.itemDisuse(request));
    }

    /**
     * 撤回保存前的校验
     */
    @ApiOperation(value = "撤回保存前的校验", notes = "撤回保存前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/back/save/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSaveVerify(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        if (purPurchaseOrder.getPurchaseOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.backSaveVerify(purPurchaseOrder));
    }

    /**
     * 撤回保存
     */
    @ApiOperation(value = "撤回保存", notes = "撤回保存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/back/save")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSave(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        if (purPurchaseOrder.getPurchaseOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        if (StrUtil.isBlank(purPurchaseOrder.getComment())) {
            throw new CheckedException("撤回说明不能为空");
        }
        return toAjax(purPurchaseOrderService.backSave(purPurchaseOrder));
    }

    /**
     * 维护纸质合同号
     */
    @ApiOperation(value = "维护纸质合同号", notes = "维护纸质合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/set/paperContract")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult setPaperContract(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        if (purPurchaseOrder.getPurchaseOrderSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderService.setPaperContract(purPurchaseOrder));
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        return purPurchaseOrderAttachmentService.check(purPurchaseOrderAttachment);
    }

    @ApiOperation(value = "新增采购订单查询页面上传附件", notes = "新增采购订单查询页面上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        int row = purPurchaseOrderAttachmentService.insertPurPurchaseOrderAttachment(purPurchaseOrderAttachment);
        if (row > 0) {
            if (ConstantsOrder.PAPER_CONTRACT_XSDDHT_PUR.equals(purPurchaseOrderAttachment.getFileType())) {
                purPurchaseOrderService.update(new UpdateWrapper<PurPurchaseOrder>().lambda()
                        .eq(PurPurchaseOrder::getPurchaseOrderSid, purPurchaseOrderAttachment.getPurchaseOrderSid())
                        .set(PurPurchaseOrder::getUploadStatus, ConstantsEms.CONTRACT_UPLOAD_STATUS_Y));
            }
        }
        return AjaxResult.success(row);
    }

    /**
     * 获取采购订单详细信息
     */
    @ApiOperation(value = "交货单页面获取采购订单详细信息", notes = "交货单获取采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/getInfo/delivery")
    public AjaxResult getInfoPurchase(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        PurPurchaseOrder purPurchaseOrder = purPurchaseOrderService.selectPurPurchaseOrderById(purchaseOrderSid);
        PurPurchaseOrder order = purPurchaseOrderItemService.handleIndexDelievery(purPurchaseOrder);
        return AjaxResult.success(order);
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/checkProcess")
    public AjaxResult checkProcess(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderService.checkProcess(purchaseOrderSid));
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "多笔提交时校验-(最新版)", notes = "多笔提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/checkProcess/list")
    public AjaxResult checkProcessList(@RequestBody OrderErrRequest request) {
        return AjaxResult.success(purPurchaseOrderService.checkProcessList(request));
    }

    /**
     * 新建直接点提交/编辑点提交
     */
    @ApiOperation(value = "新建直接点提交/编辑点提交", notes = "新建直接点提交/编辑点提交")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody PurPurchaseOrder purPurchaseOrder, String jump) {
        return purPurchaseOrderService.submit(purPurchaseOrder, jump);
    }

    /**
     * 报表更新采购价
     */
    @ApiOperation(value = "报表更新采购价", notes = "报表更新采购价")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/update/price")
    public AjaxResult updatePrice(@RequestBody List<PurPurchaseOrderItem> request) {
        return purPurchaseOrderService.updatePrice(request);
    }

    /**
     * 提交时校验
     */
    @ApiOperation(value = "多笔提交-有明细行提醒", notes = "多笔提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/checkProcess/list/freeFlag")
    public AjaxResult checkProcessListFreeFlag(@RequestBody List<Long> sidList) {
        if (CollectionUtil.isEmpty(sidList)) {
            throw new CheckedException("参数缺失");
        }
        return purPurchaseOrderService.checkListFree(sidList);
    }

    /**
     * 校验核销状态
     */
    @ApiOperation(value = "校验核销状态", notes = "校验核销状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSaleContract.class))
    @PostMapping("/judge")
    public AjaxResult judge(@RequestBody orderJudgeRequest request) {
        PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
        purPurchaseOrder.setPurchaseOrderSid(request.getPurchaseOrderSid())
                .setItemNumList(request.getItemNumList());
        return AjaxResult.success(purPurchaseOrderService.judgeReceipt(purPurchaseOrder));
    }

    /**
     * 拷贝采购订单详细信息
     */
    @ApiOperation(value = "拷贝采购订单详细信息", notes = "拷贝采购订单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long purchaseOrderSid) {
        if (purchaseOrderSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(purPurchaseOrderService.copy(purchaseOrderSid));
    }

    /**
     * 新增采购订单
     */
    @ApiOperation(value = "新增采购订单", notes = "新增采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PurPurchaseOrder purPurchaseOrder) {
        int row = purPurchaseOrderService.insertPurPurchaseOrder(purPurchaseOrder);
        if (row > 0) {
            return AjaxResult.success("操作成功", new PurPurchaseOrder().setPurchaseOrderSid(purPurchaseOrder.getPurchaseOrderSid()));
        }
        return toAjax(row);
    }

    /**
     * 物料需求测算报表 采购订单
     */
    @ApiOperation(value = "物料需求测算报表-采购订单", notes = "物料需求测算报表-采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/get")
    public AjaxResult getOrder(@RequestBody List<TecBomItemReport> orderList) {
        return AjaxResult.success(purPurchaseOrderService.getOrder(orderList));
    }

    /**
     * 修改采购订单
     */
    @ApiOperation(value = "修改采购订单", notes = "修改采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid PurPurchaseOrder purPurchaseOrder) {
        return toAjax(purPurchaseOrderService.updatePurPurchaseOrder(purPurchaseOrder));
    }

    @ApiOperation(value = "设置盖章件签收", notes = "设置盖章件签收")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/sign/status")
    public AjaxResult signStsu(@RequestBody OrderItemStatusSignRequest order) {
        return toAjax(purPurchaseOrderService.setSignStatus(order));
    }

    @ApiOperation(value = "修改面辅料状态、客供料状态", notes = "修改面辅料状态、客供料状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/status")
    public AjaxResult editStatus(@RequestBody OrderItemStatusRequest order) {
        return toAjax(purPurchaseOrderService.changeStatus(order));
    }

    @ApiOperation(value = "设置到期天数", notes = "设置到期天数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SalSalesOrder.class))
    @PostMapping("/set/toexpire")
    public AjaxResult setToexpire(@RequestBody OrderItemToexpireRequest request) {
        return AjaxResult.success(purPurchaseOrderService.setToexpireDays(request));
    }

    /**
     * 修改采购订单
     */
    @ApiOperation(value = "校验采购订单合同金额是否超过", notes = "校验采购订单合同金额是否超过")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/judge/contract")
    public AjaxResult judge(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        return toAjax(purPurchaseOrderService.judgeConstract(purPurchaseOrder));
    }

    /**
     * 删除采购订单
     */
    @ApiOperation(value = "删除采购订单", notes = "删除采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] purchaseOrderSids) {
        if (ArrayUtil.isEmpty(purchaseOrderSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.deletePurPurchaseOrderByIds(purchaseOrderSids));
    }

    /**
     * 采购订单确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "采购订单确认", notes = "采购订单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult confirm(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        return AjaxResult.success(purPurchaseOrderService.confirm(purPurchaseOrder));
    }

    /**
     * 采购订单变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "采购订单变更", notes = "采购订单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid PurPurchaseOrder purPurchaseOrder) {
        return AjaxResult.success(purPurchaseOrderService.change(purPurchaseOrder));
    }

    @PostMapping("/skip/inv")
    @ApiOperation(value = "明细报表跳转到入库页面", notes = "明细报表跳转到入库页面")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult exchangeInv(@RequestBody List<PurPurchaseOrderItem> items) {
        return AjaxResult.success(purPurchaseOrderService.exChange(items));
    }

    /**
     * 采购订单明细报表
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "采购订单明细报表", notes = "采购订单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderItem.class))
    public TableDataInfo list(@RequestBody PurPurchaseOrderItem purPurchaseOrderItem) {
        startPage(purPurchaseOrderItem);
        List<PurPurchaseOrderItem> list = purPurchaseOrderItemService.getItemList(purPurchaseOrderItem);
        TableDataInfo data = getDataTable(list);
        List<PurPurchaseOrderItem> purPurchaseOrderItems = purPurchaseOrderItemService.handleIndex(list);
        data.setRows(purPurchaseOrderItems);
        return data;
    }

    /**
     * 移动端采购进度
     */
    @PostMapping("/mob/process")
    @ApiOperation(value = "移动端采购进度", notes = "移动端采购进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderItem.class))
    @Idempotent(interval = 3000, message = "请勿重复查询")
    public TableDataInfo mobProcess(@RequestBody PurPurchaseOrderItem order) {
        startPage(order);
        List<PurPurchaseOrderItem> list = purPurchaseOrderItemService.selectMobProcessList(order);
        return getDataTable(list);
    }

    /**
     * 获取采购订单标签样式
     */
    @ApiOperation(value = "采购订单标签样式", notes = "采购订单标签样式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    @PostMapping("/get/grCode")
    public AjaxResult getQrCode(@RequestBody List<PurPurchaseOrderItem> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new CheckedException("未选择行");
        }
        return AjaxResult.success(purPurchaseOrderService.getQr(list));
    }

    /**
     * 录入合同号
     */
    @ApiOperation(value = "录入合同号", notes = "录入合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/contract")
    public AjaxResult setConstract(@RequestBody PurChaseOrderSetRequest request) {
        PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
        BeanCopyUtils.copyProperties(request, purPurchaseOrder);
        return AjaxResult.success(purPurchaseOrderService.setConstract(purPurchaseOrder));
    }

    /**
     * 变更合同号
     */
    @ApiOperation(value = "变更合同号", notes = "变更合同号")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/update/contractCode")
    public AjaxResult setConstractCode(@RequestBody PurChaseOrderSetRequest request) {
        if (request.getPurchaseOrderSid() == null) {
            throw new BaseException("请选择行");
        }
        PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
        BeanCopyUtils.copyProperties(request, purPurchaseOrder);
        return AjaxResult.success(purPurchaseOrderService.setConstractCode(purPurchaseOrder));
    }

    /**
     * 创建合同信息
     */
    @ApiOperation(value = "创建合同信息", notes = "创建合同信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseContract.class))
    @PostMapping("/contract/add")
    public AjaxResult toConstract(@RequestBody List<PurPurchaseOrder> purPurchaseOrderList, String jump) {
        return AjaxResult.success(purPurchaseOrderService.constractAdd(purPurchaseOrderList, jump));
    }

    /**
     * 导出采购订单明细报表
     */
    @ApiOperation(value = "导出采购订单明细报表", notes = "导出采购订单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void export(HttpServletResponse response, PurPurchaseOrderItem purPurchaseOrderItem) throws IOException {
        List<PurPurchaseOrderItem> list = purPurchaseOrderItemService.getItemList(purPurchaseOrderItem);
        list = purPurchaseOrderItemService.handleIndex(list);
        String isViewPrice = ApiThreadLocalUtil.get().getSysUser().getIsViewPricePur();
        String materialType = purPurchaseOrderItem.getMaterialType();
        if (!ConstantsEms.MATERIAL_F.equals(materialType)) {
            list.forEach(li -> {
                if (!ConstantsEms.YES.equals(isViewPrice)) {
                    li.setPurchasePriceTax(null)
                            .setPurchasePrice(null)
                            .setPriceTax(null)
                            .setPrice(null);

                }
            });
        }
        if (ConstantsEms.VENDOR_SPECIAL_BUS_CATEGORY.equals(purPurchaseOrderItem.getSpecialBusCategory())) {
            Map<String, Object> dataMap = sysDictDataService.getDictDataList();
            ExcelUtil<PurPurchaseOrderReportVenResponse> util = new ExcelUtil<>(PurPurchaseOrderReportVenResponse.class, dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurPurchaseOrderReportVenResponse::new), "供应商寄售结算单明细报表" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
        } else {
            if (ConstantsEms.MATERIAL_F.equals(materialType)) {
                Map<String, Object> dataMap = sysDictDataService.getDictDataList();
                ExcelUtil<PurPurchaseOrderReportResponse> util = new ExcelUtil<>(PurPurchaseOrderReportResponse.class, dataMap);
                util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurPurchaseOrderReportResponse::new), "辅料采购订单明细报表");
            } else {
                Map<String, Object> dataMap = sysDictDataService.getDictDataList();
                ExcelUtil<PurPurchaseOrderReportResponse> util = new ExcelUtil<>(PurPurchaseOrderReportResponse.class, dataMap);
                util.exportExcel(response, BeanCopyUtils.copyListProperties(list, PurPurchaseOrderReportResponse::new), "采购订单明细报表");
            }
        }
    }

    /**
     * 物料需求报表(商品采购订单)
     */
    @ApiOperation(value = "物料需求报表(商品采购订单)", notes = "物料需求报表(商品采购订单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/getListByCode")
    public AjaxResult getMaterialRequireListByCode(Long purchaseOrderCode) {
        if (purchaseOrderCode == null) {
            throw new CheckedException("请输入采购订单号");
        }
        return AjaxResult.success(purPurchaseOrderService.getMaterialRequireListByCode(purchaseOrderCode));
    }

    /**
     * 导入-物料采购订单
     */
    @PostMapping("/import/M")
    @ApiOperation(value = "导入-物料采购订单", notes = "导入-物料采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataGBack(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purPurchaseOrderService.importDataM(file));
    }

    /**
     * 采购订单关闭
     */
    @PostMapping("/close")
    @ApiOperation(value = "采购订单关闭", notes = "采购订单关闭")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult close(@RequestBody PurPurchaseOrder purPurchaseOrder) {
        return AjaxResult.success(purPurchaseOrderService.close(purPurchaseOrder));
    }

    /**
     * 采购订单明细关闭
     */
    @PostMapping("/item/close")
    @ApiOperation(value = "采购订单明细关闭", notes = "采购订单明细关闭")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult itemClose(@RequestBody PurPurchaseOrderItem purPurchaseOrderItem) {
        if (purPurchaseOrderItem == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(purPurchaseOrderService.itemClose(purPurchaseOrderItem));
    }

    /**
     * 导入-物料采购退货订单
     */
    @PostMapping("/import/M/re")
    @ApiOperation(value = "导入-物料采购退货订单", notes = "导入-物料采购退货订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataRe(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purPurchaseOrderService.importDataRe(file));
    }

    @ApiOperation(value = "下载物料采购订单导入模板", notes = "下载物料采购订单导入模板")
    @PostMapping("/importTemplate/M")
    public void importTemplateR(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_物料采购订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料采购订单_V0.1.xlsx", "UTF-8"));
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

    @ApiOperation(value = "下载物料采购退货订单导入模板", notes = "下载物料采购退货订单导入模板")
    @PostMapping("/importTemplate/M/re")
    public void importTemplateB(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_物料采购退货订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料采购退货订单_V0.1.xlsx", "UTF-8"));
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
     * 导入-商品采购订单
     */
    @PostMapping("/import/G")
    @ApiOperation(value = "导入-商品采购订单", notes = "导入-商品采购订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purPurchaseOrderService.importDataG(file));
    }

    /**
     * 导入-商品采购退货订单
     */
    @PostMapping("/import/G/re")
    @ApiOperation(value = "导入-商品采购退货订单", notes = "导入-商品采购退货订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataGRe(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purPurchaseOrderService.importDataGre(file));
    }

    /**
     * 导入-供应商寄售结算单
     */
    @PostMapping("/import/ve")
    @ApiOperation(value = "导入-供应商寄售结算单", notes = "导入-供应商寄售结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataVe(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(purPurchaseOrderService.importDataVe(file));
    }

    @ApiOperation(value = "下载商品采购订单导入模板", notes = "下载商品采购订单导入模板")
    @PostMapping("/importTemplate/G")
    public void importTemplateG(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_商品采购订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_商品采购订单_V0.1.xlsx", "UTF-8"));
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

    @ApiOperation(value = "下载商品采购退货订单导入模板", notes = "下载商品采购退货订单导入模板")
    @PostMapping("/importTemplate/G/re")
    public void importTemplateGRe(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_商品采购退货订单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_商品采购退货订单_V0.1.xlsx", "UTF-8"));
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

    @ApiOperation(value = "下载供应商寄售结算单导入模板", notes = "下载商供应商寄售结算单导入模板")
    @PostMapping("/importTemplate/ve")
    public void importTemplateVe(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_导入模板_供应商寄售结算单_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_供应商寄售结算单_V0.1.xlsx", "UTF-8"));
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
     * 采购入库进度跟踪报表
     */
    @PostMapping("/process/tracking")
    @ApiOperation(value = "采购入库进度跟踪报表", notes = "采购入库进度跟踪报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrderProcessTracking.class))
    @Idempotent(interval = 3000, message = "请勿重复查询")
    public TableDataInfo processTracking(@RequestBody PurPurchaseOrderProcessTracking request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PurPurchaseOrderProcessTracking> total = purPurchaseOrderService.selectPurPurchaseProcessTrackingList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<PurPurchaseOrderProcessTracking> list = purPurchaseOrderService.selectPurPurchaseProcessTrackingList(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    /**
     * 导出采购入库进度跟踪报表
     */
    @ApiOperation(value = "导出采购入库进度跟踪报表", notes = "导出采购入库进度跟踪报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/process/tracking/export")
    public void overDueExport(HttpServletResponse response, PurPurchaseOrderProcessTracking request) throws IOException {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<PurPurchaseOrderProcessTracking> list = purPurchaseOrderService.selectPurPurchaseProcessTrackingList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PurPurchaseOrderProcessTracking> util = new ExcelUtil<>(PurPurchaseOrderProcessTracking.class, dataMap);
        util.exportExcel(response, list, "采购入库进度跟踪报表");
    }

    /**
     * 明细数据来源列表查询
     */
    @ApiOperation(value = "明细数据来源列表查询", notes = "明细数据来源列表查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/item/source/list")
    public AjaxResult itemDataSourceList(@RequestBody PurPurchaseOrderDataSource dataSource) {
        return AjaxResult.success(purPurchaseOrderItemService.selectPurPurchaseOrderDataSourceList(dataSource));
    }

    /**
     * 修改明细数据来源列表
     */
    @ApiOperation(value = "修改明细数据来源列表", notes = "修改明细数据来源列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/item/source/update")
    public AjaxResult updateItemDataSourceList(@RequestBody List<PurPurchaseOrderDataSource> dataSourceList, String keep) {
        return AjaxResult.success(purPurchaseOrderItemService.updatePurPurchaseOrderDataSourceList(dataSourceList, keep));
    }

    /**
     * 订单明细更新来源数量时得到新的订单量返回前端
     */
    @ApiOperation(value = "订单明细更新来源数量时得到新的订单量返回前端", notes = "订单明细更新来源数量时得到新的订单量返回前端")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchaseOrder.class))
    @PostMapping("/item/source/update/newQuantity")
    public AjaxResult getPurPurchaseOrderItemQuantityByDataSource(@RequestBody List<PurPurchaseOrderDataSource> dataSourceList, String keep) {
        return AjaxResult.success(purPurchaseOrderItemService.getPurPurchaseOrderItemQuantityByDataSource(dataSourceList, keep));
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
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(new PurPurchaseOrderItem().setItemSidList(orderItemSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            String orderCode = itemList.get(0).getPurchaseOrderCode();
            for (int i = 0; i < itemList.size(); i++) {
                if (!orderCode.equals(itemList.get(i).getPurchaseOrderCode())) {
                    return AjaxResult.error("请选择相同采购订单的明细！");
                }
                if (!ConstantsEms.CHECK_STATUS.equals(itemList.get(i).getHandleStatus())
                        || !ConstantsInventory.INV_CONTROL_MODE_GX.equals(itemList.get(i).getInventoryControlMode())
                        || !ConstantsOrder.DELIVERY_TYPE_DD.equals(itemList.get(i).getDeliveryType())
                        || !ConstantsEms.YES.equals(itemList.get(i).getIsReturnGoods())
                        || !ConstantsEms.NO.equals(itemList.get(i).getIsConsignmentSettle())) {
                    return AjaxResult.error("勾选的采购订单不符合出库条件，请核实");
                }
            }
            String[] codes = itemList.stream().map(PurPurchaseOrderItem::getPurchaseOrderCode).distinct().toArray(String[]::new);
            List<Long> sids = Arrays.asList(Arrays.stream(orderItemSid).map(Long::valueOf).toArray(Long[]::new));
            InvInventoryDocument invInventoryDocument = invInventoryDocumentService.getInvInventoryDocument(codes[0], ConstantsOrder.ORDER_DOC_TYPE_RPO,
                    null, ConstantsInventory.MOVEMENT_TYPE_SC03, ConstantsInventory.DOCUMENT_CATEGORY_OUT, null);
            if (CollectionUtil.isNotEmpty(invInventoryDocument.getInvInventoryDocumentItemList())) {
                List<InvInventoryDocumentItem> list = invInventoryDocument.getInvInventoryDocumentItemList()
                        .stream().filter(o -> sids.contains(o.getReferDocumentItemSid())).collect(Collectors.toList());
                invInventoryDocument.setInvInventoryDocumentItemList(list);
            }
            return AjaxResult.success(invInventoryDocument);
        }
        return AjaxResult.error("请重新刷新页面");
    }

    /**
     * 订单明细报表跳转出入库（多订单）
     */
    @PostMapping("/jumpToInvMulti")
    @ApiOperation(value = "订单明细报表跳转出入库（多订单）", notes = "订单明细报表跳转出入库（多订单）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public AjaxResult jumpToInvMulti(@RequestBody String[] orderItemSid) {
        if (orderItemSid == null || orderItemSid.length == 0) {
            throw new BaseException("参数缺失");
        }
        List<PurPurchaseOrderItem> itemList = purPurchaseOrderItemMapper.getItemList(new PurPurchaseOrderItem().setItemSidList(orderItemSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            Long vendorSid = null;
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getVendorSid() != null) {
                    vendorSid = itemList.get(i).getVendorSid();
                    break;
                }
            }
            Long companySid = null;
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getCompanySid() != null) {
                    companySid = itemList.get(i).getCompanySid();
                    break;
                }
            }
            String documentType = itemList.get(0).getDocumentType();
            for (int i = 0; i < itemList.size(); i++) {
                if (!ConstantsEms.CHECK_STATUS.equals(itemList.get(i).getHandleStatus())
                        || !ConstantsInventory.INV_CONTROL_MODE_GX.equals(itemList.get(i).getInventoryControlMode())
                        || !ConstantsOrder.DELIVERY_TYPE_DD.equals(itemList.get(i).getDeliveryType())
                        || !ConstantsEms.NO.equals(itemList.get(i).getIsReturnGoods())
                        || !ConstantsEms.NO.equals(itemList.get(i).getIsConsignmentSettle())) {
                    return AjaxResult.error("勾选的采购订单" + itemList.get(i).getPurchaseOrderCode() + "不符合入库条件，请核实");
                }
                if (vendorSid != null && !vendorSid.equals(itemList.get(i).getVendorSid())) {
                    return AjaxResult.error("请勾选相同供应商的采购订单明细！");
                }
                if (companySid != null && !companySid.equals(itemList.get(i).getCompanySid())) {
                    return AjaxResult.error("请勾选相同公司的采购订单明细！");
                }
                if (documentType != null && !documentType.equals(itemList.get(i).getDocumentType())) {
                    return AjaxResult.error("请勾选相同单据类型的采购订单明细！");
                }
            }
            String[] codes = itemList.stream().map(PurPurchaseOrderItem::getPurchaseOrderCode).distinct().toArray(String[]::new);
            Long[] sids = Arrays.stream(orderItemSid).map(Long::valueOf).toArray(Long[]::new);
            InvInventoryDocument invInventoryDocument = invInventoryDocumentService.getInvInventoryDocumentList(new
                    InvInventoryDocumentOrders().setCodeList(codes).setItemSidList(sids)
                    .setMovementType(ConstantsInventory.MOVEMENT_TYPE_SR01).setReferDocCategory(ConstantsOrder.ORDER_DOC_TYPE_PO));
            return AjaxResult.success(invInventoryDocument);
        }
        return AjaxResult.error("请重新刷新页面");
    }
}
