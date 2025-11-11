package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.DelDeliveryNote;
import com.platform.ems.domain.DelDeliveryNoteItem;
import com.platform.ems.domain.dto.request.DelDeliveryNoteCreateRequest;
import com.platform.ems.domain.dto.response.export.DelDeliveryNoteItemPurResponse;
import com.platform.ems.domain.dto.response.export.DelDeliveryNoteItemSalResponse;
import com.platform.ems.service.IDelDeliveryNoteItemService;
import com.platform.ems.service.IDelDeliveryNoteService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 交货单Controller
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/del/delivery/note")
@Api(tags = "交货单")
public class DelDeliveryNoteController extends BaseController {

    @Autowired
    private IDelDeliveryNoteService delDeliveryNoteService;
    @Autowired
    private IDelDeliveryNoteItemService delDeliveryNoteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询交货单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询交货单列表", notes = "查询交货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    public TableDataInfo list(@RequestBody DelDeliveryNote delDeliveryNote) {
        startPage(delDeliveryNote);
        List<DelDeliveryNote> list = delDeliveryNoteService.selectDelDeliveryNoteList(delDeliveryNote);
        return getDataTable(list);
    }

    /**
     * 导出交货单列表
     */
    @ApiOperation(value = "导出交货单列表", notes = "导出交货单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DelDeliveryNote delDeliveryNote) throws IOException {
        List<DelDeliveryNote> list = delDeliveryNoteService.selectDelDeliveryNoteList(delDeliveryNote);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<DelDeliveryNote> util = new ExcelUtil<>(DelDeliveryNote.class,dataMap);
        util.exportExcel(response, list, "交货单");
    }

    /**
     * 获取交货单详细信息
     */
    @ApiOperation(value = "获取交货单详细信息", notes = "获取交货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long deliveryNoteSid, String deliveryType) {
        if (deliveryNoteSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.selectDelDeliveryNoteById(deliveryNoteSid, deliveryType));
    }

    /**
     * 获取采购交货单
     */
    @ApiOperation(value = "采购交货单", notes = "采购交货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/get/grCode")
    public AjaxResult getQrCode(@RequestBody List<DelDeliveryNote> list) {
        if (CollectionUtils.isEmpty(list)){
            throw new CheckedException("未选择行");
        }
        return AjaxResult.success(delDeliveryNoteService.getQr(list));
    }

    @ApiOperation(value = "出库", notes = "出库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/ck")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult InvCK(Long deliveryNoteSid) {
        if (deliveryNoteSid == null){
            throw new CheckedException("参数缺失");
        }
        return delDeliveryNoteService.invCK(deliveryNoteSid);
    }

    @ApiOperation(value = "根据单号获取对应的商品编码", notes = "根据单号获取对应的商品编码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/get/materialCode")
    public AjaxResult getCode(Long code,String type) {
        if (code == null||type==null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.getMaterialCode(code,type));
    }

    @ApiOperation(value = "生产直发出库", notes = "生产直发出库")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/ck/direct")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult InvCkDirect(Long deliveryNoteSid) {
        if (deliveryNoteSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.invCkDirect(deliveryNoteSid));
    }

    /**
     * 易码通获取交货单详细信息
     */
    @ApiOperation(value = "外部接口-通获取交货单详细信息", notes = "外部接口-通获取交货单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNote.class))
    @PostMapping("/getInfo/out")
    public AjaxResult getInfoOut(Long purchaseOrderSid) {
        if (purchaseOrderSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.getOutDelDeliveryNote(purchaseOrderSid));
    }

    /**
     * 新增交货单
     */
    @ApiOperation(value = "新增交货单", notes = "新增交货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid DelDeliveryNote delDeliveryNote) {
        int row = delDeliveryNoteService.insertDelDeliveryNote(delDeliveryNote);
        if (row > 0) {
            return AjaxResult.success(delDeliveryNoteService.selectDelDeliveryNoteById
                    (delDeliveryNote.getDeliveryNoteSid(), delDeliveryNote.getDeliveryType()));
        }
        return toAjax(row);
    }

    /**
     * 撤回保存前的校验
     */
    @ApiOperation(value = "撤回保存前的校验", notes = "撤回保存前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/back/save/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSaveVerify(@RequestBody DelDeliveryNote delDeliveryNote) {
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNoteService.backSaveVerify(delDeliveryNote));
    }

    /**
     * 撤回保存
     */
    @ApiOperation(value = "撤回保存", notes = "撤回保存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/back/save")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult backSave(@RequestBody DelDeliveryNote delDeliveryNote) {
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new CheckedException("参数缺失");
        }
        if (StrUtil.isBlank(delDeliveryNote.getComment())) {
            throw new CheckedException("撤回说明不能为空");
        }
        return toAjax(delDeliveryNoteService.backSave(delDeliveryNote));
    }

    /**
     * 维护物流信息
     */
    @ApiOperation(value = "维护物流信息", notes = "维护物流信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/set/carrier")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult setCarrier(@RequestBody DelDeliveryNote delDeliveryNote) {
        if (delDeliveryNote.getDeliveryNoteSid() == null) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNoteService.setCarrier(delDeliveryNote));
    }

    /**
     * 修改交货单
     */
    @ApiOperation(value = "修改交货单", notes = "修改交货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid DelDeliveryNote delDeliveryNote) {
        return toAjax(delDeliveryNoteService.updateDelDeliveryNote(delDeliveryNote));
    }

    @ApiOperation(value = "判断是否超量", notes = "判断是否超量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/judge/over")
    public AjaxResult judge(@RequestBody @Valid DelDeliveryNote delDeliveryNote) {
        return delDeliveryNoteService.checkProcess(delDeliveryNote);
    }

    @ApiOperation(value = "提交时校验", notes = "提交时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/process/check")
    public AjaxResult process(@RequestBody List<Long> deliveryNoteSidList) {
        return AjaxResult.success(delDeliveryNoteService.processCheck(deliveryNoteSidList));
    }

    @ApiOperation(value = "明细报表-生成库存预留", notes = "明细报表-生成库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/create/reserve")
    public AjaxResult creReportInv(@RequestBody List<Long> deliveryNoteSidList) {
        return AjaxResult.success(delDeliveryNoteService.reportCreateInv(deliveryNoteSidList));
    }

    @ApiOperation(value = "明细报表-释放库存预留", notes = "明细报表-释放库存预留")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PostMapping("/free/reserve")
    public AjaxResult freeReportInv(@RequestBody List<Long> deliveryNoteSidList) {
        return AjaxResult.success(delDeliveryNoteService.reportFreeInv(deliveryNoteSidList));
    }
    /**
     * 删除交货单
     */
    @ApiOperation(value = "删除交货单", notes = "删除交货单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] deliveryNoteSids) {
        if (ArrayUtil.isEmpty(deliveryNoteSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(delDeliveryNoteService.deleteDelDeliveryNoteByIds(deliveryNoteSids));
    }

    @ApiOperation(value = "获取打印交货单数据", notes = "获取打印交货单数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/print")
    public AjaxResult print(@RequestBody Long[] deliveryNoteSids) {
        if (ArrayUtil.isEmpty(deliveryNoteSids)){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(delDeliveryNoteService.getPrint(deliveryNoteSids));
    }

    /**
     * 创建物料销售订单
     */
    @ApiOperation(value = "创建物料销售订单", notes = "创建物料销售订单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/create/saleOrder")
    public AjaxResult createSale(@RequestBody DelDeliveryNoteCreateRequest request) {
        return AjaxResult.success(delDeliveryNoteService.createSaleOrder(request));
    }

    /**
     * 创建常特转移单
     */
    @ApiOperation(value = "创建常特转移单", notes = "创建常特转移单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/create/invSpec")
    public AjaxResult createSaleInv(@RequestBody List<Long> sidList) {
        return AjaxResult.success(delDeliveryNoteService.createInvSpec(sidList));
    }


    /**
     * 交货单确认
     */
    @PostMapping("/confirm")
    @ApiOperation(value = "交货单确认", notes = "交货单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult confirm(@RequestBody DelDeliveryNote delDeliveryNote) {
        return AjaxResult.success(delDeliveryNoteService.confirm(delDeliveryNote));
    }

    /**
     * 交货单变更
     */
    @PostMapping("/change")
    @ApiOperation(value = "交货单变更", notes = "交货单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid DelDeliveryNote delDeliveryNote) {
        return AjaxResult.success(delDeliveryNoteService.updateDelDeliveryNote(delDeliveryNote));
    }

    /**
     * 销售发货单明细/采购交货单明细报表
     */
    @PostMapping("/getShipmentsItemList")
    @ApiOperation(value = "销售发货单明细/采购交货单明细报表", notes = "销售发货单明细/采购交货单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DelDeliveryNoteItem.class))
    public TableDataInfo getShipmentsItemList(@RequestBody DelDeliveryNoteItem delDeliveryNoteItem) {
        if(StrUtil.isEmpty(delDeliveryNoteItem.getDeliveryCategory())){
            throw new BaseException("类型不能为空");
        }
        startPage(delDeliveryNoteItem);
        List<DelDeliveryNoteItem> list = delDeliveryNoteItemService.getShipmentsItemList(delDeliveryNoteItem);
        TableDataInfo data = getDataTable(list);
        delDeliveryNoteItemService.handleInoutStatus(list);
        data.setRows(list);
        return data;
    }

    /**
     * 销售发货单明细/采购交货单明细报表
     */
    @ApiOperation(value = "导出销售发货单明细/采购交货单明细报表", notes = "导出销售发货单明细/采购交货单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/shipmentsItem/export")
    public void exportShipmentsItem(HttpServletResponse response, DelDeliveryNoteItem delDeliveryNoteItem) throws IOException {
        if(StrUtil.isEmpty(delDeliveryNoteItem.getDeliveryCategory())){
            throw new BaseException("类型不能为空");
        }
        String deliveryCategory = delDeliveryNoteItem.getDeliveryCategory();
        if(ConstantsEms.DEL_CATEGORY_SD.equals(deliveryCategory)){
            List<DelDeliveryNoteItem> list = delDeliveryNoteItemService.getShipmentsItemList(delDeliveryNoteItem);
            delDeliveryNoteItemService.handleInoutStatus(list);
            Map<String,Object> dataMap=sysDictDataService.getDictDataList();
            ExcelUtil<DelDeliveryNoteItemSalResponse> util = new ExcelUtil<>(DelDeliveryNoteItemSalResponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list,DelDeliveryNoteItemSalResponse::new), "销售发货单明细报表");
        }else{
            List<DelDeliveryNoteItem> list = delDeliveryNoteItemService.getShipmentsItemList(delDeliveryNoteItem);
            delDeliveryNoteItemService.handleInoutStatus(list);
            Map<String,Object> dataMap=sysDictDataService.getDictDataList();
            ExcelUtil<DelDeliveryNoteItemPurResponse> util = new ExcelUtil<>(DelDeliveryNoteItemPurResponse.class,dataMap);
            util.exportExcel(response, BeanCopyUtils.copyListProperties(list,DelDeliveryNoteItemPurResponse::new), "采购交货单明细报表");
        }
    }

}
