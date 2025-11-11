package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FrmPhotoSampleGain;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IFrmPhotoSampleGainService;
import com.platform.ems.service.ISystemDictDataService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 视觉设计单Controller
 *
 * @author chenkw
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/frm/photo/sample/gain")
@Api(tags = "视觉设计单")
public class FrmPhotoSampleGainController extends BaseController {

    @Autowired
    private IFrmPhotoSampleGainService frmPhotoSampleGainService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询视觉设计单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PHOTO_SAMPLE_GAIN_ALL)
    @ApiOperation(value = "查询视觉设计单列表", notes = "查询视觉设计单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmPhotoSampleGain.class))
    public TableDataInfo list(@RequestBody FrmPhotoSampleGain frmPhotoSampleGain) {
        startPage(frmPhotoSampleGain);
        List<FrmPhotoSampleGain> list = frmPhotoSampleGainService.selectFrmPhotoSampleGainList(frmPhotoSampleGain);
        return getDataTable(list);
    }

    /**
     * 导出视觉设计单列表
     */
    @Log(title = "视觉设计单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_PHOTO_SAMPLE_GAIN_ALL, loc = 1)
    @ApiOperation(value = "导出视觉设计单列表", notes = "导出视觉设计单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmPhotoSampleGain frmPhotoSampleGain) throws IOException {
        List<FrmPhotoSampleGain> list = frmPhotoSampleGainService.selectFrmPhotoSampleGainList(frmPhotoSampleGain);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmPhotoSampleGain> util = new ExcelUtil<>(FrmPhotoSampleGain.class, dataMap);
        util.exportExcel(response, list, "视觉设计单");
    }

    /**
     * 获取视觉设计单详细信息
     */
    @ApiOperation(value = "获取视觉设计单详细信息", notes = "获取视觉设计单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmPhotoSampleGain.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long photoSampleGainSid) {
        if (photoSampleGainSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmPhotoSampleGainService.selectFrmPhotoSampleGainById(photoSampleGainSid));
    }

    /**
     * 新增视觉设计单
     */
    @ApiOperation(value = "新增视觉设计单", notes = "新增视觉设计单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "视觉设计单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmPhotoSampleGain frmPhotoSampleGain) {
        int row = frmPhotoSampleGainService.insertFrmPhotoSampleGain(frmPhotoSampleGain);
        if (row > 0) {
            return AjaxResult.success(frmPhotoSampleGainService.selectFrmPhotoSampleGainById(frmPhotoSampleGain.getPhotoSampleGainSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改视觉设计单", notes = "修改视觉设计单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "视觉设计单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmPhotoSampleGain frmPhotoSampleGain) {
        return toAjax(frmPhotoSampleGainService.updateFrmPhotoSampleGain(frmPhotoSampleGain));
    }

    /**
     * 变更视觉设计单
     */
    @ApiOperation(value = "变更视觉设计单", notes = "变更视觉设计单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "视觉设计单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmPhotoSampleGain frmPhotoSampleGain) {
        return toAjax(frmPhotoSampleGainService.changeFrmPhotoSampleGain(frmPhotoSampleGain));
    }

    /**
     * 删除视觉设计单
     */
    @ApiOperation(value = "删除视觉设计单", notes = "删除视觉设计单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "视觉设计单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> photoSampleGainSids) {
        if (CollectionUtils.isEmpty(photoSampleGainSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmPhotoSampleGainService.deleteFrmPhotoSampleGainByIds(photoSampleGainSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmPhotoSampleGain frmPhotoSampleGain) {
        frmPhotoSampleGain.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmPhotoSampleGainService.submitVerify(frmPhotoSampleGain));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long photoSampleGainSid) {
        FrmPhotoSampleGain frmPhotoSampleGain = frmPhotoSampleGainService.selectFrmPhotoSampleGainById(photoSampleGainSid);
        if (frmPhotoSampleGain != null) {
            frmPhotoSampleGain.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmPhotoSampleGainService.submitVerify(frmPhotoSampleGain));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    /**
     * 修改视觉设计单处理状态（确认）
     */
    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "视觉设计单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmPhotoSampleGain frmPhotoSampleGain) {
        if (ArrayUtil.isEmpty(frmPhotoSampleGain.getPhotoSampleGainSidList())) {
            throw new CheckedException("请选择行");
        }
        if (StrUtil.isBlank(frmPhotoSampleGain.getHandleStatus())
                && StrUtil.isBlank(frmPhotoSampleGain.getBusinessType())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmPhotoSampleGainService.check(frmPhotoSampleGain));
    }

}
