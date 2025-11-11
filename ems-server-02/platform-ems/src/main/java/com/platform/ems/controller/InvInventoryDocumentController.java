package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.DocumentAddItemRequest;
import com.platform.ems.domain.dto.request.InvInventoryDocumentOrders;
import com.platform.ems.domain.dto.request.InvInventoryDocumentReportRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.export.InvInventoryDocumentPurReportExport;
import com.platform.ems.domain.dto.response.export.InvInventoryDocumentReportExport;
import com.platform.ems.domain.dto.response.form.InvInventoryProductUserMaterial;
import com.platform.ems.enums.DocCategory;
import com.platform.ems.enums.DocumentCategory;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IInvInventoryDocumentService;
import com.platform.system.service.ISysDictDataService;
import com.platform.ems.service.impl.DelDeliveryNoteServiceImpl;
import com.platform.ems.service.impl.FinBookPaymentEstimationServiceImpl;
import com.platform.ems.service.impl.FinBookReceiptEstimationServiceImpl;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 库存凭证Controller
 *
 * @author linhongwei
 * @date 2021-04-16
 */
@RestController
@RequestMapping("/document")
@Api(tags = "库存凭证")
public class InvInventoryDocumentController extends BaseController {

    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private ISysDictDataService sysDictDataService;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient minioClient;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询库存凭证列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询库存凭证列表", notes = "查询库存凭证列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public TableDataInfo list(@RequestBody InvInventoryDocument invInventoryDocument) {
        startPage(invInventoryDocument);
        List<InvInventoryDocument> list = invInventoryDocumentService.selectInvInventoryDocumentList(invInventoryDocument);
        return getDataTable(list);
    }

    @PostMapping("/get/item")
    @ApiOperation(value = "查询获取单据明细", notes = "查询获取单据明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public TableDataInfo getItem(@RequestBody DocumentAddItemRequest request) {
        startPage(request);
        List<InvInventoryDocumentItem> list = invInventoryDocumentService.getItemAdd(request);
        return getDataTable(list);
    }

    /**
     * 查询出入库明细报表
     *
     * @param invInventoryDocumentItem 库存凭证  ----停用
     * @return 库存凭证明细报表
     */
    @PostMapping("/report")
    @ApiOperation(value = "查询出入库明细报表", notes = "查询出入库明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocumentItem.class))
    public AjaxResult detailReport(@RequestBody InvInventoryDocumentItem invInventoryDocumentItem){
        Map<String, Object> map = invInventoryDocumentService.detailReport(invInventoryDocumentItem);
        return AjaxResult.success(map);
    }

    /**
     * 查询库存凭证/甲供料结算单明细报表
     *
     * @param request 库存凭证
     * @return 库存凭证明细报表
     */
    @PostMapping("/Inventory/report")
    @ApiOperation(value = "查询库存凭证/甲供料结算单细报表", notes = "查询库存凭证/甲供料结算单细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocumentReportResponse.class))
    public TableDataInfo report(@RequestBody InvInventoryDocumentReportRequest request){
        startPage(request);
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(request);
        return getDataTable(list);
    }

    /**
     * 销售出入库明细报表导出
     */
    @PostMapping("/inventory/report/export")
    @ApiOperation(value = "销售出入库明细报表导出", notes = "销售出入库明细报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocumentReportResponse.class))
    public void reportExport(HttpServletResponse response, InvInventoryDocumentReportRequest request) throws IOException {
        if (ArrayUtil.isEmpty(request.getMovementTypeList())) {
            throw new BaseException("作业类型不能为空!");
        }
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryDocumentReportExport> util = new ExcelUtil<>(InvInventoryDocumentReportExport.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInventoryDocumentReportExport::new), "销售出入库明细报表");
    }

    /**
     * 采购出入库明细报表导出
     */
    @PostMapping("/inventory/report/pur/export")
    @ApiOperation(value = "采购出入库明细报表导出", notes = "采购出入库明细报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocumentReportResponse.class))
    public void reportPurExport(HttpServletResponse response, InvInventoryDocumentReportRequest request) throws IOException {
        if (ArrayUtil.isEmpty(request.getMovementTypeList())) {
            throw new BaseException("作业类型不能为空!");
        }
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryDocumentPurReportExport> util = new ExcelUtil<>(InvInventoryDocumentPurReportExport.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInventoryDocumentPurReportExport::new), "采购出入库明细报表");
    }

    /**
     * 导出库存凭证列表
     */
    @Log(title = "库存凭证", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存凭证明细报表", notes = "导出库存凭证明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, InvInventoryDocumentReportRequest request) throws IOException {
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryDocumentReportResponse> util = new ExcelUtil<>(InvInventoryDocumentReportResponse.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInventoryDocumentReportResponse::new), "库存凭证明细报表");
    }

    @ApiOperation(value = "导出采购结算单", notes = "导出采购结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/pur/item")
    public void exportPurItem(HttpServletResponse response, Long[] inventoryDocumentItemSidList) throws IOException {
        InvInventoryDocumentReportRequest invInventoryDocumentReportRequest = new InvInventoryDocumentReportRequest();
        invInventoryDocumentReportRequest.setInventoryDocumentItemSidList(inventoryDocumentItemSidList);
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(invInventoryDocumentReportRequest);
        invInventoryDocumentService.exportPur(response,list);
    }

    @ApiOperation(value = "导出销售结算单", notes = "导出销售结算单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/sal/item")
    public void exportSalItem(HttpServletResponse response, Long[] inventoryDocumentItemSidList) throws IOException {
        InvInventoryDocumentReportRequest invInventoryDocumentReportRequest = new InvInventoryDocumentReportRequest();
        invInventoryDocumentReportRequest.setInventoryDocumentItemSidList(inventoryDocumentItemSidList);
        List<InvInventoryDocumentReportResponse> list = invInventoryDocumentService.selectDocumentReport(invInventoryDocumentReportRequest);
        invInventoryDocumentService.exportSal(response,list);
    }

    /**
     * 通过业务单号查询相关数据
     */
    @PostMapping("/getByCode")
    @ApiOperation(value = "通过业务单号查询相关数据", notes = "通过业务单号查询相关数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public AjaxResult getByCode(String code,String referDocCategory,String type,String movementType,String productCodes, String documentCategory, String status) {
        InvInventoryDocument invInventoryDocument = invInventoryDocumentService.getInvInventoryDocument(code, referDocCategory, type, movementType, documentCategory, null);
        List<InvInventoryDocumentItem> items = invInventoryDocumentService.filter(invInventoryDocument, productCodes, status);
        invInventoryDocument.setInvInventoryDocumentItemList(items);
        return AjaxResult.success(invInventoryDocument);
    }

    /**
     * 通过业务单号查询相关数据(多订单)
     */
    @PostMapping("/getByCodeByList")
    @ApiOperation(value = "通过业务单号查询相关数据(多订单)", notes = "通过业务单号查询相关数据(多订单)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public AjaxResult getByCodeByList(@RequestBody InvInventoryDocumentOrders invInventoryDocumentOrders) {
        InvInventoryDocument invInventoryDocument = invInventoryDocumentService.getInvInventoryDocumentList(invInventoryDocumentOrders);
        return AjaxResult.success(invInventoryDocument);
    }

    /**
     * 导出库存凭证列表
     */
    @Log(title = "库存凭证", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出库存凭证列表", notes = "导出库存凭证列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, InvInventoryDocument invInventoryDocument) throws IOException {
        List<InvInventoryDocument> list = invInventoryDocumentService.selectInvInventoryDocumentList(invInventoryDocument);
        String documentCategory = invInventoryDocument.getDocumentCategory();
        String movementType = invInventoryDocument.getMovementType();
        if(DocumentCategory.RU.getCode().equals(documentCategory)){
            if(ConstantsEms.MOVE_TYPE_RK_YP.equals(movementType)){
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExYPResponse> util = new ExcelUtil<>(InvInventoryDocumentExYPResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExYPResponse::new), "样品入库"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }else{
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExResponse> util = new ExcelUtil<>(InvInventoryDocumentExResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExResponse::new), "库存凭证"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }
        }else if(DocumentCategory.CHK.getCode().equals(documentCategory)){
            if(ConstantsEms.MOVE_TYPE_CHk_YP.equals(movementType)){
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExChkYPResponse> util = new ExcelUtil<>(InvInventoryDocumentExChkYPResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExChkYPResponse::new), "样品出库"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }else{
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExChkResponse> util = new ExcelUtil<>(InvInventoryDocumentExChkResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExChkResponse::new), "库存凭证"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }
        }else if(DocumentCategory.YK.getCode().equals(documentCategory)||DocumentCategory.CTKZ.getCode().equals(documentCategory)){
            if("SY61".equals(movementType)||"SY62".equals(movementType)){
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExYPYkResponse> util = new ExcelUtil<>(InvInventoryDocumentExYPYkResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExYPYkResponse::new), "样品移库"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }else{
                Map<String,Object> dataMap=sysDictDataService.getDictDataList();
                ExcelUtil<InvInventoryDocumentExYkResponse> util = new ExcelUtil<>(InvInventoryDocumentExYkResponse.class,dataMap);
                util.exportExcel(response,  BeanCopyUtils.copyListProperties(list, InvInventoryDocumentExYkResponse::new), "库存凭证"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
            }
        }
    }

    /**
     * 获取库存凭证详细信息
     */
    @ApiOperation(value = "获取库存凭证详细信息", notes = "获取库存凭证详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long inventoryDocumentSid) {
        if (inventoryDocumentSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryDocumentService.selectInvInventoryDocumentById(inventoryDocumentSid));
    }

    /**
     * 获取采购入库标签样式
     */
    @ApiOperation(value = "采购入库标签样式", notes = "采购入库标签样式")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    @PostMapping("/get/grCode")
    public AjaxResult getQrCode(@RequestBody List<InvInventoryDocumentItem> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new CheckedException("未选择行");
        }
        return AjaxResult.success(invInventoryDocumentService.getQr(list));
    }

    /**
     * 新增库存凭证
     */
    @ApiOperation(value = "新增库存凭证", notes = "新增库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid InvInventoryDocument invInventoryDocument) {
        int row = invInventoryDocumentService.insertInvInventoryDocument(invInventoryDocument);
        return AjaxResult.success(invInventoryDocument);
    }

    /**
     * 多作业类型出库
     */
    @ApiOperation(value = "新增库存凭证", notes = "新增库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.INSERT)
    @PostMapping("/add/byMovementType")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult addByMovementType(@RequestBody @Valid InvInventoryDocument invInventoryDocument) {
        return toAjax(invInventoryDocumentService.insertInvInventoryDocumentByMovementType(invInventoryDocument));
    }


    /**
     * 修改库存凭证
     */
    @ApiOperation(value = "修改库存凭证", notes = "修改库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody InvInventoryDocument invInventoryDocument) {
        return toAjax(invInventoryDocumentService.updateInvInventoryDocument(invInventoryDocument));
    }

    /**
     * 删除库存凭证
     */
    @ApiOperation(value = "删除库存凭证", notes = "删除库存凭证")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] inventoryDocumentSids) {
        if (ArrayUtil.isEmpty( inventoryDocumentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryDocumentService.deleteInvInventoryDocumentByIds(inventoryDocumentSids));
    }

    @ApiOperation(value = "获取打印出库单数据", notes = "获取打印出库单数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.DELETE)
    @PostMapping("/print/ck")
    public AjaxResult print(@RequestBody Long[] inventoryDocumentSids) {
        if (ArrayUtil.isEmpty( inventoryDocumentSids)){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryDocumentService.getPrintck(inventoryDocumentSids));
    }

    /**
     * 库存凭证冲销
     */
    @ApiOperation(value = "库存凭证冲销", notes = "库存凭证冲销")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "库存凭证", businessType = BusinessType.DELETE)
    @PostMapping("/cancelled")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult remove(@RequestBody List<Long> inventoryDocumentSids) {
        if (ArrayUtil.isEmpty(inventoryDocumentSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryDocumentService.invDocumentCX(inventoryDocumentSids));
    }

    /**
     * 库存凭证确认
     */
    @Log(title = "库存凭证", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm")
    @ApiOperation(value = "库存凭证确认", notes = "库存凭证确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(Long[] inventoryDocumentSidList) {
        return AjaxResult.success(invInventoryDocumentService.confirm(inventoryDocumentSidList));
    }

    /**
     * 库存凭证变更
     */
    @Log(title = "库存凭证", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "库存凭证变更", notes = "库存凭证变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody InvInventoryDocument invInventoryDocument) {
        return AjaxResult.success(invInventoryDocumentService.change(invInventoryDocument));
    }

    /**
     * 复制
     */
    @ApiOperation(value = "复制凭证详细信息", notes = "复制库存凭证详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    @PostMapping("/copy")
    public AjaxResult getCopy(Long inventoryDocumentSid) {
        if (inventoryDocumentSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryDocumentService.getCopy(inventoryDocumentSid));
    }

    /**
     * 移库方式为两步法且入库状态为未入库的移库进行入库 (移库单查询页面入库按钮)
     */
    @ApiOperation(value = "移库方式为两步法且入库状态为未入库的移库进行入库", notes = "移库方式为两步法且入库状态为未入库的移库进行入库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    @PostMapping("/yikuInStock")
    public AjaxResult yikuInStock(InvInventoryDocument invInventoryDocument) {
        if (invInventoryDocument.getInventoryDocumentSid() == null){
            throw new CheckedException("参数缺失");
        }
        invInventoryDocumentService.yikuInStock(invInventoryDocument);
        return AjaxResult.success();
    }

    /**
     * 查询页面更新信息按钮获取信息
     */
    @PostMapping("/detail")
    @ApiOperation(value = "查询页面更新信息按钮获取信息", notes = "查询页面更新信息按钮获取信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryDocument.class))
    public AjaxResult detail(Long inventoryDocumentSid) {
        if (inventoryDocumentSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(invInventoryDocumentService.selectInvInventoryDocumentDetailById(inventoryDocumentSid));
    }

    /**
     * 查询页面更新信息按钮修改信息
     */
    @PostMapping("/detail/update")
    @ApiOperation(value = "查询页面更新信息按钮修改信息", notes = "查询页面更新信息按钮修改信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult updateDetail(@RequestBody InvInventoryDocument invInventoryDocument) {
        if (invInventoryDocument.getInventoryDocumentSid() == null){
            throw new CheckedException("参数缺失");
        }
        return toAjax(invInventoryDocumentService.updateInvInventoryDocumentDetailById(invInventoryDocument));
    }

    /**
     * 仓库物料信息表设置使用频率
     */
    @PostMapping("/store/material/setUsage")
    @ApiOperation(value = "设置使用频率", notes = "设置使用频率")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult storehouseMaterialSetUsage(@RequestBody List<InvStorehouseMaterial> invStorehouseMaterialList, String usageFrequencyFlag) {
        return AjaxResult.success(invInventoryDocumentService.storehouseMaterialSetUsage(invStorehouseMaterialList,usageFrequencyFlag));
    }

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑： 客户和供应商字段是否填写的校验
     */
    @PostMapping("/add/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑", notes = "出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addVerify(@RequestBody @Valid InvInventoryDocument invInventoryDocument) {
        return AjaxResult.success(invInventoryDocumentService.insertVerifyInvInventoryDocument(invInventoryDocument));
    }

    /**
     * 出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑： 客户和供应商字段是否填写的校验 ( 多作业类型出库 )
     */
    @PostMapping("/movementType/add/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑 ( 多作业类型出库 )", notes = "出入库按钮前的校验关于“客户、供应商”录入要求的校验逻辑 ( 多作业类型出库 )")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult movementTypeAddVerify(@RequestBody @Valid InvInventoryDocument invInventoryDocument) {
        return AjaxResult.success(invInventoryDocumentService.insertVerifyInvInventoryDocumentByMovementType(invInventoryDocument));
    }

    /**
     * 出入库添加明细行时获取价格回传前端
     */
    @PostMapping("/item/setPrice")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "出入库添加明细行时获取价格回传前端", notes = "出入库添加明细行时获取价格回传前端")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult addItemSetPrice(@RequestBody InvInventoryDocument invInventoryDocument) {
        return AjaxResult.success(invInventoryDocumentService.setInvInventoryDocumentItemPrice(invInventoryDocument));
    }

    /**
     * 出入库明细获取库存量
     */
    @PostMapping("/item/getUnlimitedQuantity")
    @Idempotent(message = "系统处理中，请勿频繁点击“明细信息”页签")
    @ApiOperation(value = "出入库明细获取库存量", notes = "出入库明细获取库存量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult getItemUnlimitedQuantity(@RequestBody InvInventoryDocument invInventoryDocument) {
        return AjaxResult.success(invInventoryDocumentService.getItemUnlimitedQuantity(invInventoryDocument));
    }

    /**
     * 多作业类型出库  按明细获取库存量
     */
    @PostMapping("/item/getUnlimitedQuantity/byMovementType")
    @Idempotent(message = "系统处理中，请勿频繁切换库位")
    @ApiOperation(value = "多作业类型出库  按明细获取库存量", notes = "多作业类型出库  按明细获取库存量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult getItemUnlimitedQuantityByMovementType(@RequestBody List<InvInventoryDocumentItem> item) {
        return AjaxResult.success(invInventoryDocumentService.getItemUnlimitedQuantityBymovementType(item));
    }


    /**
     * 明细页签导入明细列表
     */
    @PostMapping("/item/importList")
    @ApiOperation(value = "明细页签导入明细列表", notes = "明细页签导入明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult itemImportList(MultipartFile file, String documentCategory) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(invInventoryDocumentService.importItemList(file, documentCategory));
    }

    @ApiOperation(value = "下载明细页签导入明细列表的导入模板", notes = "下载明细页签导入明细列表的导入模板")
    @PostMapping("/item/importList/template")
    public void importTemplate(HttpServletResponse response, String documentCategory) throws IOException {
        InputStream inputStream = null;
        OutputStream out =null;
        String name = "";
        if (ConstantsInventory.DOCUMENT_CATEGORY_OUT.equals(documentCategory)) {
            name = "协服SCM_导入模板_出库明细_V1.0.xlsx";
        }
        else if (ConstantsInventory.DOCUMENT_CATEGORY_IN.equals(documentCategory)) {
            name = "协服SCM_导入模板_入库明细_V1.0.xlsx";
        }
        else if (ConstantsInventory.DOCUMENT_CATEGORY_YK.equals(documentCategory)) {
            name = "协服SCM_导入模板_移库明细_V1.0.xlsx";
        }
        else {
            return;
        }
        String fileName = FILLE_PATH + "/" + name;
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = minioClient.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out  = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }finally {
            if(inputStream!=null){
                inputStream.close();
            }
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     * 查询商品领用物料统计报表
     *
     * @param request
     * @return
     */
    @PostMapping("/product/user/material/statistics")
    @ApiOperation(value = "查询商品领用物料统计报表", notes = "查询商品领用物料统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = InvInventoryProductUserMaterial.class))
    public TableDataInfo productUserMaterialStatistics(@RequestBody InvInventoryProductUserMaterial request){
        startPage(request);
        List<InvInventoryProductUserMaterial> list = invInventoryDocumentService.productUserMaterialStatistics(request);
        return getDataTable(list);
    }

    /**
     * 导出商品领用物料统计报表
     */
    @ApiOperation(value = "导出商品领用物料统计报表", notes = "导出商品领用物料统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/product/user/material/statistics/export")
    public void productUserMaterialStatisticsExport(HttpServletResponse response, InvInventoryProductUserMaterial request) throws IOException {
        List<InvInventoryProductUserMaterial> list = invInventoryDocumentService.productUserMaterialStatistics(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<InvInventoryProductUserMaterial> util = new ExcelUtil<>(InvInventoryProductUserMaterial.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, InvInventoryProductUserMaterial::new), "商品领用物料统计报表");
    }

    @Autowired
    private InvInventoryDocumentMapper invInventoryDocumentMapper;
    @Autowired
    private InvInventoryDocumentItemMapper invInventoryDocumentItemMapper;
    @Autowired
    private PurPurchaseOrderMapper purchaseOrderMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purchaseOrderItemMapper;
    @Autowired
    private SalSalesOrderMapper salesOrderMapper;
    @Autowired
    private SalSalesOrderItemMapper salesOrderItemMapper;
    @Autowired
    private DelDeliveryNoteMapper deliveryNoteMapper;
    @Autowired
    private DelDeliveryNoteItemMapper deliveryNoteItemMapper;
    @Autowired
    private DelDeliveryNoteServiceImpl delDeliveryNoteServiceImpl;

    /**
     * 采购订单生成流水
     */
    @ApiOperation(value = "采购订单生成流水", notes = "采购订单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/payment/estimation/purchase")
    public void createPaymentEstimation() {
        changguiPurchase();
    }

    /**
     * 采购订单生成流水
     */
    @ApiOperation(value = "采购订单生成流水", notes = "采购订单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/payment/estimation/purchase/cx")
    public void createPaymentEstimationCx() {
        chongxiaoPurchase();
    }

    /**
     * 销售订单生成流水
     */
    @ApiOperation(value = "销售订单生成流水", notes = "销售订单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/receipt/estimation/sale")
    public void createReceiptEstimation() {
        changguiSale();
    }

    /**
     * 销售订单生成流水
     */
    @ApiOperation(value = "销售订单生成流水", notes = "销售订单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/receipt/estimation/sale/cx")
    public void createReceiptEstimationCx() {
        chongxiaoSale();
    }

    /**
     * 发货单交货单生成流水
     */
    @ApiOperation(value = "发货单交货单生成流水", notes = "发货单交货单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/estimation/delivery")
    public void createEstimation() {
        changguiDelivery();
    }

    /**
     * 发货单交货单生成流水
     */
    @ApiOperation(value = "发货单交货单生成流水", notes = "发货单交货单生成流水")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/create/estimation/delivery/cx")
    public void createEstimationCx() {
        chongxiaoDelivery();
    }

    public void changguiPurchase() {
        // 先不找冲销
        // 按采购退货订单 SC03、 按采购订单 SR01
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getMovementType, new String[]{"SC03", "SR01"})
                .le(InvInventoryDocument::getCreateDate, "2024-05-20 23:59:59")
                .ne(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 获取库存凭证明细
                List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                        .lambda().eq(InvInventoryDocumentItem::getInventoryDocumentSid, document.getInventoryDocumentSid()));
                // 写入主表需要的字段（从明细拿关联单据的sid，为了适合多订单出入库主表没存referdocSid的情况）
                if (CollectionUtils.isEmpty(itemList) || itemList.get(0).getReferDocumentSid() == null) {
                    continue;
                }
                else {
                    PurPurchaseOrder order = purchaseOrderMapper.selectById(itemList.get(0).getReferDocumentSid());
                    if (order == null) {
                        continue;
                    }
                    else {
                        document.setIsFinanceBookYfzg(order.getIsFinanceBookYfzg());
                        document.setIsReturnGoods(order.getIsReturnGoods());
                        document.setPurchaseOrderCode(String.valueOf(order.getPurchaseOrderCode()));
                    }
                }
                // 特殊字段 ： 是否冲销
                document.setDocumentTypeInv(document.getDocumentType());
                // 对明细遍历获取生成流水需要的数据（针对之前生成流水的方法进行写入数据）
                for (InvInventoryDocumentItem item : itemList) {
                    if (item.getReferDocumentItemSid() != null) {
                        PurPurchaseOrderItem orderItem = purchaseOrderItemMapper.selectById(item.getReferDocumentItemSid());
                        if (orderItem != null) {
                            item.setInvPrice(orderItem.getPurchasePrice());
                            item.setInvPriceTax(orderItem.getPurchasePriceTax());
                            item.setPurchaseOrderSid(orderItem.getPurchaseOrderSid());
                            item.setPurchaseContractSid(orderItem.getPurchaseContractSid());
                            item.setPurchaseContractCode(orderItem.getPurchaseContractCode());
                        }
                    }
                }
                document.setInvInventoryDocumentItemList(itemList);
                // 调用生成流水的方法
                createpayment(document, itemList);
            }
        }
    }

    public void chongxiaoPurchase() {
        // 最后找冲销
        // 按采购退货订单 SC03、 按采购订单 SR01
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getInventoryDocumentSid, "1792731553108406274", "1792725991591325698")
                .in(InvInventoryDocument::getMovementType, new String[]{"SC03", "SR01"})
                .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 特殊字段 ： 是否冲销
                document.setDocumentTypeInv(document.getDocumentType());
                // 调用生成流水的方法
                createpaymentChongxiao(document);
            }
        }
    }

    public void changguiSale() {
        // 先不找冲销
        // SC01、按销售订单(供应商直发)  SC011、按销售订单(供应商直发)  SR03、按销售退货订单
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getMovementType, new String[]{"SC01", "SC011", "SR03"})
                .le(InvInventoryDocument::getCreateDate, "2024-05-20 23:59:59")
                .ne(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 获取库存凭证明细
                List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                        .lambda().eq(InvInventoryDocumentItem::getInventoryDocumentSid, document.getInventoryDocumentSid()));
                // 写入主表需要的字段（从明细拿关联单据的sid，为了适合多订单出入库主表没存referdocSid的情况）
                if (CollectionUtils.isEmpty(itemList) || itemList.get(0).getReferDocumentSid() == null) {
                    continue;
                }
                else {
                    SalSalesOrder order = salesOrderMapper.selectById(itemList.get(0).getReferDocumentSid());
                    if (order == null) {
                        continue;
                    }
                    else {
                        document.setIsFinanceBookYszg(order.getIsFinanceBookYszg());
                        document.setIsReturnGoods(order.getIsReturnGoods());
                        document.setSalesOrderCode(String.valueOf(order.getSalesOrderCode()));
                    }
                }
                // 特殊字段 ： 是否冲销
                document.setDocumentTypeInv(document.getDocumentType());
                // 对明细遍历获取生成流水需要的数据（针对之前生成流水的方法进行写入数据）
                for (InvInventoryDocumentItem item : itemList) {
                    if (item.getReferDocumentItemSid() != null) {
                        SalSalesOrderItem orderItem = salesOrderItemMapper.selectById(item.getReferDocumentItemSid());
                        if (orderItem != null) {
                            item.setInvPrice(orderItem.getSalePrice());
                            item.setInvPriceTax(orderItem.getSalePriceTax());
                            item.setSalesOrderSid(orderItem.getSalesOrderSid());
                            item.setSaleContractSid(orderItem.getSaleContractSid());
                            item.setSaleContractCode(orderItem.getSaleContractCode());
                        }
                    }
                }
                document.setInvInventoryDocumentItemList(itemList);
                // 调用生成流水的方法
                createReceipt(document, itemList);
            }
        }
    }

    public void chongxiaoSale() {
        // 先不找冲销
        // SC01、按销售订单(供应商直发)  SC011、按销售订单(供应商直发)  SR03、按销售退货订单
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getMovementType, new String[]{"SC01", "SC011", "SR03"})
                .le(InvInventoryDocument::getCreateDate, "2024-05-20 23:59:59")
                .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 特殊字段 ： 是否冲销
                document.setDocumentTypeInv(document.getDocumentType());
                // 调用生成流水的方法
                createReceiptChongxiao(document);
            }
        }
    }

    public void changguiDelivery() {
        // 先不找冲销
        // SC04 按采购退货发货单  SR02 按采购交货单 SC02、 按销售发货单  SC021、 按销售发货单(直发)  SR04  按销售退货收货单
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getMovementType, new String[]{"SC04", "SR02", "SC02", "SC021", "SR04"})
                .le(InvInventoryDocument::getCreateDate, "2024-05-20 23:59:59")
                .ne(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 获取库存凭证明细
                List<InvInventoryDocumentItem> itemList = invInventoryDocumentItemMapper.selectList(new QueryWrapper<InvInventoryDocumentItem>()
                        .lambda().eq(InvInventoryDocumentItem::getInventoryDocumentSid, document.getInventoryDocumentSid()));
                // 写入主表需要的字段（从明细拿关联单据的sid，为了适合多订单出入库主表没存referdocSid的情况）
                if (CollectionUtils.isEmpty(itemList) || itemList.get(0).getReferDocumentSid() == null) {
                    continue;
                }
                else {
                    DelDeliveryNote note = delDeliveryNoteServiceImpl.selectDelDeliveryNoteById(itemList.get(0).getReferDocumentSid(), null);
                    Map<Long, DelDeliveryNoteItem> map = new HashMap<>();
                    if (note == null) {
                        continue;
                    }
                    if (CollectionUtils.isNotEmpty(note.getDelDeliveryNoteItemList())) {
                        map = note.getDelDeliveryNoteItemList().stream().collect(Collectors.toMap(
                                DelDeliveryNoteItem::getDeliveryNoteItemSid, Function.identity()));
                    }
                    document.setIsFinanceBookYszg(note.getIsFinanceBookYszg());
                    document.setIsFinanceBookYfzg(note.getIsFinanceBookYfzg());
                    document.setIsReturnGoods(note.getIsReturnGoods());
                    document.setDeliveryNoteSid(note.getDeliveryNoteSid());
                    document.setDeliveryNoteCode(note.getDeliveryNoteCode());
                    // 特殊字段 ： 是否冲销
                    document.setDocumentTypeInv(document.getDocumentType());
                    // 对明细遍历获取生成流水需要的数据（针对之前生成流水的方法进行写入数据）
                    for (InvInventoryDocumentItem item : itemList) {
                        if (item.getReferDocumentItemSid() != null) {
                            DelDeliveryNoteItem noteItem = map.get(item.getReferDocumentItemSid());
                            if (noteItem != null) {
                                if (document.getReferDocCategory().equals(DocCategory.SALE_RU.getCode())
                                        || document.getReferDocCategory().equals(DocCategory.RETURN_BACK_SALE_RECEPIT.getCode())) {
                                    item.setInvPrice(noteItem.getSalePrice());
                                    item.setInvPriceTax(noteItem.getSalePriceTax());
                                }
                                else {
                                    item.setInvPrice(noteItem.getPurchasePrice());
                                    item.setInvPriceTax(noteItem.getPurchasePriceTax());
                                }
                                item.setSalesOrderSid(noteItem.getSalesOrderSid());
                                item.setSaleContractSid(noteItem.getSaleContractSid());
                                item.setSaleContractCode(noteItem.getSaleContractCode());
                                item.setPurchaseOrderSid(noteItem.getPurchaseOrderSid());
                                item.setPurchaseContractSid(noteItem.getPurchaseContractSid());
                            }
                        }
                    }
                    document.setInvInventoryDocumentItemList(itemList);
                    // 调用生成流水的方法
                    createpayment(document, itemList);
                    // 调用生成流水的方法
                    createReceipt(document, itemList);
                }
            }
        }
    }

    public void chongxiaoDelivery() {
        // 先不找冲销
        // SC04 按采购退货发货单  SR02 按采购交货单 SC02、 按销售发货单  SC021、 按销售发货单(直发)  SR04  按销售退货收货单
        List<InvInventoryDocument> list = invInventoryDocumentMapper.selectList(new QueryWrapper<InvInventoryDocument>().lambda()
                .in(InvInventoryDocument::getMovementType, new String[]{"SC04", "SR02", "SC02", "SC021", "SR04"})
                .le(InvInventoryDocument::getCreateDate, "2024-05-20 23:59:59")
                .eq(InvInventoryDocument::getDocumentType, ConstantsEms.DOCUMNET_TYPE_CX));
        if (CollectionUtils.isNotEmpty(list)) {
            for (InvInventoryDocument document : list) {
                // 特殊字段 ： 是否冲销
                document.setDocumentTypeInv(document.getDocumentType());
                // 调用生成流水的方法
                createpaymentChongxiao(document);
                // 调用生成流水的方法
                createReceiptChongxiao(document);
            }
        }
    }

    @Autowired
    private FinBookPaymentEstimationServiceImpl finBookPaymentEstimationServiceImpl;
    @Autowired
    private FinBookReceiptEstimationServiceImpl finBookReceiptEstimationServiceImpl;
    @Autowired
    private FinBookReceiptEstimationMapper finBookReceiptEstimationMapper;
    @Autowired
    private FinBookReceiptEstimationItemMapper finBookReceiptEstimationItemMapper;
    @Autowired
    private FinBookPaymentEstimationMapper finBookPaymentEstimationMapper;
    @Autowired
    private FinBookPaymentEstimationItemMapper finBookPaymentEstimationItemMapper;

    public void createpayment(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        String documentCategory = invInventoryDocument.getDocumentCategory();
        String isFinanceBookYfzg = invInventoryDocument.getIsFinanceBookYfzg();
        String isReturnGoods = invInventoryDocument.getIsReturnGoods();
        if (ConstantsEms.YES.equals(isFinanceBookYfzg)) {
            FinBookPaymentEstimation finBookPaymentEstimation = new FinBookPaymentEstimation();
            BeanCopyUtils.copyProperties(invInventoryDocument, finBookPaymentEstimation);
            finBookPaymentEstimation.setCreateDate(invInventoryDocument.getCreateDate())
                    .setCreatorAccount(invInventoryDocument.getCreatorAccount())
                    .setUpdateDate(null).setUpdaterAccount(null)
                    .setConfirmDate(invInventoryDocument.getCreateDate()).setConfirmerAccount(invInventoryDocument.getConfirmerAccount());
            finBookPaymentEstimation.setHandleStatus(ConstantsEms.CHECK_STATUS);
            finBookPaymentEstimation.setBookSourceCategory(ConstantsEms.PURCHASE_CATEGORY);
            if (code.equals(ConstantsEms.PURCHASE_ORDER_RU_L) || code.equals(ConstantsEms.PURCHASE_ORDER_RU_R)) {
                finBookPaymentEstimation.setBookSourceCategory(ConstantsEms.PURCHASE_CATEGORY_BACK);
            }

            // 若作业类型为“按采购订单”、“按采购交货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“否”
            // 若作业类型为“按销售退货订单”、“按销售退货收货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“是”
            if (ConstantsInventory.MOVEMENT_TYPE_SR01.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SR02.equals(invInventoryDocument.getMovementType())) {
                finBookPaymentEstimation.setIsTuihuo(ConstantsEms.NO);
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SC03.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SC04.equals(invInventoryDocument.getMovementType())) {
                finBookPaymentEstimation.setIsTuihuo(ConstantsEms.YES);
            }
            // 对账账期格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            finBookPaymentEstimation.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
            if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
                ArrayList<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = new ArrayList<>();
                invInventoryDocumentItemList.forEach(item -> {
                    if (!ConstantsEms.YES.equals(item.getFreeFlag()) && item.getInvPrice() != null && BigDecimal.ZERO.compareTo(item.getInvPrice()) != 0) {
                        FinBookPaymentEstimationItem finBookPaymentEstimationItem = new FinBookPaymentEstimationItem();
                        BeanCopyUtils.copyProperties(item, finBookPaymentEstimationItem);
                        finBookPaymentEstimationItem.setCreateDate(item.getCreateDate())
                                .setCreatorAccount(item.getCreatorAccount());
                        int itemNum = item.getItemNum();
                        finBookPaymentEstimationItem.setCreateDate(new Date())
                                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setPriceTax(item.getInvPriceTax())
                                .setItemNum(Long.valueOf(itemNum))
                                .setUnitPrice(item.getUnitPrice())
                                .setQuantity(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity())
                                .setPrice(item.getInvPrice())
                                .setReferDocSid(invInventoryDocument.getInventoryDocumentSid())
                                .setReferDocItemSid(item.getInventoryDocumentItemSid())
                                .setPurchaseContractSid(item.getPurchaseContractSid())
                                .setPurchaseContractCode(item.getPurchaseOrderCode())
                                .setPurchaseOrderSid(item.getPurchaseOrderSid())
                                .setDeliveryNoteSid(invInventoryDocument.getDeliveryNoteSid())
                                .setClearStatus("WHX")
                                .setIsBusinessVerify(ConstantsEms.NO)
                                .setReferDocCategory(documentCategory)
                                .setClearStatusMoney("WHX")
                                .setClearStatusQuantity("WHX");
                        // 对账账期
                        if (invInventoryDocument.getAccountDate() != null) {
                            finBookPaymentEstimationItem.setBusinessVerifyPeriod(sdf.format(invInventoryDocument.getAccountDate()));
                        }
                        if (ConstantsEms.YES.equals(isReturnGoods)) {
                            if (item.getQuantity() != null && item.getInvPriceTax() != null) {
                                finBookPaymentEstimationItem.setQuantity((item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().abs()).multiply(new BigDecimal(-1)));
                                finBookPaymentEstimationItem.setCurrencyAmountTax(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity().multiply(new BigDecimal(-1).multiply(item.getInvPriceTax())));
                            }
                        }
                        if (item.getInvPriceTax() != null) {
                            finBookPaymentEstimationItem.setCurrencyAmountTax(finBookPaymentEstimationItem.getQuantity().multiply(item.getInvPriceTax()));
                        }
                        finBookPaymentEstimationItems.add(finBookPaymentEstimationItem);
                    }
                });
                finBookPaymentEstimation.setItemList(finBookPaymentEstimationItems);
            }
            List<FinBookPaymentEstimationItem> itemList = finBookPaymentEstimation.getItemList();
            if (CollectionUtil.isNotEmpty(itemList)) {
                finBookPaymentEstimationServiceImpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
            }
        }

    }

    public void createpaymentChongxiao(InvInventoryDocument invInventoryDocument) {
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        //冲销
        if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
            List<FinBookPaymentEstimationItem> finBookPaymentEstimationItems = finBookPaymentEstimationItemMapper.selectList(new QueryWrapper<FinBookPaymentEstimationItem>().lambda()
                    .eq(FinBookPaymentEstimationItem::getReferDocSid, invInventoryDocument.getPreInventoryDocumentSid())
            );
            if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
                FinBookPaymentEstimation finBookPaymentEstimation = finBookPaymentEstimationMapper.selectById(finBookPaymentEstimationItems.get(0).getBookPaymentEstimationSid());
                //修改原流水
                UpdateWrapper<FinBookPaymentEstimationItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.in("book_payment_estimation_sid", finBookPaymentEstimation.getBookPaymentEstimationSid())
                        .set("clear_status", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("clear_status_quantity", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("clear_status_money", ConstantsFinance.CLEAR_STATUS_QHX)
                        .set("currency_amount_tax_hxz", BigDecimal.ZERO)
                        .set("quantity_hxz", BigDecimal.ZERO);
                updateWrapper.setSql("currency_amount_tax_yhx = currency_amount_tax , quantity_yhx = quantity");
                int row = finBookPaymentEstimationItemMapper.update(null, updateWrapper);
                finBookPaymentEstimation.setBookPaymentEstimationSid(null)
                        .setBookFeature(ConstantsFinance.BOOK_FEATURE_CX)
                        .setAccountDate(new Date())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setBookPaymentEstimationCode(null);
                finBookPaymentEstimationItems.forEach(li -> {
                    if (li.getQuantity() != null) {
                        li.setQuantity(li.getQuantity().multiply(new BigDecimal(-1)));
                    }
                    if (li.getCurrencyAmountTax() != null) {
                        li.setCurrencyAmountTax(li.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
                    }
                    li.setReferDocSid(invInventoryDocument.getInventoryDocumentSid());
                    li.setBookPaymentEstimationItemSid(null);
                });
                finBookPaymentEstimation.setItemList(finBookPaymentEstimationItems);
                finBookPaymentEstimationServiceImpl.insertFinBookPaymentEstimation(finBookPaymentEstimation);
            }
        }
    }

    public void createReceipt(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> invInventoryDocumentItemList) {
        String code = invInventoryDocument.getMovementType();
        String documentCategory = invInventoryDocument.getDocumentCategory();
        String isFinanceBookYszg = invInventoryDocument.getIsFinanceBookYszg();
        String isReturnGoods = invInventoryDocument.getIsReturnGoods();
        //应收暂估流水：销售退货订单、销售退货收货单
        if (ConstantsEms.YES.equals(isFinanceBookYszg)) {
            FinBookReceiptEstimation finBookReceiptEstimation = new FinBookReceiptEstimation();
            BeanCopyUtils.copyProperties(invInventoryDocument, finBookReceiptEstimation);
            finBookReceiptEstimation.setBookSourceCategory(ConstantsEms.SALE_CATEGORY);
            finBookReceiptEstimation.setHandleStatus(ConstantsEms.CHECK_STATUS);
            finBookReceiptEstimation.setProductSeasonSid(invInventoryDocument.getProductSeasonSid());

            // 若作业类型为“按销售订单”、“按销售发货单”，若生成暂估流水，默认生成暂估流水的“是否退货”为“否”
            // 若作业类型为“按采购退货订单”、“按采购退货发货单” ，若生成暂估流水，默认生成暂估流水的“是否退货”为“是”
            if (ConstantsInventory.MOVEMENT_TYPE_SC01.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SC02.equals(invInventoryDocument.getMovementType())) {
                finBookReceiptEstimation.setIsTuihuo(ConstantsEms.NO);
            }
            else if (ConstantsInventory.MOVEMENT_TYPE_SR03.equals(invInventoryDocument.getMovementType())
                    || ConstantsInventory.MOVEMENT_TYPE_SR04.equals(invInventoryDocument.getMovementType())) {
                finBookReceiptEstimation.setIsTuihuo(ConstantsEms.YES);
            }
            if (code.equals(ConstantsEms.SALE_ORDER_RU_R) || code.equals(ConstantsEms.SALE_ORDER_RU_L)) {
                finBookReceiptEstimation.setBookSourceCategory(ConstantsEms.SALE_CATEGORY_BACK);
            }
            // 对账账期格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            finBookReceiptEstimation.setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN);
            if (CollectionUtils.isNotEmpty(invInventoryDocumentItemList)) {
                ArrayList<FinBookReceiptEstimationItem> finBookPaymentEstimationItems = new ArrayList<>();
                invInventoryDocumentItemList.forEach(item -> {
                    // TODO invPrice
                    if (!ConstantsEms.YES.equals(item.getFreeFlag()) && item.getInvPrice() != null && BigDecimal.ZERO.compareTo(item.getInvPrice()) != 0) {
                        FinBookReceiptEstimationItem finBookReceipEstimationItem = new FinBookReceiptEstimationItem();
                        BeanCopyUtils.copyProperties(item, finBookReceipEstimationItem);
                        int itemNum = item.getItemNum();
                        finBookReceipEstimationItem.setCreateDate(new Date())
                                .setItemNum(Long.valueOf(itemNum))
                                // TODO
                                .setPriceTax(item.getInvPriceTax())
                                .setQuantity(item.getPriceQuantity() != null ? item.getPriceQuantity() : item.getQuantity())
                                // TODO
                                .setPrice(item.getInvPrice())
                                .setUnitPrice(item.getUnitPrice())
                                .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setReferDocSid(invInventoryDocument.getInventoryDocumentSid())
                                .setReferDocItemSid(item.getInventoryDocumentItemSid())
                                // TODO
                                .setSaleContractCode(item.getSaleContractCode())
                                // TODO
                                .setSaleContractSid(item.getSaleContractSid())
                                // TODO
                                .setSalesOrderSid(item.getSalesOrderSid())
                                // TODO
                                .setDeliveryNoteSid(invInventoryDocument.getDeliveryNoteSid())
                                .setClearStatus("WHX")
                                .setIsBusinessVerify(ConstantsEms.NO)
                                .setReferDocCategory(documentCategory)
                                .setClearStatusMoney("WHX")
                                .setClearStatusQuantity("WHX");
                        // 对账账期
                        if (invInventoryDocument.getAccountDate() != null) {
                            finBookReceipEstimationItem.setBusinessVerifyPeriod(sdf.format(invInventoryDocument.getAccountDate()));
                        }
                        // TODO
                        String salesOrderCode = invInventoryDocument.getSalesOrderCode();
                        if (salesOrderCode != null) {
                            finBookReceipEstimationItem.setSalesOrderCode(String.valueOf(salesOrderCode));
                        }
                        if (ConstantsEms.YES.equals(isReturnGoods)) {
                            // TODO
                            if (item.getQuantity() != null && item.getInvPriceTax() != null) {
                                finBookReceipEstimationItem.setQuantity((item.getPriceQuantity() != null ? item.getPriceQuantity()
                                        : item.getQuantity().abs()).multiply(new BigDecimal(-1)));
                                finBookReceipEstimationItem.setCurrencyAmountTax(item.getPriceQuantity() != null ? item.getPriceQuantity()
                                        // TODO
                                        : item.getQuantity().multiply(new BigDecimal(-1).multiply(item.getInvPriceTax())));
                            }
                        }
                        // TODO
                        if (item.getInvPriceTax() != null) {
                            finBookReceipEstimationItem.setCurrencyAmountTax(finBookReceipEstimationItem.getQuantity().multiply(item.getInvPriceTax()));
                        }
                        finBookPaymentEstimationItems.add(finBookReceipEstimationItem);
                    }
                });
                finBookReceiptEstimation.setItemList(finBookPaymentEstimationItems);
                if (CollectionUtil.isNotEmpty(finBookPaymentEstimationItems)) {
                    finBookReceiptEstimationServiceImpl.insertFinBookReceiptEstimation(finBookReceiptEstimation);
                }
            }
        }
    }

    public void createReceiptChongxiao(InvInventoryDocument invInventoryDocument) {
        String documentTypeInv = invInventoryDocument.getDocumentTypeInv();
        if (ConstantsEms.DOCUMNET_TYPE_CX.equals(documentTypeInv)) {
            List<FinBookReceiptEstimationItem> finBookReceiptEstimationItems = finBookReceiptEstimationItemMapper.selectList(new QueryWrapper<FinBookReceiptEstimationItem>().lambda()
                    .eq(FinBookReceiptEstimationItem::getReferDocSid, invInventoryDocument.getPreInventoryDocumentSid())
            );
            if (CollectionUtil.isNotEmpty(finBookReceiptEstimationItems)) {
                FinBookReceiptEstimation finBookReceiptEstimation = finBookReceiptEstimationMapper.selectById(finBookReceiptEstimationItems.get(0).getBookReceiptEstimationSid());
                //修改原流水
                UpdateWrapper<FinBookReceiptEstimationItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("clear_status", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("clear_status_quantity", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("clear_status_money", ConstantsFinance.CLEAR_STATUS_QHX);
                updateWrapper.set("currency_amount_tax_hxz", BigDecimal.ZERO);
                updateWrapper.set("quantity_hxz", BigDecimal.ZERO);
                updateWrapper.setSql("currency_amount_tax_yhx = currency_amount_tax , quantity_yhx = quantity");
                updateWrapper.in("book_receipt_estimation_sid", finBookReceiptEstimation.getBookReceiptEstimationSid());
                int row = finBookReceiptEstimationItemMapper.update(null, updateWrapper);
                finBookReceiptEstimation.setBookReceiptEstimationSid(null)
                        .setBookFeature(ConstantsFinance.BOOK_FEATURE_CX)
                        .setAccountDate(new Date())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                        .setBookReceiptEstimationCode(null);
                finBookReceiptEstimationItems.forEach(li -> {
                    if (li.getQuantity() != null) {
                        li.setQuantity(li.getQuantity().multiply(new BigDecimal(-1)));
                    }
                    if (li.getCurrencyAmountTax() != null) {
                        li.setCurrencyAmountTax(li.getCurrencyAmountTax().multiply(new BigDecimal(-1)));
                    }
                    li.setReferDocSid(invInventoryDocument.getInventoryDocumentSid());
                    li.setBookReceiptEstimationItemSid(null);
                });
                finBookReceiptEstimation.setItemList(finBookReceiptEstimationItems);
                finBookReceiptEstimationServiceImpl.insertFinBookReceiptEstimation(finBookReceiptEstimation);
            }
        }
    }



}
