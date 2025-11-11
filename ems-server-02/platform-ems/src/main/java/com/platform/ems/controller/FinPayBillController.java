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
import com.platform.ems.domain.dto.request.form.FinPayBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinPayBillItemFormResponse;
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
 * 付款单Controller
 *
 * @author linhongwei
 * @date 2021-04-21
 */
@RestController
@RequestMapping("/pay/bill")
@Api(tags = "付款单")
public class FinPayBillController extends BaseController {

    @Autowired
    private IFinPayBillService finPayBillService;
    @Autowired
    private IFinPayBillItemService finPayBillItemService;
    @Autowired
    private IFinPayBillItemInvoiceService billItemInvoiceService;
    @Autowired
    private IFinPayBillItemKoukuanService koukuanService;
    @Autowired
    private IFinPayBillItemYufuService yufuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询付款单列表
     */
    @ApiOperation(value = "查询付款单列表", notes = "查询付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBill.class))
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody FinPayBill finPayBill) {
        startPage(finPayBill);
        List<FinPayBill> list = finPayBillService.selectFinPayBillList(finPayBill);
        return getDataTable(list);
    }

    /**
     * 导出付款单列表
     */
    @ApiOperation(value = "导出付款单列表", notes = "导出付款单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinPayBill finPayBill) throws IOException {
        List<FinPayBill> list = finPayBillService.selectFinPayBillList(finPayBill);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPayBill> util = new ExcelUtil<>(FinPayBill.class, dataMap);
        util.exportExcel(response, list, "付款单");
    }

    /**
     * 获取付款单详细信息
     */
    @ApiOperation(value = "获取付款单详细信息", notes = "获取付款单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBill.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long payBillSid) {
        if (payBillSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finPayBillService.selectFinPayBillById(payBillSid));
    }

    /**
     * 计算基本信息的待核销金额
     */
    @ApiOperation(value = "计算基本信息的待核销金额", notes = "计算基本信息的待核销金额")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/count/dai")
    public AjaxResult countDai(@RequestBody FinPayBill finPayBill) {
        SysDefaultSettingClient settingClient = finPayBillService.getClientSetting();
        finPayBillService.countBaseDai(finPayBill, settingClient);
        return AjaxResult.success(finPayBill);
    }

    /**
     * 新增付款单
     */
    @ApiOperation(value = "新增付款单", notes = "新增付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid FinPayBill finPayBill) {
        int row = finPayBillService.insertFinPayBill(finPayBill);
        if (row > 0) {
            return AjaxResult.success("操作成功", new FinPayBill().setPayBillSid(finPayBill.getPayBillSid()));
        }
        return toAjax(row);
    }

    @ApiOperation(value = "修改付款单", notes = "修改付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid FinPayBill finPayBill) {
        return toAjax(finPayBillService.updateFinPayBill(finPayBill));
    }

    /**
     * 变更付款单
     */
    @ApiOperation(value = "变更付款单", notes = "变更付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FinPayBill finPayBill) {
        return toAjax(finPayBillService.changeFinPayBill(finPayBill));
    }

    /**
     * 删除付款单
     */
    @ApiOperation(value = "删除付款单", notes = "删除付款单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> payBillSids) {
        if (CollectionUtils.isEmpty(payBillSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(finPayBillService.deleteFinPayBillByIds(payBillSids));
    }

    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "提交前校验", notes = "提交前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    public AjaxResult submitVerify(@RequestBody FinPayBill finPayBill) {
        if (ArrayUtil.isNotEmpty(finPayBill.getPayBillSidList())) {
            // 查询页面的提交按钮
            for (Long sid : finPayBill.getPayBillSidList()) {
                finPayBill = finPayBillService.selectFinPayBillById(sid);
                finPayBill.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
                EmsResultEntity result = finPayBillService.submitVerify(finPayBill);
                if (EmsResultEntity.WARN_TAG.equals(result.getTag())) {
                    return AjaxResult.success(result);
                }
            }
            return AjaxResult.success(EmsResultEntity.success());
        }
        else {
            // 新建、编辑页面的提交按钮
            return AjaxResult.success(finPayBillService.submitVerify(finPayBill));
        }
    }

    @ApiOperation(value = "修改处理状态接口", notes = "修改处理状态接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody FinPayBill finPayBill) {
        return toAjax(finPayBillService.check(finPayBill));
    }

    @ApiOperation(value = "到账", notes = "到账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/receipt")
    public AjaxResult receipt(@RequestBody FinPayBill finPayBill) {
        return toAjax(finPayBillService.receipt(finPayBill));
    }

    @ApiOperation(value = "撤回保存", notes = "撤回保存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/revocation")
    public AjaxResult revocation(@RequestBody FinPayBill finPayBill) {
        return toAjax(finPayBillService.revocation(finPayBill));
    }

    @ApiOperation(value = "更新发票台账弹窗查询", notes = "更新发票台账弹窗查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/invoice/list")
    public AjaxResult invoiceList(@RequestBody FinPayBill finPayBill) {
        return AjaxResult.success(finPayBillService.invoiceList(finPayBill));
    }

    @ApiOperation(value = "更新发票台账", notes = "更新发票台账")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/invoice/update")
    public AjaxResult invoiceUpdate(@RequestBody FinPayBill finPayBill) {
        return AjaxResult.success(finPayBillService.invoiceUpdate(finPayBill));
    }

    @ApiOperation(value = "设置是否有票", notes = "设置是否有票")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/set/isyoupiao")
    public AjaxResult setIsyoupiao(@RequestBody FinPayBill finPayBill) {
        return AjaxResult.success(finPayBillService.setIsyoupiao(finPayBill));
    }

    /**
     * 查询付款单明细报表列表
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "查询付款单明细报表列表", notes = "查询付款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBillItemFormResponse.class))
    public TableDataInfo itemList(@RequestBody FinPayBillItemFormRequest request) {
        FinPayBillItem finPayBillItem = new FinPayBillItem();
        BeanUtil.copyProperties(request, finPayBillItem);
        startPage(request);
        List<FinPayBillItem> list = finPayBillItemService.selectFinPayBillItemList(finPayBillItem);
        return getDataTable(list, FinPayBillItemFormResponse::new);
    }

    /**
     * 导出付款单明细报表列表
     */
    @ApiOperation(value = "导出付款单明细报表列表", notes = "导出付款单明细报表列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/export")
    public void exportItem(HttpServletResponse response, FinPayBillItemFormRequest request) throws IOException {
        FinPayBillItem finPayBillItem = new FinPayBillItem();
        BeanUtil.copyProperties(request, finPayBillItem);
        List<FinPayBillItem> list = finPayBillItemService.selectFinPayBillItemList(finPayBillItem);
        List<FinPayBillItemFormResponse> responsesList = BeanCopyUtils.copyListProperties(list, FinPayBillItemFormResponse::new);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPayBillItemFormResponse> util = new ExcelUtil<>(FinPayBillItemFormResponse.class, dataMap);
        util.exportExcel(response, responsesList, "付款单明细报表");
    }

    /**
     * 获取付款单明细报表详细信息
     */
    @ApiOperation(value = "获取付款单明细报表详细信息", notes = "获取付款单明细报表详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBillItem.class))
    @PostMapping("/item/getInfo")
    public AjaxResult getItemInfo(Long payBillItemSid) {
        if (payBillItemSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(finPayBillItemService.selectFinPayBillItemById(payBillItemSid));
    }

    /**
     * 查询供应商发票台账核销记录
     */
    @PostMapping("/item/invoice/list")
    @ApiOperation(value = "查询供应商发票台账核销记录", notes = "查询供应商发票台账核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBillItemInvoice.class))
    public TableDataInfo itemInvoiceList(@RequestBody FinPayBillItemInvoice request) {
        startPage(request);
        List<FinPayBillItemInvoice> list = billItemInvoiceService.selectFinPayBillItemInvoiceList(request);
        return getDataTable(list);
    }

    /**
     * 导出供应商发票台账核销记录
     */
    @ApiOperation(value = "导出供应商发票台账核销记录", notes = "导出供应商发票台账核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/invoice/export")
    public void exportItem(HttpServletResponse response, FinPayBillItemInvoice request) throws IOException {
        List<FinPayBillItemInvoice> list = billItemInvoiceService.selectFinPayBillItemInvoiceList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPayBillItemInvoice> util = new ExcelUtil<>(FinPayBillItemInvoice.class, dataMap);
        util.exportExcel(response, list, "供应商发票台账核销记录");
    }

    /**
     * 查询核销扣款日志列表
     */
    @ApiOperation(value = "查询核销扣款日志列表", notes = "查询核销扣款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBillItemKoukuan.class))
    @PostMapping("/item/koukuan/list")
    public TableDataInfo listKoukuan(@RequestBody FinPayBillItemKoukuan request) {
        startPage(request);
        List<FinPayBillItemKoukuan> list = koukuanService.selectFinPayBillItemKoukuanList(request);
        return getDataTable(list);
    }

    /**
     * 导出核销扣款日志列表
     */
    @ApiOperation(value = "导出核销扣款日志列表", notes = "导出核销扣款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/koukuan/export")
    public void exportKoukuan(HttpServletResponse response, FinPayBillItemKoukuan request) throws IOException {
        List<FinPayBillItemKoukuan> list = koukuanService.selectFinPayBillItemKoukuanList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPayBillItemKoukuan> util = new ExcelUtil<>(FinPayBillItemKoukuan.class, dataMap);
        util.exportExcel(response, list, "供应商扣款核销记录");
    }

    /**
     * 查询核销供应商已预收款日志列表
     */
    @ApiOperation(value = "查询核销供应商已预收款日志列表", notes = "查询核销供应商已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBillItemYufu.class))
    @PostMapping("/item/yufu/list")
    public TableDataInfo listYufu(@RequestBody FinPayBillItemYufu request) {
        startPage(request);
        List<FinPayBillItemYufu> list = yufuService.selectFinPayBillItemYufuList(request);
        return getDataTable(list);
    }

    /**
     * 导出核销供应商已预收款日志列表
     */
    @ApiOperation(value = "导出核销供应商已预收款日志列表", notes = "导出核销供应商已预收款日志列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/item/yufu/export")
    public void exportYufu(HttpServletResponse response, FinPayBillItemYufu request) throws IOException {
        List<FinPayBillItemYufu> list = yufuService.selectFinPayBillItemYufuList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FinPayBillItemYufu> util = new ExcelUtil<>(FinPayBillItemYufu.class, dataMap);
        util.exportExcel(response, list, "已预付款核销记录");
    }

    /**
     * 查询扣款核销记录
     */
    @PostMapping("/book/koukuan/bill/list")
    @ApiOperation(value = "查询扣款核销记录", notes = "查询扣款核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBill.class))
    public TableDataInfo itemKouKuanList(@RequestBody FinPayBillItemKoukuan request) {
        List<FinPayBillItemKoukuan> koukuanList = koukuanService.selectFinPayBillItemKoukuanList(request);
        if (CollectionUtils.isNotEmpty(koukuanList)) {
            Long[] sids = koukuanList.stream().map(FinPayBillItemKoukuan::getPayBillSid).toArray(Long[]::new);
            //
            FinPayBill finPayBill = new FinPayBill();
            finPayBill.setPayBillSidList(sids);
            return this.list(finPayBill);
        }
        return getDataTable(new ArrayList<>());
    }

    /**
     * 查询预付款核销记录
     */
    @PostMapping("/book/yufu/bill/list")
    @ApiOperation(value = "查询预付款核销记录", notes = "查询预付款核销记录")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FinPayBill.class))
    public TableDataInfo itemYufuList(@RequestBody FinPayBillItemYufu request) {
        List<FinPayBillItemYufu> yufuList = yufuService.selectFinPayBillItemYufuList(request);
        if (CollectionUtils.isNotEmpty(yufuList)) {
            Long[] sids = yufuList.stream().map(FinPayBillItemYufu::getPayBillSid).toArray(Long[]::new);
            //
            FinPayBill finPayBill = new FinPayBill();
            finPayBill.setPayBillSidList(sids);
            return this.list(finPayBill);
        }
        return getDataTable(new ArrayList<>());
    }

}
