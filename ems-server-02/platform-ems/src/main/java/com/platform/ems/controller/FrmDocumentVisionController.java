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
import com.platform.ems.domain.FrmDocumentVision;
import com.platform.ems.service.IFrmDocumentVisionService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 文案脚本单Controller
 *
 * @author chenkw
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/frm/document/vision")
@Api(tags = "文案脚本单")
public class FrmDocumentVisionController extends BaseController {

    @Autowired
    private IFrmDocumentVisionService frmDocumentVisionService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询文案脚本单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PHOTO_SAMPLE_GAIN_ALL)
    @ApiOperation(value = "查询文案脚本单列表", notes = "查询文案脚本单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmDocumentVision.class))
    public TableDataInfo list(@RequestBody FrmDocumentVision frmDocumentVision) {
        startPage(frmDocumentVision);
        List<FrmDocumentVision> list = frmDocumentVisionService.selectFrmDocumentVisionList(frmDocumentVision);
        return getDataTable(list);
    }

    /**
     * 导出文案脚本单列表
     */
    @Log(title = "文案脚本单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PHOTO_SAMPLE_GAIN_ALL, loc = 1)
    @ApiOperation(value = "导出文案脚本单列表", notes = "导出文案脚本单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmDocumentVision frmDocumentVision) throws IOException {
        List<FrmDocumentVision> list = frmDocumentVisionService.selectFrmDocumentVisionList(frmDocumentVision);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmDocumentVision> util = new ExcelUtil<>(FrmDocumentVision.class, dataMap);
        util.exportExcel(response, list, "文案脚本单");
    }


    /**
     * 获取文案脚本单详细信息
     */
    @ApiOperation(value = "获取文案脚本单详细信息", notes = "获取文案脚本单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmDocumentVision.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long documentVisionSid) {
        if (documentVisionSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmDocumentVisionService.selectFrmDocumentVisionById(documentVisionSid));
    }

    /**
     * 新增文案脚本单
     */
    @ApiOperation(value = "新增文案脚本单", notes = "新增文案脚本单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文案脚本单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmDocumentVision frmDocumentVision) {
        int row = frmDocumentVisionService.insertFrmDocumentVision(frmDocumentVision);
        if (row > 0) {
            return AjaxResult.success(frmDocumentVisionService.selectFrmDocumentVisionById(frmDocumentVision.getDocumentVisionSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改文案脚本单", notes = "修改文案脚本单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文案脚本单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmDocumentVision frmDocumentVision) {
        return toAjax(frmDocumentVisionService.updateFrmDocumentVision(frmDocumentVision));
    }

    /**
     * 变更文案脚本单
     */
    @ApiOperation(value = "变更文案脚本单", notes = "变更文案脚本单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文案脚本单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmDocumentVision frmDocumentVision) {
        return toAjax(frmDocumentVisionService.changeFrmDocumentVision(frmDocumentVision));
    }

    /**
     * 删除文案脚本单
     */
    @ApiOperation(value = "删除文案脚本单", notes = "删除文案脚本单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文案脚本单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> documentVisionSids) {
        if (CollectionUtils.isEmpty(documentVisionSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmDocumentVisionService.deleteFrmDocumentVisionByIds(documentVisionSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmDocumentVision frmDocumentVision) {
        frmDocumentVision.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmDocumentVisionService.submitVerify(frmDocumentVision));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long documentVisionSid) {
        FrmDocumentVision frmDocumentVision = frmDocumentVisionService.selectFrmDocumentVisionById(documentVisionSid);
        if (frmDocumentVision != null) {
            frmDocumentVision.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmDocumentVisionService.submitVerify(frmDocumentVision));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    /**
     * 修改文案脚本单处理状态（确认）
     */
    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "文案脚本单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmDocumentVision frmDocumentVision) {
        if (ArrayUtil.isEmpty(frmDocumentVision.getDocumentVisionSidList())) {
            throw new CheckedException("请选择行");
        }
        if (StrUtil.isBlank(frmDocumentVision.getHandleStatus())
                && StrUtil.isBlank(frmDocumentVision.getBusinessType())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmDocumentVisionService.check(frmDocumentVision));
    }

}
