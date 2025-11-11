package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsObject;
import com.platform.ems.domain.*;
import com.platform.ems.service.IReqPurchaseRequireItemService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.data.BigDecimalSum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 采购申请单-明细Controller
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@RestController
@RequestMapping("/require/item")
@Api(tags = "采购申请单-明细")
public class ReqPurchaseRequireItemController extends BaseController {

    @Autowired
    private IReqPurchaseRequireItemService reqPurchaseRequireItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询采购申请单-明细列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购申请单-明细列表", notes = "查询采购申请单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireItem.class))
    public TableDataInfo list(@RequestBody ReqPurchaseRequireItem reqPurchaseRequireItem) {
        startPage(reqPurchaseRequireItem);
        List<ReqPurchaseRequireItem> list = reqPurchaseRequireItemService.selectReqPurchaseRequireItemList(reqPurchaseRequireItem);
        return getDataTable(list);
    }

    /**
     * 查询采购申请单-明细列表
     */
    @PostMapping("/jumpToOrder")
    @ApiOperation(value = "跳转", notes = "查询采购申请单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireItem.class))
    public AjaxResult jumpToOrder(@RequestBody ReqPurchaseRequireItem reqPurchaseRequireItem) {
        List<ReqPurchaseRequireItem> list = reqPurchaseRequireItemService.selectReqPurchaseRequireItemList(reqPurchaseRequireItem);
        if (CollectionUtil.isNotEmpty(list)) {
            PurPurchaseOrder order = new PurPurchaseOrder();
            List<PurPurchaseOrderItem> orderItemList = new ArrayList<>();
            BeanCopyUtils.copyProperties(list.get(0), order);
            order.setHandleStatus(ConstantsEms.SAVA_STATUS).setRemark(null)
                    .setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                    .setUpdateDate(null).setUpdaterAccount(null).setUpdaterAccountName(null)
                    .setConfirmDate(null).setConfirmerAccount(null).setConfirmerAccountName(null);
            // 明细
            Map<String, List<ReqPurchaseRequireItem>> map = list.stream()
                    .collect(Collectors.groupingBy(e -> String.valueOf(e.getMaterialSid()) + "-" + String.valueOf(e.getSku1Sid())
                     + "-" + String.valueOf(e.getSku2Sid()) + "-" + String.valueOf(e.getBarcodeSid())));
            for (String key : map.keySet()) {
                List<ReqPurchaseRequireItem> itemList = map.get(key);
                PurPurchaseOrderItem orderItem = new PurPurchaseOrderItem();
                BeanCopyUtils.copyProperties(itemList.get(0), orderItem);
                BigDecimal quantity = itemList.stream().map(ReqPurchaseRequireItem::getDaiExecuteQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                orderItem.setBarcode(String.valueOf(itemList.get(0).getBarcodeCode())).setQuantity(quantity)
                        .setHandleStatus(null).setItemNum(null).setRemark(null)
                        .setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                        .setUpdateDate(null).setUpdaterAccount(null)
                        .setConfirmDate(null).setConfirmerAccount(null)
                        .setReferDocCategory(ConstantsObject.DATA_OBJECT_PURCHASE_REQUIRE);
                // 数据来源类别
                List<PurPurchaseOrderDataSource> sourceList = new ArrayList<>();
                itemList.forEach(item->{
                    PurPurchaseOrderDataSource source = new PurPurchaseOrderDataSource();
                    source.setReferDocSid(item.getPurchaseRequireSid()).setQuantity(item.getDaiExecuteQuantity())
                            .setReferDocCode(Long.valueOf(item.getPurchaseRequireCode()))
                            .setReferDocItemSid(item.getPurchaseRequireItemSid()).setUnitBase(item.getUnitBase())
                            .setReferDocItemNum(item.getItemNum());
                    source.setRemark(item.getRemark());
                    sourceList.add(source);
                });
                orderItem.setOrderDataSourceList(sourceList);
                orderItemList.add(orderItem);
            }
            order.setPurPurchaseOrderItemList(orderItemList);
            return AjaxResult.success(order);
        }
        return AjaxResult.success();
    }

    /**
     * 导出采购申请单-明细列表
     */
    @Log(title = "采购申请单-明细", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购申请单-明细列表", notes = "导出采购申请单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqPurchaseRequireItem reqPurchaseRequireItem) throws IOException {
        List<ReqPurchaseRequireItem> list = reqPurchaseRequireItemService.selectReqPurchaseRequireItemList(reqPurchaseRequireItem);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ReqPurchaseRequireItem> util = new ExcelUtil<>(ReqPurchaseRequireItem.class, dataMap);
        util.exportExcel(response, list, "采购申请单明细报表");
    }

    /**
     * 获取采购申请单-明细详细信息
     */
    @ApiOperation(value = "获取采购申请单-明细详细信息", notes = "获取采购申请单-明细详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireItem.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseRequireItemSid) {
        if (purchaseRequireItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(reqPurchaseRequireItemService.selectReqPurchaseRequireItemById(purchaseRequireItemSid));
    }

    /**
     * 新增采购申请单-明细
     */
    @ApiOperation(value = "新增采购申请单-明细", notes = "新增采购申请单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购申请单-明细", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return toAjax(reqPurchaseRequireItemService.insertReqPurchaseRequireItem(reqPurchaseRequireItem));
    }

    /**
     * 修改采购申请单-明细
     */
    @ApiOperation(value = "修改采购申请单-明细", notes = "修改采购申请单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购申请单-明细", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqPurchaseRequireItem reqPurchaseRequireItem) {
        return toAjax(reqPurchaseRequireItemService.updateReqPurchaseRequireItem(reqPurchaseRequireItem));
    }

    /**
     * 删除采购申请单-明细
     */
    @ApiOperation(value = "删除采购申请单-明细", notes = "删除采购申请单-明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购申请单-明细", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> purchaseRequireItemSids) {
        if (ArrayUtil.isEmpty(purchaseRequireItemSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(reqPurchaseRequireItemService.deleteReqPurchaseRequireItemByIds(purchaseRequireItemSids));
    }
}
