package com.platform.ems.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.form.FinReceivableBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinReceivableBillItemFormResponse;
import com.platform.ems.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 收款单Controller
 *
 * @author linhongwei
 * @date 2021-04-22
 */
@RestController
@RequestMapping("/receivable/bill")
@Api(tags = "收款单")
public class FinReceivableBillController extends BaseController {

    @Autowired
    private IFinReceivableBillService finReceivableBillService;
    @Autowired
    private IFinReceivableBillItemService finReceivableBillItemService;
    @Autowired
    private IFinReceivableBillItemInvoiceService billItemInvoiceService;
    @Autowired
    private IFinReceivableBillItemKoukuanService koukuanService;
    @Autowired
    private IFinReceivableBillItemYushouService yushouService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询收款单列表
     */
    @ApiOperation(value = "查询收款单列表", notes = "查询收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBill.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinReceivableBill finReceivableBill) {
        startPage(finReceivableBill);
        List<FinReceivableBill> list = finReceivableBillService.selectFinReceivableBillList(finReceivableBill);
        return getDataTable(list);
    }

    /**
     * 导出收款单列表
     */
    @ApiOperation(value = "导出收款单列表", notes = "导出收款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinReceivableBill finReceivableBill) throws IOException {
        List<FinReceivableBill> list = finReceivableBillService.selectFinReceivableBillList(finReceivableBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceivableBill> util = new ExcelUtil<>(FinReceivableBill.class, dataMap);
        util.exportExcel(response, list, "收款单");
    }

    /**
     * 获取收款单详细信息
     */
    @ApiOperation(value = "获取收款单详细信息", notes = "获取收款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long receivableBillSid) {
        if (receivableBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finReceivableBillService.selectFinReceivableBillById(receivableBillSid));
    }

    /**
     * 计算基本信息的待核销金额
     */
    @ApiOperation(value = "计算基本信息的待核销金额", notes = "计算基本信息的待核销金额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/count/dai")
    public AjaxResult countDai(@RequestBody FinReceivableBill finReceivableBill) {
        SysDefaultSettingClient settingClient = finReceivableBillService.getClientSetting();
        finReceivableBillService.countBaseDai(finReceivableBill, settingClient);
        return AjaxResult.success(finReceivableBill);
    }

    /**
     * 新增收款单
     */
    @ApiOperation(value = "新增收款单", notes = "新增收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinReceivableBill finReceivableBill) {
        int row = finReceivableBillService.insertFinReceivableBill(finReceivableBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinReceivableBill().setReceivableBillSid(finReceivableBill.getReceivableBillSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改收款单", notes = "修改收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinReceivableBill finReceivableBill) {
        return toAjax(finReceivableBillService.updateFinReceivableBill(finReceivableBill));
    }

    /**
     * 变更收款单
     */
    @ApiOperation(value = "变更收款单", notes = "变更收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinReceivableBill finReceivableBill) {
        return toAjax(finReceivableBillService.changeFinReceivableBill(finReceivableBill));
    }

    /**
     * 删除收款单
     */
    @ApiOperation(value = "删除收款单", notes = "删除收款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> receivableBillSids) {
        if (CollectionUtils.isEmpty(receivableBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finReceivableBillService.deleteFinReceivableBillByIds(receivableBillSids));
    }

    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "提交前校验", notes = "提交前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    public AjaxResult submitVerify(@RequestBody FinReceivableBill finReceivableBill) {
        if (ArrayUtil.isNotEmpty(finReceivableBill.getReceivableBillSidList())) {
            // 查询页面的提交按钮
            for (Long sid : finReceivableBill.getReceivableBillSidList()) {
                finReceivableBill = finReceivableBillService.selectFinReceivableBillById(sid);
                finReceivableBill.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
                EmsResultEntity result = finReceivableBillService.submitVerify(finReceivableBill);
                if (EmsResultEntity.WARN_TAG.equals(result.getTag())) {
                    return AjaxResult.success(result);
                }
            }
            return AjaxResult.success(EmsResultEntity.success());
        }
        else {
            // 新建、编辑页面的提交按钮
            return AjaxResult.success(finReceivableBillService.submitVerify(finReceivableBill));
        }
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinReceivableBill finReceivableBill) {
        return toAjax(finReceivableBillService.check(finReceivableBill));
    }

    @ApiOperation(value = "到账", notes = "到账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/receipt")
    public AjaxResult receipt(@RequestBody FinReceivableBill finReceivableBill) {
        return toAjax(finReceivableBillService.receipt(finReceivableBill));
    }

    @ApiOperation(value = "撤回保存", notes = "撤回保存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/revocation")
    public AjaxResult revocation(@RequestBody FinReceivableBill finReceivableBill) {
        return toAjax(finReceivableBillService.revocation(finReceivableBill));
    }

    @ApiOperation(value = "更新发票台账", notes = "更新发票台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/invoice/list")
    public AjaxResult invoiceList(@RequestBody FinReceivableBill finReceivableBill) {
        return AjaxResult.success(finReceivableBillService.invoiceList(finReceivableBill));
    }

    @ApiOperation(value = "更新发票台账", notes = "更新发票台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/invoice/update")
    public AjaxResult invoiceUpdate(@RequestBody FinReceivableBill finReceivableBill) {
        return AjaxResult.success(finReceivableBillService.invoiceUpdate(finReceivableBill));
    }

    @ApiOperation(value = "设置是否有票", notes = "设置是否有票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/set/isyoupiao")
    public AjaxResult setIsyoupiao(@RequestBody FinReceivableBill finReceivableBill) {
        return AjaxResult.success(finReceivableBillService.setIsyoupiao(finReceivableBill));
    }

    /**
     * 查询收款单明细报表列表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "查询收款单明细报表列表", notes = "查询收款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBillItemFormResponse.class))
    public TableDataInfo itemList(@RequestBody FinReceivableBillItemFormRequest request) {
        FinReceivableBillItem finReceivableBillItem = new FinReceivableBillItem();
        BeanUtil.copyProperties(request, finReceivableBillItem);
        startPage(request);
        List<FinReceivableBillItem> list = finReceivableBillItemService.selectFinReceivableBillItemList(finReceivableBillItem);
        return getDataTable(list, FinReceivableBillItemFormResponse::new);
    }

    /**
     * 导出收款单明细报表列表
     */
    @ApiOperation(value = "导出收款单明细报表列表", notes = "导出收款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void exportItem(HttpServletResponse response, FinReceivableBillItemFormRequest request) throws IOException {
        FinReceivableBillItem finReceivableBillItem = new FinReceivableBillItem();
        BeanUtil.copyProperties(request, finReceivableBillItem);
        List<FinReceivableBillItem> list = finReceivableBillItemService.selectFinReceivableBillItemList(finReceivableBillItem);
        List<FinReceivableBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinReceivableBillItemFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceivableBillItemFormResponse> util = new ExcelUtil<>(FinReceivableBillItemFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "收款单明细报表");
    }


    /**
     * 获取收款单明细报表详细信息
     */
    @ApiOperation(value = "获取收款单明细报表详细信息", notes = "获取收款单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBillItem.class))
    @PostMapping("/item/getInfo")
    public AjaxResult getItemInfo(Long receivableBillItemSid) {
        if (receivableBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finReceivableBillItemService.selectFinReceivableBillItemById(receivableBillItemSid));
    }

    /**
     * 查询客户发票台账核销记录
     */
    @PostMapping("/item/invoice/list")
    @ApiOperation(value = "查询客户发票台账核销记录", notes = "查询客户发票台账核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBillItemInvoice.class))
    public TableDataInfo itemInvoiceList(@RequestBody FinReceivableBillItemInvoice request) {
        startPage(request);
        List<FinReceivableBillItemInvoice> list = billItemInvoiceService.selectFinReceivableBillItemInvoiceList(request);
        return getDataTable(list);
    }

    /**
     * 导出客户发票台账核销记录
     */
    @ApiOperation(value = "导出客户发票台账核销记录", notes = "导出客户发票台账核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/invoice/export")
    public void exportItem(HttpServletResponse response, FinReceivableBillItemInvoice request) throws IOException {
        List<FinReceivableBillItemInvoice> list = billItemInvoiceService.selectFinReceivableBillItemInvoiceList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceivableBillItemInvoice> util = new ExcelUtil<>(FinReceivableBillItemInvoice.class, dataMap);
        util.exportExcel(response, list, "客户发票台账核销记录");
    }

    /**
     * 查询核销扣款日志列表
     */
    @ApiOperation(value = "查询核销扣款日志列表", notes = "查询核销扣款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBillItemKoukuan.class))
    @PostMapping("/item/koukuan/list")
    public TableDataInfo listKoukuan(@RequestBody FinReceivableBillItemKoukuan request) {
        startPage(request);
        List<FinReceivableBillItemKoukuan> list = koukuanService.selectFinReceivableBillItemKoukuanList(request);
        return getDataTable(list);
    }

    /**
     * 导出核销扣款日志列表
     */
    @ApiOperation(value = "导出核销扣款日志列表", notes = "导出核销扣款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/koukuan/export")
    public void exportKoukuan(HttpServletResponse response, FinReceivableBillItemKoukuan request) throws IOException {
        List<FinReceivableBillItemKoukuan> list = koukuanService.selectFinReceivableBillItemKoukuanList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceivableBillItemKoukuan> util = new ExcelUtil<>(FinReceivableBillItemKoukuan.class, dataMap);
        util.exportExcel(response, list, "客户扣款核销记录");
    }

    /**
     * 查询核销客户已预收款日志列表
     */
    @ApiOperation(value = "查询核销客户已预收款日志列表", notes = "查询核销客户已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBillItemYushou.class))
    @PostMapping("/item/yushou/list")
    public TableDataInfo listYushou(@RequestBody FinReceivableBillItemYushou request) {
        startPage(request);
        List<FinReceivableBillItemYushou> list = yushouService.selectFinReceivableBillItemYushouList(request);
        return getDataTable(list);
    }

    /**
     * 导出核销客户已预收款日志列表
     */
    @ApiOperation(value = "导出核销客户已预收款日志列表", notes = "导出核销客户已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/yushou/export")
    public void exportYushou(HttpServletResponse response, FinReceivableBillItemYushou request) throws IOException {
        List<FinReceivableBillItemYushou> list = yushouService.selectFinReceivableBillItemYushouList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinReceivableBillItemYushou> util = new ExcelUtil<>(FinReceivableBillItemYushou.class, dataMap);
        util.exportExcel(response, list, "已预收款核销记录");
    }

    /**
     * 查询扣款核销记录
     */
    @PostMapping("/book/koukuan/bill/list")
    @ApiOperation(value = "查询扣款核销记录", notes = "查询扣款核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBill.class))
    public TableDataInfo itemKouKuanList(@RequestBody FinReceivableBillItemKoukuan request) {
        List<FinReceivableBillItemKoukuan> koukuanList = koukuanService.selectFinReceivableBillItemKoukuanList(request);
        if (CollectionUtils.isNotEmpty(koukuanList)) {
            Long[] sids = koukuanList.stream().map(FinReceivableBillItemKoukuan::getReceivableBillSid).toArray(Long[]::new);
            //
            FinReceivableBill finReceivableBill = new FinReceivableBill();
            finReceivableBill.setReceivableBillSidList(sids);
            return this.list(finReceivableBill);
        }
        return getDataTable(new ArrayList<>());
    }

    /**
     * 查询预收款核销记录
     */
    @PostMapping("/book/yushou/billl/list")
    @ApiOperation(value = "查询预收款核销记录", notes = "查询预收款核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinReceivableBill.class))
    public TableDataInfo itemYushouList(@RequestBody FinReceivableBillItemYushou request) {
        List<FinReceivableBillItemYushou> yushouList = yushouService.selectFinReceivableBillItemYushouList(request);
        if (CollectionUtils.isNotEmpty(yushouList)) {
            Long[] sids = yushouList.stream().map(FinReceivableBillItemYushou::getReceivableBillSid).toArray(Long[]::new);
            //
            FinReceivableBill finReceivableBill = new FinReceivableBill();
            finReceivableBill.setReceivableBillSidList(sids);
            return this.list(finReceivableBill);
        }
        return getDataTable(new ArrayList<>());
    }
}
