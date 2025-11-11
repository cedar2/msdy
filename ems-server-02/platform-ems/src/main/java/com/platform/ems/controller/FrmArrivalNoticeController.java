package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FrmNewproductTrialsalePlan;
import com.platform.ems.domain.base.EmsResultEntity;
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
import com.platform.ems.domain.FrmArrivalNotice;
import com.platform.ems.service.IFrmArrivalNoticeService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 到货通知单Controller
 *
 * @author chenkw
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/frm/arrival/notice")
@Api(tags = "到货通知单")
public class FrmArrivalNoticeController extends BaseController {

    @Autowired
    private IFrmArrivalNoticeService frmArrivalNoticeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询到货通知单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_ARRIVAL_NOTICE_ALL)
    @ApiOperation(value = "查询到货通知单列表", notes = "查询到货通知单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmArrivalNotice.class))
    public TableDataInfo list(@RequestBody FrmArrivalNotice frmArrivalNotice) {
        startPage(frmArrivalNotice);
        List<FrmArrivalNotice> list = frmArrivalNoticeService.selectFrmArrivalNoticeList(frmArrivalNotice);
        return getDataTable(list);
    }

    /**
     * 导出到货通知单列表
     */
    @Log(title = "到货通知单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_ARRIVAL_NOTICE_ALL, loc = 1)
    @ApiOperation(value = "导出到货通知单列表", notes = "导出到货通知单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmArrivalNotice frmArrivalNotice) throws IOException {
        List<FrmArrivalNotice> list = frmArrivalNoticeService.selectFrmArrivalNoticeList(frmArrivalNotice);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmArrivalNotice> util = new ExcelUtil<>(FrmArrivalNotice.class, dataMap);
        util.exportExcel(response, list, "到货通知单");
    }

    /**
     * 获取到货通知单详细信息
     */
    @ApiOperation(value = "获取到货通知单详细信息", notes = "获取到货通知单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmArrivalNotice.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long arrivalNoticeSid) {
        if (arrivalNoticeSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmArrivalNoticeService.selectFrmArrivalNoticeById(arrivalNoticeSid));
    }

    /**
     * 新增到货通知单
     */
    @ApiOperation(value = "新增到货通知单", notes = "新增到货通知单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "到货通知单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmArrivalNotice frmArrivalNotice) {
        int row = frmArrivalNoticeService.insertFrmArrivalNotice(frmArrivalNotice);
        if (row > 0) {
            return AjaxResult.success(frmArrivalNoticeService.selectFrmArrivalNoticeById(frmArrivalNotice.getArrivalNoticeSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改到货通知单", notes = "修改到货通知单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "到货通知单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmArrivalNotice frmArrivalNotice) {
        return toAjax(frmArrivalNoticeService.updateFrmArrivalNotice(frmArrivalNotice));
    }

    /**
     * 变更到货通知单
     */
    @ApiOperation(value = "变更到货通知单", notes = "变更到货通知单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "到货通知单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmArrivalNotice frmArrivalNotice) {
        return toAjax(frmArrivalNoticeService.changeFrmArrivalNotice(frmArrivalNotice));
    }

    /**
     * 删除到货通知单
     */
    @ApiOperation(value = "删除到货通知单", notes = "删除到货通知单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "到货通知单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> arrivalNoticeSids) {
        if (CollectionUtils.isEmpty(arrivalNoticeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmArrivalNoticeService.deleteFrmArrivalNoticeByIds(arrivalNoticeSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmArrivalNotice frmArrivalNotice) {
        frmArrivalNotice.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmArrivalNoticeService.submitVerify(frmArrivalNotice));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long arrivalNoticeSid) {
        FrmArrivalNotice frmArrivalNotice = frmArrivalNoticeService.selectFrmArrivalNoticeById(arrivalNoticeSid);
        if (frmArrivalNotice != null) {
            frmArrivalNotice.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmArrivalNoticeService.submitVerify(frmArrivalNotice));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    /**
     * 修改到货通知单处理状态（确认）
     */
    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "到货通知单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmArrivalNotice frmArrivalNotice) {
        if (ArrayUtil.isEmpty(frmArrivalNotice.getArrivalNoticeSidList())) {
            throw new CheckedException("请选择行");
        }
        if (StrUtil.isBlank(frmArrivalNotice.getHandleStatus())
                && StrUtil.isBlank(frmArrivalNotice.getBusinessType())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmArrivalNoticeService.check(frmArrivalNotice));
    }

}
