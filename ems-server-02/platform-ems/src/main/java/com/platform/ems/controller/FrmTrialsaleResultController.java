package com.platform.ems.controller;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IBasSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.FrmTrialsaleResult;
import com.platform.ems.service.IFrmTrialsaleResultService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 试销结果单Controller
 *
 * @author chenkw
 * @date 2022-12-19
 */
@RestController
@RequestMapping("/frm/trialsale/result")
@Api(tags = "试销结果单")
public class FrmTrialsaleResultController extends BaseController {

    @Autowired
    private IFrmTrialsaleResultService frmTrialsaleResultService;
    @Autowired
    private IBasSkuService basSkuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询试销结果单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_TRIALSALE_RESULT_ALL)
    @ApiOperation(value = "查询试销结果单列表", notes = "查询试销结果单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmTrialsaleResult.class))
    public TableDataInfo list(@RequestBody FrmTrialsaleResult frmTrialsaleResult) {
        startPage(frmTrialsaleResult);
        List<FrmTrialsaleResult> list = frmTrialsaleResultService.selectFrmTrialsaleResultList(frmTrialsaleResult);
        TableDataInfo rsp = getDataTable(list);
        setSku1Name(list);
        return rsp;
    }

    /**
     * 导出试销结果单列表
     */
    @Log(title = "试销结果单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_TRIALSALE_RESULT_ALL, loc = 1)
    @ApiOperation(value = "导出试销结果单列表", notes = "导出试销结果单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmTrialsaleResult frmTrialsaleResult) throws IOException {
        List<FrmTrialsaleResult> list = frmTrialsaleResultService.selectFrmTrialsaleResultList(frmTrialsaleResult);
        setSku1Name(list);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmTrialsaleResult> util = new ExcelUtil<>(FrmTrialsaleResult.class, dataMap);
        util.exportExcel(response, list, "试销结果单");
    }

    /**
     * 根据SKU1code 分号字段获取对应的sku名称返回
     */
    private void setSku1Name(List<FrmTrialsaleResult> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                // sku1Code
                if (StrUtil.isNotBlank(item.getSku1Code())) {
                    String[] sku1Code = item.getSku1Code().split(";");
                    List<BasSku> skuList = basSkuService.selectBasSkuList(new BasSku().setSkuCodeList(sku1Code)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
                    if (ArrayUtil.isNotEmpty(skuList)) {
                        String sku1Name = "";
                        for (int i = 0; i < skuList.size(); i++) {
                            sku1Name = sku1Name + skuList.get(i).getSkuName() + ";";
                        }
                        item.setSku1Name(sku1Name);
                    }
                }
            });
        }
    }

    /**
     * 获取试销结果单详细信息
     */
    @ApiOperation(value = "获取试销结果单详细信息", notes = "获取试销结果单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmTrialsaleResult.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long trialsaleResultSid) {
        if (trialsaleResultSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmTrialsaleResultService.selectFrmTrialsaleResultById(trialsaleResultSid));
    }

    /**
     * 新增试销结果单
     */
    @ApiOperation(value = "新增试销结果单", notes = "新增试销结果单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "试销结果单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmTrialsaleResult frmTrialsaleResult) {
        int row = frmTrialsaleResultService.insertFrmTrialsaleResult(frmTrialsaleResult);
        if (row > 0) {
            return AjaxResult.success(frmTrialsaleResultService.selectFrmTrialsaleResultById(
                    frmTrialsaleResult.getTrialsaleResultSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改试销结果单", notes = "修改试销结果单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "试销结果单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmTrialsaleResult frmTrialsaleResult) {
        return toAjax(frmTrialsaleResultService.updateFrmTrialsaleResult(frmTrialsaleResult));
    }

    /**
     * 变更试销结果单
     */
    @ApiOperation(value = "变更试销结果单", notes = "变更试销结果单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "试销结果单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmTrialsaleResult frmTrialsaleResult) {
        return toAjax(frmTrialsaleResultService.changeFrmTrialsaleResult(frmTrialsaleResult));
    }

    /**
     * 删除试销结果单
     */
    @ApiOperation(value = "删除试销结果单", notes = "删除试销结果单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "试销结果单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> trialsaleResultSids) {
        if (CollectionUtils.isEmpty(trialsaleResultSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmTrialsaleResultService.deleteFrmTrialsaleResultByIds(trialsaleResultSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmTrialsaleResult frmTrialsaleResult) {
        frmTrialsaleResult.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmTrialsaleResultService.submitVerify(frmTrialsaleResult));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long trialsaleResultSid) {
        FrmTrialsaleResult frmTrialsaleResult = frmTrialsaleResultService.selectFrmTrialsaleResultById(trialsaleResultSid);
        if (frmTrialsaleResult != null) {
            frmTrialsaleResult.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmTrialsaleResultService.submitVerify(frmTrialsaleResult));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "试销结果单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmTrialsaleResult frmTrialsaleResult) {
        if (ArrayUtil.isEmpty(frmTrialsaleResult.getTrialsaleResultSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(frmTrialsaleResult.getHandleStatus())
                && StrUtil.isBlank(frmTrialsaleResult.getBusinessType())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(frmTrialsaleResultService.check(frmTrialsaleResult));
    }

}
